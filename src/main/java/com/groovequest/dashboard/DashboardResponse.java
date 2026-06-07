package com.groovequest.dashboard;

import com.groovequest.coaching.CoachingInsightResponse;
import com.groovequest.session.DanceSkill;
import com.groovequest.skill.SkillProgressionResponse;

import java.util.List;

public class DashboardResponse {

    private Long totalXp;
    private Integer dancerLevel;
    private Long totalTrainingMinutes;
    private Long sessionsCount;
    private DanceSkill topSkill;
    private List<SkillProgressionResponse> skillProgression;
    private List<RecentTrainingDistributionResponse> recentTrainingDistribution;
    private List<DanceSkill> neglectedSkills;
    private List<CoachingInsightResponse> coachingInsights;

    public DashboardResponse(
            Long totalXp,
            Integer dancerLevel,
            Long totalTrainingMinutes,
            Long sessionsCount,
            DanceSkill topSkill,
            List<SkillProgressionResponse> skillProgression,
            List<RecentTrainingDistributionResponse> recentTrainingDistribution,
            List<DanceSkill> neglectedSkills,
            List<CoachingInsightResponse> coachingInsights
    ) {
        this.totalXp = totalXp;
        this.dancerLevel = dancerLevel;
        this.totalTrainingMinutes = totalTrainingMinutes;
        this.sessionsCount = sessionsCount;
        this.topSkill = topSkill;
        this.skillProgression = skillProgression;
        this.recentTrainingDistribution = recentTrainingDistribution;
        this.neglectedSkills = neglectedSkills;
        this.coachingInsights = coachingInsights;
    }

    public Long getTotalXp() {
        return totalXp;
    }

    public Integer getDancerLevel() {
        return dancerLevel;
    }

    public Long getTotalTrainingMinutes() {
        return totalTrainingMinutes;
    }

    public Long getSessionsCount() {
        return sessionsCount;
    }

    public DanceSkill getTopSkill() {
        return topSkill;
    }

    public List<SkillProgressionResponse> getSkillProgression() {
        return skillProgression;
    }

    public List<RecentTrainingDistributionResponse> getRecentTrainingDistribution() {
        return recentTrainingDistribution;
    }

    public List<DanceSkill> getNeglectedSkills() {
        return neglectedSkills;
    }

    public List<CoachingInsightResponse> getCoachingInsights() {
        return coachingInsights;
    }
}