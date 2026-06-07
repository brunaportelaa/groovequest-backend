package com.groovequest.coaching;

import com.groovequest.dashboard.RecentTrainingDistributionResponse;
import com.groovequest.session.DanceSkill;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoachingInsightServiceTest {

    private final CoachingInsightService coachingInsightService = new CoachingInsightService();

    @Test
    void shouldGenerateInsightForEachNeglectedSkill() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(DanceSkill.FLEXIBILITY, DanceSkill.MUSICALITY),
                List.of(),
                0L
        );

        assertEquals(2, insights.size());
        assertEquals(CoachingInsightType.NEGLECTED_SKILL, insights.get(0).getType());
        assertEquals(CoachingInsightType.NEGLECTED_SKILL, insights.get(1).getType());
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoInsightConditions() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(),
                List.of(),
                0L
        );

        assertTrue(insights.isEmpty());
    }

    @Test
    void shouldFormatMultiWordSkillNamesForHumanReadableMessages() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(DanceSkill.HIP_HOP, DanceSkill.JAZZ_FUNK),
                List.of(),
                0L
        );

        assertEquals(
                "Hip hop has not been trained recently. Consider adding it to your next practice session.",
                insights.get(0).getMessage()
        );

        assertEquals(
                "Jazz funk has not been trained recently. Consider adding it to your next practice session.",
                insights.get(1).getMessage()
        );
    }

    @Test
    void shouldGenerateTrainingBalanceInsightWhenOneSkillDominatesRecentTraining() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(),
                List.of(
                        new RecentTrainingDistributionResponse(DanceSkill.PERFORMANCE, 2L, 135L),
                        new RecentTrainingDistributionResponse(DanceSkill.FOUNDATION, 1L, 45L)
                ),
                0L
        );

        assertEquals(1, insights.size());
        assertEquals(CoachingInsightType.TRAINING_BALANCE, insights.get(0).getType());
        assertEquals(
                "Performance represents most of your recent training. Consider balancing it with other skills.",
                insights.get(0).getMessage()
        );
    }

    @Test
    void shouldNotGenerateTrainingBalanceInsightWhenTrainingIsBalanced() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(),
                List.of(
                        new RecentTrainingDistributionResponse(DanceSkill.PERFORMANCE, 1L, 55L),
                        new RecentTrainingDistributionResponse(DanceSkill.FOUNDATION, 1L, 45L)
                ),
                0L
        );

        assertTrue(insights.isEmpty());
    }

    @Test
    void shouldGenerateConsistencyInsightWhenTrainingDaysReachThreshold() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(),
                List.of(),
                7L
        );

        assertEquals(1, insights.size());
        assertEquals(CoachingInsightType.CONSISTENCY, insights.get(0).getType());
        assertEquals(
                "You trained on 7 different days recently. Great consistency.",
                insights.get(0).getMessage()
        );
    }

    @Test
    void shouldNotGenerateConsistencyInsightBelowThreshold() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(),
                List.of(),
                6L
        );

        assertTrue(insights.isEmpty());
    }

    @Test
    void shouldGenerateMultipleInsightTypesTogether() {
        List<CoachingInsightResponse> insights = coachingInsightService.generateInsights(
                List.of(DanceSkill.FLEXIBILITY),
                List.of(
                        new RecentTrainingDistributionResponse(DanceSkill.PERFORMANCE, 2L, 135L),
                        new RecentTrainingDistributionResponse(DanceSkill.FOUNDATION, 1L, 45L)
                ),
                7L
        );

        assertEquals(3, insights.size());

        assertTrue(insights.stream().anyMatch(insight ->
                insight.getType() == CoachingInsightType.NEGLECTED_SKILL
        ));

        assertTrue(insights.stream().anyMatch(insight ->
                insight.getType() == CoachingInsightType.TRAINING_BALANCE
        ));

        assertTrue(insights.stream().anyMatch(insight ->
                insight.getType() == CoachingInsightType.CONSISTENCY
        ));
    }
}