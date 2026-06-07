package com.groovequest.dashboard;

import com.groovequest.session.TrainingSessionRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
public class DashboardResourceIntegrationTest {

    @Inject
    TrainingSessionRepository trainingSessionRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase(){
        trainingSessionRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyDashboardWhenThereAreNoSessions() {
        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("totalXp", equalTo(0))
                .body("dancerLevel", equalTo(1))
                .body("totalTrainingMinutes", equalTo(0))
                .body("sessionsCount", equalTo(0))
                .body("topSkill", nullValue())
                .body("skillProgression.size()", equalTo(0));
    }

    @Test
    void shouldReturnDashboardSummaryFromPersistedSessions() {
        createSession("2026-06-06", 75, "HIGH", "PERFORMANCE", "Full-out choreography runs.");
        createSession("2026-06-07", 60, "MEDIUM", "PERFORMANCE", "Stage presence and stamina.");
        createSession("2026-06-08", 45, "LOW", "FOUNDATION", "Basic groove drills.");

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("totalXp", equalTo(285))
                .body("dancerLevel", equalTo(3))
                .body("totalTrainingMinutes", equalTo(180))
                .body("sessionsCount", equalTo(3))
                .body("topSkill", equalTo("PERFORMANCE"))
                .body("skillProgression.size()", equalTo(2))
                .body("skillProgression[0].skill", equalTo("PERFORMANCE"))
                .body("skillProgression[0].totalXp", equalTo(240))
                .body("skillProgression[0].level", equalTo(3))
                .body("skillProgression[0].xpToNextLevel", equalTo(107))
                .body("skillProgression[1].skill", equalTo("FOUNDATION"))
                .body("skillProgression[1].totalXp", equalTo(45))
                .body("skillProgression[1].level", equalTo(1))
                .body("skillProgression[1].xpToNextLevel", equalTo(55));
    }

    private void createSession(
            String date,
            int durationMinutes,
            String intensity,
            String skill,
            String notes
    ) {
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
