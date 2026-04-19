package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.ElectionResultResponseDTO;
import in.scalive.votezy.service.ElectionResultService;
import in.scalive.votezy.util.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/election-results")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class ElectionResultController {

    private final ElectionResultService electionResultService;
    private final CurrentUserUtil currentUserUtil;

    public ElectionResultController(ElectionResultService electionResultService, CurrentUserUtil currentUserUtil) {
        this.electionResultService = electionResultService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping("/declare/{electionId}")
    public ResponseEntity<ApiResponse<ElectionResultResponseDTO>> declareResult(
            @PathVariable Long electionId,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        ElectionResultResponseDTO response = electionResultService.declareElectionResult(electionId, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election result declared successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElectionResultResponseDTO>>> getAllResults(
    		HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        List<ElectionResultResponseDTO> response = electionResultService.getAllResults(currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All election results fetched successfully", response)
        );
    }

    @GetMapping("/{electionId}")
    public ResponseEntity<ApiResponse<ElectionResultResponseDTO>> getResultByElectionId(
            @PathVariable Long electionId,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        ElectionResultResponseDTO response = electionResultService.getResultByElectionId(electionId, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Election result fetched successfully", response)
        );
    }
}