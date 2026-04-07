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
	public VoteResponseDTO casteVote(VoteRequestDTO request) {
		Voter voter=voterRepository.findById(request.getVoterId()).orElseThrow(()->new ResourceNotFoundException("Voter not found with id: "+request.getVoterId()));
		Candidate candidate= candidateRepository.findById(request.getCandidateId()).orElseThrow(()-> new ResourceNotFoundException("Candidate not found with id: " +request.getCandidateId()));
		Election election= electionRepository.findById(request.getElectionId()).orElseThrow(()-> new ResourceNotFoundException("election not found with id: " +request.getElectionId()));
		if(election.getStatus()!=ElectionStatus.ACTIVE) {
			throw new IllegalStateException("Vote can be cast only when election is ACTIVE");
		}
        if(candidate.getElection()==null || !candidate.getElection().getId().equals(election.getId())) {
        	throw new IllegalStateException(
                    "Candidate does not belong to this election");
        }
        if (voteRepository.existsByVoterAndElection(voter, election)) {
        	throw new VoteNotAllowedException("Voter has already cast the vote in this election");
        }
		
		Vote vote=new Vote();
		vote.setVoter(voter);
		vote.setCandidate(candidate);
		vote.setElection(election);
		voteRepository.save(vote);
		
		candidate.setVoteCount(candidate.getVoteCount()+1);
		candidateRepository.save(candidate);
		
		return new VoteResponseDTO("Vote cast successfully",true,voter.getId(),candidate.getId(),election.getId());
	}
	public List<Vote> getAllVotes(){
		return voteRepository.findAll();
	}
	
	
}
