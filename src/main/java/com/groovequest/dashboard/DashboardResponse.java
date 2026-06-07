package com.groovequest.dashboard;

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

    public DashboardResponse(
            Long totalXp,
            Integer dancerLevel,
            Long totalTrainingMinutes,
            Long sessionsCount,
            DanceSkill topSkill,
            List<SkillProgressionResponse> skillProgression
    ) {
        this.totalXp = totalXp;
        this.dancerLevel = dancerLevel;
        this.totalTrainingMinutes = totalTrainingMinutes;
        this.sessionsCount = sessionsCount;
        this.topSkill = topSkill;
        this.skillProgression = skillProgression;
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
}