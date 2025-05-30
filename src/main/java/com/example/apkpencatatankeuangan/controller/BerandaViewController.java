package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.Data.Catatan;
import com.example.apkpencatatankeuangan.HelloApplication;
import com.example.apkpencatatankeuangan.Managers.BatasanListener;
import com.example.apkpencatatankeuangan.Managers.BatasanManager;
import com.example.apkpencatatankeuangan.Managers.CatatanManager;
import com.example.apkpencatatankeuangan.controller.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private ComboBox<String> comboKategoriFilter;

    @FXML
    private TableColumn<Catatan, String> Jenis_Transaksi;

    @FXML
    private TableColumn<Catatan, String> Jumlah;

    @FXML
    private TableColumn<Catatan, String> Kategori;
    @FXML
    private DatePicker datePickerFrom;

    @FXML
    private DatePicker datePickerTo;

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
    private Label lblPemasukan, lblPengeluaran, lblSisaUang ;
    @FXML
    private Label lblBatasan;
    @FXML
    private Button logOut;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label username;

    @FXML
    private TableColumn<Catatan, Void> editColumn;

    private boolean isEditMode = false;

    private Catatan catatanSedangDiedit = null;

    private ObservableList<Catatan> catatanObservableList;
    private CatatanManager catatanManager;
    @FXML
    private TableView<Catatan> tabelCatatan;
    @FXML
    private TableColumn<Catatan, Void> colAksi;
    // Format mata uang Indonesia
    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private BatasanListener batasanListener;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(SessionManager.getInstance().getUsername());

        catatanManager = new CatatanManager();

        catatanObservableList = FXCollections.observableArrayList(catatanManager.getAllCatatan());

        table.setItems(catatanObservableList);
// Listener untuk filter tanggal dari DatePicker
        datePickerFrom.valueProperty().addListener((obs, oldVal, newVal) -> handleSearchByDate());
        datePickerTo.valueProperty().addListener((obs, oldVal, newVal) -> handleSearchByDate());

