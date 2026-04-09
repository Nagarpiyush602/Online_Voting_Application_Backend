package in.scalive.votezy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.ElectionRepository;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public Election createElection(Election election) {
        if (election.getEndTime().isBefore(election.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        election.setStatus(calculateStatus(election));
        return electionRepository.save(election);
    }

    public List<Election> getAllElections() {
        List<Election> elections = electionRepository.findAll();
        for (Election election : elections) {
            election.setStatus(calculateStatus(election));
        }
        return elections;
    }

    public Election getElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        election.setStatus(calculateStatus(election));
        return election;
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
    
    private ElectionResponseDTO convertToDTO(Election election) {
        return new ElectionResponseDTO(election.getId(),election.getName(),election.getStartTime(),election.getEndTime(),calculateStatus(election));
    }
}