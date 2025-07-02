package com.nielvid.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseUtil {
    // public static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";

    public static final String DATABASE_URL =  AppConfig.DATABASE_URL;
    public static final String USERNAME =  AppConfig.USERNAME;
    public static final String PASSWORD =  AppConfig.PASSWORD;


//    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static final HikariDataSource dataSource = createDataSource();
    private static final String exceptionFormat = "exception in %s, message: %s, code: %s";

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DATABASE_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        config.setLeakDetectionThreshold(0);
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "30000");
        return new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
       
    

    public static void handleSqlException(String method, SQLException e, Logger log) {
        log.warning(String.format(exceptionFormat, method, e.getMessage(), e.getErrorCode()));
        throw new RuntimeException(e);
    }

    

}
