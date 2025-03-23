package si.janez.resources;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import si.janez.api.MatchApi;
import si.janez.api.model.MatchResponse;
import si.janez.services.match.MatchService;

@ApplicationScoped
public class MatchResource implements MatchApi {

    @Inject
    MatchService matchService;

    @Override
    public MatchResponse apiMatchGet(String matchId) {
        Log.info("ApiMatchGet : matchId = " + matchId);
        var matchResponse = matchService.getMatchDetails(matchId);
        Log.info("ApiMatchGet : matchResponse = " + matchResponse);
        return matchResponse;
    }

    @Override
    public Boolean apiMatchProcessingGet() {
        Log.info("ApiMatchProcessingGet");
        var processing = matchService.isProcessing();
        Log.info("ApiMatchProcessingGet : processing = " + processing);
        return processing;

    }

    @Override
    public Boolean apiMatchValidGet() {
        Log.info("ApiMatchValidGet");
        var valid = matchService.isValid();
        Log.info("ApiMatchValidGet : valid = " + valid);
        return valid;
    }


}
