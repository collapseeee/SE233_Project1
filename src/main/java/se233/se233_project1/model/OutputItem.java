package se233.se233_project1.model;

import javafx.beans.property.*;
import javafx.scene.control.Button;

public class OutputItem {
    private final StringProperty fileName = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final BooleanProperty completed = new SimpleBooleanProperty(false);
    private String convertedPath;
    private final Button downloadButton = new Button("Download");
    private final Button previewButton = new Button("Preview");
    private final Button deleteButton = new Button("Delete");

    public OutputItem(String fileName, double progress) {
        this.fileName.set(fileName);
    }

    public String getFileName() { return fileName.get(); }

    public DoubleProperty progressProperty() { return progress; }
    public BooleanProperty completedProperty() { return completed; }
    public void setProgress(double value) { this.progress.set(value); }
    public void setCompleted(boolean value) { this.completed.set(value); }

    public void setConvertedPath(String path) { this.convertedPath = path; }
    public String getConvertedPath() { return convertedPath; }

    public void markFailed() {
        this.fileName.set("[FAILED] " + fileName.get());
    }

    public Button getDownloadButton() { return downloadButton; }
    public Button getPreviewButton() { return previewButton; }
}
