package com.groovequest.session;

import com.groovequest.user.User;
import com.groovequest.user.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestSecurity(user = "tester@example.com", roles = "user")
public class TrainingSessionResourceIntegrationTest {

    @Inject
    TrainingSessionRepository trainingSessionRepository;

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        trainingSessionRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.persist(new User("tester@example.com", "test-hash", "user"));
    }

    @Test
    void shouldCreateTrainingSessionWithCalculatedXp() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "date": "2026-06-06",
                          "durationMinutes": 75,
                          "intensity": "HIGH",
                          "skill": "PERFORMANCE",
                          "notes": "Full-out choreography runs."
                        }
                        """)
                .when()
                .post("/api/sessions")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("date", equalTo("2026-06-06"))
                .body("durationMinutes", equalTo(75))
                .body("intensity", equalTo("HIGH"))
                .body("skill", equalTo("PERFORMANCE"))
                .body("notes", equalTo("Full-out choreography runs."))
                .body("xpGained", equalTo(150));
    }

    @Test
    void shouldListTrainingSessionOrderedByNewestFirst(){
        createSession("2026-06-01", 30, "LOW", "FOUNDATION", "Older session.");
        createSession("2026-06-10", 60, "MEDIUM", "FLEXIBILITY", "Newest session.");

        given()
                .when()
                .get("/api/sessions")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(2))
                .body("[0].date", equalTo("2026-06-10"))
                .body("[0].skill", equalTo("FLEXIBILITY"))
                .body("[0].xpGained", equalTo(90))
                .body("[1].date", equalTo("2026-06-01"))
                .body("[1].skill", equalTo("FOUNDATION"))
                .body("[1].xpGained", equalTo(30));
    }

    @Test
    void shouldReturnSkillProgressionWithProgressiveLeveling() {
        createSession("2026-06-06", 75, "HIGH", "PERFORMANCE", "Performance training.");
        createSession("2026-06-07", 60, "MEDIUM", "PERFORMANCE", "Stage presence.");
        createSession("2026-06-08", 45, "LOW", "FOUNDATION", "Basic groove drills.");

        given()
                .when()
                .get("/api/skills/progression")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(2))
                .body("[0].skill", equalTo("PERFORMANCE"))
                .body("[0].totalXp", equalTo(240))
                .body("[0].level", equalTo(3))
                .body("[0].xpToNextLevel", equalTo(107))
                .body("[1].skill", equalTo("FOUNDATION"))
                .body("[1].totalXp", equalTo(45))
                .body("[1].level", equalTo(1))
                .body("[1].xpToNextLevel", equalTo(55));

    }

    @Test
    void shouldReturnValidationErrorForInvalidTrainingSession() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "date": "2026-06-06",
                          "durationMinutes": 0,
                          "intensity": "HIGH",
                          "skill": "PERFORMANCE",
                          "notes": "Invalid duration."
                        }
                        """)
                .when()
                .post("/api/sessions")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed."))
                .body("status", equalTo(400))
                .body("timestamp", notNullValue())
                .body("errors", hasItem("Duration must be at least 1 minute."));
    }

    private void createSession(
            String date,
            int durationMinutes,
            String intensity,
            String skill,
            String notes
    ){
        given()
                .contentType("application/json")
                .body("""
                        {
                          "date": "%s",
                          "durationMinutes": %d,
                          "intensity": "%s",
                          "skill": "%s",
                          "notes": "%s"
                        }
                        """.formatted(date, durationMinutes, intensity, skill, notes))
                .when()
                .post("/api/sessions")
                .then()
                .statusCode(201);
    }
}
