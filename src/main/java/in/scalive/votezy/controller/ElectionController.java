package in.scalive.votezy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.votezy.dto.ElectionResponseDTO;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.service.ElectionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = "https://nagarpiyush602.github.io")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }
    
    @GetMapping("/active")
    public ResponseEntity<ElectionResponseDTO> getActiveElection(){
    	return ResponseEntity.ok(electionService.getActiveElection());
    }

    @PostMapping("/add")
    public ResponseEntity<Election> createElection(@RequestBody @Valid Election election) {
        Election savedElection = electionService.createElection(election);
        return new ResponseEntity<>(savedElection, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Election>> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElectionById(id));
    }
}