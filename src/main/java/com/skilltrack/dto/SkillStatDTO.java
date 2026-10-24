package com.skilltrack.dto;

public class SkillStatDTO {
    private Long id;
    private String name;
    private String category;
    private String colorHex;
    private double weeklyGoalHours;
    private double totalHours;
    private String totalHoursFormatted;
    private double weeklyHours;
    private int weeklyGoalPercentage;
    private int streakDays;
    private String lastSessionFormatted;

    public SkillStatDTO() {}

    // Getters and Setters
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

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public String getTotalHoursFormatted() {
        return totalHoursFormatted;
    }

    public void setTotalHoursFormatted(String totalHoursFormatted) {
        this.totalHoursFormatted = totalHoursFormatted;
    }

    public double getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(double weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public int getWeeklyGoalPercentage() {
        return weeklyGoalPercentage;
    }

    public void setWeeklyGoalPercentage(int weeklyGoalPercentage) {
        this.weeklyGoalPercentage = weeklyGoalPercentage;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public String getLastSessionFormatted() {
        return lastSessionFormatted;
    }

    public void setLastSessionFormatted(String lastSessionFormatted) {
        this.lastSessionFormatted = lastSessionFormatted;
    }
}
