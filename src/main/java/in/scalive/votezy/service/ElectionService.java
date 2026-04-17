package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionRequestDTO;
import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.InvalidRequestException;
import in.scalive.votezy.exception.VoteNotAllowedException;
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

    public ElectionResponseDTO createElection(ElectionRequestDTO request, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        validateElectionTimes(request);

        Election election = new Election();
        election.setName(request.getName().trim());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());

        Election savedElection = electionRepository.save(election);
        return convertToDTO(savedElection);
    }

    public ElectionResponseDTO updateElection(Long id, ElectionRequestDTO request, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        validateElectionTimes(request);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        election.setName(request.getName().trim());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());

        Election updatedElection = electionRepository.save(election);
        return convertToDTO(updatedElection);
    }

    public void deleteElection(Long id, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        electionRepository.delete(election);
    }

    public List<ElectionResponseDTO> getAllElections(CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        return electionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ElectionResponseDTO getElectionById(Long id, CurrentUserDTO currentUser) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        Voter voter = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getUserId()));

        if (voter.getRole() == Role.ADMIN) {
            return convertToDTO(election);
        }

        if (getElectionStatus(election) == ElectionStatus.UPCOMING) {
            throw new InvalidRequestException("This election is not visible yet");
        }

        return convertToDTO(election);
    }

    public List<ElectionResponseDTO> getActiveElections() {
        return electionRepository.findAll()
                .stream()
                .filter(election -> getElectionStatus(election) == ElectionStatus.ACTIVE)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ElectionResponseDTO getActiveElection() {
        List<Election> activeElections = electionRepository.findAll()
                .stream()
                .filter(election -> getElectionStatus(election) == ElectionStatus.ACTIVE)
                .collect(Collectors.toList());

        if (activeElections.isEmpty()) {
            throw new ResourceNotFoundException("No active election found");
        }

        if (activeElections.size() > 1) {
            throw new InvalidRequestException("Multiple active elections found");
        }

        return convertToDTO(activeElections.get(0));
    }

    private void validateAdmin(CurrentUserDTO currentUser) {
        Voter voter = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getUserId()));

        if (voter.getRole() != Role.ADMIN) {
            throw new VoteNotAllowedException("Only admin can perform this action");
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

    private ElectionStatus getElectionStatus(Election election) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(election.getStartTime())) {
            return ElectionStatus.UPCOMING;
        } else if (now.isAfter(election.getEndTime())) {
            return ElectionStatus.COMPLETED;
        } else {
            return ElectionStatus.ACTIVE;
        }
    }

    private ElectionResponseDTO convertToDTO(Election election) {
        ElectionResponseDTO response = new ElectionResponseDTO();
        response.setId(election.getId());
        response.setName(election.getName());
        response.setStartTime(election.getStartTime());
        response.setEndTime(election.getEndTime());
        response.setStatus(getElectionStatus(election).name());
        return response;
    }
}