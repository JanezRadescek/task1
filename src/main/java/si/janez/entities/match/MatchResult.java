package si.janez.entities.match;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import si.janez.dtos.match.MatchResultDto;

import java.time.Instant;

@Entity
public class MatchResult extends MatchResultDto { //Not realy good that its extending but it is what it is

    public MatchResult() {}
    public MatchResult(Long id, MatchResultDto matchResultDto) {
        this.id = id;
        this.matchId = matchResultDto.matchId;
        this.marketId = matchResultDto.marketId;
        this.outcomeId = matchResultDto.outcomeId;
        this.specifiers = matchResultDto.specifiers;
    }

    @Id
    public Long id;

    public String result;
    public Instant dateInserted;
}
