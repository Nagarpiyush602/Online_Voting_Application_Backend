package in.scalive.votezy.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.votezy.dto.ElectionResultResponseDTO;
import in.scalive.votezy.service.ElectionResultService;

@RestController
@RequestMapping("/api/election-results")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class ElectionResultController {
	private ElectionResultService electionResultService;

	public ElectionResultController(ElectionResultService electionResultService) {
		this.electionResultService = electionResultService;
	}
	@PostMapping("/declare/{electionName}")
	public ResponseEntity<ElectionResultResponseDTO> declareElectionResult(@PathVariable String electionName){
		return ResponseEntity.ok(electionResultService.declareElectionResult(electionName));

	}
	@GetMapping
	public ResponseEntity<List<ElectionResultResponseDTO>> getAllResults(){
		return ResponseEntity.ok(electionResultService.getAllResults());
		}
	
}
