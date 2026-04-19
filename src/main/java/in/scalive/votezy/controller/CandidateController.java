package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.CandidateRequestDTO;
import in.scalive.votezy.dto.CandidateResponseDTO;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.service.CandidateService;
import in.scalive.votezy.util.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class CandidateController {

    private final CandidateService candidateService;
    private final CurrentUserUtil currentUserUtil;

    public CandidateController(CandidateService candidateService, CurrentUserUtil currentUserUtil) {
        this.candidateService = candidateService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> addCandidate(
            @RequestBody @Valid CandidateRequestDTO request,
            HttpServletRequest servletRequest) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(servletRequest);
        CandidateResponseDTO response = candidateService.addCandidate(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Candidate added successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> getCandidateById(
            @PathVariable Long id,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        CandidateResponseDTO response = candidateService.getCandidateById(id, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidate fetched successfully", response)
        );
    }

    @GetMapping("/active-election")
    public ResponseEntity<ApiResponse<List<CandidateResponseDTO>>> getCandidatesForActiveElection(
    		HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        List<CandidateResponseDTO> response = candidateService.getCandidatesForActiveElection(currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Active election candidates fetched successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateResponseDTO>>> getAllCandidates(
    		HttpServletRequest request
            ) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        List<CandidateResponseDTO> response = candidateService.getAllCandidates(currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All candidates fetched successfully", response)
        );
    }

    @GetMapping("/election/{electionId}")
    public ResponseEntity<ApiResponse<List<CandidateResponseDTO>>> getCandidatesByElectionId(
            @PathVariable Long electionId,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        List<CandidateResponseDTO> response = candidateService.getCandidatesByElectionId(electionId, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidates fetched successfully for election", response)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> updateCandidate(
            @PathVariable Long id,
            @RequestBody @Valid CandidateRequestDTO request,
            HttpServletRequest servletRequest) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(servletRequest);
        CandidateResponseDTO response = candidateService.updateCandidate(id, request, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidate updated successfully", response)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCandidate(
            @PathVariable Long id,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        candidateService.deleteCandidate(id, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidate with id: " + id + " deleted successfully", null)
        );
    }
}