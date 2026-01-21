package in.scalive.votezy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voter {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@NotBlank(message="Name is required")
	private String name;
	
	@NotBlank(message="Email is required")
	@Email(message="Invalid email format")
	private String email;
	
	private boolean hasVoted=false;
	
	@OneToOne(mappedBy="voter",cascade=CascadeType.ALL)
	@JsonIgnore
	private Vote vote;
}
