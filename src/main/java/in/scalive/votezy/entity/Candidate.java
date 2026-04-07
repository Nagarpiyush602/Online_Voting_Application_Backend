package in.scalive.votezy.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Candidate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@NotBlank(message="Name is required")
	private String name;
	
	@NotBlank(message="Party is required")
	private String party;
	
	private int voteCount=0;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="election_id",nullable=false)
	@JsonIgnore
	private Election election;
	
	@OneToMany(mappedBy="candidate",cascade=CascadeType.ALL)
	@JsonIgnore
	private List <Vote>votes;
}
