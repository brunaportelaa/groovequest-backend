package com.groovequest.skill;

import com.groovequest.session.DanceSkill;

public class SkillProgressionResponse {

    private DanceSkill skill;
    private Long totalXp;
    private Integer level;
    private Long xpToNextLevel;

    public SkillProgressionResponse(
            DanceSkill skill,
            Long totalXp,
            Integer level,
            Long xpToNextLevel
    ) {
        this.skill = skill;
        this.totalXp = totalXp;
        this.level = level;
        this.xpToNextLevel = xpToNextLevel;
    }

    public DanceSkill getSkill() {
        return skill;
    }

    public Long getTotalXp() {
        return totalXp;
    }

    public Integer getLevel() {
        return level;
    }

    public Long getXpToNextLevel() {
        return xpToNextLevel;
    }
}