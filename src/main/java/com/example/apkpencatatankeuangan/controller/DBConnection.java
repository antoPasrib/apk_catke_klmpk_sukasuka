package com.example.apkpencatatankeuangan.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:keuangan.db";
    private static Connection connection;
    private void DBConnectionManager() {

    }
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}