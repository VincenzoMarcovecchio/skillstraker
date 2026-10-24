package com.skilltrack.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PracticeSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long skillId;
    private String skillName;
    private String skillColorHex;
    private int durationMinutes;
    private String notes;
    private LocalDate sessionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PracticeSession() {}

    public PracticeSession(Long id, Long skillId, int durationMinutes, String notes, LocalDate sessionDate) {
        this.id = id;
        this.skillId = skillId;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.sessionDate = sessionDate;
    }

    public String getFormattedDuration() {
        if (durationMinutes < 60) {
            return durationMinutes + " min";
        }
        int hours = durationMinutes / 60;
        int remainingMins = durationMinutes % 60;
        if (remainingMins == 0) {
            return hours + "h";
        }
        return hours + "h " + remainingMins + "m";
    }

    public String getRelativeDateLabel() {
        if (sessionDate == null) return "";
        LocalDate today = LocalDate.now();
        if (sessionDate.isEqual(today)) {
            return "Today";
        } else if (sessionDate.isEqual(today.minusDays(1))) {
            return "Yesterday";
        } else {
            return sessionDate.getMonth().name().substring(0, 3) + " " + sessionDate.getDayOfMonth();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillColorHex() {
        return skillColorHex != null ? skillColorHex : "#00875A";
    }

    public void setSkillColorHex(String skillColorHex) {
        this.skillColorHex = skillColorHex;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
