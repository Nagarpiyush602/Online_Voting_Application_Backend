package in.scalive.votezy.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.scalive.votezy.entity.ElectionResult;
import in.scalive.votezy.service.ElectionResultService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/election-result")
@CrossOrigin
public class ElectionResultController {
	private ElectionResultService electionResultService;

	public ElectionResultController(ElectionResultService electionResultService) {
		this.electionResultService = electionResultService;
	}
	@PostMapping("/declare/{electionName}")
	public ResponseEntity<ElectionResult> declareElectionResult(@PathVariable String electionName){
		ElectionResult result=electionResultService.declareElectionResult(electionName);

		return ResponseEntity.ok(result);
	}
	@GetMapping
	public ResponseEntity<List<ElectionResult>> getAllResults(){
		List <ElectionResult> results = electionResultService.getAllResults();
		return ResponseEntity.ok(results);
	}
	
}
