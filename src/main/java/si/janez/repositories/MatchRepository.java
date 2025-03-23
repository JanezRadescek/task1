package si.janez.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.janez.entities.match.MatchResult;
import si.janez.exceptions.ApplicationException;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<MatchResult> {
    public long maxMatchId() {
        return find("SELECT COALESCE(MAX(id), 0) FROM MatchResult")
                .project(Long.class)
                .firstResult().longValue();
    }

    public Instant firstMatchEvent(long matchId) {
        return matchEvent(matchId, "ASC");
    }

    public Instant lastMatchEvent(long matchId) {
        return matchEvent(matchId, "DESC");
    }

    private Instant matchEvent(long matchId, String ordering) {
        return find("SELECT m.dateInserted FROM MatchResult m WHERE m.matchId = ?1 AND m.dateInserted IS NOT NULL ORDER BY m.dateInserted " + ordering, matchId)
                .project(Instant.class)
                .firstResultOptional()
                .orElseThrow(() -> new ApplicationException(
                        "No events with dateInserted found", 404));
    }

    public List<Long> invalidMatchesId() {
        return find("SELECT DISTINCT matchId FROM MatchResult WHERE dateInserted IS NULL " +
                "UNION " +
                "SELECT DISTINCT mr1.matchId " +
                "FROM MatchResult mr1, MatchResult mr2 " +
                "WHERE mr1.matchId = mr2.matchId " +
                "AND mr1.id < mr2.id " +
                "AND mr1.dateInserted > mr2.dateInserted " +
                "AND mr1.dateInserted IS NOT NULL " +
                "AND mr2.dateInserted IS NOT NULL")
                .project(Long.class)
                .list();
    }
}
