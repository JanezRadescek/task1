package si.janez.services.match;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.janez.api.model.MatchResponse;
import si.janez.dtos.match.MatchResultDto;
import si.janez.entities.match.MatchResult;
import si.janez.exceptions.ApplicationException;
import si.janez.repositories.MatchRepository;

import java.time.Instant;
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
    private final BlockingQueue<MatchResult> matchQueue = new LinkedBlockingQueue<>();
    private final int threadCount = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    private static long currentMaxMatchId = 0; // Not good

    @PostConstruct
    public void init() {
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(this::processQueueItems);
        }

        currentMaxMatchId = matchRepository.getMaxMatchId();
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
    }

    public void ProcessMatch(MatchResultDto matchResultDto) {
        try {
            if (!timestampDebt.containsKey(matchResultDto.getMatchId())) {
                timestampDebt.put(matchResultDto.getMatchId(), new MatchLedger());
            }
            currentMaxMatchId++;
            var matchResult = new MatchResult(currentMaxMatchId, matchResultDto);
            timestampDebt.get(matchResultDto.getMatchId()).addDebt(new Debt(currentMaxMatchId));
            matchQueue.put(matchResult);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Failed to queue match for processing", e);
        }
    }

    @Transactional
    public MatchResponse getMatchDetails(String matchId) {
        var matchIdlong = MatchResultDto.parseMatchId(matchId);
        Instant firstEventTime = matchRepository.getFirstEvent(matchIdlong);
        Instant lastEventTime = matchRepository.getLastEvent(matchIdlong);
        return new MatchResponse()
                .firstEventTime(firstEventTime)
                .lastEventTime(lastEventTime);
    }

    @ActivateRequestContext
    protected void processQueueItems() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MatchResult match = matchQueue.take(); //blocking if empty
                Log.debug("took match from queue: " + match.matchId);
                ProcessMatch(match);
                Log.debug("processed match : " + match.matchId);
                timestampDebt.get(match.matchId).settleDebt(match, matchRepository);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.error("Match processor thread interrupted");
                break;
            } catch (Exception e) {
                Log.error("Error processing match", e); //TODO better error handling
            }
        }
    }

    private int aCounter = 0;
    private void ProcessMatch(MatchResult matchResult) {
        try {
            if (matchResult.outcomeId.length() % 2 == 0) {
                Log.info("Match of type A, processing 1s");
                aCounter++;
                Log.info("Total A matches processed: " + aCounter);
                Thread.sleep(1000);
            } else {
                Log.info("Match of type B, processing 1ms");
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            throw new ApplicationException("¯\\_(ツ)_/¯", 500, e);
        }
        matchResult.result = "DONE";
    }
}
