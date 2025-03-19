package si.janez.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.helper.JsonHelper;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@QuarkusTest
class TaxResourceTest {

    @Test
    void testApiTax() throws IOException {
        int maxTestCase = 5;
        for (int testCase = 1; testCase <= maxTestCase; testCase++) {
            var request = JsonHelper.loadJsonFromResource("/cases/tax/request" + testCase + ".json", TaxRequest.class);

            var response = given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/tax")
                    .then()
                    .statusCode(200)
                    .extract().as(TaxResponse.class);

            var expectedResponse = JsonHelper.loadJsonFromResource("/cases/tax/response" + testCase + ".json", TaxResponse.class);

            Assertions.assertEquals(response, expectedResponse);
        }
    }
}