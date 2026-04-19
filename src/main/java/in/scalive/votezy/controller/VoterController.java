package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.VoterRequestDTO;
import in.scalive.votezy.dto.VoterResponseDTO;
import in.scalive.votezy.service.VoterService;
import in.scalive.votezy.util.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/voters")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class VoterController {

    private final VoterService voterService;
    private final CurrentUserUtil currentUserUtil;

    public VoterController(VoterService voterService, CurrentUserUtil currentUserUtil) {
        this.voterService = voterService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> registerVoter(
            @RequestBody @Valid VoterRequestDTO dto) {

        VoterResponseDTO response = voterService.registerVoter(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Voter registered successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> getVoterById(
            @PathVariable Long id,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        VoterResponseDTO response = voterService.getVoterById(id, currentUser);

        return ResponseEntity.ok(new ApiResponse<>(true, "Voter fetched successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoterResponseDTO>>> getAllVoters(HttpServletRequest request) {
        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        List<VoterResponseDTO> response = voterService.getAllVoters(currentUser);

        return ResponseEntity.ok(new ApiResponse<>(true, "All voters fetched successfully", response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> upadeteVoters(
            @PathVariable Long id,
            @RequestBody @Valid VoterRequestDTO dto,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        VoterResponseDTO response = voterService.updateVoter(id, dto, currentUser);

        return ResponseEntity.ok(new ApiResponse<>(true, "Voter updated successfully", response));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteVoter(
            @PathVariable Long id,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        voterService.deleteVoter(id, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Voter with id: " + id + " deleted successfully", null)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> getMyProfile(HttpServletRequest request) {
        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        VoterResponseDTO response = voterService.getMyProfile(currentUser);

        return ResponseEntity.ok(new ApiResponse<>(true, "My profile fetched successfully", response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<VoterResponseDTO>> updateMyProfile(
            @RequestBody @Valid VoterRequestDTO dto,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        VoterResponseDTO response = voterService.updateMyProfile(dto, currentUser);

        return ResponseEntity.ok(new ApiResponse<>(true, "My profile updated successfully", response));
    }
}