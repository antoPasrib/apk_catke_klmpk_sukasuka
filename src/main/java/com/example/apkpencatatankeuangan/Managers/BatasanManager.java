package com.example.apkpencatatankeuangan.Managers;

import com.example.apkpencatatankeuangan.controller.SessionManager;

import java.sql.*;

public class BatasanManager {

    private static double batasPengeluaran ;

    public static double getBatasPengeluaran() {
        return batasPengeluaran;
    }

    public static void setBatasPengeluaran(double batas) {
        batasPengeluaran = batas;
    }

    private static void buatTabelJikaBelumAda() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS batasan (" +
                "username TEXT NOT NULL, " +
                "nilai DOUBLE, " +
                "PRIMARY KEY (username))";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void simpanBatasPengeluaranKeDB() throws SQLException {
        buatTabelJikaBelumAda();
        String username = SessionManager.getInstance().getUsername();

        try (Connection conn = DBConnection.getConnection()) {
            String cekSql = "SELECT COUNT(*) FROM batasan WHERE username = ?";
            try (PreparedStatement cekStmt = conn.prepareStatement(cekSql)) {
                cekStmt.setString(1, username);
                ResultSet rs = cekStmt.executeQuery();

                rs.next();
                int count = rs.getInt(1);

                if (count == 0) {
                    String insertSql = "INSERT INTO batasan (username, nilai) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, username);
                        insertStmt.setDouble(2, batasPengeluaran);
                        insertStmt.executeUpdate();
                    }
                } else {
                    String updateSql = "UPDATE batasan SET nilai = ? WHERE username = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, batasPengeluaran);
                        updateStmt.setString(2, username);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }
    public static void muatBatasPengeluaranDariDB() {
        String username = SessionManager.getInstance().getUsername();

        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username tidak ditemukan atau kosong.");
            batasPengeluaran = 0; // Atur batas default
            return;
        }

        String sql = "SELECT nilai FROM batasan WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    batasPengeluaran = rs.getDouble("nilai");
                } else {
                    batasPengeluaran = 0; // Default jika data tidak ditemukan
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat memuat batas pengeluaran dari database: " + e.getMessage());
            batasPengeluaran = 0; // Set default jika terjadi error
        }
    }


    public static boolean resetBatasPengeluaranDiDatabase() {
        String sql = "DELETE FROM batasan WHERE username = ?";
        String username = SessionManager.getInstance().getUsername();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            batasPengeluaran = 0; // Reset nilai di memori
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
