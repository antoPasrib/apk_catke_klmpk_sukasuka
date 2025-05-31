package com.example.apkpencatatankeuangan.Managers;

import com.example.apkpencatatankeuangan.Data.Catatan;
import com.example.apkpencatatankeuangan.controller.SessionManager;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatatanManager {

    public CatatanManager() {
        buatTabelJikaBelumAda();
    }

    private void buatTabelJikaBelumAda() {
        String sql = "CREATE TABLE IF NOT EXISTS transaksi (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "tanggal DATE NOT NULL, " +
                "jenis_transaksi TEXT NOT NULL, " +
                "jumlah REAL NOT NULL, " +
                "kategori TEXT NOT NULL)";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    public boolean tambahTransaksi(Catatan catatan) {
        String sql = "INSERT INTO transaksi (username, tanggal, jenis_transaksi, jumlah, kategori) VALUES (?, ?, ?, ?, ?)";
        String username = SessionManager.getInstance().getUsername();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, username);
            psmt.setString(2, catatan.getTanggal());
            psmt.setString(3, catatan.getJenis_Transaksi());
            psmt.setDouble(4, Double.parseDouble(catatan.getJumlah()));
            psmt.setString(5, catatan.getKategori());
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
        String sql = "SELECT * FROM transaksi WHERE username = ?";
        String username = SessionManager.getInstance().getUsername();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

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

    public Map<String, Double> getTotalPerKategoriByUsername(String username) {
        Map<String, Double> result = new HashMap<>();

        // Contoh query SQL
        String sql = "SELECT kategori, SUM(jumlah) as total " +
                "FROM transaksi " +
                "WHERE username = ? " +
                "GROUP BY kategori";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString("kategori");
                double total = rs.getDouble("total");
                result.put(kategori, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    // Hapus method getCatatanByDateRange() dan gunakan hanya satu method berikut:

    public List<Catatan> getTransaksiByTanggal(LocalDate tanggalFilter) {
        List<Catatan> result = new ArrayList<>();
        String username = SessionManager.getInstance().getUsername();

        // Query untuk filter berdasarkan tanggal spesifik
        String sql = "SELECT * FROM transaksi " +
                "WHERE username = ? " +
                "AND date(tanggal) = date(?) " +
                "ORDER BY tanggal ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Format parameter query sebagai yyyy-MM-dd
            String filterDate = tanggalFilter.format(DateTimeFormatter.ISO_DATE);

            stmt.setString(1, username);
            stmt.setString(2, filterDate);

            System.out.println("Executing query: " + stmt.toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Format output sebagai dd-MM-yyyy
                LocalDate dbDate = LocalDate.parse(rs.getString("tanggal"));
                String formattedDate = dbDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                result.add(new Catatan(
                        rs.getInt("id"),
                        formattedDate,
                        rs.getString("jenis_transaksi"),
                        rs.getString("jumlah"),
                        rs.getString("kategori")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            System.err.println("Date Format Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Data found for date " + tanggalFilter + ": " + result.size());
        if (!result.isEmpty()) {
            System.out.println("Contoh data pertama:");
            Catatan first = result.get(0);
            System.out.println("ID: " + first.getId());
            System.out.println("Tanggal: " + first.getTanggal());
            System.out.println("Jenis: " + first.getJenis_Transaksi());
        }
        return result;
    }
}

