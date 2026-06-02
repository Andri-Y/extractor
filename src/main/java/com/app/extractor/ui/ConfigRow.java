package com.app.extractor.ui;

import com.app.extractor.core.SearchEngine;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.util.function.Consumer;

public class ConfigRow {
    private final HBox view;
    private final TextField paramInput = new TextField();
    private final TextField columnInput = new TextField();
    private final ComboBox<SearchEngine.MatchMode> modeCombo = new ComboBox<>();

    public ConfigRow(Consumer<ConfigRow> onDelete) {
        paramInput.setPromptText("Параметри пошуку");
        columnInput.setPromptText("Заголовок стовпця");
        modeCombo.getItems().addAll(SearchEngine.MatchMode.values());
        modeCombo.getSelectionModel().selectFirst();

        Button delBtn = new Button("-");
        delBtn.setOnAction(e -> onDelete.accept(this));

        view = new HBox(10, paramInput, columnInput, modeCombo, delBtn);
    }

    public HBox getView() { return view; }
}