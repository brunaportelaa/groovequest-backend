package com.groovequest.session;

import java.time.LocalDate;

public class TrainingSessionResponse {

    private Long id;
    private LocalDate date;
    private Integer durationMinutes;
    private TrainingIntensity intensity;
    private DanceSkill skill;
    private String notes;
    private Integer xpGained;

    public TrainingSessionResponse(
            Long id,
            LocalDate date,
            Integer durationMinutes,
            TrainingIntensity intensity,
            DanceSkill skill,
            String notes,
            Integer xpGained
    ) {
        this.id = id;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.skill = skill;
        this.notes = notes;
        this.xpGained = xpGained;
    }

    public static TrainingSessionResponse fromEntity(TrainingSession trainingSession) {
        return new TrainingSessionResponse(
                trainingSession.getId(),
                trainingSession.getDate(),
                trainingSession.getDurationMinutes(),
                trainingSession.getIntensity(),
                trainingSession.getSkill(),
                trainingSession.getNotes(),
                trainingSession.getXpGained()
        );
    }

    public Long getId() {
        return id;
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

    public Integer getXpGained() {
        return xpGained;
    }
}