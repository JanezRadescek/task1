package si.janez.resources;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import si.janez.api.MatchApi;
import si.janez.api.model.MatchResponse;
import si.janez.services.match.MatchService;

@ApplicationScoped
public class MatchResource implements MatchApi {

    @Inject
    MatchService matchService;

    public void startStream(@Observes StartupEvent ev){
        Log.info("Application is starting, triggering stream...");
        simulateStream();
    }


//    Setting up kafka, rabbitmq or some other actual streaming service is too much work for the purpose of this task
    private void simulateStream(){


    }


    @Override
    public MatchResponse apiMatchGet(String matchId) {
        Log.info("ApiMatchGet : matchId = " + matchId);
        var matchResponse = matchService.getMatchDetails(matchId);
        Log.info("ApiMatchGet : matchResponse = " + matchResponse);
        return matchResponse;
    }
}
