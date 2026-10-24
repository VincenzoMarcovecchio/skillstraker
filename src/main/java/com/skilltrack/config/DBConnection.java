package com.skilltrack.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static HikariDataSource dataSource;
    private static String lastInitError = null;

    static {
        initDataSource();
    }

    private static synchronized void initDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            HikariConfig config = new HikariConfig();

            // Check all common database URL variables
            String rawUrl = System.getenv("MYSQL_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("DATABASE_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("DB_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("AIVEN_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("SPRING_DATASOURCE_URL");

            String username = System.getenv("MYSQLUSER");
            if (username == null || username.isBlank()) username = System.getenv("MYSQL_USER");
            if (username == null || username.isBlank()) username = System.getenv("DB_USER");

            String password = System.getenv("MYSQLPASSWORD");
            if (password == null) password = System.getenv("MYSQL_PASSWORD");
            if (password == null) password = System.getenv("DB_PASSWORD");

            String jdbcUrl;

            if (rawUrl != null && !rawUrl.isBlank()) {
                String clean = rawUrl.trim();
                // Strip quotes if user wrapped in "" or ''
                if ((clean.startsWith("\"") && clean.endsWith("\"")) || (clean.startsWith("'") && clean.endsWith("'"))) {
                    clean = clean.substring(1, clean.length() - 1).trim();
                }

                // Strip leading "jdbc:" if present
                if (clean.startsWith("jdbc:")) {
                    clean = clean.substring("jdbc:".length()).trim();
                }

                // Strip leading "mysql://"
                if (clean.startsWith("mysql://")) {
                    clean = clean.substring("mysql://".length()).trim();
                }

                // Check for user:pass@
                String hostAndPath = clean;
                int atIdx = clean.lastIndexOf('@');
                if (atIdx != -1) {
                    String userPass = clean.substring(0, atIdx);
                    hostAndPath = clean.substring(atIdx + 1);

                    int colonIdx = userPass.indexOf(':');
                    if (colonIdx != -1) {
                        String rawUser = userPass.substring(0, colonIdx);
                        String rawPass = userPass.substring(colonIdx + 1);
                        try {
                            if (username == null) username = URLDecoder.decode(rawUser, StandardCharsets.UTF_8);
                            if (password == null) password = URLDecoder.decode(rawPass, StandardCharsets.UTF_8);
                        } catch (Exception e) {
                            if (username == null) username = rawUser;
                            if (password == null) password = rawPass;
                        }
                    } else {
                        if (username == null) username = userPass;
                    }
                }

                // Parse host:port and database
                String hostPort = hostAndPath;
                String database = "defaultdb";

                int slashIdx = hostAndPath.indexOf('/');
                if (slashIdx != -1) {
                    hostPort = hostAndPath.substring(0, slashIdx);
                    String afterSlash = hostAndPath.substring(slashIdx + 1);
                    if (afterSlash.contains("?")) {
                        database = afterSlash.substring(0, afterSlash.indexOf('?'));
                    } else {
                        database = afterSlash;
                    }
                }

                if (database == null || database.isBlank()) {
                    database = "defaultdb";
                }

                boolean isCloud = rawUrl.contains("aivencloud") || rawUrl.contains("render") || rawUrl.contains("ssl-mode");
                String sslParams = isCloud
                        ? "useSSL=true&requireSSL=true&allowPublicKeyRetrieval=true"
                        : "useSSL=false&allowPublicKeyRetrieval=true";

                // Standard clean JDBC URL format expected by MySQL Connector/J
                jdbcUrl = "jdbc:mysql://" + hostPort + "/" + database
                        + "?" + sslParams + "&serverTimezone=UTC&characterEncoding=UTF-8";

            } else {
                // Local fallback (e.g. XAMPP on localhost)
                String host = System.getenv("MYSQLHOST");
                if (host == null || host.isBlank()) host = "localhost";
                String port = System.getenv("MYSQLPORT");
                if (port == null || port.isBlank()) port = "3306";
                String db = System.getenv("MYSQLDATABASE");
                if (db == null || db.isBlank()) db = "skilltrack";

                if (username == null) username = "root";
                if (password == null) password = "";

                jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
                        + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
            }

            LOGGER.info("Connecting to MySQL at: " + jdbcUrl);

            config.setJdbcUrl(jdbcUrl);
            if (username != null && !username.isBlank()) config.setUsername(username);
            if (password != null) config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            lastInitError = null;
            LOGGER.info("HikariCP DataSource successfully initialized.");

        } catch (Exception e) {
            lastInitError = e.getClass().getSimpleName() + ": " + e.getMessage();
            LOGGER.log(Level.SEVERE, "Failed to initialize DataSource: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        if (dataSource == null) {
            throw new SQLException("DataSource could not be initialized: " + (lastInitError != null ? lastInitError : "Unknown error"));
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("HikariCP DataSource closed.");
        }
    }
}
