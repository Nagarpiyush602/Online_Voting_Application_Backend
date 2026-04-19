package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionRequestDTO;
import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.InvalidRequestException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.ElectionRepository;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final AuthorizationService authorizationService;

    public ElectionService(ElectionRepository electionRepository,
                           AuthorizationService authorizationService) {
        this.electionRepository = electionRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ElectionResponseDTO createElection(ElectionRequestDTO request, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        validateElectionTimes(request);
        validateDuplicateElectionName(request.getName());

        Election election = new Election();
        election.setName(request.getName().trim());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());
        election.setStatus(calculateElectionStatus(election));

        Election savedElection = electionRepository.save(election);
        return convertToDTO(savedElection);
    }

    @Transactional
    public ElectionResponseDTO updateElection(Long id, ElectionRequestDTO request, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        validateElectionTimes(request);

        Election election = getElectionEntityById(id);
        validateDuplicateElectionNameForUpdate(request.getName(), election.getId());

        election.setName(request.getName().trim());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());
        election.setStatus(calculateElectionStatus(election));

        Election updatedElection = electionRepository.save(election);
        return convertToDTO(updatedElection);
    }

    @Transactional
    public void deleteElection(Long id, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        Election election = getElectionEntityById(id);
        electionRepository.delete(election);
    }

    @Transactional
    public List<ElectionResponseDTO> getAllElections(CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        syncAllElectionStatuses();

        return electionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ElectionResponseDTO getElectionById(Long id, CurrentUserDTO currentUser) {
        Election election = getElectionEntityById(id);

        Voter user = authorizationService.getCurrentUserEntity(currentUser);

        if (user.getRole() == Role.ADMIN) {
            return convertToDTO(election);
        }

        if (election.getStatus() == ElectionStatus.UPCOMING) {
            throw new InvalidRequestException("This election is not visible yet");
        }

        return convertToDTO(election);
    }

    @Transactional
    public List<ElectionResponseDTO> getActiveElections() {
        syncAllElectionStatuses();

        return electionRepository.findAll()
                .stream()
                .filter(election -> election.getStatus() == ElectionStatus.ACTIVE)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ElectionResponseDTO getActiveElection() {
        Election activeElection = getSingleActiveElectionEntity();
        return convertToDTO(activeElection);
    }

    @Transactional
    public Election getElectionEntityById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        syncElectionStatus(election);
        return election;
    }

    @Transactional
    public Election getSingleActiveElectionEntity() {
        syncAllElectionStatuses();

        List<Election> activeElections = electionRepository.findAll()
                .stream()
                .filter(election -> election.getStatus() == ElectionStatus.ACTIVE)
                .collect(Collectors.toList());

        if (activeElections.isEmpty()) {
            throw new ResourceNotFoundException("No active election found");
        }

        if (activeElections.size() > 1) {
            throw new InvalidRequestException("Multiple active elections found. Please keep only one active election at a time");
        }

        return activeElections.get(0);
    }

    public ElectionStatus calculateElectionStatus(Election election) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(election.getStartTime())) {
            return ElectionStatus.UPCOMING;
        } else if (now.isAfter(election.getEndTime())) {
            return ElectionStatus.COMPLETED;
        } else {
            return ElectionStatus.ACTIVE;
        }
    }

    private void syncElectionStatus(Election election) {
        ElectionStatus calculatedStatus = calculateElectionStatus(election);

        if (election.getStatus() == null || election.getStatus() != calculatedStatus) {
            election.setStatus(calculatedStatus);
            electionRepository.save(election);
        }
    }

    private void syncAllElectionStatuses() {
        List<Election> elections = electionRepository.findAll();

        for (Election election : elections) {
            ElectionStatus calculatedStatus = calculateElectionStatus(election);

            if (election.getStatus() == null || election.getStatus() != calculatedStatus) {
                election.setStatus(calculatedStatus);
                electionRepository.save(election);
            }
        }
    }

    private void validateElectionTimes(ElectionRequestDTO request) {
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new InvalidRequestException("Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidRequestException("End time must be after start time");
        }
    }

    private void validateDuplicateElectionName(String name) {
        electionRepository.findByName(name.trim())
                .ifPresent(e -> {
                    throw new InvalidRequestException("Election with this name already exists");
                });
    }

    private void validateDuplicateElectionNameForUpdate(String name, Long electionId) {
        electionRepository.findByName(name.trim())
                .ifPresent(existingElection -> {
                    if (!existingElection.getId().equals(electionId)) {
                        throw new InvalidRequestException("Election with this name already exists");
                    }
                });
    }

    private ElectionResponseDTO convertToDTO(Election election) {
        ElectionResponseDTO response = new ElectionResponseDTO();
        response.setId(election.getId());
        response.setName(election.getName());
        response.setStartTime(election.getStartTime());
        response.setEndTime(election.getEndTime());
        response.setStatus(election.getStatus() != null
                ? election.getStatus().name()
                : calculateElectionStatus(election).name());
        return response;
    }
}