package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DashboardService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final SkillLevelService skillLevelService;

    public DashboardService(
            TrainingSessionRepository trainingSessionRepository,
            SkillLevelService skillLevelService
    ) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.skillLevelService = skillLevelService;
    }

    public DashboardResponse getDashboard() {
        Long totalXp = trainingSessionRepository.sumTotalXp();
        Long totalTrainingMinutes = trainingSessionRepository.sumTotalTrainingMinutes();
        Long sessionCount = trainingSessionRepository.count();

        Integer dancerLevel = skillLevelService.calculateLevel(totalXp);
        DanceSkill topSkill = findTopSkill();

        return new DashboardResponse(
                totalXp,
                dancerLevel,
                totalTrainingMinutes,
                sessionCount,
                topSkill
        );
    }

    private DanceSkill findTopSkill() {
        List<Object[]> skillXpTotals = trainingSessionRepository.sumXpGroupedBySkill();

        if (skillXpTotals.isEmpty()) {
            return null;
        }

        return (DanceSkill) skillXpTotals.get(0)[0];
    }
}
