package in.scalive.votezy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote,Long> {
	long countByElection_Id(Long electionId);
	List<Vote> findByElection_Id(Long electionId);
}
