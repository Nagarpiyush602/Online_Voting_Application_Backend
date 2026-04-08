package in.scalive.votezy.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ElectionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "election_id", unique = true, nullable = false)
    @JsonIgnore
    private Election election;

    private int totalVotes;

    @OneToOne
    @JoinColumn(name = "winner_id")
    @JsonIgnore
    private Candidate winner;

    private Integer winnerVoteCount;

    @Enumerated(EnumType.STRING)
    private ResultStatus resultStatus;

    private LocalDateTime declaredAt;

    @JsonProperty("winnerId")
    public Long getWinnerId() {
        return winner != null ? winner.getId() : null;
    }

    @JsonProperty("electionId")
    public Long getElectionId() {
        return election != null ? election.getId() : null;
    }

    @JsonProperty("electionName")
    public String getElectionName() {
        return election != null ? election.getName() : null;
    }
}