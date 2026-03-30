package in.scalive.votezy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VoterRequestDTO {
	@NotBlank(message="Name is required") 
	private String name;
	
	@Email(message="Invalid Email format")
	@NotBlank(message="Email isRequired")
	private String email; 
}
