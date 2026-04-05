package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.VoteNotAllowedException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionRepository;
import in.scalive.votezy.repository.VoteRepository;
import in.scalive.votezy.repository.VoterRepository;
import jakarta.transaction.Transactional;

@Service
public class VotingService {
	private VoteRepository voteRepository;
	private CandidateRepository candidateRepository;
	private VoterRepository voterRepository;
	private ElectionRepository electionRepository;
	
	public VotingService(VoteRepository voteRepository, CandidateRepository candidateRepository,
			VoterRepository voterRepository,ElectionRepository electionRepository) {
		this.voteRepository = voteRepository;
		this.electionRepository=electionRepository;
		this.candidateRepository = candidateRepository;
		this.voterRepository = voterRepository;
	}
	@Transactional
	public Vote casteVote(Long voterId,Long candidateId,Long electionId) {
		Voter voter=voterRepository.findById(voterId).orElseThrow(()->new ResourceNotFoundException("Voter not found with id: "+voterId));
		Candidate candidate= candidateRepository.findById(candidateId).orElseThrow(()-> new ResourceNotFoundException("Candidate not found with id: " +candidateId));
		Election election= electionRepository.findById(electionId).orElseThrow(()-> new ResourceNotFoundException("election not found with id: " +electionId));
		
		ElectionStatus currentStatus = getCurrentElectionStatus(election);
        election.setStatus(currentStatus);
        
        if(currentStatus!=ElectionStatus.ACTIVE) {
        	throw new VoteNotAllowedException(
                    "Voting is not allowed because election status is " + currentStatus);
        }
		if(voter.isHasVoted()) {
			throw new VoteNotAllowedException("Voter with id " + voterId + " has already cast a vote");
		}
		
		Vote vote=new Vote();
		vote.setVoter(voter);
		vote.setCandidate(candidate);
		vote.setElection(election);
		Vote savedVote = voteRepository.save(vote);
		
		candidate.setVoteCount(candidate.getVoteCount()+1);
		candidateRepository.save(candidate);
		voter.setVote(savedVote);
		voter.setHasVoted(true);
		voterRepository.save(voter);
		return savedVote;
	}
	public List<Vote> getAllVotes(){
		return voteRepository.findAll();
	}
	private ElectionStatus getCurrentElectionStatus(Election election) {
		LocalDateTime now = LocalDateTime.now();
		
		if(now.isBefore(election.getStartTime())) {
			return ElectionStatus.UPCOMING;
		}
		if(now.isAfter(election.getEndTime())) {
			return ElectionStatus.COMPLETED;
		}
		return ElectionStatus.ACTIVE;
	}
	
}
