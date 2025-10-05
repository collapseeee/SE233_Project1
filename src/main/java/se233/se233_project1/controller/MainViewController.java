package se233.se233_project1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import se233.se233_project1.model.FileEntry;

public class MainViewController {

    @FXML
    private ListView<FileEntry> inputListView;
    @FXML
    private ListView<FileEntry> outputListView;
    @FXML
    private Label inputLabel1;
    @FXML
    private Label inputLabel2;

    public void initialize() {

    }
}
