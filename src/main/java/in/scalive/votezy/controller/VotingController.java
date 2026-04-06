package in.scalive.votezy.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.votezy.dto.VoteRequestDTO;
import in.scalive.votezy.dto.VoteResponseDTO;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.service.VotingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class VotingController {

    private VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @PostMapping("/cast")
    public ResponseEntity<VoteResponseDTO> castVote(@RequestBody @Valid VoteRequestDTO voteRequest) {
    	VoteResponseDTO voteResponse = votingService.casteVote(voteRequest);
        return new ResponseEntity<>(voteResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VoteResponseDTO>> getAllVotes() {
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

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}