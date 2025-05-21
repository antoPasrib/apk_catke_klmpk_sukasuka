package com.example.apkpencatatankeuangan.Managers;

import com.example.apkpencatatankeuangan.Data.Catatan;

import java.sql.*;
import java.util.ArrayList;

public class CatatanManager {

    public CatatanManager() {
        buatTabelJikaBelumAda();
    }

    private void buatTabelJikaBelumAda() {
        String sql = "CREATE TABLE IF NOT EXISTS transaksi("+
                "id int PRIMARY KEY,"+
                "tanggal date NOT NULL, "+
                "jenis_transaksi TEXT NOT NULL,"+
                "jumlah TEXT NOT NULL,"+
                "kategori TEXT NOT NULL)";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

        }catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }finally {
            DBConnection.closeConnection();
        }
    }
    public boolean tambahTransaksi(Catatan catatan){
        String sql = "INSERT INTO transaksi (tanggal,jenis_transaksi,jumlah,kategori) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)){
            psmt.setString(1,catatan.getTanggal());
            psmt.setString(2,catatan.getJenis_Transaksi());
            psmt.setString(3,catatan.getJumlah());
            psmt.setString(4, catatan.getKategori());
            psmt.executeUpdate();
            System.out.println("Transaksi berhasil ditambah");
            return true;
        }catch (SQLException e){
            System.err.println("Gagal membuat tabel: " + e.getMessage());
            return false;
        }finally {
            DBConnection.closeConnection();
        }
    }

    public ArrayList<Catatan> getAllCatatan(){
        ArrayList<Catatan> listcatatan= new ArrayList<>();
        String sql = "SELECT * FROM transaksi";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                listcatatan.add(new Catatan(
                rs.getInt("id"),
                rs.getString("tanggal"),
                rs.getString("jenis_transaksi"),
                rs.getString("jumlah"),
                rs.getString("kategori")
                ));
            }
        }catch (SQLException e){
            System.err.println("Gagal membaca transaksi: " + e.getMessage());
        } finally {
            DBConnection.closeConnection();
        }
        return listcatatan;
    }
    //UPDATE
    public boolean updateCatatan(Catatan catatan){
        String sql = "UPDATE transaksi SET tanggal = ?, jenis_transaksi = ?,jumlah = ?,kategori = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,catatan.getTanggal());
            pstmt.setString(2, catatan.getJenis_Transaksi());
            pstmt.setString(3, catatan.getJumlah());
            pstmt.setString(4, catatan.getKategori());
            int rowAffected = pstmt.executeUpdate();
            if (rowAffected > 0){
                System.out.println("Transaksi berhasil ditambah");
                return true;
            }

        }catch (SQLException e){
            System.err.println("Gagal mengupdate transaksi: " + e.getMessage());
        }finally {
            DBConnection.closeConnection();
        }
        return false;
    }
    //DELETE
    public boolean deleteCatatan(String id){
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,id);
            int rowAffected = pstmt.executeUpdate();
            if (rowAffected > 0){
                System.out.println("Transaksi berhasil ditambah");
                return true;
            }
        }catch (SQLException e){
            System.err.println("Gagal menghapus transaksi: " + e.getMessage());
        }finally {
            DBConnection.closeConnection();
        }return false;
    }
}
