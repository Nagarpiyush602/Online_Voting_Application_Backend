package in.scalive.votezy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Voter;

public interface VoterRepository extends JpaRepository<Voter,Long>{
	
	boolean existsByEmailAndIdNot(String email,Long id); 
	boolean existsByEmail(String email);
}
