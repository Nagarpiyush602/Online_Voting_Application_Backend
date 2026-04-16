package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionResultResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionResult;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.ResultStatus;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.VoteNotAllowedException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionRepository;
import in.scalive.votezy.repository.ElectionResultRepository;
import in.scalive.votezy.repository.VoteRepository;
import in.scalive.votezy.repository.VoterRepository;

@Service
public class ElectionResultService {

    private final ElectionRepository electionRepository;
    private final ElectionResultRepository electionResultRepository;
    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;

    public ElectionResultService(ElectionRepository electionRepository,
                                 ElectionResultRepository electionResultRepository,
                                 VoterRepository voterRepository,
                                 VoteRepository voteRepository,
                                 CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.electionResultRepository = electionResultRepository;
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
        this.voterRepository = voterRepository;
    }

    public ElectionResultResponseDTO declareElectionResult(Long electionId, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        if (election.getStatus() != ElectionStatus.COMPLETED) {
            throw new VoteNotAllowedException("Result can be declared only after election is completed");
        }

        if (electionResultRepository.existsByElection_Id(electionId)) {
            ElectionResult existingResult = electionResultRepository.findByElection_Id(electionId).get();
            return convertToDTO(existingResult);
        }

        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No candidates found for this election");
        }

        long totalVotesCount = voteRepository.countByElection_Id(electionId);
        int totalVotes = (int) totalVotesCount;

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

    public List<ElectionResultResponseDTO> getAllResults() {
        List<ElectionResult> results = electionResultRepository.findAll();
        List<ElectionResultResponseDTO> responseList = new ArrayList<>();

        for (ElectionResult result : results) {
            responseList.add(convertToDTO(result));
        }

        return responseList;
    }

    public ElectionResultResponseDTO getResultByElectionId(Long electionId) {
        ElectionResult result = electionResultRepository.findByElection_Id(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found for election id: " + electionId));

        return convertToDTO(result);
    }

    private void validateAdmin(CurrentUserDTO currentUser) {
        Voter voter = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getUserId()));

        if (voter.getRole() != Role.ADMIN) {
            throw new VoteNotAllowedException("Only admin can perform this action");
        }
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