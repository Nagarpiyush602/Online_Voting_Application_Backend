package in.scalive.votezy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Vote {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="voter_id",nullable =false)
	private Voter voter;
	
	@ManyToOne
	@JoinColumn(name="candidate_id",nullable=false)
	private Candidate candidate;
	
	@ManyToOne
	@JoinColumn(name="election_id",nullable=false)
	private Election election;
	
	@JsonProperty("voterId")
	public Long getVoterId() {
		return voter!=null?voter.getId():null;
	}
	
	@JsonProperty("candidateId")
	public Long getCandidateId() {
		return candidate!=null?candidate.getId():null;
	}
	
	@JsonProperty("electionId")
	public Long getElectionId() {
		return election!=null ? election.getId():null;
	}
}
