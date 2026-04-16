package in.scalive.votezy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CandidateRequestDTO;
import in.scalive.votezy.dto.CandidateResponseDTO;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.VoteNotAllowedException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionRepository;
import in.scalive.votezy.repository.VoterRepository;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final ElectionService electionService;
    private final VoterRepository voterRepository;

    public CandidateService(CandidateRepository candidateRepository, VoterRepository voterRepository,
                            ElectionRepository electionRepository, ElectionService electionService) {
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.electionService = electionService;
        this.voterRepository = voterRepository;
    }

    public CandidateResponseDTO addCandidate(CandidateRequestDTO request, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + request.getElectionId()));

        String normalizedName = request.getName().trim();
        String normalizedParty = request.getParty().trim();

        if (candidateRepository.existsByPartyIgnoreCaseAndElection(normalizedParty, election)) {
            throw new VoteNotAllowedException("Party '" + normalizedParty + "' alredy has a candidate in this election");
        }

        Candidate candidate = new Candidate();
        candidate.setName(normalizedName);
        candidate.setParty(normalizedParty);
        candidate.setVoteCount(0);
        candidate.setElection(election);

        Candidate savedCandidate = candidateRepository.save(candidate);
        return convertToDTO(savedCandidate);
    }

    public List<CandidateResponseDTO> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CandidateResponseDTO getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        return convertToDTO(candidate);
    }

    public List<CandidateResponseDTO> getCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElectionId(electionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CandidateResponseDTO> getCandidatesForActiveElection() {
        ElectionResponseDTO activeElection = electionService.getActiveElection();

        List<Candidate> candidates = candidateRepository.findByElectionId(activeElection.getId());

        return candidates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CandidateResponseDTO updateCandidate(Long id, CandidateRequestDTO request, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + request.getElectionId()));

        String normalizedName = request.getName().trim();
        String normalizedParty = request.getParty().trim();

        if (candidateRepository.existsByPartyIgnoreCaseAndElectionAndIdNot(normalizedParty, election, id)) {
            throw new VoteNotAllowedException("Party '" + normalizedParty + "' already has another candidate in this election");
        }

        candidate.setName(normalizedName);
        candidate.setParty(normalizedParty);
        candidate.setElection(election);

        Candidate updatedCandidate = candidateRepository.save(candidate);
        return convertToDTO(updatedCandidate);
    }

    public void deleteCandidate(Long id, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        candidateRepository.delete(candidate);
    }

    private void validateAdmin(CurrentUserDTO currentUser) {
        Voter voter = voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getUserId()));

        if (voter.getRole() != Role.ADMIN) {
            throw new VoteNotAllowedException("Only admin can perform this action");
        }
    }

    private CandidateResponseDTO convertToDTO(Candidate candidate) {
        CandidateResponseDTO response = new CandidateResponseDTO();
        response.setId(candidate.getId());
        response.setName(candidate.getName());
        response.setParty(candidate.getParty());
        response.setVoteCount(candidate.getVoteCount());
        response.setElectionId(candidate.getElection() != null ? candidate.getElection().getId() : null);
        return response;
    }
}