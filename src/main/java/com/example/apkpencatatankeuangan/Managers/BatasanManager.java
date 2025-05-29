package com.example.apkpencatatankeuangan.Managers;

import java.sql.*;

public class BatasanManager {

    private static double batasPengeluaran;

    public static double getBatasPengeluaran() {
        return batasPengeluaran;
    }

    public static void setBatasPengeluaran(double batas) {
        batasPengeluaran = batas;
    }

    private static void buatTabelJikaBelumAda() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS batasan (nilai DOUBLE)";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    public static void resetBatasPengeluaran() {
        batasPengeluaran = 0;
    }
    public static void simpanBatasPengeluaranKeDB() throws SQLException {
        buatTabelJikaBelumAda();
        try (Connection conn = DBConnection.getConnection()) {
            // Cek apakah sudah ada baris
            String cekSql = "SELECT COUNT(*) FROM batasan";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(cekSql)) {

                rs.next();
                int count = rs.getInt(1);

                if (count == 0) {
                    String insertSql = "INSERT INTO batasan (nilai) VALUES (?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setDouble(1, batasPengeluaran);
                        insertStmt.executeUpdate();
                    }
                } else {
                    String updateSql = "UPDATE batasan SET nilai = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, batasPengeluaran);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

    public static void loadBatasPengeluaranDariDB() throws SQLException {
        buatTabelJikaBelumAda();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT nilai FROM batasan LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    batasPengeluaran = rs.getDouble("nilai");
                }
            }
        }
    }

    public static boolean resetBatasPengeluaranDiDatabase() {
        String sql = "DELETE FROM batasan"; // atau sesuaikan dengan tabel dan kolommu
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affectedRows = stmt.executeUpdate();
            System.out.println("Rows affected: " + affectedRows);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

//    public static void resetBatasPengeluaran() {
//        batasPengeluaran = 0;
//    }
    }
}
