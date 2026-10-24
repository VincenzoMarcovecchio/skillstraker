-- SkillTrack MySQL Initialization Script
-- Compatible with MySQL 8.x / MariaDB / Render MySQL

CREATE DATABASE IF NOT EXISTS skilltrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE skilltrack;

-- Drop tables if needed (for fresh restart)
-- DROP TABLE IF EXISTS practice_sessions;
-- DROP TABLE IF EXISTS skills;

-- Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100) DEFAULT 'General',
    color_hex VARCHAR(20) NOT NULL DEFAULT '#00875A',
    weekly_goal_hours DOUBLE NOT NULL DEFAULT 5.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Practice Sessions Table
CREATE TABLE IF NOT EXISTS practice_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL,
    duration_minutes INT NOT NULL,
    notes TEXT,
    session_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initial Seed Data matching the screenshot
INSERT INTO skills (name, category, color_hex, weekly_goal_hours) VALUES
('Spanish', 'Language', '#00875A', 8.0),
('Guitar', 'Music', '#7C3AED', 5.0),
('TypeScript', 'Programming', '#2563EB', 6.0),
('Cooking', 'Culinary', '#DC2626', 4.0)
ON DUPLICATE KEY UPDATE name=name;

-- Sample Sessions
INSERT INTO practice_sessions (skill_id, duration_minutes, notes, session_date) VALUES
(1, 45, 'Practiced past tense conjugations. Struggled with irregular verbs.', CURDATE()),
(2, 60, 'Practiced major scale variations and arpeggios.', CURDATE() - INTERVAL 1 DAY),
(3, 90, 'Built utility types and tested generic constraints.', CURDATE() - INTERVAL 2 DAY),
(4, 50, 'Made handmade sourdough loaf and focaccia.', CURDATE() - INTERVAL 3 DAY),
(1, 55, 'Listened to Spanish podcast and took vocabulary notes.', CURDATE() - INTERVAL 4 DAY),
(1, 45, 'Spanish conversation partner session on Zoom.', CURDATE() - INTERVAL 6 DAY),
(2, 45, 'Acoustic fingerpicking technique exercise.', CURDATE() - INTERVAL 8 DAY);
