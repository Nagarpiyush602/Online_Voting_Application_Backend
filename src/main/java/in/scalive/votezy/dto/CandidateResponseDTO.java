package in.scalive.votezy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponseDTO {

	private Long id;
	
	private String name;
	
	private String party;
	
	private int voteCount;
	
	private Long electionId;
}
