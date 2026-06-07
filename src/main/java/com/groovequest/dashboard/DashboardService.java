package com.groovequest.dashboard;

import com.groovequest.coaching.CoachingInsightResponse;
import com.groovequest.coaching.CoachingInsightService;
import com.groovequest.session.DanceSkill;
import com.groovequest.session.TrainingSessionRepository;
import com.groovequest.skill.SkillLevelService;
import com.groovequest.skill.SkillProgressionResponse;
import com.groovequest.skill.SkillProgressionService;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class DashboardService {

    private static final int RECENT_ACTIVITY_DAYS = 30;

    private final TrainingSessionRepository trainingSessionRepository;
    private final SkillLevelService skillLevelService;
    private final SkillProgressionService skillProgressionService;
    private final CoachingInsightService coachingInsightService;

    public DashboardService(
            TrainingSessionRepository trainingSessionRepository,
            SkillLevelService skillLevelService,
            SkillProgressionService skillProgressionService,
            CoachingInsightService coachingInsightService
    ) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.skillLevelService = skillLevelService;
        this.skillProgressionService = skillProgressionService;
        this.coachingInsightService = coachingInsightService;
    }


    public DashboardResponse getDashboard() {
        Long totalXp = trainingSessionRepository.sumTotalXp();
        Long totalTrainingMinutes = trainingSessionRepository.sumTotalTrainingMinutes();
        Long sessionCount = trainingSessionRepository.count();

        Integer dancerLevel = skillLevelService.calculateLevel(totalXp);

        List<SkillProgressionResponse> skillProgression = skillProgressionService.getSkillProgression();
        DanceSkill topSkill = findTopSkill();

        LocalDate recentStartDate = calculateRecentStartDate();

        List<RecentTrainingDistributionResponse> recentTrainingDistribution = getRecentTrainingDistribution(recentStartDate);

        List<DanceSkill> neglectedSkills = findNeglectedSkills(recentStartDate);
        Long recentTrainingDays = trainingSessionRepository.countDistinctTrainingDaysSince(calculateRecentStartDate());

        List<CoachingInsightResponse> coachingInsights = coachingInsightService.generateInsights(
                neglectedSkills,
                recentTrainingDistribution,
                recentTrainingDays
        );

        return new DashboardResponse(
                totalXp,
                dancerLevel,
                totalTrainingMinutes,
                sessionCount,
                topSkill,
                skillProgression,
                recentTrainingDistribution,
                neglectedSkills,
                coachingInsights
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

    private LocalDate calculateRecentStartDate() {
        return LocalDate.now().minusDays(RECENT_ACTIVITY_DAYS);
    }}
