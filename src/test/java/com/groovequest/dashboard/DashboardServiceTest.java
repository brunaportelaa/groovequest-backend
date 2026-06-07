package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    TrainingSessionRepository trainingSessionRepository;

    @Mock
    SkillLevelService skillLevelService;

    @InjectMocks
    DashboardService dashboardService;

    @Test
    void shouldReturnDashboardSummary() {
        when(trainingSessionRepository.sumTotalXp()).thenReturn(285L);
        when(trainingSessionRepository.sumTotalTrainingMinutes()).thenReturn(180L);
        when(trainingSessionRepository.count()).thenReturn(3L);
        when(trainingSessionRepository.sumXpGroupedBySkill())
                .thenReturn(List.of(
                        new Object[]{DanceSkill.PERFORMANCE, 240L},
                        new Object[]{DanceSkill.FOUNDATION, 45L}
                ));
        when(skillLevelService.calculateLevel(285L)).thenReturn(3);

        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(285L, response.getTotalXp());
        assertEquals(3, response.getDancerLevel());
        assertEquals(180L, response.getTotalTrainingMinutes());
        assertEquals(3L, response.getSessionsCount());
        assertEquals(DanceSkill.PERFORMANCE, response.getTopSkill());
    }

    @Test
    void shouldReturnSafeDefaultsWhenThereAreNoSessions() {
        when(trainingSessionRepository.sumTotalXp()).thenReturn(0L);
        when(trainingSessionRepository.sumTotalTrainingMinutes()).thenReturn(0L);
        when(trainingSessionRepository.count()).thenReturn(0L);
        when(trainingSessionRepository.sumXpGroupedBySkill()).thenReturn(List.of());
        when(skillLevelService.calculateLevel(0L)).thenReturn(1);

        DashboardResponse response = dashboardService.getDashboard();

        assertEquals(0L, response.getTotalXp());
        assertEquals(1, response.getDancerLevel());
        assertEquals(0L, response.getTotalTrainingMinutes());
        assertEquals(0L, response.getSessionsCount());
        assertNull(response.getTopSkill());
    }
}
