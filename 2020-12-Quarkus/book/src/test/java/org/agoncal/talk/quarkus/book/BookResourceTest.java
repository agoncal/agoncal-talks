package org.agoncal.talk.quarkus.book;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;

@QuarkusTest
public class BookResourceTest {

    @Test
    public void shouldCreateAQuarkusBook() {
        given()
            .body("title of the book").
        when()
            .post("/api/books").
        then()
            .statusCode(200)
            .body("title", is("title of the book"))
            .body("topic", is("Quarkus"))
            .body("$", hasKey("createdAt"))
            .body("$", hasKey("isbn"));
    }
}
