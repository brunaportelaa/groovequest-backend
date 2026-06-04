package com.groovequest.session;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XpCalculationService {

    public int calculate(Integer durationMinutes,
                         TrainingIntensity intensity) {
        double multiplier = getIntensityMultiplier(intensity);
        return (int) Math.round(durationMinutes * multiplier);
    }

    private double getIntensityMultiplier(TrainingIntensity intensity) {
        return switch (intensity) {
            case LOW -> 1.0;
            case MEDIUM -> 1.5;
            case HIGH -> 2.0;
        };
    }
}
