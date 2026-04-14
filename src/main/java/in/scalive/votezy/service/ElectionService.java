package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.ElectionRequestDTO;
import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.UnauthorizedActionException;
import in.scalive.votezy.repository.ElectionRepository;
import in.scalive.votezy.repository.VoterRepository;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final VoterRepository voterRepository;

    public ElectionService(ElectionRepository electionRepository, VoterRepository voterRepository) {
        this.electionRepository = electionRepository;
        this.voterRepository = voterRepository;
    }

    public ElectionResponseDTO createElection(ElectionRequestDTO request, Long adminId) {
        validateAdmin(adminId);

        Election election = new Election();
        election.setName(request.getName());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());

        validateElectionDates(election);
        election.setStatus(calculateStatus(election));

        Election savedElection = electionRepository.save(election);
        return convertToDTO(savedElection);
    }

    public ElectionResponseDTO updateElection(Long id, ElectionRequestDTO request, Long adminId) {
        validateAdmin(adminId);

        Election existingElection = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        existingElection.setName(request.getName());
        existingElection.setStartTime(request.getStartTime());
        existingElection.setEndTime(request.getEndTime());

        validateElectionDates(existingElection);
        existingElection.setStatus(calculateStatus(existingElection));

        Election updatedElection = electionRepository.save(existingElection);
        return convertToDTO(updatedElection);
    }

    public void deleteElection(Long id, Long adminId) {
        validateAdmin(adminId);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        electionRepository.delete(election);
    }

    public List<ElectionResponseDTO> getAllElections() {
        List<Election> elections = electionRepository.findAll();

        return elections.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ElectionResponseDTO getElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        return convertToDTO(election);
    }

    public ElectionResponseDTO getActiveElection() {
        List<Election> activeElections = electionRepository.findByStatus(ElectionStatus.ACTIVE);

        if (activeElections.isEmpty()) {
            throw new ResourceNotFoundException("No active election found");
        }

        if (activeElections.size() > 1) {
            throw new RuntimeException("Multiple active elections found");
        }

        return convertToDTO(activeElections.get(0));
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

    private void validateAdmin(Long adminId) {
        Voter voter = voterRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));

        if (voter.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only ADMIN can perform this action");
        }
    }

    private void validateElectionDates(Election election) {
        if (election.getName() == null || election.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Election name is required");
        }

        if (election.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (election.getEndTime() == null) {
            throw new IllegalArgumentException("End time is required");
        }

        if (!election.getEndTime().isAfter(election.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    private ElectionResponseDTO convertToDTO(Election election) {
        return new ElectionResponseDTO(
                election.getId(),
                election.getName(),
                election.getStartTime(),
                election.getEndTime(),
                calculateStatus(election)
        );
    }
}