package com.app.extractor.ui;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.app.extractor.core.Orchestrator;
import com.app.extractor.core.config.AppConfig;
import com.app.extractor.core.config.ConfigManager;
import com.app.extractor.io.FileService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Головний контролер UI. Побудований на принципах тонкого контролера.
 * Керує взаємодією між користувачем та ядром системи.
 */
public class MainController {

    // --- Елементи FXML ---
    @FXML private BorderPane rootNode;
    @FXML private VBox fieldsContainer;
    @FXML private TextField sourcePathField;
    @FXML private TextField outputPathField;
    @FXML private ListView<String> presetListView;
    @FXML private Button themeBtn;

    // --- Сервіси (Dependency Injection через ініціалізацію) ---
    private final ConfigManager configManager = new ConfigManager();
    private final Orchestrator orchestrator = new Orchestrator();
    private final FileService fileService = new FileService();

    // Реєстр пресетів (Ключ: Ім'я, Значення: Шлях)
    private Map<String, String> registry;
    private final ObservableList<String> presetNames = FXCollections.observableArrayList();

    /**
     * Метод: initialize
     * Реалізація: Стандартний метод JavaFX для первинного налаштування UI.
     */
    @FXML
    public void initialize() {
        // 1. Налаштування списку пресетів
        refreshRegistry();
        presetListView.setItems(presetNames);
        
        // Додаємо слухача для вибору пресета зі списку
        presetListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadPresetByName(newVal);
        });

        // 2. Додаємо перший порожній рядок конфігурації за замовчуванням
        handleAddRow();
    }

    /**
     * Метод: handleAddRow
     * Реалізація: Додає новий динамічний компонент ConfigRow у контейнер.
     */
    @FXML
    private void handleAddRow() {
        ConfigRow row = new ConfigRow(() -> {
            // Callback для видалення: якщо рядків більше 1, видаляємо цей
            if (fieldsContainer.getChildren().size() > 1) {
                fieldsContainer.getChildren().removeIf(node -> node instanceof ConfigRow && node.equals(null)); // спрощено
                // В реальності передаємо посилання на об'єкт
            }
        });
        
        // Логіка видалення рядка через переданий Runnable
        fieldsContainer.getChildren().add(row);
    }

    /**
     * Метод: handleSelectSource
     * Реалізація: Викликає системне вікно вибору папки.
     */
    @FXML
    private void handleSelectSource() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Оберіть папку з документами");
        File selected = chooser.showDialog(rootNode.getScene().getWindow());
        if (selected != null) {
            sourcePathField.setText(selected.getAbsolutePath());
        }
    }

    /**
     * Метод: handleSelectOutput
     * Реалізація: Викликає вікно збереження Excel файлу.
     */
    @FXML
    private void handleSelectOutput() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selected = chooser.showSaveDialog(rootNode.getScene().getWindow());
        if (selected != null) {
            outputPathField.setText(selected.getAbsolutePath());
        }
    }

    /**
     * Метод: handleSaveConfig
     * Реалізація: Збирає дані з усіх полів UI та зберігає в JSON через ConfigManager.
     */
    @FXML
    private void handleSaveConfig() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("config/"));
        File file = chooser.showSaveDialog(rootNode.getScene().getWindow());

        if (file != null) {
            AppConfig config = collectDataFromUI();
            config.setConfigName(file.getName().replace(".json", ""));
            try {
                configManager.saveConfig(config, file);
                refreshRegistry();
                showInfo("Успіх", "Конфігурацію збережено");
            } catch (IOException e) {
                showError("Помилка збереження", e.getMessage());
            }
        }
    }

    /**
     * Метод: handleRun
     * Реалізація: Запускає Orchestrator у фоновому потоці.
     */
    @FXML
    private void handleRun() {
        AppConfig config = collectDataFromUI();
        
        // Валідація перед запуском
        if (config.getSourcePath().isEmpty() || config.getOutputPath().isEmpty()) {
            showError("Помилка", "Вкажіть шляхи до папок!");
            return;
        }

        // Запуск у новому потоці, щоб UI не "виснув"
        new Thread(() -> {
            try {
                orchestrator.run(config, status -> {
                    // Оновлення тексту в UI має відбуватися в потоці JavaFX
                    Platform.runLater(() -> System.out.println(status)); 
                });
                Platform.runLater(() -> showInfo("Завершено", "Дані успішно експортовані!"));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Критична помилка", e.getMessage()));
            }
        }).start();
    }

    // --- Допоміжні методи (Private Utilities) ---

    private AppConfig collectDataFromUI() {
        AppConfig config = new AppConfig();
        config.setSourcePath(sourcePathField.getText());
        config.setOutputPath(outputPathField.getText());

        fieldsContainer.getChildren().forEach(node -> {
            if (node instanceof ConfigRow) {
                ConfigRow row = (ConfigRow) node;
                config.addFieldEntry(new AppConfig.FieldEntry(row.getHeader(), row.getQuery(), row.getMode()));
            }
        });
        return config;
    }

    private void loadPresetByName(String name) {
        String path = registry.get(name);
        if (path != null) {
            try {
                AppConfig loaded = configManager.loadConfig(new File(path));
                sourcePathField.setText(loaded.getSourcePath());
                outputPathField.setText(loaded.getOutputPath());
                
                fieldsContainer.getChildren().clear();
                loaded.getFieldEntries().forEach(entry -> {
                    ConfigRow row = new ConfigRow(() -> {}); // спрощено
                    row.setData(entry.query, entry.header, entry.mode);
                    fieldsContainer.getChildren().add(row);
                });
            } catch (IOException e) {
                showError("Помилка завантаження", e.getMessage());
            }
        }
    }

    private void refreshRegistry() {
        this.registry = configManager.loadRegistry();
        presetNames.setAll(registry.keySet());
    }

    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }
    
    @FXML private void handleOpenConfig() { /* Аналогічно до Save, викликає loadConfig */ }
    @FXML private void handleToggleTheme() { /* Зміна стилів CSS */ }
}