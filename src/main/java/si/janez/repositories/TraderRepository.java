package si.janez.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.janez.entities.Trader;

@ApplicationScoped
public class TraderRepository implements PanacheRepository<Trader> {
}
