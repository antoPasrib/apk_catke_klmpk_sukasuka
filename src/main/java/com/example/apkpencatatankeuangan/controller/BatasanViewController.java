package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.Managers.BatasanListener;
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

    private BatasanListener batasanListener;

    public void setBatasanListener(BatasanListener listener) {
        this.batasanListener = listener;
    }

    @FXML
    private void simpanBatas() {
        try {
            double batas = Double.parseDouble(tfBatas.getText().trim());
            BatasanManager.setBatasPengeluaran(batas);

            try (Connection conn = DBConnection.getConnection()) {
                BatasanManager.simpanBatasPengeluaranKeDB();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText(null);
            alert.setContentText("Batas pengeluaran berhasil disimpan.");
            alert.showAndWait();

            // Panggil listener agar Beranda bisa update label
            if (batasanListener != null) {
                batasanListener.onBatasanDisimpan();
            }

            ((Stage) tfBatas.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Masukkan angka yang valid untuk batas pengeluaran.");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Terjadi kesalahan saat menyimpan ke database.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

}

