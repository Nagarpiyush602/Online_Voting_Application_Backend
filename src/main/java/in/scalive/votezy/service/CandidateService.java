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
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.UnauthorizedActionException;
import in.scalive.votezy.exception.VoteNotAllowedException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionRepository;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final ElectionService electionService;

    public CandidateService(CandidateRepository candidateRepository,
                            ElectionRepository electionRepository,
                            ElectionService electionService) {
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.electionService = electionService;
    }

    public CandidateResponseDTO addCandidate(CandidateRequestDTO request, CurrentUserDTO currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only ADMIN can add candidate");
        }

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + request.getElectionId()));

        Candidate candidate = new Candidate();
        candidate.setName(request.getName());
        candidate.setParty(request.getParty());
        candidate.setVoteCount(0);
        candidate.setElection(election); // 🔥 request wala election use hoga

        Candidate savedCandidate = candidateRepository.save(candidate);

        return convertToDTO(savedCandidate);
    }

    public List<CandidateResponseDTO> getAllCandidates(CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        return candidateRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CandidateResponseDTO getCandidateById(Long id, CurrentUserDTO currentUser) {
        validateAdmin(currentUser);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        return convertToDTO(candidate);
    }

    public List<CandidateResponseDTO> getCandidatesByElectionId(Long electionId, CurrentUserDTO currentUser) {
        validateAdminOrVoter(currentUser);

        electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        return candidateRepository.findByElectionId(electionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CandidateResponseDTO> getCandidatesForActiveElection(CurrentUserDTO currentUser) {
        validateAdminOrVoter(currentUser);

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
        if (currentUser == null || currentUser.getRole() == null || currentUser.getRole() != Role.ADMIN) {
            throw new VoteNotAllowedException("Only admin can access this");
        }
    }

    private void validateAdminOrVoter(CurrentUserDTO currentUser) {
        if (currentUser == null || currentUser.getRole() == null) {
            throw new VoteNotAllowedException("User not authenticated");
        }

        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.VOTER) {
            throw new VoteNotAllowedException("Only admin or voter can access this");
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