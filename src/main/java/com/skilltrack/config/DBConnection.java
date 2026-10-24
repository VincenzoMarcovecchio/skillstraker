package com.skilltrack.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static HikariDataSource dataSource;

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

            // 1. Check for Render MySQL plugin environment variables:
            // MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD
            String mysqlHost = System.getenv("MYSQLHOST");
            String mysqlPort = System.getenv("MYSQLPORT");
            String mysqlDatabase = System.getenv("MYSQLDATABASE");
            String mysqlUser = System.getenv("MYSQLUSER");
            String mysqlPassword = System.getenv("MYSQLPASSWORD");

            // 2. Check for URL variables (MYSQL_URL, DATABASE_URL, DB_URL, SPRING_DATASOURCE_URL)
            String rawUrl = System.getenv("MYSQL_URL");
            if (rawUrl == null || rawUrl.isBlank()) {
                rawUrl = System.getenv("DATABASE_URL");
            }
            if (rawUrl == null || rawUrl.isBlank()) {
                rawUrl = System.getenv("DB_URL");
            }
            if (rawUrl == null || rawUrl.isBlank()) {
                rawUrl = System.getenv("SPRING_DATASOURCE_URL");
            }

            String jdbcUrl;
            String username = System.getenv("DB_USER");
            if (username == null || username.isBlank()) {
                username = System.getenv("MYSQL_USER");
            }
            if (username == null || username.isBlank()) {
                username = System.getenv("SPRING_DATASOURCE_USERNAME");
            }

            String password = System.getenv("DB_PASSWORD");
            if (password == null) {
                password = System.getenv("MYSQL_PASSWORD");
            }
            if (password == null) {
                password = System.getenv("SPRING_DATASOURCE_PASSWORD");
            }

            if (mysqlHost != null && !mysqlHost.isBlank() && mysqlDatabase != null) {
                // Render Managed MySQL Environment
                String port = (mysqlPort != null && !mysqlPort.isBlank()) ? mysqlPort : "3306";
                jdbcUrl = "jdbc:mysql://" + mysqlHost + ":" + port + "/" + mysqlDatabase
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                if (username == null && mysqlUser != null) username = mysqlUser;
                if (password == null && mysqlPassword != null) password = mysqlPassword;
            } else if (rawUrl != null && !rawUrl.isBlank()) {
                // Parse URI or JDBC URL
                if (rawUrl.startsWith("mysql://")) {
                    URI uri = new URI(rawUrl);
                    String host = uri.getHost();
                    int port = uri.getPort() == -1 ? 3306 : uri.getPort();
                    String path = uri.getPath();
                    if (path != null && path.startsWith("/")) path = path.substring(1);
                    
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        if (username == null) username = parts[0];
                        if (password == null) password = parts[1];
                    }
                    jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + path
                            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                } else if (rawUrl.startsWith("jdbc:mysql://")) {
                    jdbcUrl = rawUrl;
                } else {
                    jdbcUrl = "jdbc:mysql://" + rawUrl;
                }
            } else {
                // Local Default (e.g. XAMPP MySQL or local install)
                jdbcUrl = "jdbc:mysql://localhost:3306/skilltrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
            }

            if (username == null || username.isBlank()) {
                username = "root";
            }
            if (password == null) {
                password = "";
            }

            LOGGER.info("Configuring DB connection to: " + jdbcUrl.replaceAll("password=[^&]*", "password=***"));

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP DataSource initialized successfully.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DataSource: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        if (dataSource == null) {
            throw new SQLException("DataSource could not be initialized.");
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
