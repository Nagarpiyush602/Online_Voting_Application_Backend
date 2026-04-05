package in.scalive.votezy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class ElectionResult {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@OneToOne
	@JoinColumn(name="election_id",unique=true)
	@JsonIgnore
	private Election election;
	
	private int totalVotes;
	
	@OneToOne
	@JoinColumn(name="winner_id")
	@JsonIgnore
	private Candidate winner;
	
	@JsonProperty("winnerId")
	public Long getWinnerId() {
		return winner!=null?winner.getId():null;
	}
	
	@JsonProperty("electionId")
	public Long getElectionId() {
		return election!=null?election.getId():null;
	}
	
	public String getElectionName() {
		return election!=null?election.getName():null;
	}
	
	
}
