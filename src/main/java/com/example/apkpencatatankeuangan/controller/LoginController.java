package com.example.apkpencatatankeuangan.controller;

import com.example.apkpencatatankeuangan.HelloApplication;
import com.example.apkpencatatankeuangan.Managers.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.apkpencatatankeuangan.HelloApplication.loadFXML;

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
        alert.showAndWait();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        HelloApplication.openViewWithModal("register-view", true);
    }


    @FXML
    void btnLoginClick(ActionEvent event){
        String inputUsername = username.getText();
        String inputPassword = password.getText();
        String sql = "select * from users where username=? and password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,inputUsername);
            stmt.setString(2,inputPassword);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
                HelloApplication.openViewWithModal("beranda-view2",false);

            }else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Login Failed");
                alert.setContentText("Username or Password is incorrect");
                alert.show();
            }
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login Failed");
            alert.setContentText("Terjadi Kesalahan");
            e.printStackTrace();

        }
        }
}