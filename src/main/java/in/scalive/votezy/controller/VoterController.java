package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.VoterRequestDTO;
import in.scalive.votezy.dto.VoterResponseDTO;
import in.scalive.votezy.service.VoterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/voters")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class VoterController {

    private final VoterService voterService;

    public VoterController(VoterService voterService) {
        this.voterService = voterService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> registerVoter(
            @RequestBody @Valid VoterRequestDTO dto) {

        VoterResponseDTO response = voterService.registerVoter(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Voter registered successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> getVoterById(@PathVariable Long id) {
        VoterResponseDTO response = voterService.getVoterById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Voter fetched successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoterResponseDTO>>> getAllVoters() {
        List<VoterResponseDTO> response = voterService.getAllVoters();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All voters fetched successfully", response)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> upadeteVoters(
            @PathVariable Long id,
            @RequestBody VoterRequestDTO dto) {

        VoterResponseDTO response = voterService.updateVoter(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Voter updated successfully", response)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteVoter(@PathVariable Long id) {
        voterService.deleteVoter(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Voter with id: " + id + " deleted successfully", null)
        );
    }
}