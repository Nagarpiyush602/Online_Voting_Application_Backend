package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.VoteCheckResponseDTO;
import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.InvalidRequestException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.UnauthorizedActionException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionRepository;
import in.scalive.votezy.repository.VoteRepository;
import in.scalive.votezy.repository.VoterRepository;

@Service
public class VotingService {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;
    private final ElectionRepository electionRepository;

    public VotingService(VoteRepository voteRepository,
                         CandidateRepository candidateRepository,
                         VoterRepository voterRepository,
                         ElectionRepository electionRepository) {
        this.voteRepository = voteRepository;
        this.candidateRepository = candidateRepository;
        this.voterRepository = voterRepository;
        this.electionRepository = electionRepository;
    }

    public VoteResponseDTO castVote(VoteRequestDTO request, CurrentUserDTO currentUser) {
        if (currentUser.getRole() != Role.VOTER) {
            throw new UnauthorizedActionException("Only VOTER is allowed to cast vote");
        }

        Voter voter = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found with id: " + currentUser.getUserId()));

        if (voter.getRole() != Role.VOTER) {
            throw new UnauthorizedActionException("Only voter are allowed to cast vote");
        }

        Election activeElection = getSingleActiveElection();

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + request.getCandidateId()));

        if (candidate.getElection() == null || !candidate.getElection().getId().equals(activeElection.getId())) {
            throw new InvalidRequestException("Candidate does not belong to the active election");
        }

        if (voteRepository.existsByVoterAndElection(voter, activeElection)) {
            throw new InvalidRequestException("Voter has already voted in the active election");
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
        if (currentUser.getRole() != Role.VOTER) {
            throw new UnauthorizedActionException("Only VOTER is allowed to check vote status");
        }

        Voter voter = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found with id: " + currentUser.getUserId()));

        if (voter.getRole() != Role.VOTER) {
            throw new UnauthorizedActionException("Only voter are allowed to check vote status");
        }

        Election activeElection = getSingleActiveElection();

        boolean hasVoted = voteRepository.existsByVoterAndElection(voter, activeElection);

        return new VoteCheckResponseDTO(
                voter.getId(),
                activeElection.getId(),
                hasVoted
        );
    }

    public List<VoteResponseDTO> getAllVotes(CurrentUserDTO currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only ADMIN is allowed to fetch all votes");
        }

        Voter admin = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getUserId()));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only admin are allowed to fetch all votes");
        }

        List<Vote> votes = voteRepository.findAll();

        return votes.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private Election getSingleActiveElection() {
        List<Election> activeElections = electionRepository.findAll()
                .stream()
                .filter(election -> calculateStatus(election) == ElectionStatus.ACTIVE)
                .toList();

        if (activeElections.isEmpty()) {
            throw new ResourceNotFoundException("No active election found");
        }

        if (activeElections.size() > 1) {
            throw new InvalidRequestException("Multiple active elections found");
        }

        return activeElections.get(0);
    }

    private ElectionStatus calculateStatus(Election election) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(election.getStartTime())) {
            return ElectionStatus.UPCOMING;
        }

        if (now.isAfter(election.getEndTime())) {
            return ElectionStatus.COMPLETED;
        }

        return ElectionStatus.ACTIVE;
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