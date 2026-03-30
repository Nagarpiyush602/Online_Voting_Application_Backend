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

import in.scalive.votezy.dto.VoterRequestDTO;
import in.scalive.votezy.dto.VoterResponseDTO;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.service.VoterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/voters")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class VoterController {
	private VoterService voterService;

	public VoterController(VoterService voterService) {
		this.voterService = voterService;
	}
	@PostMapping("/register")
	public ResponseEntity<VoterResponseDTO> registerVoter(@RequestBody @Valid VoterRequestDTO dto) {
		return new ResponseEntity<>(voterService.registerVoter(dto),HttpStatus.CREATED);
	}
	@GetMapping("/{id}")
	public ResponseEntity<VoterResponseDTO> getVoterById(@PathVariable Long id){
		return new ResponseEntity<>(voterService.getVoterById(id),HttpStatus.OK);
	}
	@GetMapping()
	public ResponseEntity<List<VoterResponseDTO>> getAllVoters(){
		return new ResponseEntity<>(voterService.getAllVoters(),HttpStatus.OK);
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<VoterResponseDTO> UpadeteVoters(@PathVariable Long id,@RequestBody VoterRequestDTO dto){
		return new ResponseEntity<>(voterService.updateVoter(id,dto),HttpStatus.OK);
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteVoter(@PathVariable Long id){
		voterService.deleteVoter(id);
		return new ResponseEntity<>("Voter with id:"+id+" deleted",HttpStatus.OK);
	}
}
