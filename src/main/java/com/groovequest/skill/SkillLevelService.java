package com.groovequest.skill;

public class SkillLevelService {

    // XP required for the first level-up.
    private static final long BASE_XP_REQUIREMENT = 100L;

    // Makes each next level require 15% more XP.
    private static final double GROWTH_FACTOR = 1.15;

    public int calculateLevel(Long totalXp) {
        int level = 1;
        long remainingXp = totalXp;

        // Keep leveling up while there is enough XP for the next level.
        while (remainingXp >= getXpRequiredForNextLevel(level)) {
            remainingXp -= getXpRequiredForNextLevel(level);
            level++;
        }

        return level;
    }

    public long calculateXpToNextLevel(Long totalXp) {
        int currentLevel = calculateLevel(totalXp);

        // Total XP already "spent" to reach the current level.
        long totalXpRequiredToReachCurrentLevel  = calculateTotalXpRequiredToReachLevel(currentLevel);

        // XP earned inside the current level.
        long xpProgressInsideCurrentLevel  = totalXp - totalXpRequiredToReachCurrentLevel ;

        // Total XP needed to leave the current level.
        long xpRequiredToLeaveCurrentLevel  = getXpRequiredForNextLevel(currentLevel);

        return xpRequiredToLeaveCurrentLevel - xpProgressInsideCurrentLevel;
    }

    private long calculateTotalXpRequiredToReachLevel(int level) {
        long totalRequiredXp = 0;

        // Sum XP requirements for all previous level-ups.
        for (int currentLevel = 1; currentLevel < level; currentLevel++) {
            totalRequiredXp += getXpRequiredForNextLevel(currentLevel);
        }

        return totalRequiredXp;
    }

    // Calculates the size of this level's XP bar.
    // Example: if level = 2, this returns how much XP is needed
    // to go from level 2 to level 3, not total XP from level 1.
    private long getXpRequiredForNextLevel(int currentLevel) {
        // Formula: base XP * growth factor^(current level - 1)
        return Math.round(BASE_XP_REQUIREMENT * Math.pow(GROWTH_FACTOR, currentLevel - 1));
    }

}