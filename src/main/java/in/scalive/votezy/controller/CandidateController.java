package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.CandidateRequestDTO;
import in.scalive.votezy.dto.CandidateResponseDTO;
import in.scalive.votezy.service.CandidateService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> addCandidate(
            @RequestBody @Valid CandidateRequestDTO request,@RequestParam Long adminId) {

        CandidateResponseDTO response = candidateService.addCandidate(request,adminId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Candidate added successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> getCandidateById(@PathVariable Long id) {
        CandidateResponseDTO response = candidateService.getCandidateById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidate fetched successfully", response)
        );
    }

    @GetMapping("/active-election")
    public ResponseEntity<ApiResponse<List<CandidateResponseDTO>>> getCandidatesForActiveElection() {
        List<CandidateResponseDTO> response = candidateService.getCandidatesForActiveElection();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Active election candidates fetched successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateResponseDTO>>> getAllCandidates() {
        List<CandidateResponseDTO> response = candidateService.getAllCandidates();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All candidates fetched successfully", response)
        );
    }

    @GetMapping("/election/{electionId}")
    public ResponseEntity<ApiResponse<List<CandidateResponseDTO>>> getCandidatesByElectionId(
            @PathVariable Long electionId) {

        List<CandidateResponseDTO> response = candidateService.getCandidatesByElectionId(electionId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidates fetched successfully for election", response)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> updateCandidate(
            @PathVariable Long id,
            @RequestBody CandidateRequestDTO request) {

        CandidateResponseDTO response = candidateService.updateCandidate(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidate updated successfully", response)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Candidate with id: " + id + " deleted successfully", null)
        );
    }
}