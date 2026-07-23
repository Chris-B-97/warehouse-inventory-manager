package com.chrisb97.warehouse.config;

import com.chrisb97.warehouse.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private final DatabaseConfig config;

    public ConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(config.url(), config.user(), config.password());
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to connect to MySQL.", exception);
        }
    }
}
