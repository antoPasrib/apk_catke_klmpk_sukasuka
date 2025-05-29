package com.example.apkpencatatankeuangan;

import com.example.apkpencatatankeuangan.controller.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Login");
        if (SessionManager.getInstance().isLoggedIn()) {
            primaryStage.setScene(new Scene(loadFXML("Beranda2-view")));
        } else {
            primaryStage.setScene(new Scene(loadFXML("login-view")));
        }
        primaryStage.show();
    }

    public static Parent loadFXML(String fxmlName) {
        String path = "/com/example/apkpencatatankeuangan/" + fxmlName + ".fxml";
        System.out.println("Loading FXML from: " + path);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(path));
            if (fxmlLoader.getLocation() == null) {
                throw new RuntimeException("FXML file not found: " + path);
            }
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal memuat FXML: " + path);
        }
    }

    public static void openViewWithModal(String fxmlName, boolean isResizable) {
        Stage stage = new Stage();
        stage.setScene(new Scene(loadFXML(fxmlName)));
        stage.sizeToScene();
        stage.setResizable(isResizable);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();

    }

    public static void setRoot(String fxmlName, boolean isResizable) {
        primaryStage.getScene().setRoot(loadFXML(fxmlName));
        primaryStage.sizeToScene();
        primaryStage.setResizable(isResizable);
    }

    public static void main(String[] args) {
        launch();
    }
}
