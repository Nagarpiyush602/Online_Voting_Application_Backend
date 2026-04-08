package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ElectionResultResponseDTO;
import in.scalive.votezy.service.ElectionResultService;

@RestController
@RequestMapping("/api/election-results")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class ElectionResultController {

    private final ElectionResultService electionResultService;

    public ElectionResultController(ElectionResultService electionResultService) {
        this.electionResultService = electionResultService;
    }

    @PostMapping("/declare/{electionId}")
    public ResponseEntity<ElectionResultResponseDTO> declareResult(@PathVariable Long electionId) {
        return ResponseEntity.ok(electionResultService.declareElectionResult(electionId));
    }

    @GetMapping
    public ResponseEntity<List<ElectionResultResponseDTO>> getAllResults() {
        return ResponseEntity.ok(electionResultService.getAllResults());
    }

    @GetMapping("/{electionId}")
    public ResponseEntity<ElectionResultResponseDTO> getResultByElectionId(@PathVariable Long electionId) {
        return ResponseEntity.ok(electionResultService.getResultByElectionId(electionId));
    }
}