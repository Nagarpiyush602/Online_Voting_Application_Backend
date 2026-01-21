package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import in.scalive.votezy.entity.Candidate;
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
	public ResponseEntity<Candidate> addCandidate(@RequestBody @Valid Candidate candidate){
		Candidate saveCandidate=candidateService.addCandidate(candidate);
		return new ResponseEntity<Candidate>(saveCandidate,HttpStatus.CREATED);
	}
	@GetMapping()
	public ResponseEntity<List<Candidate>> getAllCandidates(){
		List<Candidate> candidateList=this.candidateService.getAllCandidates();
		return new ResponseEntity<List<Candidate>>(candidateList,HttpStatus.OK);
	}
	@GetMapping("/{id}")
	public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id){
		Candidate candidate=this.candidateService.getCandidateById(id);
		return new ResponseEntity<Candidate>(candidate,HttpStatus.OK);
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id,@RequestBody Candidate candidate){
		Candidate updateCandidate = candidateService.updateCandidate(id,candidate);
		return new ResponseEntity<>(updateCandidate,HttpStatus.OK);
		}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteCandidate(@PathVariable Long id){
		candidateService.deleteCandidate(id);
		return new ResponseEntity<>("Candidate with id:"+id+" deleted successfully",HttpStatus.OK);
	}
	
}
