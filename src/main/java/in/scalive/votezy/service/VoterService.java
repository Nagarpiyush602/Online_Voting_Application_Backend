package in.scalive.votezy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.VoterRequestDTO;
import in.scalive.votezy.dto.VoterResponseDTO;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.DuplicateResourceException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.UnauthorizedActionException;
import in.scalive.votezy.repository.VoterRepository;
import jakarta.transaction.Transactional;

@Service
public class VoterService {

    private final VoterRepository voterRepository;
    private final AuthorizationService authorizationService;

    public VoterService(VoterRepository voterRepository,
                        AuthorizationService authorizationService) {
        this.voterRepository = voterRepository;
        this.authorizationService = authorizationService;
    }

    public VoterResponseDTO registerVoter(VoterRequestDTO dto) {
        String normalizedName = dto.getName().trim();
        String normalizedEmail = dto.getEmail().trim();

        if (voterRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Voter with email id " + normalizedEmail + " already exists");
        }

        Voter voter = new Voter();
        voter.setName(normalizedName);
        voter.setEmail(normalizedEmail);
        voter.setRole(Role.VOTER);

        Voter saved = voterRepository.save(voter);
        return mapToResponseDTO(saved);
    }

    public List<VoterResponseDTO> getAllVoters(CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        return voterRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public VoterResponseDTO getVoterById(Long id, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        Voter voter = findVoterByIdOrThrow(id);
        return mapToResponseDTO(voter);
    }

    public VoterResponseDTO updateVoter(Long id, VoterRequestDTO dto, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        Voter voter = findVoterByIdOrThrow(id);

        String normalizedName = dto.getName().trim();
        String normalizedEmail = dto.getEmail().trim();

        if (voterRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new DuplicateResourceException("Email " + normalizedEmail + " already exists");
        }

        voter.setName(normalizedName);
        voter.setEmail(normalizedEmail);

        Voter updated = voterRepository.save(voter);
        return mapToResponseDTO(updated);
    }

    @Transactional
    public void deleteVoter(Long id, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        Voter voter = findVoterByIdOrThrow(id);
        voterRepository.delete(voter);
    }

    public VoterResponseDTO getMyProfile(CurrentUserDTO currentUser) {
        Voter voter = authorizationService.getCurrentUserEntity(currentUser);
        return mapToResponseDTO(voter);
    }

    public VoterResponseDTO updateMyProfile(VoterRequestDTO dto, CurrentUserDTO currentUser) {
        Voter voter = authorizationService.requireVoter(currentUser);

        String normalizedName = dto.getName().trim();
        String normalizedEmail = dto.getEmail().trim();

        if (voterRepository.existsByEmailAndIdNot(normalizedEmail, voter.getId())) {
            throw new DuplicateResourceException("Email " + normalizedEmail + " already exists");
        }

        voter.setName(normalizedName);
        voter.setEmail(normalizedEmail);

        Voter updated = voterRepository.save(voter);
        return mapToResponseDTO(updated);
    }

    private Voter findVoterByIdOrThrow(Long id) {
        return voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter with id " + id + " not found"));
    }

    private VoterResponseDTO mapToResponseDTO(Voter voter) {
        VoterResponseDTO dto = new VoterResponseDTO();
        dto.setId(voter.getId());
        dto.setName(voter.getName());
        dto.setEmail(voter.getEmail());
        dto.setRole(voter.getRole());
        return dto;
    }
}