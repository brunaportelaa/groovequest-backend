package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;

public class RecentTrainingDistributionResponse {

    private DanceSkill skill;
    private Long sessionsCount;
    private Long totalMinutes;

    public RecentTrainingDistributionResponse(
            DanceSkill skill,
            Long sessionsCount,
            Long totalMinutes
    ) {
        this.skill = skill;
        this.sessionsCount = sessionsCount;
        this.totalMinutes = totalMinutes;
    }

    public DanceSkill getSkill() {
        return skill;
    }

    public Long getSessionsCount() {
        return sessionsCount;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }
}