package com.skilltrack.dao;

import com.skilltrack.config.DBConnection;
import com.skilltrack.dto.SkillStat;
import com.skilltrack.model.Skill;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillDAO {

    public Long create(Skill skill) throws SQLException {
        String sql = "INSERT INTO skills (name, category, color_hex, weekly_goal_hours) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, skill.getName());
            ps.setString(2, skill.getCategory());
            ps.setString(3, skill.getColorHex());
            ps.setDouble(4, skill.getWeeklyGoalHours());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    skill.setId(id);
                    return id;
                }
            }
        }
        return null;
    }

    public Optional<Skill> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM skills WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Skill> findAll() throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToSkill(rs));
            }
        }
        return list;
    }

    public boolean update(Skill skill) throws SQLException {
        String sql = "UPDATE skills SET name = ?, category = ?, color_hex = ?, weekly_goal_hours = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, skill.getName());
            ps.setString(2, skill.getCategory());
            ps.setString(3, skill.getColorHex());
            ps.setDouble(4, skill.getWeeklyGoalHours());
            ps.setLong(5, skill.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM skills WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Calculates all stats for a skill (hours, streak, percentage, last session)
    public SkillStat getSkillStat(Skill skill) throws SQLException {
        SkillStat stat = new SkillStat();
        stat.setId(skill.getId());
        stat.setName(skill.getName());
        stat.setColorHex(skill.getColorHex());
        stat.setWeeklyGoalHours(skill.getWeeklyGoalHours());

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Total hours
            String totalSql = "SELECT COALESCE(SUM(duration_minutes), 0) FROM practice_sessions WHERE skill_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(totalSql)) {
                ps.setLong(1, skill.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int totalMins = rs.getInt(1);
                        double hours = totalMins / 60.0;
                        stat.setTotalHours(hours);
                        stat.setTotalHoursFormatted(formatHours(hours));
                    }
                }
            }

            // 2. Weekly goal progress
            String weekSql = "SELECT COALESCE(SUM(duration_minutes), 0) FROM practice_sessions WHERE skill_id = ? AND session_date >= ?";
            try (PreparedStatement ps = conn.prepareStatement(weekSql)) {
                ps.setLong(1, skill.getId());
                ps.setDate(2, java.sql.Date.valueOf(startOfWeek));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int weekMins = rs.getInt(1);
                        double weekHours = weekMins / 60.0;
                        int pct = 0;
                        if (skill.getWeeklyGoalHours() > 0) {
                            pct = (int) Math.round((weekHours / skill.getWeeklyGoalHours()) * 100);
                        }
                        stat.setWeeklyPercentage(pct);
                    }
                }
            }

            // 3. Day streak
            stat.setDayStreak(calculateDayStreak(conn, skill.getId()));

            // 4. Last session text
            String lastSql = "SELECT duration_minutes, session_date FROM practice_sessions WHERE skill_id = ? ORDER BY session_date DESC, id DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(lastSql)) {
                ps.setLong(1, skill.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int dur = rs.getInt("duration_minutes");
                        LocalDate sDate = rs.getDate("session_date").toLocalDate();
                        String dayLabel;
                        if (sDate.isEqual(today)) {
                            dayLabel = "Today";
                        } else if (sDate.isEqual(today.minusDays(1))) {
                            dayLabel = "Yesterday";
                        } else {
                            dayLabel = sDate.getMonth().name().substring(0, 1) + sDate.getMonth().name().substring(1, 3).toLowerCase() + " " + sDate.getDayOfMonth();
                        }
                        stat.setLastSessionFormatted(dayLabel + " — " + dur + " min");
                    } else {
                        stat.setLastSessionFormatted("No sessions yet");
                    }
                }
            }
        }
        return stat;
    }

    private int calculateDayStreak(Connection conn, Long skillId) throws SQLException {
        String sql = "SELECT DISTINCT session_date FROM practice_sessions WHERE skill_id = ? ORDER BY session_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, skillId);
            try (ResultSet rs = ps.executeQuery()) {
                LocalDate expected = LocalDate.now();
                int streak = 0;
                boolean first = true;

                while (rs.next()) {
                    LocalDate actual = rs.getDate("session_date").toLocalDate();
                    if (first) {
                        first = false;
                        if (actual.isEqual(expected)) {
                            streak++;
                            expected = expected.minusDays(1);
                        } else if (actual.isEqual(expected.minusDays(1))) {
                            // Practiced yesterday, streak is still active
                            streak++;
                            expected = expected.minusDays(2);
                        } else {
                            // Streak broken
                            break;
                        }
                    } else {
                        if (actual.isEqual(expected)) {
                            streak++;
                            expected = expected.minusDays(1);
                        } else {
                            break;
                        }
                    }
                }
                return streak;
            }
        }
    }

    private String formatHours(double hours) {
        if (hours == (long) hours) {
            return String.format("%dh", (long) hours);
        } else {
            return String.format("%.1fh", hours);
        }
    }

    private Skill mapRowToSkill(ResultSet rs) throws SQLException {
        Skill s = new Skill();
        s.setId(rs.getLong("id"));
        s.setName(rs.getString("name"));
        s.setCategory(rs.getString("category"));
        s.setColorHex(rs.getString("color_hex"));
        s.setWeeklyGoalHours(rs.getDouble("weekly_goal_hours"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) s.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) s.setUpdatedAt(ut.toLocalDateTime());
        return s;
    }
}
