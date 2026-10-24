package com.skilltrack.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class HeatmapDay implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private String dateStr;
    private int dayOfWeek; // 1 = Monday ... 7 = Sunday
    private int minutes;
    private int level; // 0, 1, 2, 3, 4
    private String tooltip;

    public HeatmapDay() {}

    public HeatmapDay(LocalDate date, int minutes) {
        this.date = date;
        this.dateStr = date.toString();
        this.dayOfWeek = date.getDayOfWeek().getValue();
        this.minutes = minutes;
        this.level = calculateLevel(minutes);
        this.tooltip = formatTooltip(date, minutes);
    }

    private int calculateLevel(int mins) {
        if (mins <= 0) return 0;
        if (mins <= 30) return 1;
        if (mins <= 60) return 2;
        if (mins <= 120) return 3;
        return 4;
    }

    private String formatTooltip(LocalDate d, int mins) {
        String month = d.getMonth().name().substring(0, 1) + d.getMonth().name().substring(1, 3).toLowerCase();
        if (mins <= 0) {
            return "No activity on " + month + " " + d.getDayOfMonth() + ", " + d.getYear();
        }
        return mins + " min practiced on " + month + " " + d.getDayOfMonth() + ", " + d.getYear();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
        this.level = calculateLevel(minutes);
        if (date != null) {
            this.tooltip = formatTooltip(date, minutes);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
