package se233.se233_project1.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import se233.se233_project1.model.FileEntry;
import se233.se233_project1.model.OutputItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewController {
    List<FileEntry> fileEntryList;
    private ExecutorService executor;

    @FXML
    private Region fileDropRegion;
    @FXML
    private ListView<FileEntry> inputFileListView;
    @FXML
    private Button selectFileButton;

    @FXML
    private ButtonBar buttonBar;
    @FXML
    private ToggleGroup OutputFormat;

    @FXML
    private Pane leftPane;
    @FXML
    private ToggleGroup FandR;
    @FXML
    private CheckBox fadeinButton;
    @FXML
    private CheckBox fadeoutButton;
    @FXML
    private CheckBox reverseButton;

    @FXML
    private Pane middlePane;
    @FXML
    private MenuButton constantMenuButton;
    @FXML
    private MenuButton variableMenuButton;
    @FXML
    private MenuButton sampleRateMenuButton;
    @FXML
    private ToggleGroup Channels;
    @FXML
    private ToggleButton channel1Button;
    @FXML
    private ToggleButton channel2Button;

    @FXML
    private Pane rightPane;
    @FXML
    private ToggleGroup Quality;
    @FXML
    private ToggleButton bestButton;
    @FXML
    private Label bestLabel;
    @FXML
    private ToggleButton goodButton;
    @FXML
    private Label goodLabel;
    @FXML
    private ToggleButton standardButton;
    @FXML
    private Label standardLabel;
    @FXML
    private ToggleButton economyButton;
    @FXML
    private Label economyLabel;

    @FXML
    private Button convertButton;

    @FXML
    private ListView<OutputItem> outputListView;

    public void initialize() {
        executor = Executors.newFixedThreadPool(4);
        fileEntryList = new ArrayList<>();

        setInitialDisableState();

        MenuItem delete = new MenuItem("Delete");
        ContextMenu inputListContextMenu = new ContextMenu(delete);
        inputFileListView.setContextMenu(inputListContextMenu);

        // Add file handler.
        selectFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Audio File(s)");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.m4a", "*.flac"));
            List<File> file = fileChooser.showOpenMultipleDialog(selectFileButton.getScene().getWindow());
            if (file != null) {
                for (File f : file) {
                    FileEntry fileEntry = new FileEntry(f.getName(), f.getAbsolutePath());
                    inputFileListView.getItems().add(fileEntry);
                    fileEntryList.add(fileEntry);
                    System.out.println("File " + f.getName() + " is added.");
                }
            }
            buttonBar.setDisable(false);
        });
        fileDropRegion.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            String dragboardFileName = dragboard.getFiles().get(0).getName().toLowerCase();
            final boolean isAccept = dragboardFileName.endsWith(".wav") || dragboardFileName.endsWith(".mp3") || dragboardFileName.endsWith(".m4a") || dragboardFileName.endsWith(".flac");
            if (dragboard.hasFiles() && isAccept) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        fileDropRegion.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                success = true;
                for (File f : dragboard.getFiles()) {
                    FileEntry fileEntry = new FileEntry(f.getName(), f.getAbsolutePath());
                    inputFileListView.getItems().add(fileEntry);
                    fileEntryList.add(fileEntry);
                    System.out.println("File " + f.getName() + " is added.");
                }
            }
            event.setDropCompleted(success);
            buttonBar.setDisable(false);
            event.consume();
        });

        // Delete added file handler.
        delete.setOnAction(event -> {
            FileEntry selectedFile = inputFileListView.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                fileEntryList.remove(selectedFile);
                inputFileListView.getItems().remove(selectedFile);
                System.out.println("File " + selectedFile.getFileName() + " is deleted.");
            }
            if (inputFileListView.getSelectionModel().getSelectedItem() == null) {
                setInitialDisableState();
            }
        });

        // Select output format handler.
        OutputFormat.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null) {
               leftPane.setDisable(false);
               middlePane.setDisable(false);
               rightPane.setDisable(false);
               convertButton.setDisable(false);
               ToggleButton button = (ToggleButton) newValue;
               String selectedOutputFormat = button.getText();
               System.out.println("Select Output Format: " + selectedOutputFormat);
               setValuesCorrespondingToFormat(selectedOutputFormat);
           } else {
               leftPane.setDisable(true);
               middlePane.setDisable(true);
               rightPane.setDisable(true);
               convertButton.setDisable(true);
               System.out.println("Select Output Format: null");
           }
        });

        // Converting handler
        convertButton.setOnAction(event -> {
            handlingConvertButton();
        });

        outputListView.setCellFactory(listView -> new ListCell<OutputItem>() {
            @Override
            protected void updateItem(OutputItem outputItem, boolean empty) {
                super.updateItem(outputItem, empty);

                if (empty || outputItem == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/se233/se233_project1/OutputItemCell.fxml"));
                        HBox cellRoot = fxmlLoader.load();
                        OutputItemCellController controller = fxmlLoader.getController();
                        ToggleButton selectedFormatButton = (ToggleButton) OutputFormat.getSelectedToggle();
                        controller.setData(outputItem, () -> getListView().getItems().remove(outputItem), selectedFormatButton.getText());
                        setGraphic(cellRoot);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void setInitialDisableState() {
        buttonBar.setDisable(true);
        leftPane.setDisable(true);
        middlePane.setDisable(true);
        rightPane.setDisable(true);
        convertButton.setDisable(true);
    }

    private void setValuesCorrespondingToFormat(String format) {
        // Default values
        constantMenuButton.setText("Constant");
        variableMenuButton.setText("Variable");
        sampleRateMenuButton.setText("32000 Khz");
        channel2Button.setSelected(true);
        goodButton.setSelected(true);

        switch (format) {
            case "mp3": {
                rightPane.setDisable(false);
                bestLabel.setText("Best (320 kbps)");
                goodLabel.setText("Good (192 kbps)");
                standardLabel.setText("Standard (128 kbps)");
                economyLabel.setText("Economy (64 kbps)");

                constantMenuButton.getItems().clear();
                constantMenuButton.setDisable(false);
                int[] bitrates = {32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320};
                for (Integer bitrate : bitrates) {
                    MenuItem item = new MenuItem(bitrate + " kbps");
                    item.setOnAction(event -> {
                        variableMenuButton.setText("Variable");
                        constantMenuButton.setText(item.getText());
                    });
                    constantMenuButton.getItems().add(item);
                }

                variableMenuButton.getItems().clear();
                variableMenuButton.setDisable(false);
                int[] variables = {0,1,2,3,4,5,6,7,8,9};
                for (Integer variable : variables) {
                    MenuItem item = new MenuItem(variable.toString());
                    item.setOnAction(event -> {
                        variableMenuButton.setText(item.getText());
                        constantMenuButton.setText("Constant");
                    });
                    variableMenuButton.getItems().add(item);
                }

                sampleRateMenuButton.getItems().clear();
                int[] rates = {32000,44100,48000};
                for (Integer rate : rates) {
                    MenuItem item = new MenuItem(rate + " Khz");
                    item.setOnAction(event -> {
                        sampleRateMenuButton.setText(item.getText());
                    });
                    sampleRateMenuButton.getItems().add(item);
                }
                break;
            }

            case "wav": {
                rightPane.setDisable(true);

                constantMenuButton.setDisable(true);

                variableMenuButton.setDisable(true);

                sampleRateMenuButton.getItems().clear();
                int[] rates = {8000,11025,12000,16000,22050,24000,32000,44100,48000,64000,88200,96000};
                for (Integer rate : rates) {
                    MenuItem item = new MenuItem(rate + " Khz");
                    item.setOnAction(event -> {
                        sampleRateMenuButton.setText(item.getText());
                    });
                    sampleRateMenuButton.getItems().add(item);
                }
                break;
            }

            case "m4a": {
                rightPane.setDisable(false);
                bestLabel.setText("Best (256 kbps)");
                goodLabel.setText("Good (160 kbps)");
                standardLabel.setText("Standard (128 kbps)");
                economyLabel.setText("Economy (64 kbps)");

                constantMenuButton.getItems().clear();
                constantMenuButton.setDisable(false);
                int[] bitrates = {16, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384, 448, 512};
                for (Integer bitrate : bitrates) {
                    MenuItem item = new MenuItem(bitrate + " kbps");
                    item.setOnAction(event -> {
                        constantMenuButton.setText(item.getText());
                    });
                    constantMenuButton.getItems().add(item);
                }

                variableMenuButton.setDisable(true);

                sampleRateMenuButton.getItems().clear();
                int[] rates = {8000,11025,12000,16000,22050,24000,32000,44100,48000};
                for (Integer rate : rates) {
                    MenuItem item = new MenuItem(rate + " Khz");
                    item.setOnAction(event -> {
                        sampleRateMenuButton.setText(item.getText());
                    });
                    sampleRateMenuButton.getItems().add(item);
                }
                break;
            }

            case "flac": {
                rightPane.setDisable(true);

                constantMenuButton.setDisable(true);

                variableMenuButton.setDisable(true);

                sampleRateMenuButton.getItems().clear();
                int[] rates = {8000,11025,12000,16000,22050,24000,32000,44100,48000};
                for (Integer rate : rates) {
                    MenuItem item = new MenuItem(rate + " Khz");
                    item.setOnAction(event -> {
                        sampleRateMenuButton.setText(item.getText());
                    });
                    sampleRateMenuButton.getItems().add(item);
                }
                break;
            }

            case "ogg": {
                rightPane.setDisable(false);
                bestLabel.setText("Best (256 kbps)");
                goodLabel.setText("Good (160 kbps)");
                standardLabel.setText("Standard (128 kbps)");
                economyLabel.setText("Economy (64 kbps)");

                constantMenuButton.getItems().clear();
                constantMenuButton.setDisable(false);
                int[] bitrates = {96, 112, 128, 160, 192, 224, 256};
                for (Integer bitrate : bitrates) {
                    MenuItem item = new MenuItem(bitrate + " kbps");
                    item.setOnAction(event -> {
                        constantMenuButton.setText(item.getText());
                    });
                    constantMenuButton.getItems().add(item);
                }

                variableMenuButton.setDisable(true);

                sampleRateMenuButton.getItems().clear();
                int[] rates = {8000,11025,12000,16000,22050,24000,32000,44100,48000};
                for (Integer rate : rates) {
                    MenuItem item = new MenuItem(rate + " Khz");
                    item.setOnAction(event -> {
                        sampleRateMenuButton.setText(item.getText());
                    });
                    sampleRateMenuButton.getItems().add(item);
                }
                break;
            }

            case "mp2": {
                rightPane.setDisable(false);
                bestLabel.setText("Best (256 kbps)");
                goodLabel.setText("Good (160 kbps)");
                standardLabel.setText("Standard (128 kbps)");
                economyLabel.setText("Economy (64 kbps)");

                constantMenuButton.getItems().clear();
                constantMenuButton.setDisable(false);
                int[] bitrates = {64, 80, 96, 112, 128, 160, 192, 224, 256, 320};
                for (Integer bitrate : bitrates) {
                    MenuItem item = new MenuItem(bitrate + " kbps");
                    item.setOnAction(event -> {
                        constantMenuButton.setText(item.getText());
                    });
                    constantMenuButton.getItems().add(item);
                }

                variableMenuButton.setDisable(true);

                sampleRateMenuButton.getItems().clear();
                int[] rates = {22050,24000,32000,44100,48000};
                for (Integer rate : rates) {
                    MenuItem item = new MenuItem(rate + " Khz");
                    item.setOnAction(event -> {
                        sampleRateMenuButton.setText(item.getText());
                    });
                    sampleRateMenuButton.getItems().add(item);
                }
                break;
            }
        }
    }

    private String getBitrateByQuality(String format) {
        if (bestButton.isSelected()) {
            switch (format) {
                case "mp3": return "320 kbps";
                case "m4a": return "256 kbps";
                case "ogg" : return "256 kbps";
                case "mp2": return "256 kbps";
                default: return "96000";
            }
        } else if (goodButton.isSelected()) {
            switch (format) {
                case "mp3": return "192 kbps";
                case "m4a": return "160 kbps";
                case "ogg": return "160 kbps";
                case "mp2": return "160 kbps";
                default: return "48000";
            }
        } else if (standardButton.isSelected()) {
            switch (format) {
                case "mp3": return "128 kbps";
                case "m4a": return "128 kbps";
                case "ogg": return "128 kbps";
                case "mp2": return "128 kbps";
                default: return "44100";
            }
        } else if (economyButton.isSelected()) {
            switch (format) {
                case "mp3": return "64 kbps";
                case "m4a": return "64 kbps";
                case "ogg": return "64 kbps";
                case "mp2": return "64 kbps";
                default: return "22050";
            }
        }
        return null;


    }

    private void handlingConvertButton() {
        ToggleButton selectedFormatButton = (ToggleButton) OutputFormat.getSelectedToggle();

        String format = selectedFormatButton.getText();

        String constant = constantMenuButton.getText().equals("Constant") ? getBitrateByQuality(format) : constantMenuButton.getText();
        String variable = variableMenuButton.getText().equals("Variable") ? null : variableMenuButton.getText();
        String sampleRate =  sampleRateMenuButton.getText().replace(" Khz", "");
        String channel = channel1Button.isSelected() ? "1" : "2";
        boolean fadeIn = fadeinButton.isSelected();
        boolean fadeOut = fadeoutButton.isSelected();
        boolean reverse = reverseButton.isSelected();

        for (FileEntry entry : fileEntryList) {
            OutputItem outputItem = new OutputItem(entry.getFileName(), 0);
            outputListView.getItems().add(outputItem);

            Converter converterTask = new Converter(entry, format, constant, variable, sampleRate, channel, fadeIn, fadeOut, reverse, outputItem::setProgress);

            executor.submit(() -> {
               try {
                   FileEntry converted = converterTask.call();
                   Platform.runLater(() -> {
                       outputItem.setProgress(1.0);
                       outputItem.setCompleted(true);
                       outputItem.setConvertedPath(converted.getFilePath());
                   });
               } catch (Exception e) {
                   e.printStackTrace();
                   Platform.runLater(outputItem::markFailed);
               }
            });
        }
    }
}
