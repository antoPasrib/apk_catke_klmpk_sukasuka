package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.Key;

public class LoginController {

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;
    @FXML
    private Hyperlink lbllupa;

    @FXML
    protected void RegisterbtnClick(ActionEvent event) throws IOException {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Informasi");
        alert.setContentText("Lanjut Ke Halaman Register");
        alert.show();
        HelloApplication.setRoot("registrasi-view", true);
    }


    @FXML
    void btnLoginClick(ActionEvent event) throws IOException {
        if (username.getText().equals("admin") && password.getText().equals("admin")) {
            lbllupa.setText("Berhasil Login");
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("beranda-view.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username/Password tidak valid");
            alert.show();
        }


    }
}




