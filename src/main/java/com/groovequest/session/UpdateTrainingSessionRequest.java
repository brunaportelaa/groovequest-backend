package com.groovequest.session;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateTrainingSessionRequest {

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @NotNull(message = "Duration is required.")
    @Min(value = 1, message = "Duration must be at least 1 minute.")
    private Integer durationMinutes;

    @NotNull(message = "Intensity is required.")
    private TrainingIntensity intensity;

    @NotNull(message = "Skill is required.")
    private DanceSkill skill;

    @Size(max = 500, message = "Notes must not exceed 500 characters.")
    private String notes;

    public UpdateTrainingSessionRequest(LocalDate date, Integer durationMinutes, TrainingIntensity intensity, DanceSkill skill, String notes) {
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.skill = skill;
        this.notes = notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public TrainingIntensity getIntensity() {
        return intensity;
    }

    public DanceSkill getSkill() {
        return skill;
    }

    public String getNotes() {
        return notes;
    }
}