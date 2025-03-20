package si.janez.services.match;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import si.janez.entities.match.MatchResult;
import si.janez.repositories.MatchRepository;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class MatchLedger {

    private List<Debt> debts = new LinkedList<>();

    public synchronized void addDebt(Debt debt) {
        debts.add(debt);
    }

    public synchronized void settleDebt(MatchResult matchResult, MatchRepository matchRepository) {
        for (Debt debt : debts) {
            if (debt.id == matchResult.id) {
                debt.processed = true;
                break;
            }
        }
        Log.debug("Marked debt as settled for matchId: " + matchResult.id);
        QuarkusTransaction.begin();
        try {
            Log.debug("Starting DB : " + matchResult.id);
            settleDebtInRepo(matchResult, matchRepository);
            Log.debug("Ending DB : " + matchResult.id);
            QuarkusTransaction.commit();
        } finally {
            if (QuarkusTransaction.isActive()) {
                QuarkusTransaction.rollback();
            }
        }

    }


    //    @Transactional //doesnt work on private functions :(
    private void settleDebtInRepo(MatchResult matchResult, MatchRepository matchRepository) {
        matchRepository.persist(matchResult);
        // TODO test if persistance works

        List<Long> readyIds = new LinkedList<>();
        while (!debts.isEmpty() && debts.getFirst().processed) {
            var first = debts.removeFirst();
            readyIds.add(first.id);
        }

        Log.debug("Found ready Ids : " + readyIds + " unready ids size : " + debts.size());
        if(readyIds.isEmpty()){
            return;
        }
        Instant baseTime = Instant.now();
        List<MatchResult> results = matchRepository.list("id in ?1", readyIds);
        for (int i = 0; i < results.size(); i++) {
            results.get(i).dateInserted = baseTime.plusNanos(i);
        }
    }

}
