package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionResultResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionResult;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.ResultStatus;
import in.scalive.votezy.exception.InvalidRequestException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.VoteNotAllowedException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionResultRepository;
import in.scalive.votezy.repository.VoteRepository;

@Service
public class ElectionResultService {

    private final ElectionResultRepository electionResultRepository;
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;
    private final AuthorizationService authorizationService;

    public ElectionResultService(ElectionResultRepository electionResultRepository,
                                 VoteRepository voteRepository,
                                 CandidateRepository candidateRepository,
                                 ElectionService electionService,
                                 AuthorizationService authorizationService) {
        this.electionResultRepository = electionResultRepository;
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ElectionResultResponseDTO declareElectionResult(Long electionId, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        Election election = electionService.getElectionEntityById(electionId);

        if (electionService.calculateElectionStatus(election) != ElectionStatus.COMPLETED) {
            throw new VoteNotAllowedException("Result can be declared only after election is completed");
        }

        if (electionResultRepository.existsByElection_Id(electionId)) {
            ElectionResult existingResult = electionResultRepository.findByElection_Id(electionId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Result already exists but could not be fetched"));
            return convertToDTO(existingResult);
        }

        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);

        if (candidates.isEmpty()) {
            throw new InvalidRequestException("No candidates found for this election");
        }

        int totalVotes = (int) voteRepository.countByElection_Id(electionId);

        ElectionResult result = new ElectionResult();
        result.setElection(election);
        result.setTotalVotes(totalVotes);
        result.setDeclaredAt(LocalDateTime.now());

        if (totalVotes == 0) {
            result.setWinner(null);
            result.setWinnerVoteCount(0);
            result.setResultStatus(ResultStatus.NO_VOTES);

            ElectionResult savedResult = electionResultRepository.save(result);
            return convertToDTO(savedResult);
        }

        int maxVotes = -1;
        List<Candidate> topCandidates = new ArrayList<>();

        for (Candidate candidate : candidates) {
            int candidateVotes = candidate.getVoteCount();

            if (candidateVotes > maxVotes) {
                maxVotes = candidateVotes;
                topCandidates.clear();
                topCandidates.add(candidate);
            } else if (candidateVotes == maxVotes) {
                topCandidates.add(candidate);
            }
        }

        if (topCandidates.size() == 1) {
            Candidate winner = topCandidates.get(0);
            result.setWinner(winner);
            result.setWinnerVoteCount(winner.getVoteCount());
            result.setResultStatus(ResultStatus.DECLARED);
        } else {
            result.setWinner(null);
            result.setWinnerVoteCount(maxVotes);
            result.setResultStatus(ResultStatus.TIE);
        }

        ElectionResult savedResult = electionResultRepository.save(result);
        return convertToDTO(savedResult);
    }

    public List<ElectionResultResponseDTO> getAllResults(CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        List<ElectionResult> results = electionResultRepository.findAll();
        List<ElectionResultResponseDTO> responseList = new ArrayList<>();

        for (ElectionResult result : results) {
            responseList.add(convertToDTO(result));
        }

        return responseList;
    }

    public ElectionResultResponseDTO getResultByElectionId(Long electionId, CurrentUserDTO currentUser) {
        authorizationService.requireAdminOrVoter(currentUser);

        ElectionResult result = electionResultRepository.findByElection_Id(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found for election id: " + electionId));

        return convertToDTO(result);
    }

    private ElectionResultResponseDTO convertToDTO(ElectionResult result) {
        ElectionResultResponseDTO dto = new ElectionResultResponseDTO();

        dto.setElectionId(result.getElection().getId());
        dto.setElectionName(result.getElection().getName());
        dto.setTotalVotes(result.getTotalVotes());
        dto.setResultStatus(result.getResultStatus());
        dto.setDeclaredAt(result.getDeclaredAt());

        if (result.getWinner() != null) {
            dto.setWinnerName(result.getWinner().getName());
        } else {
            dto.setWinnerName(null);
        }

        dto.setWinnerVotes(result.getWinnerVoteCount() != null ? result.getWinnerVoteCount() : 0);

        if (result.getResultStatus() == ResultStatus.TIE) {
            List<Candidate> candidates = candidateRepository.findByElectionId(result.getElection().getId());
            List<String> tiedCandidates = new ArrayList<>();

            for (Candidate candidate : candidates) {
                if (candidate.getVoteCount() == result.getWinnerVoteCount()) {
                    tiedCandidates.add(candidate.getName());
                }
            }

            dto.setTiedCandidates(tiedCandidates);
            dto.setMessage("Result declared as tie");
        } else if (result.getResultStatus() == ResultStatus.NO_VOTES) {
            dto.setTiedCandidates(new ArrayList<>());
            dto.setMessage("Result declared: no votes were cast in this election");
        } else {
            dto.setTiedCandidates(new ArrayList<>());
            dto.setMessage("Result declared successfully");
        }

        return dto;
    }
}