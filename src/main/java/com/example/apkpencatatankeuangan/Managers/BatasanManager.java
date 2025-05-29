package com.example.apkpencatatankeuangan.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BatasanManager {

        private static double batasPengeluaran;

        public static double getBatasPengeluaran() {
            return batasPengeluaran;
        }

        public static void setBatasPengeluaran(double batas) {
            batasPengeluaran = batas;
        }

        public static void buatTabelJikaBelumAda(Connection conn) throws SQLException {
            String sql = """
            CREATE TABLE IF NOT EXISTS batasan (
                nilai DOUBLE
            )
            """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }

        public static void simpanBatasPengeluaranKeDB(Connection conn) throws SQLException {
            buatTabelJikaBelumAda(conn);

            // Cek apakah sudah ada baris
            String cekSql = "SELECT COUNT(*) FROM batasan";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(cekSql)) {
                rs.next();
                int count = rs.getInt(1);

                if (count == 0) {
                    // INSERT jika kosong
                    String insertSql = "INSERT INTO batasan (nilai) VALUES (?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setDouble(1, batasPengeluaran);
                        insertStmt.executeUpdate();
                    }
                } else {
                    // UPDATE jika sudah ada
                    String updateSql = "UPDATE batasan SET nilai = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, batasPengeluaran);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }

        public static void loadBatasPengeluaranDariDB(Connection conn) throws SQLException {
            buatTabelJikaBelumAda(conn);

            String sql = "SELECT nilai FROM batasan LIMIT 1";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    batasPengeluaran = rs.getDouble("nilai");
                }
            }
        }

    public static void resetBatasPengeluaran() {
        batasPengeluaran = 0; // Atau nilai default lainnya
    }
}

