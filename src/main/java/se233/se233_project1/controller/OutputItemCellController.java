package se233.se233_project1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import se233.se233_project1.model.OutputItem;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OutputItemCellController {
    @FXML
    private Label fileNameLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button previewButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button removeButton;

    private OutputItem outputItem;
    private Runnable removeCallback;

    public void setData(OutputItem outputItem, Runnable removeCallback, String outputFormat) {
        this.outputItem = outputItem;
        this.removeCallback = removeCallback;

        fileNameLabel.setText(outputItem.getFileName() + " to ." + outputFormat);

        progressBar.progressProperty().bind(outputItem.progressProperty());

        previewButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(new File(outputItem.getConvertedPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        downloadButton.setOnAction(event -> {
            try {
                File file = new File(outputItem.getConvertedPath());
                Desktop.getDesktop().open(file.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        removeButton.setOnAction(event -> {
            removeCallback.run();
        });
    }
}
