package com.example.apkpencatatankeuangan.controller;

import java.sql.*;

import com.example.apkpencatatankeuangan.HelloApplication;
import com.example.apkpencatatankeuangan.Managers.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button buttonSubmit;


    public RegisterController() {buatTabelJikaBelumAda();}
    private void buatTabelJikaBelumAda() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL)";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        }catch (SQLException e) {
            System.err.println("Gagal membuat tabel: " + e.getMessage());
        }finally {
            DBConnection.closeConnection();
        }
    }

    @FXML
    void ClickButtonSubmit(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Form tidak boleh kosong!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users(username, password) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            showAlert(AlertType.INFORMATION, "Registrasi berhasil!");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();


            Platform.runLater(() -> HelloApplication.openViewWithModal("login-view", false));

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Gagal registrasi: " + e.getMessage());
        }
    }


    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Button getButtonSubmit() {
        return buttonSubmit;
    }

    public void setButtonSubmit(Button buttonSubmit) {
        this.buttonSubmit = buttonSubmit;
    }
}