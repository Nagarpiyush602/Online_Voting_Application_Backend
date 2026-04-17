package in.scalive.votezy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteCheckResponseDTO {
	
	private Long voterId;
	private Long electionId;
	private boolean hasVoted;
}
