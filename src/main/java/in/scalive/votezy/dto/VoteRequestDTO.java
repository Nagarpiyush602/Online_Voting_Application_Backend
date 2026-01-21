package in.scalive.votezy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequestDTO {
	@NotNull(message="Viter ID is Required")
	Long voterId;
	@NotNull(message="Candidate ID is Required")
	Long candidateId;
}
