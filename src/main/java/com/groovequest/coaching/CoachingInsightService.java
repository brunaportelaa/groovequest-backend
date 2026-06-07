package com.groovequest.coaching;

import com.groovequest.dashboard.RecentTrainingDistributionResponse;
import com.groovequest.session.DanceSkill;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoachingInsightService {

    private static final double DOMINANCE_THRESHOLD = 0.60;
    private static final long CONSISTENT_TRAINING_DAYS_THRESHOLD = 7;

    public List<CoachingInsightResponse> generateInsights(
            List<DanceSkill> neglectedSkills,
            List<RecentTrainingDistributionResponse> recentTrainingDistribution,
            Long recentTrainingDays) {
        List<CoachingInsightResponse> insights = new ArrayList<>();

        insights.addAll(generateNeglectedSkillInsights(neglectedSkills));
        createTrainingBalanceInsight(recentTrainingDistribution)
                .ifPresent(insights::add);

        createConsistencyInsight(recentTrainingDays)
                .ifPresent(insights::add);

        return insights;
    }

    private List<CoachingInsightResponse> generateNeglectedSkillInsights(List<DanceSkill> neglectedSkills) {
        return neglectedSkills
                .stream()
                .map(this::createNeglectedSkillInsight)
                .toList();
    }

    private CoachingInsightResponse createNeglectedSkillInsight(DanceSkill skill) {
        return new CoachingInsightResponse(
                CoachingInsightType.NEGLECTED_SKILL,
                formatSkillName(skill) + " has not been trained recently. Consider adding it to your next practice session."
        );
    }

    private Optional<CoachingInsightResponse> createConsistencyInsight(Long recentTrainingDays) {
        if (recentTrainingDays < CONSISTENT_TRAINING_DAYS_THRESHOLD) {
            return Optional.empty();
        }

        return Optional.of(new CoachingInsightResponse(
                CoachingInsightType.CONSISTENCY,
                "You trained on " + recentTrainingDays + " different days recently. Great consistency."
        ));
    }

    private Optional<CoachingInsightResponse> createTrainingBalanceInsight(
            List<RecentTrainingDistributionResponse> recentTrainingDistribution
    ){
        if (recentTrainingDistribution.isEmpty()){
            return Optional.empty();
        }

        long totalRecentMinutes = recentTrainingDistribution.stream()
                .mapToLong(RecentTrainingDistributionResponse::getTotalMinutes)
                .sum();

        if (totalRecentMinutes == 0) {
            return Optional.empty();
        }

        RecentTrainingDistributionResponse dominantSkillData = recentTrainingDistribution.get(0);
        double dominantSkillRatio = (double) dominantSkillData.getTotalMinutes() / totalRecentMinutes;

        if (dominantSkillRatio < DOMINANCE_THRESHOLD) {
            return Optional.empty();
        }

        DanceSkill dominantSkill = dominantSkillData.getSkill();

        return Optional.of(
                new CoachingInsightResponse(
                        CoachingInsightType.TRAINING_BALANCE,
                        formatSkillName(dominantSkill) + " represents most of your recent training. Consider balancing it with other skills."
                )
        );

    }

    private String formatSkillName(DanceSkill skill) {
        String lowercaseName = skill.name()
                .replace("_", " ")
                .toLowerCase();

        return Character.toUpperCase(lowercaseName.charAt(0)) + lowercaseName.substring(1);
    }
}
