package com.example.apkpencatatankeuangan.Data;

public class Catatan {
    private int id;
    private String Jenis_Transaksi;
    private String Jumlah;
    private String Kategori;
    private String Tanggal;

    public Catatan(int id , String tanggal, String jenis_Transaksi, String jumlah, String kategori) {
        this.id = id;
        Jenis_Transaksi = jenis_Transaksi;
        Jumlah = jumlah;
        Kategori = kategori;
        Tanggal = tanggal;
    }

    //konstanta
    public static String CATATAN_PEMASUKAN = "Pemasukan";
    public static String CATATAN_PENGELUARAN = "Pengeluaran";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJenis_Transaksi() {
        return Jenis_Transaksi;
    }

    public void setJenis_Transaksi(String jenis_Transaksi) {
        Jenis_Transaksi = jenis_Transaksi;
    }

    public String getJumlah() {
        return Jumlah;
    }

    public void setJumlah(String Jumlah) {
        this.Jumlah = Jumlah;
    }

    public String getKategori() {
        return Kategori;
    }

    public void setKategori(String kategori) {
        this.Kategori = kategori;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }
}
