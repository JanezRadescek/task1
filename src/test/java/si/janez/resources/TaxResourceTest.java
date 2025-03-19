package si.janez.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;

import java.io.IOException;
import java.io.InputStream;

import static io.restassured.RestAssured.given;

@QuarkusTest
class TaxResourceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testHelloEndpoint() throws IOException {

        var request = loadJsonFromResource("/examples/tax/request1.json", TaxRequest.class);

        var response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/tax")
                .then()
                .statusCode(200)
                .extract().as(TaxResponse.class);

        var expectedResponse = loadJsonFromResource("/examples/tax/response1.json", TaxResponse.class);

        Assertions.assertEquals(response, expectedResponse);
    }


    private <T> T loadJsonFromResource(String resourcePath, Class<T> valueType) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, valueType);
        }
    }

}