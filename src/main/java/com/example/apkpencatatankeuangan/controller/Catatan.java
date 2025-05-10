package com.example.apkpencatatankeuangan.controller;

public class Catatan {
    private int id;
    private String Jenis_Transaksi;
    private int Jumlah;
    private String Kategori;

    //konstanta
    public static String CATATAN_PEMASUKAN = "Pemasukan";
    public static String CATATAN_PENGELUARAN = "Pengeluaran";


    public Catatan(String Jenis_Transaksi, int Jumlah) {
        this.Jenis_Transaksi = Jenis_Transaksi;
        this.Jumlah = Jumlah;
    }

    public Catatan(int id, String Jenis_Transaksi, int Jumlah) {
        this.id = id;
        this.Jenis_Transaksi = Jenis_Transaksi;
        this.Jumlah = Jumlah;
    }

    public Catatan(int id, String Jenis_Transaksi, int Jumlah, String Kategori) {
        this.id = id;
        this.Jenis_Transaksi = Jenis_Transaksi;
        this.Jumlah = Jumlah;
        this.Kategori = Kategori;
    }
    public Catatan(String Jenis_Transaksi, int Jumlah, String Kategori) {
        this.Jenis_Transaksi = Jenis_Transaksi;
        this.Jumlah = Jumlah;
        this.Kategori = Kategori;
    }

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

    public int getJumlah() {
        return Jumlah;
    }

    public void setJumlah(int Jumlah) {
        this.Jumlah = Jumlah;
    }

    public String getKategori() {
        return Kategori;
    }

    public void setKategori(String kategori) {
        this.Kategori = kategori;
    }
}
