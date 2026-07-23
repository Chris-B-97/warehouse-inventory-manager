package com.chrisb97.warehouse.config;

import com.chrisb97.warehouse.exception.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record DatabaseConfig(String url, String user, String password) {
    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/database.properties")) {
            if (input == null) {
                throw new ConfigurationException("Missing src/main/resources/database.properties file.");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new ConfigurationException("Could not read database.properties.", exception);
        }

        String url = require(properties, "db.url");
        String user = require(properties, "db.user");
        String password = properties.getProperty("db.password", "");
        return new DatabaseConfig(url, user, password);
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new ConfigurationException("Missing database property: " + key);
        }
        return value.trim();
    }
}
