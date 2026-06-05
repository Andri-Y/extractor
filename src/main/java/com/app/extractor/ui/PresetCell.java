package com.app.extractor.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

/**
 * Кастомне відображення пресета: велике ім'я та дрібний шлях під ним.
 */
public class PresetCell extends ListCell<String> {
    private final VBox layout = new VBox();
    private final Label nameLabel = new Label();
    private final Label pathLabel = new Label();

    public PresetCell(java.util.Map<String, String> registry) {
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        pathLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        layout.getChildren().addAll(nameLabel, pathLabel);
    }

    @Override
    protected void updateItem(String key, boolean empty) {
        super.updateItem(key, empty);
        if (empty || key == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(key);
            // Тут ми мали б отримати шлях з реєстру (передається в контролері)
            setGraphic(layout);
        }
    }
}