package org.agoncal.talk.quarkus.number;
// @formatter:off

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
public class NumberResourceTest {

    @Test
    public void testHelloEndpoint() {
        given().
        when()
            .get("/api/numbers").
        then()
            .statusCode(200)
            .body(startsWith("13-"));
    }
}
