package in.scalive.votezy.dto;

import lombok.Data;

@Data
public class ElectionResultResponseDTO {
	private String electionName;
	private int totalVotes;
	private Long winnerId;
	private int winnerVotes;
	
}
