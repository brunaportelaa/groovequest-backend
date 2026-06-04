package com.groovequest.skill;

import com.groovequest.session.DanceSkill;

public class SkillProgressionResponse {

    private DanceSkill skill;
    private Long totalXp;

    public SkillProgressionResponse(DanceSkill skill, Long totalXp) {
        this.skill = skill;
        this.totalXp = totalXp;
    }

    public DanceSkill getSkill() {
        return skill;
    }

    public Long getTotalXp() {
        return totalXp;
    }
}