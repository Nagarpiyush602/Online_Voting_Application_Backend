package in.scalive.votezy.dto;

import lombok.Data;

@Data
public class VoterResponseDTO {

	private long id;
	private String name;
	private String email;
	private boolean hasVoted;
}
