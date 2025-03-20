package si.janez.resources;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import si.janez.api.model.Error;
import si.janez.api.model.MatchResponse;
import si.janez.dtos.match.MatchResultDto;
import si.janez.helper.JsonHelper;
import si.janez.services.match.MatchService;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MatchResourceTest {

    @Inject
    MatchService matchService;

    @Test
    void good() {

    }

    @Test
    void bad() throws IOException, InterruptedException {
        var match = JsonHelper.loadJsonFromResource("/cases/match/bad.json", MatchResultDto.class);
        matchService.ProcessMatch(match);

        Thread.sleep(100); // Type B so 1ms processing time

        var response = given()
                .queryParam("matchId", match.getMatchIdString())
                .when().get("/api/match")
                .then()
                .statusCode(200)
                .extract().as(MatchResponse.class);

        Assertions.assertNotNull(response.getFirstEventTime());
        Assertions.assertEquals(response.getFirstEventTime(), response.getLastEventTime());


    }

    @Test
    void ugly() {
        var error = given()
                .queryParam("matchId", 0)
                .when().get("/api/match")
                .then()
                .statusCode(404)
                .extract().as(Error.class);
        Assertions.assertEquals("No events with dateInserted found for match ID: 0", error.getMessage());
    }


}
