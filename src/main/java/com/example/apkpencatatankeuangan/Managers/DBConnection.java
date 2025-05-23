package com.example.apkpencatatankeuangan.Managers;

import com.example.apkpencatatankeuangan.Data.Catatan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:keuangan.db";
    private static Connection connection;

    private DBConnection() {
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


