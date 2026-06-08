package com.app.extractor.ui;

import com.app.extractor.core.search.MatchMode;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * UI компонент одного набору даних.
 */
public class ConfigRow extends HBox {

    private final TextField queryField = new TextField();
    private final ComboBox<MatchMode> modeCombo = new ComboBox<>();
    private final TextField headerField = new TextField();
    private final Button deleteBtn = new Button("🗑");

    public ConfigRow() {
        // Налаштування елементів
        queryField.setPromptText("Пошуковий запит / Regex");
        headerField.setPromptText("Ім'я стовпця Excel");
        modeCombo.getItems().addAll(MatchMode.values());
        modeCombo.getSelectionModel().selectFirst();

        // Розтягування полів
        HBox.setHgrow(queryField, Priority.ALWAYS);
        HBox.setHgrow(headerField, Priority.ALWAYS);

        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(new Label("Шукати:"), queryField, modeCombo, new Label("Стовпець:"), headerField, deleteBtn);
    }

    public void setOnDelete(Runnable onRemove) {
        deleteBtn.setOnAction(e -> onRemove.run());
    }

    // Геттери для збору даних при збереженні
    public String getQuery() { return queryField.getText(); }
    public String getHeader() { return headerField.getText(); }
    public MatchMode getMode() { return modeCombo.getValue(); }

    // Сеттери для завантаження конфігурації
    public void setData(String q, String h, MatchMode m) {
        queryField.setText(q);
        headerField.setText(h);
        modeCombo.setValue(m);
    }
}