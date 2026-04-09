package in.scalive.votezy.dto;

import java.time.LocalDateTime;

import in.scalive.votezy.entity.ElectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ElectionResponseDTO {
	
	    private Long id;
	    
	    private String name;
	    
	    private LocalDateTime startTime;
	    
	    private LocalDateTime endTime;
	    
	    private ElectionStatus status;
}
