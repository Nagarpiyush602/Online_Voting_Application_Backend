package in.scalive.votezy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CandidateRequestDTO {
	
    @NotBlank(message="Name is required")
	private String name;
    
    @NotBlank(message="Party is required")
	private String party;
    
    @NotNull(message="Election ID is required")
    private Long electionId;
}
