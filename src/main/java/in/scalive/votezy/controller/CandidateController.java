package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.votezy.dto.CandidateRequestDTO;
import in.scalive.votezy.dto.CandidateResponseDTO;
import in.scalive.votezy.service.CandidateService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidates")

@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class CandidateController {
	private CandidateService candidateService;

	public CandidateController(CandidateService candidateService) {
		this.candidateService = candidateService;
	}
	@PostMapping("/add")
	public ResponseEntity<CandidateResponseDTO> addCandidate(@RequestBody @Valid CandidateRequestDTO request){
		return new ResponseEntity<>(candidateService.addCandidate(request),HttpStatus.CREATED);
	}
	@GetMapping()
	public ResponseEntity<List<CandidateResponseDTO>> getAllCandidates(){
		return ResponseEntity.ok(candidateService.getAllCandidates());
	}
	@GetMapping("/{id}")
	public ResponseEntity<CandidateResponseDTO> getCandidateById(@PathVariable Long id){
		return ResponseEntity.ok(candidateService.getCandidateById(id));
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<CandidateResponseDTO> updateCandidate(@PathVariable Long id,@RequestBody CandidateRequestDTO request){
		return ResponseEntity.ok(candidateService.updateCandidate(id,request));
		}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteCandidate(@PathVariable Long id){
		candidateService.deleteCandidate(id);
		return ResponseEntity.ok("Candidate with id:"+id+" deleted successfully");
	}
	
}
