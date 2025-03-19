package si.janez.services.match;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import si.janez.dtos.match.MatchResultDto;
import si.janez.entities.match.MatchResult;
import si.janez.repositories.MatchRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class MatchService {

    @Inject
    MatchRepository matchRepository;

    Map<Long, MatchLedger> timestampDebt = new HashMap();
    private BlockingQueue<MatchResult> matchQueue = new LinkedBlockingQueue<>();
    private final int threadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    private static long id = 0; // Not good

    @PostConstruct
    public void init() {
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(this::processQueueItems);
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
    }

    public void ProcessMatch(MatchResultDto matchResultDto) {
        try {
            if (!timestampDebt.containsKey(matchResultDto.matchId)) {
                timestampDebt.put(matchResultDto.matchId, new MatchLedger());
            }
            id++;
            var matchResult = new MatchResult(id, matchResultDto);
            timestampDebt.get(matchResultDto.matchId).addDebt(new Debt(id));
            matchQueue.put(matchResult);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Failed to queue match for processing", e);
        }
    }


    private void processQueueItems() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MatchResult match = matchQueue.take(); //blocking if empty
                ProcessMatch(match);
                timestampDebt.get(match.matchId).settleDebt(match, matchRepository);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.info("Match processor thread interrupted");
                break;
            } catch (Exception e) {
                Log.error("Error processing match", e); //TODO better error handling
            }
        }
    }


    private void ProcessMatch(MatchResult matchResult) {
        try {
            if (matchResult.outcomeId % 2 == 0) {
                Log.info("Match of type A, processing 1s");
                Thread.sleep(1000);
            } else {
                Log.info("Match of type B, processing 1ms");
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        matchResult.result = "DONE";
    }
}
