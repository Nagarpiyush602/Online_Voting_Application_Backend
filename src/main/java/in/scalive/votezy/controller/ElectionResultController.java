package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.entity.ElectionResult;
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
    public ResponseEntity<ElectionResult> declareElectionResult(@PathVariable Long electionId) {
        ElectionResult result = electionResultService.declareElectionResult(electionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ElectionResult>> getAllResults() {
        return ResponseEntity.ok(electionResultService.getAllResults());
    }
}