package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionRequestDTO;
import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.service.ElectionService;
import in.scalive.votezy.util.CurrentUserUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class ElectionController {

    private final ElectionService electionService;
    private final CurrentUserUtil currentUserUtil;

    public ElectionController(ElectionService electionService, CurrentUserUtil currentUserUtil) {
        this.electionService = electionService;
        this.currentUserUtil = currentUserUtil;
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> getActiveElection() {
        ElectionResponseDTO response = electionService.getActiveElection();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Active election fetched successfully", response)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> createElection(
            @RequestBody @Valid ElectionRequestDTO request,
            @RequestHeader("X-USER-ID") String userIdHeader,
            @RequestHeader("X-USER-ROLE") String roleHeader) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(userIdHeader, roleHeader);
        ElectionResponseDTO response = electionService.createElection(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Election created successfully", response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> updateElection(
            @PathVariable Long id,
            @RequestBody @Valid ElectionRequestDTO request,
            @RequestHeader("X-USER-ID") String userIdHeader,
            @RequestHeader("X-USER-ROLE") String roleHeader) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(userIdHeader, roleHeader);
        ElectionResponseDTO response = electionService.updateElection(id, request, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election updated successfully", response)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteElection(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userIdHeader,
            @RequestHeader("X-USER-ROLE") String roleHeader) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(userIdHeader, roleHeader);
        electionService.deleteElection(id, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election deleted successfully", "Election removed")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElectionResponseDTO>>> getAllElections() {
        List<ElectionResponseDTO> elections = electionService.getAllElections();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All elections fetched successfully", elections)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> getElectionById(@PathVariable Long id) {
        ElectionResponseDTO election = electionService.getElectionById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election fetched successfully", election)
        );
    }
}