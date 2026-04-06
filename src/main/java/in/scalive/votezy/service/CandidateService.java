package in.scalive.votezy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CandidateRequestDTO;
import in.scalive.votezy.dto.CandidateResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionRepository;

@Service
public class CandidateService {
	private CandidateRepository candidateRepository;
	private ElectionRepository electionRepository;

	public CandidateService(CandidateRepository candidateRepository,ElectionRepository electionRepository) {
		this.candidateRepository = candidateRepository;
		this.electionRepository = electionRepository;
	}
	public CandidateResponseDTO addCandidate(CandidateRequestDTO request) {
		Election election = electionRepository.findById(request.getElectionId()).orElseThrow(()->new ResourceNotFoundException("Election not found with id: " +request.getElectionId()));
		Candidate candidate = new Candidate();
		candidate.setName(request.getName());
		candidate.setParty(request.getParty());
		candidate.setElection(election);
		
		candidate = candidateRepository.save(candidate);
		return mapToDTO(candidate);
	}
	public List<CandidateResponseDTO> getAllCandidates(){
		return candidateRepository.findAll().stream().map(this::mapToDTO).toList();
	}
	public CandidateResponseDTO getCandidateById(Long id) {
		Candidate candidate=candidateRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Candidate not found"));
		
		return mapToDTO(candidate);
	}
	public CandidateResponseDTO updateCandidate(Long id,CandidateRequestDTO request) {
		Candidate candidate=candidateRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Candidate not found"));
		if(request.getName()!=null) {
			candidate.setName(request.getName());
		}
		if(request.getParty()!=null) {
			candidate.setParty(request.getParty());
		}
		if(request.getElectionId()!=null) {
			Election election = electionRepository.findById(request.getElectionId()).orElseThrow(()->new ResourceNotFoundException("Election not found with id: "+request.getElectionId()));
		    candidate.setElection(election);
		}
		candidate = candidateRepository.save(candidate);
		return mapToDTO(candidate);
	}
	
	public void deleteCandidate(Long id) {
		Candidate candidate = candidateRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
		List<Vote> votes=candidate.getVote();
		for(Vote v:votes) {
			v.setCandidate(null);
		}
		candidate.getVote().clear();
		candidateRepository.delete(candidate);
	}
	private CandidateResponseDTO mapToDTO(Candidate c) {
		return new CandidateResponseDTO(c.getId(),c.getName(),c.getParty(),c.getVoteCount(),c.getElection().getId());
	}
}
