package com.groovequest.session;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    private TrainingIntensity intensity;

    @Enumerated(EnumType.STRING)
    private DanceSkill skill;

    private String notes;

    private Integer xpGained;

    public TrainingSession() {
    }

    public TrainingSession(
            LocalDate date,
            Integer durationMinutes,
            TrainingIntensity intensity,
            DanceSkill skill,
            String notes,
            Integer xpGained
    ) {
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.skill = skill;
        this.notes = notes;
        this.xpGained = xpGained;
    }

    public void update(
            LocalDate date,
            Integer durationMinutes,
            TrainingIntensity intensity,
            DanceSkill skill,
            String notes,
            Integer xpGained
    ) {
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.skill = skill;
        this.notes = notes;
        this.xpGained = xpGained;
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