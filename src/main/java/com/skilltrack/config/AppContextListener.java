package com.skilltrack.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("SkillTrack application starting up. Initializing database schema...");
        try (Connection conn = DBConnection.getConnection()) {
            initSchema(conn);
            seedDemoDataIfEmpty(conn);
            LOGGER.info("Database initialized and verified successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database initialization note: " + e.getMessage() + ". Tables will be accessed once DB is connected.", e);
        }
    }

    private void initSchema(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            // Create skills table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS skills (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    category VARCHAR(100) DEFAULT 'General',
                    color_hex VARCHAR(20) NOT NULL DEFAULT '#00875A',
                    weekly_goal_hours DOUBLE NOT NULL DEFAULT 5.0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """);

            // Create practice_sessions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS practice_sessions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    skill_id BIGINT NOT NULL,
                    duration_minutes INT NOT NULL,
                    notes TEXT,
                    session_date DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    CONSTRAINT fk_practice_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """);
        }
    }

    private void seedDemoDataIfEmpty(Connection conn) throws Exception {
        boolean hasSkills = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM skills")) {
            if (rs.next() && rs.getInt(1) > 0) {
                hasSkills = true;
            }
        }

        if (!hasSkills) {
            LOGGER.info("Seeding initial skills matching the UI design...");

            // Insert 4 skills
            long spanishId, guitarId, typeScriptId, cookingId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO skills (name, category, color_hex, weekly_goal_hours) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                // Spanish (#00875A - emerald)
                ps.setString(1, "Spanish");
                ps.setString(2, "Language");
                ps.setString(3, "#00875A");
                ps.setDouble(4, 8.0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); spanishId = rs.getLong(1); }

                // Guitar (#7C3AED - purple)
                ps.setString(1, "Guitar");
                ps.setString(2, "Music");
                ps.setString(3, "#7C3AED");
                ps.setDouble(4, 5.0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); guitarId = rs.getLong(1); }

                // TypeScript (#2563EB - blue)
                ps.setString(1, "TypeScript");
                ps.setString(2, "Programming");
                ps.setString(3, "#2563EB");
                ps.setDouble(4, 6.0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); typeScriptId = rs.getLong(1); }

                // Cooking (#DC2626 - coral red)
                ps.setString(1, "Cooking");
                ps.setString(2, "Culinary");
                ps.setString(3, "#DC2626");
                ps.setDouble(4, 4.0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); cookingId = rs.getLong(1); }
            }

            LOGGER.info("Seeding practice sessions matching Jordan's dashboard...");
            LocalDate today = LocalDate.now();

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO practice_sessions (skill_id, duration_minutes, notes, session_date) VALUES (?, ?, ?, ?)")) {

                // Today's sessions (3 skills practiced today)
                ps.setLong(1, spanishId);
                ps.setInt(2, 45);
                ps.setString(3, "Practiced past tense conjugations. Struggled with irregular verbs.");
                ps.setDate(4, java.sql.Date.valueOf(today));
                ps.addBatch();

                ps.setLong(1, guitarId);
                ps.setInt(2, 40);
                ps.setString(3, "Practiced rhythm fingerpicking and barre chords.");
                ps.setDate(4, java.sql.Date.valueOf(today));
                ps.addBatch();

                ps.setLong(1, typeScriptId);
                ps.setInt(2, 60);
                ps.setString(3, "Explored generics, mapped types, and conditional return types.");
                ps.setDate(4, java.sql.Date.valueOf(today));
                ps.addBatch();

                // Yesterday sessions
                ps.setLong(1, spanishId);
                ps.setInt(2, 45);
                ps.setString(3, "Listening exercises with native speaker podcast.");
                ps.setDate(4, java.sql.Date.valueOf(today.minusDays(1)));
                ps.addBatch();

                ps.setLong(1, cookingId);
                ps.setInt(2, 50);
                ps.setString(3, "Prepared homemade fresh tagliatelle with garlic herb butter.");
                ps.setDate(4, java.sql.Date.valueOf(today.minusDays(1)));
                ps.addBatch();

                // Generate rich historical sessions over the last 18 weeks (126 days)
                // to populate the heatmap grid realistically matching the screenshot!
                Random random = new Random(42);
                long[] allSkillIds = new long[]{spanishId, guitarId, typeScriptId, cookingId};

                for (int i = 2; i <= 126; i++) {
                    LocalDate d = today.minusDays(i);
                    // Higher density in recent weeks (matching the screenshot's right-side density)
                    double chanceOfPractice = i < 35 ? 0.85 : (i < 70 ? 0.60 : 0.40);
                    if (random.nextDouble() < chanceOfPractice) {
                        int numSessions = (i < 30 && random.nextDouble() < 0.4) ? 2 : 1;
                        for (int s = 0; s < numSessions; s++) {
                            long sid = allSkillIds[random.nextInt(allSkillIds.length)];
                            int mins = (random.nextInt(4) + 1) * 30; // 30, 60, 90, 120
                            ps.setLong(1, sid);
                            ps.setInt(2, mins);
                            ps.setString(3, "Regular practice session");
                            ps.setDate(4, java.sql.Date.valueOf(d));
                            ps.addBatch();
                        }
                    }
                }

                ps.executeBatch();
            }
            LOGGER.info("Demo data seeding completed.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("SkillTrack shutting down. Releasing database connection pool...");
        DBConnection.shutdown();
    }
}
