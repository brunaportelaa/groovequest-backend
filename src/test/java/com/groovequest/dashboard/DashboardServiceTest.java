package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import com.groovequest.skill.SkillProgressionResponse;
import com.groovequest.skill.SkillProgressionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    TrainingSessionRepository trainingSessionRepository;

    @Mock
    SkillLevelService skillLevelService;

    @Mock
    SkillProgressionService skillProgressionService;

    @InjectMocks
    DashboardService dashboardService;

    @Test
    void shouldReturnDashboardSummary() {
        List<SkillProgressionResponse> skillProgression = List.of(
                new SkillProgressionResponse(DanceSkill.PERFORMANCE, 240L, 3, 107L),
                new SkillProgressionResponse(DanceSkill.FOUNDATION, 45L, 1, 55L)
        );

        when(trainingSessionRepository.sumTotalXp()).thenReturn(285L);
        when(trainingSessionRepository.sumTotalTrainingMinutes()).thenReturn(180L);
        when(trainingSessionRepository.count()).thenReturn(3L);
        when(trainingSessionRepository.sumXpGroupedBySkill())
                .thenReturn(List.of(
                        new Object[]{DanceSkill.PERFORMANCE, 240L},
                        new Object[]{DanceSkill.FOUNDATION, 45L}
                ));
        when(skillLevelService.calculateLevel(285L)).thenReturn(3);
        when(skillProgressionService.getSkillProgression()).thenReturn(skillProgression);
        when(trainingSessionRepository.summarizeTrainingDistributionSince(any(LocalDate.class)))
                .thenReturn(List.of(
                        new Object[]{DanceSkill.PERFORMANCE, 2L, 135L},
                        new Object[]{DanceSkill.FOUNDATION, 1L, 45L}
                ));

        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(285L, response.getTotalXp());
        assertEquals(3, response.getDancerLevel());
        assertEquals(180L, response.getTotalTrainingMinutes());
        assertEquals(3L, response.getSessionsCount());
        assertEquals(DanceSkill.PERFORMANCE, response.getTopSkill());
        assertEquals(240L, response.getSkillProgression().get(0).getTotalXp());
        assertEquals(3, response.getSkillProgression().get(0).getLevel());
        assertEquals(107L, response.getSkillProgression().get(0).getXpToNextLevel());
        assertEquals(DanceSkill.PERFORMANCE, response.getRecentTrainingDistribution().get(0).getSkill());
        assertEquals(2L, response.getRecentTrainingDistribution().get(0).getSessionsCount());
        assertEquals(135L, response.getRecentTrainingDistribution().get(0).getTotalMinutes());
    }

    @Test
    void shouldReturnSafeDefaultsWhenThereAreNoSessions() {
        when(trainingSessionRepository.sumTotalXp()).thenReturn(0L);
        when(trainingSessionRepository.sumTotalTrainingMinutes()).thenReturn(0L);
        when(trainingSessionRepository.count()).thenReturn(0L);
        when(trainingSessionRepository.sumXpGroupedBySkill()).thenReturn(List.of());
        when(skillLevelService.calculateLevel(0L)).thenReturn(1);
        when(skillProgressionService.getSkillProgression()).thenReturn(List.of());
        when(trainingSessionRepository.summarizeTrainingDistributionSince(any(LocalDate.class)))
                .thenReturn(List.of());

        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(0L, response.getTotalXp());
        assertEquals(1, response.getDancerLevel());
        assertEquals(0L, response.getTotalTrainingMinutes());
        assertEquals(0L, response.getSessionsCount());
        assertNull(response.getTopSkill());
        assertTrue(response.getSkillProgression().isEmpty());
        assertTrue(response.getRecentTrainingDistribution().isEmpty());
    }
}
