package com.app.extractor.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.*;

public class MainController {
    @FXML private VBox fieldsContainer;
    @FXML private VBox rootNode;
    @FXML private TextField inputPathField;
    @FXML private TextField outputPathField;

    private final List<ConfigRow> rows = new ArrayList<>();
    private boolean isDark = false;

    @FXML
    public void initialize() {
        addRow(); // Мінімальна кількість полів - 1
    }

    @FXML
    private void addRow() {
        ConfigRow row = new ConfigRow(this::removeRow);
        rows.add(row);
        fieldsContainer.getChildren().add(row.getView());
    }

    private void removeRow(ConfigRow row) {
        if (rows.size() > 1) {
            rows.remove(row);
            fieldsContainer.getChildren().remove(row.getView());
        }
    }

    @FXML
    private void toggleTheme() {
        rootNode.getStylesheets().clear();
        isDark = !isDark;
        String theme = isDark ? "/css/dark.css" : "/css/light.css";
        rootNode.getStylesheets().add(getClass().getResource(theme).toExternalForm());
    }

    @FXML
    private void processFiles() {
        // Логіка запуску обробки
        System.out.println("Processing started...");
    }
}