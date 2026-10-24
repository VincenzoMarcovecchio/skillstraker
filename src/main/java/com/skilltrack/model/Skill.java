package com.skilltrack.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String category;
    private String colorHex;
    private double weeklyGoalHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Skill() {
        this.category = "General";
        this.colorHex = "#00875A";
        this.weeklyGoalHours = 5.0;
    }

    public Skill(Long id, String name, String category, String colorHex, double weeklyGoalHours) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.colorHex = (colorHex != null && !colorHex.isBlank()) ? colorHex : "#00875A";
        this.weeklyGoalHours = weeklyGoalHours > 0 ? weeklyGoalHours : 5.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public double getWeeklyGoalHours() {
        return weeklyGoalHours;
    }

    public void setWeeklyGoalHours(double weeklyGoalHours) {
        this.weeklyGoalHours = weeklyGoalHours;
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
