package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.service.ElectionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ElectionResponseDTO>> getActiveElection() {
        ElectionResponseDTO response = electionService.getActiveElection();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Active election fetched successfully", response)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Election>> createElection(@RequestBody @Valid Election election,@RequestParam Long adminId) {
        Election savedElection = electionService.createElection(election,adminId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Election created successfully", savedElection));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Election>>> getAllElections() {
        List<Election> elections = electionService.getAllElections();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All elections fetched successfully", elections)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Election>> getElectionById(@PathVariable Long id) {
        Election election = electionService.getElectionById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election fetched successfully", election)
        );
    }
}