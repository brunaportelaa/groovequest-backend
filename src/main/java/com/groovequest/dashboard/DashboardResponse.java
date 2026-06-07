package com.groovequest.dashboard;

import com.groovequest.session.DanceSkill;

public class DashboardResponse {

    private Long totalXp;
    private Integer dancerLevel;
    private Long totalTrainingMinutes;
    private Long sessionsCount;
    private DanceSkill topSkill;

    public DashboardResponse(
            Long totalXp,
            Integer dancerLevel,
            Long totalTrainingMinutes,
            Long sessionsCount,
            DanceSkill topSkill
    ) {
        this.totalXp = totalXp;
        this.dancerLevel = dancerLevel;
        this.totalTrainingMinutes = totalTrainingMinutes;
        this.sessionsCount = sessionsCount;
        this.topSkill = topSkill;
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
}