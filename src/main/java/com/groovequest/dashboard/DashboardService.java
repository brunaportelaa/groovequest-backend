package com.groovequest.dashboard;

import com.aayushatharva.brotli4j.common.annotations.Local;
import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import com.groovequest.skill.SkillProgressionResponse;
import com.groovequest.skill.SkillProgressionService;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class DashboardService {

    private static final int RECENT_DAYS_WINDOW = 30;

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

        LocalDate recentStartDate = LocalDate.now().minusDays(RECENT_DAYS_WINDOW);

        List<RecentTrainingDistributionResponse> recentTrainingDistribution = getRecentTrainingDistribution(recentStartDate);

        List<DanceSkill> neglectedSkills = findNeglectedSkills(recentStartDate);

        return new DashboardResponse(
                totalXp,
                dancerLevel,
                totalTrainingMinutes,
                sessionCount,
                topSkill,
                skillProgression,
                recentTrainingDistribution,
                neglectedSkills
        );
    }

    private DanceSkill findTopSkill() {
        List<Object[]> skillXpTotals = trainingSessionRepository.sumXpGroupedBySkill();

        if (skillXpTotals.isEmpty()) {
            return null;
        }

        return (DanceSkill) skillXpTotals.get(0)[0];
    }

    private List<RecentTrainingDistributionResponse> getRecentTrainingDistribution(LocalDate startDate){
        return trainingSessionRepository
                .summarizeTrainingDistributionSince(startDate)
                .stream()
                .map( row -> new RecentTrainingDistributionResponse(
                        (DanceSkill) row[0],
                        (Long) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

    private List<DanceSkill> findNeglectedSkills(LocalDate startDate) {
        List<DanceSkill> skillsTrained = trainingSessionRepository.findSkillsTrainedSince(startDate);
        return Arrays.stream(DanceSkill.values())
                .filter(skill -> !skillsTrained.contains(skill))
                .toList();
    }
}
