package se233.se233_project1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import se233.se233_project1.controller.MainViewController;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("project1-main-view.fxml"));
        System.setProperty("log4j.configurationFile", "log4j2.properties");
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TeeYai Converter");
        stage.setScene(scene);

        MainViewController controller = fxmlLoader.getController();

        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("That you want to exit?");

            if(alert.showAndWait().get() == ButtonType.NO) {
                event.consume();
                return;
            }
            controller.shutdownExecutor();
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}