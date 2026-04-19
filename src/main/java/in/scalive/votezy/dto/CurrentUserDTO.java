package in.scalive.votezy.dto;

import in.scalive.votezy.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDTO {

	private Long userId;
	private Role role;
	private String fireBaseUid;
	private String email;
}
