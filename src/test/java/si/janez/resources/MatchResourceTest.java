package si.janez.resources;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import si.janez.api.model.Error;
import si.janez.api.model.MatchResponse;
import si.janez.dtos.match.MatchResultDto;
import si.janez.helper.CSVHelper;
import si.janez.helper.JsonHelper;
import si.janez.services.match.MatchService;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MatchResourceTest {

    @Inject
    MatchService matchService;

    @Test
    void good() throws IOException, InterruptedException {
        var matches = CSVHelper.loadCsvFromResource("/cases/match/fo_random.txt");
        Log.info("Total matches: " + matches.size());
        long startTime = System.currentTimeMillis();

        var firstMatchId = matches.get(0).getMatchId(); //It repeats few times during file
        for (var match : matches) {
            matchService.ProcessMatch(match);

            if (match.getMatchId() == firstMatchId) {
                new Thread(() -> {
                    var response = given()
                            .queryParam("matchId", firstMatchId)
                            .when()
                            .get("/api/match")
                            .then()
                            .statusCode(200)
                            .extract().as(MatchResponse.class);

                    Assertions.assertNotNull(response.getFirstEventTime());
                    Assertions.assertEquals(response.getFirstEventTime(), response.getLastEventTime());
                });
            }
        }

        long endTime = System.currentTimeMillis();
        long uploadingTime = endTime - startTime;
        uploadingTime /= 1000;
        Log.info("Uploading time: " + uploadingTime);

        int maxUploadTime = 20;
        maxUploadTime *= 10; // Just in case, no idea who will run this.
        //feels weird to have timing in test, but the instruction literally says "as fast as possible".
        Assertions.assertTrue(uploadingTime < maxUploadTime,
                "uploading time should be less than " + maxUploadTime + " seconds, was: " + uploadingTime + "s");

        startTime = System.currentTimeMillis();
        int maxProcessTime = matches.size() * 20 / 1000 / (Runtime.getRuntime().availableProcessors() / 2); // 700s ~ 20ms per match per thread. I guess not perfect parallelism so 16/2
        maxProcessTime *= 10; // Just in case, no idea who will run this.
        Log.info("Max process time: " + maxProcessTime);
        while (maxProcessTime > 0) {
            maxProcessTime--;

            var processing = given()
                    .when()
                    .get("/api/match/processing")
                    .then()
                    .statusCode(200)
                    .extract().as(Boolean.class);

            if (processing) {
                Thread.sleep(1000);
            } else {
                endTime = System.currentTimeMillis();
                long processingTime = endTime - startTime;
                processingTime /= 1000;
                Log.info("Processing time: " + processingTime);

                var validProcessing = given()
                        .when()
                        .get("/api/match/valid")
                        .then()
                        .statusCode(200)
                        .extract().as(Boolean.class);
                Assertions.assertTrue(validProcessing);
                return;
            }
        }

        endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;
        processingTime /= 1000;
        Log.info("Processing time: " + processingTime);  //~ 185
        Assertions.assertTrue(false, "processing time should be less than 100s");

    }

    @Test
    void bad() throws IOException, InterruptedException {
        var match = JsonHelper.loadJsonFromResource("/cases/match/bad.json", MatchResultDto.class);
        matchService.ProcessMatch(match);

        Thread.sleep(100); // Type B so 1ms processing time

        var response = given()
                .queryParam("matchId", match.getMatchIdString())
                .when()
                .get("/api/match")
                .then()
                .statusCode(200)
                .extract().as(MatchResponse.class);

        Assertions.assertNotNull(response.getFirstEventTime());
        Assertions.assertEquals(response.getFirstEventTime(), response.getLastEventTime());


    }

    @Test
    void ugly() {
        var error = given()
                .queryParam("matchId", "sr:match:0")
                .when()
                .get("/api/match")
                .then()
                .statusCode(404)
                .extract().as(Error.class);
        Assertions.assertEquals("No events with dateInserted found", error.getMessage());
    }


}
