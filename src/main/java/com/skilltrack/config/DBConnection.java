package com.skilltrack.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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

            // Check all common URL variables
            String rawUrl = System.getenv("MYSQL_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("DATABASE_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("DB_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("AIVEN_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("JAWSDB_URL");
            if (rawUrl == null || rawUrl.isBlank()) rawUrl = System.getenv("SPRING_DATASOURCE_URL");

            // Check discrete host/user/pass variables
            String host = System.getenv("MYSQLHOST");
            if (host == null || host.isBlank()) host = System.getenv("DB_HOST");

            String port = System.getenv("MYSQLPORT");
            if (port == null || port.isBlank()) port = System.getenv("DB_PORT");
            if (port == null || port.isBlank()) port = "3306";

            String dbName = System.getenv("MYSQLDATABASE");
            if (dbName == null || dbName.isBlank()) dbName = System.getenv("DB_NAME");

            String username = System.getenv("MYSQLUSER");
            if (username == null || username.isBlank()) username = System.getenv("MYSQL_USER");
            if (username == null || username.isBlank()) username = System.getenv("DB_USER");

            String password = System.getenv("MYSQLPASSWORD");
            if (password == null) password = System.getenv("MYSQL_PASSWORD");
            if (password == null) password = System.getenv("DB_PASSWORD");

            String jdbcUrl;

            if (rawUrl != null && !rawUrl.isBlank()) {
                rawUrl = rawUrl.trim();
                LOGGER.info("Detected database connection URL from environment variables.");

                if (rawUrl.startsWith("mysql://")) {
                    // Robust parser for mysql://user:pass@host:port/dbname?ssl-mode=...
                    String withoutPrefix = rawUrl.substring("mysql://".length());
                    
                    int atIndex = withoutPrefix.lastIndexOf('@');
                    String hostAndPath;
                    if (atIndex != -1) {
                        String creds = withoutPrefix.substring(0, atIndex);
                        hostAndPath = withoutPrefix.substring(atIndex + 1);
                        int colonIdx = creds.indexOf(':');
                        if (colonIdx != -1) {
                            if (username == null) username = creds.substring(0, colonIdx);
                            if (password == null) password = creds.substring(colonIdx + 1);
                        } else {
                            if (username == null) username = creds;
                        }
                    } else {
                        hostAndPath = withoutPrefix;
                    }

                    // Separate host:port and path/query
                    String hostPort = hostAndPath;
                    String pathQuery = "defaultdb";
                    int slashIdx = hostAndPath.indexOf('/');
                    if (slashIdx != -1) {
                        hostPort = hostAndPath.substring(0, slashIdx);
                        pathQuery = hostAndPath.substring(slashIdx + 1);
                        if (pathQuery.isBlank()) pathQuery = "defaultdb";
                    }

                    // Extract database name (before any ?)
                    String database = pathQuery;
                    if (database.contains("?")) {
                        database = database.substring(0, database.indexOf('?'));
                    }
                    if (database.isBlank()) database = "defaultdb";

                    boolean isCloud = rawUrl.contains("aivencloud") || rawUrl.contains("render") || rawUrl.contains("ssl-mode");
                    String ssl = isCloud ? "useSSL=true&requireSSL=true" : "useSSL=false&allowPublicKeyRetrieval=true";

                    jdbcUrl = "jdbc:mysql://" + hostPort + "/" + database
                            + "?" + ssl + "&serverTimezone=UTC&characterEncoding=UTF-8";

                } else if (rawUrl.startsWith("jdbc:mysql://")) {
                    jdbcUrl = rawUrl;
                } else {
                    jdbcUrl = "jdbc:mysql://" + rawUrl;
                }

            } else if (host != null && !host.isBlank()) {
                String database = (dbName != null && !dbName.isBlank()) ? dbName : "defaultdb";
                boolean isCloud = host.contains("aivencloud") || host.contains("render");
                String ssl = isCloud ? "useSSL=true&requireSSL=true" : "useSSL=false&allowPublicKeyRetrieval=true";
                jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                        + "?" + ssl + "&serverTimezone=UTC&characterEncoding=UTF-8";
            } else {
                // Warning: No cloud database URL found, defaulting to localhost
                LOGGER.warning("No MYSQL_URL or database environment variables found! Defaulting to localhost:3306");
                jdbcUrl = "jdbc:mysql://localhost:3306/skilltrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                if (username == null) username = "root";
                if (password == null) password = "";
            }

            LOGGER.info("Connecting to JDBC URL: " + jdbcUrl.replaceAll("password=[^&]*", "password=***"));

            config.setJdbcUrl(jdbcUrl);
            if (username != null) config.setUsername(username);
            if (password != null) config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000); // 20s timeout
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
            String hint = "";
            if (System.getenv("MYSQL_URL") == null && System.getenv("DATABASE_URL") == null) {
                hint = " [HINT: MYSQL_URL environment variable is NOT set in Render! Please add MYSQL_URL in your Render Web Service Environment settings.]";
            }
            throw new SQLException("DataSource could not be initialized: " + (lastInitError != null ? lastInitError : "Unknown error") + hint);
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
