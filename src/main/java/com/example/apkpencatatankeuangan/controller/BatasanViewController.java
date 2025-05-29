package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.Managers.BatasanManager;
import com.example.apkpencatatankeuangan.Managers.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
public class BatasanViewController {

    @FXML
    private TextField tfBatas;

    private BerandaViewController berandaController; // simpan referensi

    public void setBerandaController(BerandaViewController controller) {
        this.berandaController = controller;
    }

    @FXML
    private void simpanBatas() {
        try {
            double batas = Double.parseDouble(tfBatas.getText().trim());
            BatasanManager.setBatasPengeluaran(batas);

            try (Connection conn = DBConnection.getConnection()) {
                BatasanManager.simpanBatasPengeluaranKeDB(conn);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText(null);
            alert.setContentText("Batas pengeluaran berhasil disimpan.");
            alert.showAndWait();

            // Reload data Beranda dulu sebelum tutu
            ((Stage) tfBatas.getScene().getWindow()).close(); // Tutup jendela
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Tidak Valid");
            alert.setHeaderText(null);
            alert.setContentText("Masukkan angka batas pengeluaran yang valid.");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal menyimpan ke database:\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}

