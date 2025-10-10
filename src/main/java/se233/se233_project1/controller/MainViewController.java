package se233.se233_project1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import se233.se233_project1.model.FileEntry;

import java.io.File;
import java.util.List;

public class MainViewController {
    List<FileEntry> fileEntryList;
    @FXML
    private Pane inputPane;
    @FXML
    private ListView<String> inputFileNameListView;
    @FXML
    private ListView<String> inputFilePathListView;
    @FXML
    private Button selectFileButton;
    @FXML
    private ListView<FileEntry> outputListView;
    @FXML
    private Label inputLabel1;
    @FXML
    private Label inputLabel2;
    @FXML
    private Button convertButton;

    public void initialize() {
        selectFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Audio File(s)");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.m4a", "*.flac"));
            List<File> file = fileChooser.showOpenMultipleDialog(selectFileButton.getScene().getWindow());
            if (!file.isEmpty()) {
                resizeInputListView();
                for (File f : file) {
                    FileEntry fileEntry = new FileEntry(f.getName(), f.getAbsolutePath());
                    inputFileNameListView.getItems().add(fileEntry.getFileName());
                    inputFilePathListView.getItems().add(fileEntry.getFilePath());
                    fileEntryList.add(fileEntry);
                    System.out.println("File " + f.getName() + " is added.");
                }
            }
        });

        inputPane.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            String dragboardFileName = dragboard.getFiles().get(0).getName().toLowerCase();
            final boolean isAccept = dragboardFileName.endsWith(".wav") || dragboardFileName.endsWith(".mp3") || dragboardFileName.endsWith(".m4a") || dragboardFileName.endsWith(".flac");
            if (dragboard.hasFiles() && isAccept) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        inputPane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                success = true;
                for (File f : dragboard.getFiles()) {
                    FileEntry fileEntry = new FileEntry(f.getName(), f.getAbsolutePath());
                    inputFileNameListView.getItems().add(fileEntry.getFileName());
                    inputFilePathListView.getItems().add(fileEntry.getFilePath());
                    fileEntryList.add(fileEntry);
                    System.out.println("File " + f.getName() + " is added.");
                }
            }
        });
    }

    public void resizeInputListView(){
        inputLabel1.setLayoutX(105);
        inputLabel1.setLayoutY(255);

        inputLabel2.setLayoutX(300);
        inputLabel2.setLayoutY(260);

        selectFileButton.setLayoutX(325);
        selectFileButton.setLayoutY(255);

        inputPane.setPrefHeight(250);
        inputPane.setLayoutY(70);

        inputFileNameListView.setPrefHeight(250);
        inputFileNameListView.setLayoutY(0);

        inputFilePathListView.setPrefHeight(250);
        inputFilePathListView.setLayoutY(0);
    }
}