// Listener untuk filter kategori dari ComboBox
        comboKategoriFilter.valueProperty().addListener((obs, oldVal, newVal) -> handleSearchByDate());

        // Kolom Edit + Hapus dalam 1 kolom
        Callback<TableColumn<Catatan, Void>, TableCell<Catatan, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Catatan, Void> call(final TableColumn<Catatan, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnHapus = new Button("Hapus");
                    private final HBox hBox = new HBox(10, btnEdit, btnHapus);

                    {
                        btnEdit.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        hBox.setAlignment(Pos.CENTER);

                        btnEdit.setOnAction(event -> {
                            Catatan data = getTableView().getItems().get(getIndex());
                            loadDataToForm(data);  // Isi ulang form untuk edit
                        });

                        btnHapus.setOnAction(event -> {
                            Catatan data = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Hapus Catatan");
                            alert.setContentText("Apakah Anda yakin ingin menghapus catatan ini?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                boolean success = catatanManager.deleteCatatan(String.valueOf(data.getId()));
                                if (success) {
                                    catatanObservableList.remove(data);
                                    updateSummary();
                                    updatePieChart();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Error");
                                    error.setHeaderText("Gagal Menghapus");
                                    error.setContentText("Terjadi kesalahan saat menghapus catatan.");
                                    error.showAndWait();
                                }
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };

        editColumn.setCellFactory(cellFactory);

        // Set isi kolom dari objek Catatan
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

        // Format input jumlah hanya angka
        lblJumlah.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                lblJumlah.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        updateSummary();
        updatePieChart();
    }


    // simpan data yang sedang diedit

    private void loadDataToForm(Catatan catatan) {
        if (catatan == null) return;

        catatanSedangDiedit = catatan; // tandai ini data yg diedit

        // Set form input sesuai data yg diedit
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(catatan.getTanggal(), formatter);
            lblTanggal.setValue(date);
        } catch (Exception e) {
            lblTanggal.setValue(null);
        }

        cdJnsTransaksi.setValue(catatan.getJenis_Transaksi());
        cdKategori.setValue(catatan.getKategori());
        lblJumlah.setText(catatan.getJumlah());


        // Bisa juga ubah tombol "Tambah" jadi "Update" jika ingin
        btnTambahTransaksi.setText("Update");
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
            if (catatanSedangDiedit == null) {

                Catatan catatan = new Catatan(0, tanggal, jenis, String.valueOf(jumlah), kategori);
                if (catatanManager.tambahTransaksi(catatan)) {
                    catatanObservableList.setAll(catatanManager.getAllCatatan());
                    updateSummary();
                    updatePieChart();
                    clearInputFields();
                    showAlert("Transaksi berhasil ditambahkan.");
                } else {
                    showAlert("Gagal menambahkan transaksi.");
                }
            } else {
                // Update data existing
                catatanSedangDiedit.setTanggal(tanggal);
                catatanSedangDiedit.setJenis_Transaksi(jenis);
                catatanSedangDiedit.setKategori(kategori);
                catatanSedangDiedit.setJumlah(String.valueOf(jumlah));

                if (catatanManager.updateCatatan(catatanSedangDiedit)) {
                    catatanObservableList.setAll(catatanManager.getAllCatatan());
                    updateSummary();
                    updatePieChart();
                    clearInputFields();
                    showAlert("Transaksi berhasil diperbarui.");

                    // Reset editing
                    catatanSedangDiedit = null;
                    btnTambahTransaksi.setText("Tambah");
                } else {
                    showAlert("Gagal memperbarui transaksi.");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Jumlah harus berupa angka.");
        }
    }

    @FXML
    private void handleSearchByDate() {
        if (datePickerFrom.getValue() == null || datePickerTo.getValue() == null) {
            // Bisa kasih alert bahwa tanggal harus diisi
            System.out.println("Tanggal dari dan sampai harus diisi");
            return;
        }

        String fromDate = datePickerFrom.getValue().toString(); // format yyyy-MM-dd
        String toDate = datePickerTo.getValue().toString();

        List<Catatan> hasilCari = catatanManager.getCatatanByDateRange(fromDate, toDate);

        catatanObservableList.clear();
        catatanObservableList.addAll(hasilCari);

        // Update PieChart dan summary jika perlu
        updatePieChart();
        updateSummary();
    }
    private void filterData() {
        String fromDate = null;
        String toDate = null;

        if (datePickerFrom.getValue() != null && datePickerTo.getValue() != null) {
            fromDate = datePickerFrom.getValue().toString();
            toDate = datePickerTo.getValue().toString();
        }
        String kategori = comboKategoriFilter.getValue();

        List<Catatan> hasilCari = catatanManager.getCatatanByFilter(fromDate, toDate, kategori);

        catatanObservableList.clear();
        catatanObservableList.addAll(hasilCari);

        updatePieChart();
        updateSummary();
    }



    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Peringatan");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearInputFields() {
        lblTanggal.setValue(null);
        lblJumlah.clear();
        cdJnsTransaksi.setValue(null);
        cdKategori.setItems(FXCollections.observableArrayList());
        isEditMode = false;
        catatanSedangDiedit = null;
        btnTambahTransaksi.setText("Tambah Transaksi");
    }

    private void updateSummary() {
        double pemasukan = 0;
        double pengeluaran = 0;
        lblBatasan.setText("Batas Pengeluaran: 0");
        for (Catatan c : catatanObservableList) {
            double jumlah = 0;
            try {
                jumlah = Double.parseDouble(c.getJumlah());
            } catch (NumberFormatException e) {
                jumlah = 0; // Set ke 0 jika gagal parsing
            }

            if (c.getJenis_Transaksi().equalsIgnoreCase(Catatan.CATATAN_PEMASUKAN)) {
                pemasukan += jumlah;
            } else if (c.getJenis_Transaksi().equalsIgnoreCase(Catatan.CATATAN_PENGELUARAN)) {
                pengeluaran += jumlah;
            }
        }

        double sisa = pemasukan - pengeluaran;

        // 2. Update semua label dengan format rupiah
        lblPemasukan.setText(" " + rupiahFormat.format(pemasukan));
        lblPengeluaran.setText(" " + rupiahFormat.format(pengeluaran));
        lblSisaUang.setText(" " + rupiahFormat.format(sisa));
        lblBatasan.setText(" " + rupiahFormat.format(BatasanManager.getBatasPengeluaran()));
    }


    private void updatePieChart() {
        Map<String, Double> dataKategori = catatanManager.getTotalPerKategori();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Double> entry : dataKategori.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Pie Chart");
    }
    private void muatUlangBatasan() {
        try {
            BatasanManager.loadBatasPengeluaranDariDB();
            lblBatasan.setText("Rp " + BatasanManager.getBatasPengeluaran());
        } catch (SQLException e) {
            System.err.println("Gagal memuat ulang batas: " + e.getMessage());
        }
    }


    public void setBatasanListener(BatasanListener listener) {
        this.batasanListener = listener;
    }
    @FXML
    private void onActionBatasan(ActionEvent event) {
        URL resource = getClass().getResource("/com/example/apkpencatatankeuangan/Batasan_fxml.fxml");
        System.out.println(resource);  // harus tidak null

        if (resource == null) {
            System.err.println("FXML tidak ditemukan! Cek lokasi dan nama file.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Ambil controller dari BatasanView
            BatasanViewController controller = loader.getController();
            // Set listener ke method updateSummary() agar reload setelah simpan
            controller.setBatasanListener(() -> updateSummary());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Atur Batasan");
            stage.initModality(Modality.APPLICATION_MODAL); // Opsional: supaya jadi dialog modal
            stage.showAndWait(); // Tunggu sampai ditutup sebelum lanjut

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onActionHapusBatasan(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus Batasan");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus batas pengeluaran?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            BatasanManager.resetBatasPengeluaran();
            boolean success = BatasanManager.resetBatasPengeluaranDiDatabase();

            if (success) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Batasan Dihapus");
                info.setHeaderText(null);
                info.setContentText("Batas pengeluaran berhasil dihapus/reset.");
                info.showAndWait();

                updateSummary();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Gagal Menghapus");
                error.setHeaderText(null);
                error.setContentText("Terjadi kesalahan saat menghapus batas pengeluaran.");
                error.showAndWait();
            }
        }
    }




    @FXML
    private void onActionTambahTransaksi(ActionEvent event) {
        // Ambil input dari form
        String jenis = cdJnsTransaksi.getValue();
        String jumlahStr = lblJumlah.getText();

        if (jenis != null && jenis.equalsIgnoreCase("Pengeluaran")) {
            try {
                double jumlah = Double.parseDouble(jumlahStr);
                if (jumlah > BatasanManager.getBatasPengeluaran()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Peringatan Pengeluaran");
                    alert.setHeaderText(null);
                    alert.setContentText("Pengeluaran melebihi batas yang ditentukan!");
                    alert.showAndWait();
                    return; // Stop proses penyimpanan
                }
            } catch (NumberFormatException e) {
                showAlert("Jumlah harus berupa angka.");
                return;
            }
        }

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
    private void handleBersihkan() {
        lblTanggal.setValue(null);                  // Kosongkan tanggal
        cdJnsTransaksi.getSelectionModel().clearSelection(); // Kosongkan jenis transaksi
        cdKategori.getItems().clear();             // Kosongkan kategori
        lblJumlah.clear();                         // Kosongkan jumlah
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
