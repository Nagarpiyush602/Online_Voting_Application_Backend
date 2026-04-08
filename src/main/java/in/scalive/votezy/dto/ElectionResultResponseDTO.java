package in.scalive.votezy.dto;

import java.time.LocalDateTime;
import java.util.List;

import in.scalive.votezy.entity.ResultStatus;
import lombok.Data;

@Data
public class ElectionResultResponseDTO {
	private Long electionId;
	private String electionName;
	private int totalVotes;
	private ResultStatus resultStatus;
	private String winnerName;
	private int winnerVotes;
	private List<String> tiedCandidates;
	private LocalDateTime declaredAt;
	private String message;
	
}
