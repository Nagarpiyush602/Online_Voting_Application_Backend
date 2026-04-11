package in.scalive.votezy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.VoterRequestDTO;
import in.scalive.votezy.dto.VoterResponseDTO;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.DuplicateResourceException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.VoterRepository;
import jakarta.transaction.Transactional;

@Service
public class VoterService {

    private VoterRepository voterRepository;

    public VoterService(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    public VoterResponseDTO registerVoter(VoterRequestDTO dto) {
        if (voterRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Voter with email id " + dto.getEmail() + " already exists");
        }

        Voter voter = new Voter();
        voter.setName(dto.getName());
        voter.setEmail(dto.getEmail());
        voter.setRole(Role.VOTER);

        Voter saved = voterRepository.save(voter);
        return mapToResponseDTO(saved);
    }

    public List<VoterResponseDTO> getAllVoters() {
        return voterRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public VoterResponseDTO getVoterById(Long id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter with id " + id + " not found"));

        return mapToResponseDTO(voter);
    }

    public VoterResponseDTO updateVoter(Long id, VoterRequestDTO dto) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter with id " + id + " not found"));

        if (dto.getName() != null) {
            voter.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            voter.setEmail(dto.getEmail());
        }

        Voter updated = voterRepository.save(voter);
        return mapToResponseDTO(updated);
    }

    @Transactional
    public void deleteVoter(Long id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot delete voter with id " + id + " as it does not exist"));

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