package in.scalive.votezy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Election;

public interface ElectionRepository extends JpaRepository<Election,Long>{
	
	Optional<Election> findByName(String name);
	
}
