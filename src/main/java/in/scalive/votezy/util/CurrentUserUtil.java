package in.scalive.votezy.util;

import in.scalive.votezy.entity.Role;

import org.springframework.stereotype.Component;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.exception.InvalidRequestException;

@Component
public class CurrentUserUtil {

	public CurrentUserDTO getCurrentUser(String userIdHeader,String roleHeader) {
		if(userIdHeader == null || userIdHeader.isBlank()) {
			throw new InvalidRequestException("X-USER-ID header is required");
		}
		if(roleHeader == null || roleHeader.isBlank()) {
			throw new InvalidRequestException("X-USER-ROLE header is required");
		}
		Long userId;
		try {
			userId = Long.parseLong(userIdHeader);
		}catch(NumberFormatException e) {
			throw new InvalidRequestException("Invalid X-USER-ID header");
		}
		Role role;
		try {
			role = Role.valueOf(roleHeader.toUpperCase());
		}catch(Exception e) {
			throw new InvalidRequestException("Invalid X-USER-ROLE header");
		}
		return new CurrentUserDTO(userId,role);
	}
	
}
