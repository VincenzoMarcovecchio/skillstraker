package com.skilltrack.dao;

import com.skilltrack.config.DBConnection;
import com.skilltrack.dto.HeatmapDay;
import com.skilltrack.model.PracticeSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SessionDAO {

    public Long create(PracticeSession session) throws SQLException {
        String sql = "INSERT INTO practice_sessions (skill_id, duration_minutes, notes, session_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, session.getSkillId());
            ps.setInt(2, session.getDurationMinutes());
            ps.setString(3, session.getNotes());
            ps.setDate(4, java.sql.Date.valueOf(session.getSessionDate()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    session.setId(id);
                    return id;
                }
            }
        }
        return null;
    }

    public Optional<PracticeSession> findById(Long id) throws SQLException {
        String sql = """
            SELECT s.*, sk.name AS skill_name, sk.color_hex AS skill_color
            FROM practice_sessions s
            JOIN skills sk ON s.skill_id = sk.id
            WHERE s.id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSession(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<PracticeSession> findAll() throws SQLException {
        List<PracticeSession> list = new ArrayList<>();
        String sql = """
            SELECT s.*, sk.name AS skill_name, sk.color_hex AS skill_color
            FROM practice_sessions s
            JOIN skills sk ON s.skill_id = sk.id
            ORDER BY s.session_date DESC, s.id DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToSession(rs));
            }
        }
        return list;
    }

    public List<PracticeSession> findRecent(int limit) throws SQLException {
        List<PracticeSession> list = new ArrayList<>();
        String sql = """
            SELECT s.*, sk.name AS skill_name, sk.color_hex AS skill_color
            FROM practice_sessions s
            JOIN skills sk ON s.skill_id = sk.id
            ORDER BY s.session_date DESC, s.id DESC
            LIMIT ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSession(rs));
                }
            }
        }
        return list;
    }

    public List<PracticeSession> findBySkill(Long skillId) throws SQLException {
        List<PracticeSession> list = new ArrayList<>();
        String sql = """
            SELECT s.*, sk.name AS skill_name, sk.color_hex AS skill_color
            FROM practice_sessions s
            JOIN skills sk ON s.skill_id = sk.id
            WHERE s.skill_id = ?
            ORDER BY s.session_date DESC, s.id DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, skillId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSession(rs));
                }
            }
        }
        return list;
    }

    public boolean update(PracticeSession session) throws SQLException {
        String sql = "UPDATE practice_sessions SET skill_id = ?, duration_minutes = ?, notes = ?, session_date = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, session.getSkillId());
            ps.setInt(2, session.getDurationMinutes());
            ps.setString(3, session.getNotes());
            ps.setDate(4, java.sql.Date.valueOf(session.getSessionDate()));
            ps.setLong(5, session.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM practice_sessions WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countDistinctSkillsPracticedOnDate(LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT skill_id) FROM practice_sessions WHERE session_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Aggregates minutes practiced per day over the last 18 weeks (126 days)
    public Map<LocalDate, Integer> getDailyActivityMap(LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<LocalDate, Integer> activity = new HashMap<>();
        String sql = """
            SELECT session_date, SUM(duration_minutes) AS total_minutes
            FROM practice_sessions
            WHERE session_date BETWEEN ? AND ?
            GROUP BY session_date
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate d = rs.getDate("session_date").toLocalDate();
                    int mins = rs.getInt("total_minutes");
                    activity.put(d, mins);
                }
            }
        }
        return activity;
    }

    private PracticeSession mapRowToSession(ResultSet rs) throws SQLException {
        PracticeSession s = new PracticeSession();
        s.setId(rs.getLong("id"));
        s.setSkillId(rs.getLong("skill_id"));
        s.setSkillName(rs.getString("skill_name"));
        s.setSkillColorHex(rs.getString("skill_color"));
        s.setDurationMinutes(rs.getInt("duration_minutes"));
        s.setNotes(rs.getString("notes"));
        Date sDate = rs.getDate("session_date");
        if (sDate != null) s.setSessionDate(sDate.toLocalDate());
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) s.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) s.setUpdatedAt(ut.toLocalDateTime());
        return s;
    }
}
