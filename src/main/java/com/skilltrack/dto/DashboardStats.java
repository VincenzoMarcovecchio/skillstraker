package com.skilltrack.dto;

import com.skilltrack.model.PracticeSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private String greeting;
    private String userName = "Jordan";
    private String userInitials = "JD";
    private int skillsPracticedTodayCount;

    private SkillStat mostPracticedSkill;
    private List<SkillStat> miniSkills = new ArrayList<>();
    private List<PracticeSession> recentSessions = new ArrayList<>();

    // 18 columns, each has 7 days (or 7 rows x 18 cols for table rendering)
    private List<List<HeatmapDay>> heatmapColumns = new ArrayList<>();
    private List<List<HeatmapDay>> heatmapRows = new ArrayList<>();

    public DashboardStats() {}

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserInitials() {
        return userInitials;
    }

    public void setUserInitials(String userInitials) {
        this.userInitials = userInitials;
    }

    public int getSkillsPracticedTodayCount() {
        return skillsPracticedTodayCount;
    }

    public void setSkillsPracticedTodayCount(int skillsPracticedTodayCount) {
        this.skillsPracticedTodayCount = skillsPracticedTodayCount;
    }

    public SkillStat getMostPracticedSkill() {
        return mostPracticedSkill;
    }

    public void setMostPracticedSkill(SkillStat mostPracticedSkill) {
        this.mostPracticedSkill = mostPracticedSkill;
    }

    public List<SkillStat> getMiniSkills() {
        return miniSkills;
    }

    public void setMiniSkills(List<SkillStat> miniSkills) {
        this.miniSkills = miniSkills;
    }

    public List<PracticeSession> getRecentSessions() {
        return recentSessions;
    }

    public void setRecentSessions(List<PracticeSession> recentSessions) {
        this.recentSessions = recentSessions;
    }

    public List<List<HeatmapDay>> getHeatmapColumns() {
        return heatmapColumns;
    }

    public void setHeatmapColumns(List<List<HeatmapDay>> heatmapColumns) {
        this.heatmapColumns = heatmapColumns;
    }

    public List<List<HeatmapDay>> getHeatmapRows() {
        return heatmapRows;
    }

    public void setHeatmapRows(List<List<HeatmapDay>> heatmapRows) {
        this.heatmapRows = heatmapRows;
    }
}
