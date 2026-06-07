package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import com.groovequest.skill.SkillProgressionResponse;
import com.groovequest.skill.SkillProgressionService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DashboardService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final SkillLevelService skillLevelService;
    private final SkillProgressionService skillProgressionService;

    public DashboardService(
            TrainingSessionRepository trainingSessionRepository,
            SkillLevelService skillLevelService,
            SkillProgressionService skillProgressionService
    ) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.skillLevelService = skillLevelService;
        this.skillProgressionService = skillProgressionService;
    }

    public DashboardResponse getDashboard() {
        Long totalXp = trainingSessionRepository.sumTotalXp();
        Long totalTrainingMinutes = trainingSessionRepository.sumTotalTrainingMinutes();
        Long sessionCount = trainingSessionRepository.count();

        Integer dancerLevel = skillLevelService.calculateLevel(totalXp);

        List<SkillProgressionResponse> skillProgression = skillProgressionService.getSkillProgression();
        DanceSkill topSkill = findTopSkill();

        return new DashboardResponse(
                totalXp,
                dancerLevel,
                totalTrainingMinutes,
                sessionCount,
                topSkill,
                skillProgression
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
