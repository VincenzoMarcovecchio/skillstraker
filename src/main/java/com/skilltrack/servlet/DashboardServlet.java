package com.skilltrack.servlet;

import com.skilltrack.dao.SessionDAO;
import com.skilltrack.dao.SkillDAO;
import com.skilltrack.dto.DashboardStats;
import com.skilltrack.dto.HeatmapDay;
import com.skilltrack.dto.SkillStat;
import com.skilltrack.model.PracticeSession;
import com.skilltrack.model.Skill;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private final SkillDAO skillDAO = new SkillDAO();
    private final SessionDAO sessionDAO = new SessionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DashboardStats stats = new DashboardStats();
        LocalDate today = LocalDate.now();

        // Time greeting
        LocalTime now = LocalTime.now();
        String greeting = (now.isBefore(LocalTime.of(12, 0))) ? "Good morning"
                        : (now.isBefore(LocalTime.of(17, 0))) ? "Good afternoon"
                        : "Good evening";
        stats.setGreeting(greeting + ", " + stats.getUserName());

        List<Skill> allSkills = new ArrayList<>();

        try {
            // Count distinct skills practiced today
            stats.setSkillsPracticedTodayCount(sessionDAO.countDistinctSkillsPracticedOnDate(today));

            // Skills and Stats
            allSkills = skillDAO.findAll();
            List<SkillStat> skillStats = new ArrayList<>();
            for (Skill s : allSkills) {
                skillStats.add(skillDAO.getSkillStat(s));
            }

            // Sort by total hours descending to identify Most Practiced
            skillStats.sort((a, b) -> Double.compare(b.getTotalHours(), a.getTotalHours()));

            if (!skillStats.isEmpty()) {
                stats.setMostPracticedSkill(skillStats.get(0));
                List<SkillStat> mini = new ArrayList<>();
                for (int i = 1; i < Math.min(skillStats.size(), 4); i++) {
                    mini.add(skillStats.get(i));
                }
                stats.setMiniSkills(mini);
            }

            // Heatmap Data (Last 18 weeks: 18 columns, 7 rows Mon-Sun)
            LocalDate heatmapEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            LocalDate heatmapStart = heatmapEnd.minusWeeks(18).plusDays(1);
            Map<LocalDate, Integer> activityMap = sessionDAO.getDailyActivityMap(heatmapStart, heatmapEnd);

            List<List<HeatmapDay>> columns = new ArrayList<>();
            LocalDate current = heatmapStart;
            for (int w = 0; w < 18; w++) {
                List<HeatmapDay> weekCol = new ArrayList<>();
                for (int d = 0; d < 7; d++) {
                    int mins = activityMap.getOrDefault(current, 0);
                    weekCol.add(new HeatmapDay(current, mins));
                    current = current.plusDays(1);
                }
                columns.add(weekCol);
            }
            stats.setHeatmapColumns(columns);

            // Recent Sessions (limit 6 for dashboard)
            List<PracticeSession> recentSessions = sessionDAO.findRecent(6);
            stats.setRecentSessions(recentSessions);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error in DashboardServlet: " + e.getMessage(), e);
            request.setAttribute("errorMessage", "Database note: " + e.getMessage() + ". Please check MySQL service.");

            // Provide default heatmap columns so view doesn't throw
            LocalDate heatmapEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            LocalDate heatmapStart = heatmapEnd.minusWeeks(18).plusDays(1);
            List<List<HeatmapDay>> columns = new ArrayList<>();
            LocalDate current = heatmapStart;
            for (int w = 0; w < 18; w++) {
                List<HeatmapDay> weekCol = new ArrayList<>();
                for (int d = 0; d < 7; d++) {
                    weekCol.add(new HeatmapDay(current, 0));
                    current = current.plusDays(1);
                }
                columns.add(weekCol);
            }
            stats.setHeatmapColumns(columns);
        }

        // Attach to request
        request.setAttribute("stats", stats);
        request.setAttribute("skills", allSkills);
        request.setAttribute("todayDate", today.toString());

        request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
    }
}
