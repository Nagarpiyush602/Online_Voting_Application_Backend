package in.scalive.votezy.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ElectionRequestDTO {

	@NotBlank(message="Election name is required")
	private String name;
	
	@NotNull(message="Start time is required")
	private LocalDateTime startTime;
	
	@NotNull(message="End time is required")
	private LocalDateTime endTime;
}
