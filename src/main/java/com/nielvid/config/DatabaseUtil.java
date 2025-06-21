package com.nielvid.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseUtil {
    // public static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/bank_app_demo";
    public static final String USERNAME = "gchinonyerem";
    public static final String PASSWORD = "gchinonyerem";
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    // private static Connection connection;


    private static final String exceptionFormat = "exception in %s, message: %s, code: %s";

     
    public static Connection getConnection() {
        Connection connection = null;
        if (connection == null) {
            synchronized (DatabaseUtil.class) {
                if (connection == null) {
                    try {
                        connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
                    } catch (SQLException e) {
                        handleSqlException("DatabaseUtils.getConnection", e, LOGGER);
                    }
                }
            }
            
        }
        return connection;
    }
       
    

    public static void handleSqlException(String method, SQLException e, Logger log) {
        log.warning(String.format(exceptionFormat, method, e.getMessage(), e.getErrorCode()));
        throw new RuntimeException(e);
    }

    

}
