package in.scalive.votezy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Election;
import in.scalive.votezy.entity.ElectionResult;
import in.scalive.votezy.entity.ElectionStatus;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.exception.VoteNotAllowedException;
import in.scalive.votezy.repository.ElectionRepository;
import in.scalive.votezy.repository.ElectionResultRepository;
import in.scalive.votezy.repository.VoteRepository;

@Service
public class ElectionResultService {

    private final ElectionRepository electionRepository;
    private final ElectionResultRepository electionResultRepository;
    private final VoteRepository voteRepository;

    public ElectionResultService(ElectionRepository electionRepository,
                                 ElectionResultRepository electionResultRepository,
                                 VoteRepository voteRepository) {
        this.electionRepository = electionRepository;
        this.electionResultRepository = electionResultRepository;
        this.voteRepository = voteRepository;
    }

    public ElectionResult declareElectionResult(Long electionId) {

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        if (electionResultRepository.findByElection_Id(electionId).isPresent()) {
            return electionResultRepository.findByElection_Id(electionId).get();
        }

        if (election.getStatus() != ElectionStatus.COMPLETED) {
            throw new VoteNotAllowedException("Result can be declared only after election is completed");
        }

        List<Vote> votes = voteRepository.findByElection_Id(electionId);

        if (votes.isEmpty()) {
            throw new IllegalStateException("Cannot declare result because no votes were cast in this election");
        }

        Candidate winner = null;
        int maxVotes = 0;

        for (Vote vote : votes) {
            Candidate candidate = vote.getCandidate();
            if (candidate.getVoteCount() > maxVotes) {
                maxVotes = candidate.getVoteCount();
                winner = candidate;
            }
        }

        ElectionResult result = new ElectionResult();
        result.setElection(election);
        result.setWinner(winner);
        result.setTotalVotes(votes.size());

        return electionResultRepository.save(result);
    }

    public List<ElectionResult> getAllResults() {
        return electionResultRepository.findAll();
    }
}