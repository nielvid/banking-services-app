package com.nielvid.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = loadProperties();

    public static final String DATABASE_URL = properties.getProperty("database.url");
    public static final String USERNAME = properties.getProperty("database.username");
    public static final String PASSWORD = properties.getProperty("database.password");
    private static Properties loadProperties() {
        String profile = System.getenv("APP_PROFILE");
        if (profile == null) {
            profile = "default";
        }

        String fileName = "default".equals(profile) ?  "application.properties":"application-development.properties";

        Properties props = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Properties file not found: " + fileName);
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fileName, e);
        }
        return props;
    }
}
