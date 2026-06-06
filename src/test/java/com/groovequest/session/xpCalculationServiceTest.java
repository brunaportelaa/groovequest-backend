package com.groovequest.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class xpCalculationServiceTest {

    private final XpCalculationService xpCalculationService = new XpCalculationService();

    @Test
    void shouldCalculateLowIntensityXp() {
        int xp = xpCalculationService.calculate(30, TrainingIntensity.LOW);
        assertEquals(30, xp);
    }

    @Test
    void shouldCalculateMediumIntensityXp() {
        int xp = xpCalculationService.calculate(60, TrainingIntensity.MEDIUM);

        assertEquals(90, xp);
    }

    @Test
    void shouldCalculateHighIntensityXp() {
        int xp = xpCalculationService.calculate(75, TrainingIntensity.HIGH);

        assertEquals(150, xp);
    }

}
