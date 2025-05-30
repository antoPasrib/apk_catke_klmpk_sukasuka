package com.example.apkpencatatankeuangan.Managers;

import com.example.apkpencatatankeuangan.Data.Catatan;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class DBConnection {
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DB_URL = "jdbc:sqlite:keuangan.db?busy_timeout=5000";
    private static Connection connection;

    private DBConnection() {
    }




    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL,USER,PASSWORD);
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


