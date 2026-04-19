package in.scalive.votezy.config;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.UnauthorizedActionException;
import in.scalive.votezy.repository.VoterRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseAuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER = "currentUser";

    private final VoterRepository voterRepository;

    public FirebaseAuthInterceptor(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedActionException("Authorization header is missing or invalid");
        }

        String idToken = authHeader.substring(7).trim();

        if (idToken.isBlank()) {
            throw new UnauthorizedActionException("Firebase token is missing");
        }

        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new UnauthorizedActionException("Invalid or expired Firebase token");
        }

        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        if (email == null || email.isBlank()) {
            throw new UnauthorizedActionException("Email not found in Firebase token");
        }

        Voter voter = getOrLinkUser(firebaseUid, email);

        CurrentUserDTO currentUser = new CurrentUserDTO(
                voter.getId(),
                voter.getRole(),
                firebaseUid,
                email
        );

        request.setAttribute(CURRENT_USER, currentUser);
        return true;
    }

    private Voter getOrLinkUser(String firebaseUid, String email) {

        Optional<Voter> voterByUid = voterRepository.findByFirebaseUid(firebaseUid);
        if (voterByUid.isPresent()) {
            return voterByUid.get();
        }

        Voter voter = voterRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedActionException(
                        "No application user found for this Firebase account"));

        if (voter.getFirebaseUid() != null
                && !voter.getFirebaseUid().isBlank()
                && !voter.getFirebaseUid().equals(firebaseUid)) {
            throw new UnauthorizedActionException(
                    "This email is already linked with another Firebase account");
        }

        voter.setFirebaseUid(firebaseUid);
        return voterRepository.save(voter);
    }
}