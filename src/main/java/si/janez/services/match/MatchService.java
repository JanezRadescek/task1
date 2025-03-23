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

        currentMaxMatchId = matchRepository.maxMatchId();
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
    }

    //    Not setting up actual streaming service since probably not the point of the task?
    // MAIN FUNCTION
    public void ProcessMatch(MatchResultDto matchResultDto) {
        // put this data into DB as fast as possible
        // "as fast as possible" comes from putting data in IO thread into queue and process it in separate multiple threads.
        // correctness/order comes from keeping track of order (timestampDebt) and only appling timestamp if all previous timestamps are settled. If they are not this timestamp will be kept in debt and settled at some latter time.
        // in a sense data is being processed for as long or short as needed, but we pretend that it was processed for so long time that it keep initial order.
        try {
            if (!timestampDebt.containsKey(matchResultDto.getMatchId())) {
                timestampDebt.put(matchResultDto.getMatchId(), new MatchLedger());
            }
            currentMaxMatchId++; // Preserves us order, latter we can check if the timestamps order matches ids order. Probably that could be used in implementation too. Not just the test. But now its done with the debtQueue so ¯\_(ツ)_/¯
            var matchResult = new MatchResult(currentMaxMatchId, matchResultDto);
            timestampDebt.get(matchResultDto.getMatchId()).addDebt(new Debt(matchResult.id));
            matchQueue.put(matchResult);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Failed to queue match for processing", e);
        }
    }

    @Transactional
    public MatchResponse getMatchDetails(String matchId) {
        var matchIdlong = MatchResultDto.parseMatchId(matchId);
        Instant firstEventTime = matchRepository.firstMatchEvent(matchIdlong);
        Instant lastEventTime = matchRepository.lastMatchEvent(matchIdlong);
        return new MatchResponse()
                .firstEventTime(firstEventTime)
                .lastEventTime(lastEventTime);
    }

    public boolean isProcessing() {
        return !matchQueue.isEmpty();
    }

    @Transactional
    public boolean isValid(){
        var invalidIds = matchRepository.invalidMatchesId();
        if(invalidIds.isEmpty()){
            return true;
        } else {
            Log.fatal("Invalid matches found: " + invalidIds);
            return false;
        }
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

    private int c = 0;
    private void ProcessMatch(MatchResult matchResult) {
        try {
            if (matchResult.outcomeId.length() % 2 == 0) {
                Log.info("Match of type A, processing 10ms");
                Thread.sleep(10);
            } else {
                Log.info("Match of type B, processing 1ms");
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            throw new ApplicationException("¯\\_(ツ)_/¯", 500, e);
        }
        c++;
        Log.info("Total matches processed: " + c);
        matchResult.result = "DONE";
    }


}
