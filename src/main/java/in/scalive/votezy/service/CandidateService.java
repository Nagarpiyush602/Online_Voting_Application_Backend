package in.scalive.votezy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CandidateRequestDTO;
import in.scalive.votezy.dto.CandidateResponseDTO;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.exception.InvalidRequestException;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;
    private final AuthorizationService authorizationService;

    public CandidateService(CandidateRepository candidateRepository,
                            ElectionService electionService,
                            AuthorizationService authorizationService) {
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
        this.authorizationService = authorizationService;
    }

    public CandidateResponseDTO addCandidate(CandidateRequestDTO request, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        Election election = electionService.getElectionEntityById(request.getElectionId());
        String normalizedName = request.getName().trim();
        String normalizedParty = request.getParty().trim();

        validateDuplicateCandidate(normalizedName, normalizedParty, election);

        Candidate candidate = new Candidate();
        candidate.setName(normalizedName);
        candidate.setParty(normalizedParty);
        candidate.setVoteCount(0);
        candidate.setElection(election);

        Candidate savedCandidate = candidateRepository.save(candidate);
        return convertToDTO(savedCandidate);
    }

    public List<CandidateResponseDTO> getAllCandidates(CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        return candidateRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CandidateResponseDTO getCandidateById(Long id, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        return convertToDTO(candidate);
    }

    public List<CandidateResponseDTO> getCandidatesByElectionId(Long electionId, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);
        electionService.getElectionEntityById(electionId);

        return candidateRepository.findByElectionId(electionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CandidateResponseDTO> getCandidatesForActiveElection(CurrentUserDTO currentUser) {
        authorizationService.requireVoter(currentUser);

        Election activeElection = electionService.getSingleActiveElectionEntity();

        return candidateRepository.findByElectionId(activeElection.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CandidateResponseDTO updateCandidate(Long id, CandidateRequestDTO request, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        Election election = electionService.getElectionEntityById(request.getElectionId());
        String normalizedName = request.getName().trim();
        String normalizedParty = request.getParty().trim();

        validateDuplicateCandidateForUpdate(normalizedName, normalizedParty, election, id);

        candidate.setName(normalizedName);
        candidate.setParty(normalizedParty);
        candidate.setElection(election);

        Candidate updatedCandidate = candidateRepository.save(candidate);
        return convertToDTO(updatedCandidate);
    }

    public void deleteCandidate(Long id, CurrentUserDTO currentUser) {
        authorizationService.requireAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        candidateRepository.delete(candidate);
    }

    private void validateDuplicateCandidate(String name, String party, Election election) {
        if (candidateRepository.existsByNameIgnoreCaseAndPartyIgnoreCaseAndElection(name, party, election)) {
            throw new InvalidRequestException(
                    "Candidate '" + name + "' from party '" + party + "' already exists in this election");
        }
    }

    private void validateDuplicateCandidateForUpdate(String name, String party, Election election, Long candidateId) {
        if (candidateRepository.existsByNameIgnoreCaseAndPartyIgnoreCaseAndElectionAndIdNot(name, party, election, candidateId)) {
            throw new InvalidRequestException(
                    "Candidate '" + name + "' from party '" + party + "' already exists in this election");
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