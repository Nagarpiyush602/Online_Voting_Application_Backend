package in.scalive.votezy.service;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.CurrentUserDTO;
import in.scalive.votezy.entity.Role;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.UnauthorizedActionException;
import in.scalive.votezy.repository.VoterRepository;

@Service
public class AuthorizationService {

    private final VoterRepository voterRepository;

    public AuthorizationService(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    public Voter getCurrentUserEntity(CurrentUserDTO currentUser) {
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new UnauthorizedActionException("User is not authenticated");
        }

        return voterRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + currentUser.getUserId()));
    }

    public Voter requireAdmin(CurrentUserDTO currentUser) {
        Voter user = getCurrentUserEntity(currentUser);

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only ADMIN can perform this action");
        }

        return user;
    }

    public Voter requireVoter(CurrentUserDTO currentUser) {
        Voter user = getCurrentUserEntity(currentUser);

        if (user.getRole() != Role.VOTER) {
            throw new UnauthorizedActionException("Only VOTER can perform this action");
        }

        return user;
    }

    public Voter requireAdminOrVoter(CurrentUserDTO currentUser) {
        Voter user = getCurrentUserEntity(currentUser);

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.VOTER) {
            throw new UnauthorizedActionException("Only ADMIN or VOTER can perform this action");
        }

        return user;
    }
}