package com.example.apkpencatatankeuangan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class BerandaViewController {

    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction, String> Jenis_Transaksi;

    @FXML
    private TableColumn<Transaction, Integer> Jumlah;

    @FXML
    private TableColumn<Transaction, String> Kategori;

    @FXML
    private ChoiceBox<String> cbTransaksi;

    @FXML
    private ChoiceBox<String> cbKategori;

    @FXML
    private TextField txtJumlah;

    @FXML
    private Button btnSimpan;

    @FXML
    private Button btnHapus;

    @FXML
    private Button btnGrafik;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inisialisasi kolom tabel
        Jenis_Transaksi.setCellValueFactory(new PropertyValueFactory<>("jenisTransaksi"));
        Jumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        Kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));

        table.setItems(transactions);

        // Isi pilihan ChoiceBox
        cbTransaksi.setItems(FXCollections.observableArrayList("Pemasukan", "Pengeluaran"));
        cbTransaksi.setOnAction(event -> updateKategoriChoiceBox());
    }

    private void updateKategoriChoiceBox() {
        String selectedJenis = cbTransaksi.getValue();
        if ("Pemasukan".equals(selectedJenis)) {
            cbKategori.setItems(FXCollections.observableArrayList("Gaji", "Bonus"));
        } else if ("Pengeluaran".equals(selectedJenis)) {
            cbKategori.setItems(FXCollections.observableArrayList("Makanan", "Transportasi", "Lainnya"));
        }
    }

    @FXML
    void onBtnSimpanClick(ActionEvent event) {
        String jenisTransaksi = cbTransaksi.getValue();
        String kategori = cbKategori.getValue();
        String jumlahText = txtJumlah.getText();

        if (jenisTransaksi == null || kategori == null || jumlahText.isEmpty()) {
            showAlert("Peringatan", "Semua bidang harus diisi!");
            return;
        }

        try {
            int jumlah = Integer.parseInt(jumlahText);
            transactions.add(new Transaction(jenisTransaksi, kategori, jumlah));
            table.refresh();

            // Reset form
            cbTransaksi.setValue(null);
            cbKategori.setValue(null);
            txtJumlah.clear();
        } catch (NumberFormatException e) {
            showAlert("Kesalahan", "Jumlah harus berupa angka!");
        }
    }

    @FXML
    void onBtnHapus(ActionEvent event) {
        Transaction selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            transactions.remove(selected);
        } else {
            showAlert("Peringatan", "Pilih transaksi yang ingin dihapus!");
        }
    }

    @FXML
    void onBtnGrafik(ActionEvent event) {
        // Aksi untuk membuka grafik, tambahkan logika sesuai kebutuhan
        System.out.println("Tombol Grafik diklik!");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class Transaction {
        private final String jenisTransaksi;
        private final String kategori;
        private final int jumlah;

        public Transaction(String jenisTransaksi, String kategori, int jumlah) {
            this.jenisTransaksi = jenisTransaksi;
            this.kategori = kategori;
            this.jumlah = jumlah;
        }

        public String getJenisTransaksi() {
            return jenisTransaksi;
        }

        public String getKategori() {
            return kategori;
        }

        public int getJumlah() {
            return jumlah;
        }
    }
}
