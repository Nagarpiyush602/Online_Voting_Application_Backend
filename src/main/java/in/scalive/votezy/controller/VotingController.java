package in.scalive.votezy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ApiResponse;
import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.service.VotingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class VotingController {

    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @PostMapping("/cast")
    public ResponseEntity<ApiResponse<VoteResponseDTO>> castVote(@RequestBody @Valid VoteRequestDTO voteRequest) {
        VoteResponseDTO voteResponse = votingService.castVote(voteRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Vote cast successfully", voteResponse));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkVoteStatus(
            @RequestParam Long voterId,
            @RequestParam Long electionId) {

        boolean hasVoted = votingService.hasVoted(voterId, electionId);
        Map<String, Boolean> response = Map.of("hasVoted", hasVoted);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Vote status fetched successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoteResponseDTO>>> getAllVotes() {
        List<Vote> votes = votingService.getAllVotes();

        List<VoteResponseDTO> response = votes.stream()
                .map(vote -> new VoteResponseDTO(
                        "Vote fetched successfully",
                        true,
                        vote.getVoter().getId(),
                        vote.getCandidate().getId(),
                        vote.getElection().getId()
                ))
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All votes fetched successfully", response)
        );
    }
}