package in.scalive.votezy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.scalive.votezy.entity.Voter;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Voter> findByEmail(String email);

    Optional<Voter> findByFirebaseUid(String firebaseUid);
}