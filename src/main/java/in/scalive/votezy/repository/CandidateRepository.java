package in.scalive.votezy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findAllByOrderByVoteCountDesc();

    List<Candidate> findByElectionId(Long electionId);
    
    boolean existsByPartyIgnoreCaseAndElection(String party,Election election);
    
    boolean existsByPartyIgnoreCaseAndElectionAndIdNot(String party,Election election,Long id);
}