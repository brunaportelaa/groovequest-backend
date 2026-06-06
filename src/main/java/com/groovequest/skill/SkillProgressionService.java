package com.groovequest.skill;

import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SkillProgressionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final SkillLevelService skillLevelService;

    public SkillProgressionService(TrainingSessionRepository trainingSessionRepository, SkillLevelService skillLevelService) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.skillLevelService = skillLevelService;
    }

    public List<SkillProgressionResponse> getSkillProgression() {
        return trainingSessionRepository
                .sumXpGroupedBySkill()
                .stream()
                .map(row -> {
                    DanceSkill skill = (DanceSkill) row[0];
                    Long totalXp = (Long) row[1];

                    return new SkillProgressionResponse(
                            skill,
                            totalXp,
                            skillLevelService.calculateLevel(totalXp),
                            skillLevelService.calculateXpToNextLevel(totalXp)
                    );
                        }
                )
                .toList();
    }
}


