package in.scalive.votezy.util;

import org.springframework.stereotype.Component;

import in.scalive.votezy.config.FirebaseAuthInterceptor;
import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.exception.UnauthorizedActionException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CurrentUserUtil {

    public CurrentUserDTO getCurrentUser(HttpServletRequest request) {
        Object currentUser = request.getAttribute(FirebaseAuthInterceptor.CURRENT_USER);

        if (currentUser == null) {
            throw new UnauthorizedActionException("Current user not found in request");
        }

        return (CurrentUserDTO) currentUser;
    }
}