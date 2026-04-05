package in.scalive.votezy.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Election {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Election name is required")
	private String name;
	
	@NotNull(message="Start time is required")
	private LocalDateTime startTime;
	
	@NotNull(message="End time is required")
	private LocalDateTime endTime;
	
	@Enumerated(EnumType.STRING)
	private ElectionStatus status;
	
	@OneToMany(mappedBy="election",cascade=CascadeType.ALL)
	@JsonIgnore
	private List<Vote> votes;
}
