package com.groovequest;

import com.groovequest.user.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AuthResourceIntegrationTest {

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUserAndNeverReturnTheHash() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "email": "bruna@example.com",
                          "password": "supersecret123"
                        }
                        """)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("email", equalTo("bruna@example.com"))
                .body("role", equalTo("user"))
                .body("$", not(hasKey("passwordHash")));
    }

    @Test
    void shouldRejectDuplicateEmailWith409() {
        String payload = """
                {
                  "email": "dupe@example.com",
                  "password": "supersecret123"
                }
                """;

        given().contentType("application/json").body(payload)
                .when().post("/api/auth/register")
                .then().statusCode(201);

        given().contentType("application/json").body(payload)
                .when().post("/api/auth/register")
                .then().statusCode(409);
    }

    @Test
    void shouldRejectInvalidEmailWith400() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "email": "not-an-email",
                          "password": "supersecret123"
                        }
                        """)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(400);
    }
}
