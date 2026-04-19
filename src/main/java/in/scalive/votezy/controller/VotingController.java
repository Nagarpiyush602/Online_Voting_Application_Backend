package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.dto.VoteCheckResponseDTO;
import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.service.VotingService;
import in.scalive.votezy.util.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class VotingController {

    private final VotingService votingService;
    private final CurrentUserUtil currentUserUtil;

    public VotingController(VotingService votingService, CurrentUserUtil currentUserUtil) {
        this.votingService = votingService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping("/cast")
    public ResponseEntity<ApiResponse<VoteResponseDTO>> castVote(
            @RequestBody @Valid VoteRequestDTO voteRequest,
            HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        VoteResponseDTO voteResponse = votingService.castVote(voteRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Vote cast successfully", voteResponse));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<VoteCheckResponseDTO>> checkVoteStatus(
    		HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        VoteCheckResponseDTO response = votingService.checkVoteStatus(currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Vote status fetched successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoteResponseDTO>>> getAllVotes(
    		HttpServletRequest request) {

        CurrentUserDTO currentUser = currentUserUtil.getCurrentUser(request);
        List<VoteResponseDTO> response = votingService.getAllVotes(currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All votes fetched successfully", response)
        );
    }
}