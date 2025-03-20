package si.janez.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.janez.entities.match.MatchResult;
import si.janez.exceptions.ApplicationException;

import java.time.Instant;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<MatchResult> {
    public long getMaxMatchId() {
        return find("SELECT COALESCE(MAX(id), 0) FROM MatchResult")
                .project(Long.class)
                .firstResult().longValue();
    }

    public Instant getFirstEvent(long matchId) {
        MatchResult result = find("matchId = ?1 AND dateInserted IS NOT NULL ORDER BY dateInserted ASC", matchId)
                .firstResult();
        if (result == null) {
            throw new ApplicationException("No events with dateInserted found for match ID: " + matchId, 404);
        }
        return result.dateInserted;
    }

    public Instant getLastEvent(long matchId) {
        MatchResult result = find("matchId = ?1 AND dateInserted IS NOT NULL ORDER BY dateInserted DESC", matchId)
                .firstResult();
        if (result == null) {
            throw new ApplicationException("No events with dateInserted found for match ID: " + matchId, 404);
        }
        return result.dateInserted;
    }
}
