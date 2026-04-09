package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
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

    public VoteResponseDTO castVote(VoteRequestDTO request) {
        Voter voter = voterRepository.findById(request.getVoterId())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        if (calculateStatus(election) != ElectionStatus.ACTIVE) {
            throw new RuntimeException("Election is not active");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(election.getStartTime()) || now.isAfter(election.getEndTime())) {
            throw new RuntimeException("Voting is allowed only during election time");
        }

        if (!candidate.getElection().getId().equals(election.getId())) {
            throw new RuntimeException("Candidate does not belong to this election");
        }

        if (voteRepository.existsByVoterAndElection(voter, election)) {
            throw new RuntimeException("Voter has already voted in this election");
        }

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setCandidate(candidate);
        vote.setElection(election);

        Vote savedVote = voteRepository.save(vote);

        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);

        return convertToDTO(savedVote);
    }

    public boolean hasVoted(Long voterId, Long electionId) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        return voteRepository.existsByVoterAndElection(voter, election);
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
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