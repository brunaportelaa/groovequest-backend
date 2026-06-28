package com.groovequest.session;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long userId;

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
            Long userId,
            LocalDate date,
            Integer durationMinutes,
            TrainingIntensity intensity,
            DanceSkill skill,
            String notes,
            Integer xpGained
    ) {
        this.userId = userId;
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

    public Long getUserId() { return userId; }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}