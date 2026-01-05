package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class App extends Application {

    private final AudioUploader uploader = new AudioUploader();
    private final TextArea logArea = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("SDK Audio Tester");

        Button chooseFileBtn = new Button("Choose Audio File");
        Button sendBtn = new Button("Send to SDK");
        Label fileLabel = new Label("No file selected");
        ProgressIndicator progress = new ProgressIndicator();
        progress.setVisible(false);

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.webm")
        );

        final File[] selectedFile = new File[1];

        chooseFileBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText("Selected: " + file.getName());
            }
        });

        sendBtn.setOnAction(e -> {
            if (selectedFile[0] == null) {
                log("?? Please select a file first.");
                return;
            }
            progress.setVisible(true);
            new Thread(() -> {
                try {
                    String response = uploader.sendAudio(selectedFile[0]);
                    log("? Response:\n" + response);
                } catch (Exception ex) {
                    log("? Error: " + ex.getMessage());
                } finally {
                    javafx.application.Platform.runLater(() -> progress.setVisible(false));
                }
            }).start();
        });

        VBox root = new VBox(10, chooseFileBtn, fileLabel, sendBtn, progress, new Label("Logs:"), logArea);
        root.setPadding(new Insets(20));
        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    private void log(String message) {
        javafx.application.Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public static void main(String[] args) {
        launch();
    }
}
