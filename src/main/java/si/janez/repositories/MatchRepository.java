package si.janez.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.janez.entities.match.MatchResult;

@ApplicationScoped
public class MatchRepository implements PanacheRepository<MatchResult> {
}
