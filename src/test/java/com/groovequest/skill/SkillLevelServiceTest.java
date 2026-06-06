package com.groovequest.skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkillLevelServiceTest {

    private final SkillLevelService skillLevelService = new SkillLevelService();

    @Test
    void shouldKeepSkillatLevelOneWhenXpIsBelowFirstThreshold(){
        assertEquals(1, skillLevelService.calculateLevel(45L));
        assertEquals(55,skillLevelService.calculateXpToNextLevel(45L));
    }

    @Test
    void shouldReachLevelTwoAtOneHundredXp() {
        assertEquals(2, skillLevelService.calculateLevel(100L));
        assertEquals(115, skillLevelService.calculateXpToNextLevel(100L));
    }

    @Test
    void shouldReachLevelThreeAtTwoHundredFifteenXp() {
        assertEquals(3, skillLevelService.calculateLevel(215L));
        assertEquals(132, skillLevelService.calculateXpToNextLevel(215L));
    }

    @Test
    void shouldCalculateProgressWithinCurrentLevel() {
        assertEquals(3, skillLevelService.calculateLevel(240L));
        assertEquals(107, skillLevelService.calculateXpToNextLevel(240L));
    }

    @Test
    void shouldReachLevelFourAtThreeHundredFortySevenXp() {
        assertEquals(4, skillLevelService.calculateLevel(347L));
        assertEquals(152, skillLevelService.calculateXpToNextLevel(347L));
    }
}
