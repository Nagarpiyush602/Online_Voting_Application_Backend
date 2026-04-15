package in.scalive.votezy.dto;

import in.scalive.votezy.entity.Role;
import lombok.Data;

@Data
public class VoterResponseDTO {

	private long id;
	
	private String name;
	
	private String email;
	
	private Role role;
}
