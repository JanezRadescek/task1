package si.janez.entities.match;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import si.janez.dtos.match.MatchResultDto;

import java.time.Instant;

@Entity
@Table(indexes = {
        @Index(name = "idx_match_result_match_id", columnList = "matchId"),
        @Index(name = "idx_match_result_date_inserted", columnList = "dateInserted"),
        @Index(name = "idx_match_result_match_id_date_inserted", columnList = "matchId, dateInserted")
})
public class MatchResult {


    public MatchResult() {
    }

    public MatchResult(Long id, MatchResultDto matchResultDto) {
        this.id = id;
        this.matchId = matchResultDto.getMatchId();
        this.marketId = matchResultDto.marketId;
        this.outcomeId = matchResultDto.outcomeId;
        this.specifiers = matchResultDto.specifiers;
    }

    public Long matchId;
    public Long marketId;
    public String outcomeId;
    public String specifiers;


    @Id
    public Long id;

    public String result;
    public Instant dateInserted;
}
