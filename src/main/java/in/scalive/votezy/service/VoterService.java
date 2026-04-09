package in.scalive.votezy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.VoterRequestDTO;
import in.scalive.votezy.dto.VoterResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.DuplicateResourceException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.VoterRepository;
import jakarta.transaction.Transactional;

@Service
public class VoterService {
	private VoterRepository voterRepository;
	private CandidateRepository candidateRepository;
	
	public VoterService(VoterRepository voterRepository, CandidateRepository candidateRepository) {
		this.voterRepository = voterRepository;
		this.candidateRepository = candidateRepository;
	}
	
	public VoterResponseDTO registerVoter(VoterRequestDTO dto) {
		if(voterRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateResourceException("voter with email id"+dto.getEmail()+" already exists"); 
		}
		Voter voter = new Voter();
		voter.setName(dto.getName());
		voter.setEmail(dto.getEmail());
		Voter saved = voterRepository.save(voter);
		return mapToResponseDTO(saved);
	}
	
	public List<VoterResponseDTO> getAllVoters(){
		return voterRepository.findAll().stream().map(this::mapToResponseDTO).toList();
	}
	
	public VoterResponseDTO getVoterById(Long id) {
		Voter voter = voterRepository.findById(id).orElse(null);
		if(voter==null) {
			throw new ResourceNotFoundException("voter with id"+id+" not found"); 
		}
		return mapToResponseDTO(voter);
	}
	
	public VoterResponseDTO updateVoter(Long id,VoterRequestDTO dto) {
		Voter voter = voterRepository.findById(id).orElse(null);
		if(voter==null) {
			throw new ResourceNotFoundException("voter with id"+id+" not found"); 
		}
		if(dto.getName()!=null) {
			voter.setName(dto.getName());
		}
		if(dto.getEmail()!=null) {
			voter.setEmail(dto.getEmail());
		}
		Voter updated = voterRepository.save(voter);
		return mapToResponseDTO(updated);
	}
	
	@Transactional
	public void deleteVoter(Long id) {
		Voter voter = voterRepository.findById(id).orElse(null);
		if(voter==null) {
			throw new ResourceNotFoundException("cannot delete voter with id : "+id+" as it does not exists"); 
		}
		Vote vote=voter.getVotes();
		if(vote!=null) {
			Candidate candidate = vote.getCandidate();
			candidate.setVoteCount(candidate.getVoteCount()-1);
			candidateRepository.save(candidate);
		}
		voterRepository.delete(voter);
	}
	
	private VoterResponseDTO mapToResponseDTO(Voter voter) {
		VoterResponseDTO dto = new VoterResponseDTO();
		dto.setId(voter.getId());
		dto.setName(voter.getName());
		dto.setEmail(voter.getEmail());
		return dto;
	}

}
