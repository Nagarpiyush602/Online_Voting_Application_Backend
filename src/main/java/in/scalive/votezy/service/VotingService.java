package in.scalive.votezy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.VoteCheckResponseDTO;
import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.InvalidRequestException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.VoteRepository;

@Service
public class VotingService {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;
    private final AuthorizationService authorizationService;

    public VotingService(VoteRepository voteRepository,
                         CandidateRepository candidateRepository,
                         ElectionService electionService,
                         AuthorizationService authorizationService) {
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public VoteResponseDTO castVote(VoteRequestDTO request, CurrentUserDTO currentUser) {
        Voter voter = authorizationService.requireVoter(currentUser);
        Election activeElection = electionService.getSingleActiveElectionEntity();

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate not found with id: " + request.getCandidateId()));

        if (candidate.getElection() == null) {
            throw new InvalidRequestException("Candidate is not linked to any election");
        }

        if (!candidate.getElection().getId().equals(activeElection.getId())) {
            throw new InvalidRequestException("Selected candidate does not belong to the active election");
        }

        if (voteRepository.existsByVoterAndElection(voter, activeElection)) {
            throw new InvalidRequestException("You have already voted in this election");
        }

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setCandidate(candidate);
        vote.setElection(activeElection);

        Vote savedVote = voteRepository.save(vote);

        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);

        return convertToDTO(savedVote);
    }

    public VoteCheckResponseDTO checkVoteStatus(CurrentUserDTO currentUser) {
        Voter voter = authorizationService.requireVoter(currentUser);
        Election activeElection = electionService.getSingleActiveElectionEntity();
        boolean hasVoted = voteRepository.existsByVoterAndElection(voter, activeElection);

        return new VoteCheckResponseDTO(
                voter.getId(),
                activeElection.getId(),
                hasVoted
        );
    }

    public List<VoteResponseDTO> getAllVotes(CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        return voteRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private VoteResponseDTO convertToDTO(Vote vote) {
        VoteResponseDTO dto = new VoteResponseDTO();
        dto.setMessage("Vote cast successfully");
        dto.setSuccess(true);
        dto.setVoterId(vote.getVoter().getId());
        dto.setCandidateId(vote.getCandidate().getId());
        dto.setElectionId(vote.getElection().getId());
        return dto;
    }
}