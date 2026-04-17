package in.scalive.votezy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequestDTO {
	
	@NotNull(message="Candidate ID is Required")
	private Long candidateId;
	
	@NotNull(message="Election ID is Required")
	private Long electionId;
}
