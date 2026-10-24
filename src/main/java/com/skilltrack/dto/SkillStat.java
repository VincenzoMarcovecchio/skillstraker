package com.skilltrack.dto;

import java.io.Serializable;

public class SkillStat implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String colorHex;
    private double weeklyGoalHours;
    private double totalHours;
    private String totalHoursFormatted;
    private int weeklyPercentage;
    private int dayStreak;
    private String lastSessionFormatted;

    public SkillStat() {}

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

    public String getColorHex() {
        return colorHex != null ? colorHex : "#00875A";
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

    public int getWeeklyPercentage() {
        return weeklyPercentage;
    }

    public void setWeeklyPercentage(int weeklyPercentage) {
        this.weeklyPercentage = weeklyPercentage;
    }

    public int getDayStreak() {
        return dayStreak;
    }

    public void setDayStreak(int dayStreak) {
        this.dayStreak = dayStreak;
    }

    public String getLastSessionFormatted() {
        return lastSessionFormatted;
    }

    public void setLastSessionFormatted(String lastSessionFormatted) {
        this.lastSessionFormatted = lastSessionFormatted;
    }
}
