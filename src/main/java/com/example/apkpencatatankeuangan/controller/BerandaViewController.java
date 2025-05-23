package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.Data.Catatan;
import com.example.apkpencatatankeuangan.HelloApplication;
import com.example.apkpencatatankeuangan.Managers.CatatanManager;
import com.example.apkpencatatankeuangan.controller.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class BerandaViewController implements Initializable {

    @FXML
    private TableView<Catatan> table;

    @FXML
    private TableColumn<Catatan, String> Jenis_Transaksi;

    @FXML
    private TableColumn<Catatan, String> Jumlah;

    @FXML
    private TableColumn<Catatan, String> Kategori;

    @FXML
    private TableColumn<Catatan, String> Tanggal;

    @FXML
    private Button btnTambahTransaksi;

    @FXML
    private ChoiceBox<String> cdJnsTransaksi;

    @FXML
    private ChoiceBox<String> cdKategori;

    @FXML
    private TextField lblJumlah;

    // Ganti lblTanggal (TextField) jadi DatePicker untuk pemilihan tanggal
    @FXML
    private DatePicker lblTanggal;

    @FXML
    private Label lblPemasukan, lblPengeluaran, lblSisaUang;

    @FXML
    private Button logOut;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label username;

    private ObservableList<Catatan> catatanObservableList;
    private CatatanManager catatanManager;

    // Format mata uang Indonesia
    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(SessionManager.getInstance().getUsername());

        catatanManager = new CatatanManager();
        catatanObservableList = FXCollections.observableArrayList(catatanManager.getAllCatatan());

        table.setItems(catatanObservableList);

        Jenis_Transaksi.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenis_Transaksi()));
        Jumlah.setCellValueFactory(data -> {
            try {
                double val = Double.parseDouble(data.getValue().getJumlah());
                return new SimpleStringProperty(rupiahFormat.format(val));
            } catch (Exception e) {
                return new SimpleStringProperty(data.getValue().getJumlah());
            }
        });
        Kategori.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKategori()));
        Tanggal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTanggal()));

        // Inisialisasi pilihan jenis transaksi
        cdJnsTransaksi.setItems(FXCollections.observableArrayList("Pemasukan", "Pengeluaran"));

        cdJnsTransaksi.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                cdKategori.setItems(FXCollections.observableArrayList());
            } else if (newVal.equalsIgnoreCase("Pemasukan")) {
                cdKategori.setItems(FXCollections.observableArrayList("Gaji", "Bonus"));
                cdKategori.setValue(null);
            } else if (newVal.equalsIgnoreCase("Pengeluaran")) {
                cdKategori.setItems(FXCollections.observableArrayList("Traveling", "Belanja", "Makanan", "Transportasi"));
                cdKategori.setValue(null);
            }
        });

        // Set label teks
        lblPemasukan.setText("Pemasukan:");
        lblPengeluaran.setText("Pengeluaran:");
        lblSisaUang.setText("Sisa Uang:");

        // Format textfield lblJumlah supaya inputan rupiah, tapi tetap bisa input angka biasa
        lblJumlah.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                lblJumlah.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        updateSummary();
        updatePieChart();
    }

    @FXML
    private void tambahTransaksi() {
        LocalDate tanggalSelected = lblTanggal.getValue();
        String tanggal = tanggalSelected != null ? tanggalSelected.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "";
        String jenis = cdJnsTransaksi.getValue();
        String kategori = cdKategori.getValue();
        String jumlahStr = lblJumlah.getText().trim();

        if (tanggal.isEmpty() || jenis == null || kategori == null || jumlahStr.isEmpty()) {
            showAlert("Lengkapi semua data terlebih dahulu.");
            return;
        }

        try {
            double jumlah = Double.parseDouble(jumlahStr);
            Catatan catatan = new Catatan(0, tanggal, jenis, String.valueOf(jumlah), kategori);
            if (catatanManager.tambahTransaksi(catatan)) {
                catatanObservableList.setAll(catatanManager.getAllCatatan());
                updateSummary();
                updatePieChart();
                clearInputFields();
            } else {
                showAlert("Gagal menambahkan transaksi.");
            }
        } catch (NumberFormatException e) {
            showAlert("Jumlah harus berupa angka.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearInputFields() {
        lblTanggal.setValue(null);
        lblJumlah.clear();
        cdJnsTransaksi.setValue(null);
        cdKategori.setItems(FXCollections.observableArrayList());
    }

    private void updateSummary() {
        double pemasukan = 0;
        double pengeluaran = 0;

        for (Catatan c : catatanObservableList) {
            double jumlah = 0;
            try {
                jumlah = Double.parseDouble(c.getJumlah());
            } catch (NumberFormatException e) {
                // Ignore atau set 0 jika error parsing
                jumlah = 0;
            }
            if (c.getJenis_Transaksi().equalsIgnoreCase(Catatan.CATATAN_PEMASUKAN)) {
                pemasukan += jumlah;
            } else if (c.getJenis_Transaksi().equalsIgnoreCase(Catatan.CATATAN_PENGELUARAN)) {
                pengeluaran += jumlah;
            }
        }

        double sisa = pemasukan - pengeluaran;

        // Pastikan ini dipanggil dengan format yang benar
        lblPemasukan.setText(" " + rupiahFormat.format(pemasukan));
        lblPengeluaran.setText(" " + rupiahFormat.format(pengeluaran));
        lblSisaUang.setText(" " + rupiahFormat.format(sisa));
    }


    private void updatePieChart() {
        double pemasukan = 0;
        double pengeluaran = 0;

        for (Catatan c : catatanObservableList) {
            double jumlah = Double.parseDouble(c.getJumlah());
            if (c.getJenis_Transaksi().equalsIgnoreCase(Catatan.CATATAN_PEMASUKAN)) {
                pemasukan += jumlah;
            } else if (c.getJenis_Transaksi().equalsIgnoreCase(Catatan.CATATAN_PENGELUARAN)) {
                pengeluaran += jumlah;
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Pemasukan", pemasukan),
                new PieChart.Data("Pengeluaran", pengeluaran)
        );

        pieChart.setData(pieChartData);
    }

    @FXML
    private void onActionTambahTransaksi(ActionEvent event) {
        tambahTransaksi();
    }

    @FXML
    private void onActionLogOut(ActionEvent event) {
        // Create a new alert with type Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit & Logout Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press OK to exit the application.");

        // Add Yes and No buttons to the alert
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        // Show the alert and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // User clicked Yes, exit the application
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
                SessionManager.getInstance().logout();
                HelloApplication.openViewWithModal("login-view", false);

            }
        });
    }

    @FXML
    public void onDownloadClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Transaksi");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("laporan_transaksi.xlsx");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Transaksi");

                // Header
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Tanggal", "Jenis", "Jumlah", "Kategori"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                // Isi data
                for (int i = 0; i < catatanObservableList.size(); i++) {
                    Catatan catatan = catatanObservableList.get(i);
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(catatan.getTanggal());
                    row.createCell(1).setCellValue(catatan.getJenis_Transaksi());
                    row.createCell(2).setCellValue(Double.parseDouble(catatan.getJumlah()));
                    row.createCell(3).setCellValue(catatan.getKategori());
                }

                // Simpan ke file
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                showAlert("Data berhasil disimpan ke Excel.");
            } catch (IOException e) {
                showAlert("Terjadi kesalahan saat menyimpan file: " + e.getMessage());
            } catch (Exception e) {
                showAlert("Gagal menyimpan file: " + e.getMessage());
            }
        }
    }

}
