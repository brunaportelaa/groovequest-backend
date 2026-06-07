package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
                .body("skillProgression.size()", equalTo(0))
                .body("recentTrainingDistribution.size()", equalTo(0))
                .body("neglectedSkills.size()", equalTo(DanceSkill.values().length));

    }

    @Test
    void shouldReturnDashboardSummaryFromPersistedSessions() {
        LocalDate today = LocalDate.now();

        createSession(today.minusDays(2).toString(), 75, "HIGH", "PERFORMANCE", "Full-out choreography runs.");
        createSession(today.minusDays(1).toString(), 60, "MEDIUM", "PERFORMANCE", "Stage presence and stamina.");
        createSession(today.toString(), 45, "LOW", "FOUNDATION", "Basic groove drills.");

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
                .body("recentTrainingDistribution.size()", equalTo(2))
                .body("recentTrainingDistribution[0].skill", equalTo("PERFORMANCE"))
                .body("recentTrainingDistribution[0].sessionsCount", equalTo(2))
                .body("recentTrainingDistribution[0].totalMinutes", equalTo(135))
                .body("recentTrainingDistribution[1].skill", equalTo("FOUNDATION"))
                .body("recentTrainingDistribution[1].sessionsCount", equalTo(1))
                .body("recentTrainingDistribution[1].totalMinutes", equalTo(45))
                .body("neglectedSkills", not(hasItem("PERFORMANCE")))
                .body("neglectedSkills", not(hasItem("FOUNDATION")))
                .body("neglectedSkills", hasItem("FLEXIBILITY"));
    }

   @Test
   void shouldTreatSkillAsNeglectedWhenNotTrainedRecently() {
        LocalDate today = LocalDate.now();

       createSession(
               today.minusDays(45).toString(),
               90,
               "HIGH",
               "FLEXIBILITY",
               "Old flexibility session."
       );

       given()
               .when()
               .get("/api/dashboard")
               .then()
               .statusCode(200)

               //Making sure session exists in database.
               .body("totalXp", equalTo(180))
               .body("totalTrainingMinutes", equalTo(90))
               .body("sessionsCount", equalTo(1))
               .body("topSkill", equalTo("FLEXIBILITY"))

               //But recent distribution ignores it
               .body("recentTrainingDistribution.size()", equalTo(0))

                //And is displayed in neglected skills.
               .body("neglectedSkills", hasItem("FLEXIBILITY"));
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

    @Test
    void shouldExcludeOldSessionsFromRecentTrainingDistribution() {

        LocalDate today = LocalDate.now();

        createSession(today.minusDays(2).toString(), 60, "MEDIUM", "PERFORMANCE", "Recent session.");
        createSession(today.minusDays(45).toString(), 90, "HIGH", "FLEXIBILITY", "Old session.");

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("totalXp", equalTo(270))
                .body("totalTrainingMinutes", equalTo(150))
                .body("sessionsCount", equalTo(2))
                .body("recentTrainingDistribution.size()", equalTo(1))
                .body("recentTrainingDistribution[0].skill", equalTo("PERFORMANCE"))
                .body("recentTrainingDistribution[0].sessionsCount", equalTo(1))
                .body("recentTrainingDistribution[0].totalMinutes", equalTo(60));

    }
}
