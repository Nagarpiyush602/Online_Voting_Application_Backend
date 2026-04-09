package in.scalive.votezy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionStatus;

public interface ElectionRepository extends JpaRepository<Election,Long>{
	
	Optional<Election> findByName(String name);
	
	List<Election> findByStatus(ElectionStatus status);
}
