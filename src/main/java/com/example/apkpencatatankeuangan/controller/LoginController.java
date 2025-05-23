package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;

import static com.example.apkpencatatankeuangan.HelloApplication.loadFXML;

public class LoginController {
    private static Stage primaryStage;
    @FXML
    private PasswordField password;

    @FXML
    private TextField username;
    @FXML
    private Hyperlink lbllupa;

    public static void setRoot(String fxml, boolean isResizeable) {
        primaryStage.getScene().setRoot(loadFXML(fxml));
        primaryStage.sizeToScene();
        primaryStage.setResizable(isResizeable);
    }

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
    void btnLoginClick(ActionEvent event){
        String user = username.getText();
        String pass = password.getText();
        if (user.equals("admin") && pass.equals("admin")) {
            HelloApplication.openViewWithModal("beranda-view2", false);
            SessionManager.getInstance().login();
        }



    }
}