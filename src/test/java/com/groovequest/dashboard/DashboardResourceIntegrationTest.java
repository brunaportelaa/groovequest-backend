package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
@TestSecurity(user = "tester@example.com", roles = "user")
class DashboardResourceIntegrationTest {

    @Inject
    TrainingSessionRepository trainingSessionRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
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
                .body("neglectedSkills.size()", equalTo(DanceSkill.values().length))

                // Since all skills are neglected and there are no recent training days,
                // the dashboard should return only neglected-skill coaching insights.
                .body("coachingInsights.size()", equalTo(DanceSkill.values().length))
                .body("coachingInsights.type", hasItem("NEGLECTED_SKILL"))
                .body("coachingInsights.type", not(hasItem("TRAINING_BALANCE")))
                .body("coachingInsights.type", not(hasItem("CONSISTENCY")));
    }

    @Test
    void shouldComputeXpAndLevelFromSessions() {
        createFourRecentSessions();

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("totalXp", equalTo(315))
                .body("dancerLevel", equalTo(3))
                .body("totalTrainingMinutes", equalTo(210))
                .body("sessionsCount", equalTo(4))
                .body("topSkill", equalTo("PERFORMANCE"));
    }

    @Test
    void shouldComputeSkillProgressionFromSessions() {
        createFourRecentSessions();

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("skillProgression.size()", equalTo(3))
                .body("skillProgression[0].skill", equalTo("PERFORMANCE"))
                .body("skillProgression[0].totalXp", equalTo(240))
                .body("skillProgression[0].level", equalTo(3))
                .body("skillProgression[0].xpToNextLevel", equalTo(107));
    }

    @Test
    void shouldBuildRecentTrainingDistributionFromSessions() {
        createFourRecentSessions();

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("recentTrainingDistribution.size()", equalTo(3))
                .body("recentTrainingDistribution[0].skill", equalTo("PERFORMANCE"))
                .body("recentTrainingDistribution[0].sessionsCount", equalTo(2))
                .body("recentTrainingDistribution[0].totalMinutes", equalTo(135));
    }

    @Test
    void shouldIdentifyNeglectedSkillsFromSessions() {
        createFourRecentSessions();

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("neglectedSkills", not(hasItem("PERFORMANCE")))
                .body("neglectedSkills", not(hasItem("FOUNDATION")))
                .body("neglectedSkills", not(hasItem("MUSICALITY")))
                .body("neglectedSkills", hasItem("FLEXIBILITY"));
    }

    @Test
    void shouldGenerateCoachingInsightsFromSessions() {
        createFourRecentSessions();

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("coachingInsights.type", hasItem("NEGLECTED_SKILL"))
                .body("coachingInsights.type", hasItem("TRAINING_BALANCE"))
                .body("coachingInsights.message", hasItem("Flexibility has not been trained recently. Consider adding it to your next practice session."))
                .body("coachingInsights.message", hasItem("Performance represents most of your recent training. Consider balancing it with other skills."));
    }

    @Test
    void shouldGenerateConsistencyInsightWhenTrainingFrequencyIsHigh() {
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            createSession(today.minusDays(i).toString(), 30, "LOW", "FOUNDATION", "Session " + i);
        }

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .body("coachingInsights.type", hasItem("CONSISTENCY"))
                .body("coachingInsights.message", hasItem("You trained on 7 different days recently. Great consistency."));
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
                .body("topSkill", equalTo("FLEXIBILITY"))

                // Old session should not count in recent distribution.
                .body("recentTrainingDistribution.size()", equalTo(1))
                .body("recentTrainingDistribution[0].skill", equalTo("PERFORMANCE"))
                .body("recentTrainingDistribution[0].sessionsCount", equalTo(1))
                .body("recentTrainingDistribution[0].totalMinutes", equalTo(60));
    }

    @Test
    void shouldTreatSkillAsNeglectedWhenItWasOnlyTrainedOutsideRecentWindow() {
        LocalDate today = LocalDate.now();

        createSession(today.minusDays(45).toString(), 90, "HIGH", "FLEXIBILITY", "Old flexibility session.");

        given()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)

                // Historical totals still include the old session.
                .body("totalXp", equalTo(180))
                .body("totalTrainingMinutes", equalTo(90))
                .body("sessionsCount", equalTo(1))
                .body("topSkill", equalTo("FLEXIBILITY"))

                // But recent distribution ignores it.
                .body("recentTrainingDistribution.size()", equalTo(0))

                // And the skill is still considered neglected.
                .body("neglectedSkills", hasItem("FLEXIBILITY"))

                // So it should also generate a neglected-skill coaching insight.
                .body("coachingInsights.message", hasItem("Flexibility has not been trained recently. Consider adding it to your next practice session."));
    }

    private void createFourRecentSessions() {
        LocalDate today = LocalDate.now();
        createSession(today.minusDays(3).toString(), 75, "HIGH", "PERFORMANCE", "Full-out choreography runs.");
        createSession(today.minusDays(2).toString(), 60, "MEDIUM", "PERFORMANCE", "Stage presence and stamina.");
        createSession(today.minusDays(1).toString(), 45, "LOW", "FOUNDATION", "Basic groove drills.");
        createSession(today.toString(), 30, "LOW", "MUSICALITY", "Musicality drills.");
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
