package com.example.apkpencatatankeuangan.Managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:keuangan.db";
    private static Connection connection;

    private DBConnection() {
        // private constructor to prevent instantiation
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);

            // Atur agar SQLite menggunakan WAL mode (lebih aman terhadap locking)
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
