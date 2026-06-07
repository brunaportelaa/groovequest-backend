package com.groovequest.dashboard;

import com.groovequest.coaching.CoachingInsightResponse;
import com.groovequest.coaching.CoachingInsightService;
import com.groovequest.coaching.CoachingInsightType;
import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import com.groovequest.skill.SkillProgressionResponse;
import com.groovequest.skill.SkillProgressionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {

    @Mock
    TrainingSessionRepository trainingSessionRepository;

    @Mock
    SkillLevelService skillLevelService;

    @Mock
    SkillProgressionService skillProgressionService;

    @Mock
    CoachingInsightService coachingInsightService;

    @InjectMocks
    DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        when(trainingSessionRepository.sumTotalXp()).thenReturn(285L);
        when(trainingSessionRepository.sumTotalTrainingMinutes()).thenReturn(180L);
        when(trainingSessionRepository.count()).thenReturn(3L);
        when(skillLevelService.calculateLevel(285L)).thenReturn(3);
        when(skillProgressionService.getSkillProgression()).thenReturn(List.of(
                new SkillProgressionResponse(DanceSkill.PERFORMANCE, 240L, 3, 107L),
                new SkillProgressionResponse(DanceSkill.FOUNDATION, 45L, 1, 55L)
        ));
        when(trainingSessionRepository.summarizeTrainingDistributionSince(any(LocalDate.class)))
                .thenReturn(List.of(
                        new Object[]{DanceSkill.PERFORMANCE, 2L, 135L},
                        new Object[]{DanceSkill.FOUNDATION, 1L, 45L}
                ));
        when(trainingSessionRepository.findSkillsTrainedSince(any(LocalDate.class)))
                .thenReturn(List.of(DanceSkill.PERFORMANCE, DanceSkill.FOUNDATION));
        when(trainingSessionRepository.sumXpGroupedBySkill())
                .thenReturn(Collections.singletonList(new Object[]{DanceSkill.PERFORMANCE, 285L}));
        when(trainingSessionRepository.countDistinctTrainingDaysSince(any(LocalDate.class)))
                .thenReturn(4L);
        when(coachingInsightService.generateInsights(any(), any(), any()))
                .thenReturn(List.of(
                        new CoachingInsightResponse(CoachingInsightType.NEGLECTED_SKILL, "any"),
                        new CoachingInsightResponse(CoachingInsightType.TRAINING_BALANCE, "any"),
                        new CoachingInsightResponse(CoachingInsightType.CONSISTENCY, "any")
                ));
    }

    @Test
    void shouldComputeSummaryFields() {
        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(285L, response.getTotalXp());
        assertEquals(3, response.getDancerLevel());
        assertEquals(180L, response.getTotalTrainingMinutes());
        assertEquals(3L, response.getSessionsCount());
        assertEquals(DanceSkill.PERFORMANCE, response.getTopSkill());
    }

    @Test
    void shouldComputeSkillProgression() {
        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(2, response.getSkillProgression().size());
        assertEquals(2, response.getRecentTrainingDistribution().size());
    }

    @Test
    void shouldIdentifyNeglectedSkills() {
        DashboardResponse response = dashboardService.getDashboard();

        assertFalse(response.getNeglectedSkills().contains(DanceSkill.PERFORMANCE));
        assertFalse(response.getNeglectedSkills().contains(DanceSkill.FOUNDATION));
        assertTrue(response.getNeglectedSkills().contains(DanceSkill.FLEXIBILITY));
    }

    @Test
    void shouldReturnCoachingInsights() {
        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(3, response.getCoachingInsights().size());
        assertEquals(CoachingInsightType.NEGLECTED_SKILL, response.getCoachingInsights().get(0).getType());
        assertEquals(CoachingInsightType.TRAINING_BALANCE, response.getCoachingInsights().get(1).getType());
        assertEquals(CoachingInsightType.CONSISTENCY, response.getCoachingInsights().get(2).getType());
    }

    @Test
    void shouldReturnSafeDefaultsWhenThereAreNoSessions() {
        when(trainingSessionRepository.sumTotalXp()).thenReturn(0L);
        when(trainingSessionRepository.sumTotalTrainingMinutes()).thenReturn(0L);
        when(trainingSessionRepository.count()).thenReturn(0L);
        when(skillLevelService.calculateLevel(0L)).thenReturn(1);
        when(skillProgressionService.getSkillProgression()).thenReturn(List.of());
        when(trainingSessionRepository.sumXpGroupedBySkill()).thenReturn(List.<Object[]>of());
        when(trainingSessionRepository.summarizeTrainingDistributionSince(any(LocalDate.class))).thenReturn(List.of());
        when(trainingSessionRepository.findSkillsTrainedSince(any(LocalDate.class))).thenReturn(List.of());
        when(trainingSessionRepository.countDistinctTrainingDaysSince(any(LocalDate.class))).thenReturn(0L);
        when(coachingInsightService.generateInsights(any(), any(), any()))
                .thenReturn(List.of(new CoachingInsightResponse(CoachingInsightType.NEGLECTED_SKILL, "any")));

        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(0L, response.getTotalXp());
        assertEquals(1, response.getDancerLevel());
        assertEquals(0L, response.getTotalTrainingMinutes());
        assertEquals(0L, response.getSessionsCount());
        assertNull(response.getTopSkill());
        assertTrue(response.getSkillProgression().isEmpty());
        assertTrue(response.getRecentTrainingDistribution().isEmpty());
        assertEquals(DanceSkill.values().length, response.getNeglectedSkills().size());
        assertEquals(1, response.getCoachingInsights().size());
    }
}
