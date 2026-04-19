package in.scalive.votezy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVoterAndElection(Voter voter, Election election);

    List<Vote> findByElection_Id(Long electionId);

    long countByElection_Id(Long electionId);
}