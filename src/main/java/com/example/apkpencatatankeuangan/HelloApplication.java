package com.example.apkpencatatankeuangan;

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
        primaryStage.setScene(new Scene(loadFXML("Hello-view")));
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    private static Parent loadFXML(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml + ".fxml"));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void setRoot(String fxml, boolean resizable) {
        primaryStage.getScene().setRoot(loadFXML(fxml));
        primaryStage.setResizable(resizable);
        primaryStage.sizeToScene();
    }
    public static void main(String[] args) {
        launch();
    }
    public static void openViewWithModal(String fxml, boolean resizable) {
        Stage stage = new Stage();
        stage.setScene(new Scene(loadFXML(fxml)));
        stage.setResizable(resizable);
        stage.sizeToScene();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }
}