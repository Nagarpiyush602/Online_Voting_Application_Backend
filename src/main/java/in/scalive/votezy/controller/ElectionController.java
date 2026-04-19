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
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ApiResponse<List<ElectionResponseDTO>>> getActiveElections() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Active elections fetched successfully", electionService.getActiveElections())
        );
    }

    @GetMapping("/active/one")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> getActiveElection() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Active election fetched successfully", electionService.getActiveElection())
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> createElection(
            @RequestBody @Valid ElectionRequestDTO request,
            HttpServletRequest servletRequest) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(servletRequest);
        ElectionResponseDTO response = electionService.createElection(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Election created successfully", response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> updateElection(
            @PathVariable Long id,
            @RequestBody @Valid ElectionRequestDTO request,
            HttpServletRequest servletRequest) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(servletRequest);
        ElectionResponseDTO response = electionService.updateElection(id, request, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election updated successfully", response)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteElection(
            @PathVariable Long id,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        electionService.deleteElection(id, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election deleted successfully", "Election removed successfully")
        );
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<ElectionResponseDTO>>> getAllElections(
    		HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All elections fetched successfully",
                        electionService.getAllElections(currentUser))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> getElectionById(
            @PathVariable Long id,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election fetched successfully",
                        electionService.getElectionById(id, currentUser))
        );
    }
}