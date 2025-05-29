package com.example.apkpencatatankeuangan.Managers;

import com.example.apkpencatatankeuangan.Data.Catatan;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatatanManager {

    public CatatanManager() {
        buatTabelJikaBelumAda();
    }

    private void buatTabelJikaBelumAda() {
        String sql = "CREATE TABLE IF NOT EXISTS transaksi(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tanggal DATE NOT NULL, " +
                "jenis_transaksi TEXT NOT NULL," +
                "jumlah REAL NOT NULL," +
                "kategori TEXT NOT NULL)";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    public boolean tambahTransaksi(Catatan catatan) {
        String sql = "INSERT INTO transaksi (tanggal, jenis_transaksi, jumlah, kategori) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, catatan.getTanggal());
            psmt.setString(2, catatan.getJenis_Transaksi());
            psmt.setDouble(3, Double.parseDouble(catatan.getJumlah()));
            psmt.setString(4, catatan.getKategori());
            psmt.executeUpdate();
            System.out.println("Transaksi berhasil ditambahkan");
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menambahkan transaksi: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Jumlah harus berupa angka: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Catatan> getAllCatatan() {
        ArrayList<Catatan> listCatatan = new ArrayList<>();
        String sql = "SELECT * FROM transaksi";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listCatatan.add(new Catatan(
                        rs.getInt("id"),
                        rs.getString("tanggal"),
                        rs.getString("jenis_transaksi"),
                        String.valueOf(rs.getDouble("jumlah")),
                        rs.getString("kategori")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal membaca transaksi: " + e.getMessage());
        }
        return listCatatan;
    }

    public boolean updateCatatan(Catatan catatan) {
        String sql = "UPDATE transaksi SET tanggal = ?, jenis_transaksi = ?, jumlah = ?, kategori = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, catatan.getTanggal());
            pstmt.setString(2, catatan.getJenis_Transaksi());
            pstmt.setDouble(3, Double.parseDouble(catatan.getJumlah()));
            pstmt.setString(4, catatan.getKategori());
            pstmt.setInt(5, catatan.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengupdate transaksi: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCatatan(String id) {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(id));
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus transaksi: " + e.getMessage());
        }
        return false;
    }

    public Map<String, Double> getTotalPerKategori() {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT kategori, SUM(jumlah) as total FROM transaksi GROUP BY kategori";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.put(rs.getString("kategori"), rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil data kategori: " + e.getMessage());
        }

        return data;
    }

    public List<Catatan> getCatatanByDateRange(String fromDate, String toDate) {
        List<Catatan> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromDate);
            pstmt.setString(2, toDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Catatan(
                        rs.getInt("id"),
                        rs.getString("tanggal"),
                        rs.getString("jenis_transaksi"),
                        String.valueOf(rs.getDouble("jumlah")),
                        rs.getString("kategori")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data transaksi per tanggal: " + e.getMessage());
        }

        return list;
    }

    public List<Catatan> getCatatanByFilter(String dari, String sampai, String kategori) {
        List<Catatan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transaksi WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (dari != null && !dari.isEmpty()) {
            sql.append(" AND tanggal >= ?");
            params.add(dari);
        }
        if (sampai != null && !sampai.isEmpty()) {
            sql.append(" AND tanggal <= ?");
            params.add(sampai);
        }
        if (kategori != null && !kategori.isEmpty()) {
            sql.append(" AND kategori LIKE ?");
            params.add("%" + kategori + "%");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Catatan(
                        rs.getInt("id"),
                        rs.getString("tanggal"),
                        rs.getString("jenis_transaksi"),
                        String.valueOf(rs.getDouble("jumlah")),
                        rs.getString("kategori")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
