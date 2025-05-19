package com.example.apkpencatatankeuangan.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import com.example.apkpencatatankeuangan.controller.DBConnection;
import com.example.apkpencatatankeuangan.controller.BerandaViewController;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
public class GrafikController implements Initializable {
    private BerandaViewController catatan;
    @FXML
    private PieChart pieChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.catatan = new BerandaViewController();
        preparedData();
    }

    private void preparedData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Gunakan GROUP BY untuk menghitung jumlah data per kategori
        String query = "SELECT kategori, COUNT(*) AS jumlah FROM catatan GROUP BY kategori";

        try (PreparedStatement preparedStatement =
                     DBConnection.getConnection().prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String kategori = resultSet.getString("kategori");
                int jumlah = resultSet.getInt("jumlah");

                // Tambahkan data ke PieChart
                pieChartData.add(new PieChart.Data(kategori, jumlah));
            }

            pieChart.setData(pieChartData);
            pieChart.setTitle("Jumlah Catatan per Kategori");
            pieChart.setClockwise(true);
            pieChart.setLabelLineLength(50);
            pieChart.setLabelsVisible(true);
            pieChart.setStartAngle(180);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
