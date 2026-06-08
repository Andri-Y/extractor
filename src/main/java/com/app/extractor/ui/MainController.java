package com.app.extractor.ui;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.app.extractor.core.Orchestrator;
import com.app.extractor.core.config.AppConfig;
import com.app.extractor.core.config.ConfigManager;

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
 * Головний контролер графічного інтерфейсу.
 * Координує взаємодію між користувацьким вводом та бізнес-логікою екстракції.
 * Розроблено за участі Gr1m.
 */
public class MainController {

    @FXML private BorderPane rootNode;
    @FXML private VBox fieldsContainer;
    @FXML private TextField sourcePathField;
    @FXML private TextField outputPathField;
    @FXML private ListView<String> presetListView;
    @FXML private Button themeBtn;

    private final ConfigManager configManager = new ConfigManager();
    private final Orchestrator orchestrator = new Orchestrator();

    private Map<String, String> registry;
    private final ObservableList<String> presetNames = FXCollections.observableArrayList();

    /**
     * Початкова конфігурація UI компонентів при запуску вікна.
     */
    @FXML
    public void initialize() {
        // Завантаження збережених пресетів у бічну панель
        refreshRegistry();
        presetListView.setItems(presetNames);
        
        // Реєстрація події вибору пресета для автоматичного заповнення полів
        presetListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadPresetByName(newVal);
        });

        // Створення початкового рядка для введення параметрів
        handleAddRow();
    }

    /**
     * Обробник натискання кнопки додавання нового набору даних.
     */
    @FXML
    public void handleAddRow() {
        createRow(null, null, null);
    }

    /**
     * Створює та налаштовує динамічний рядок конфігурації поля.
     * Кроки виконання:
     * 1. Ініціалізація об'єкта ConfigRow.
     * 2. Встановлення логіки видалення (з перевіркою мінімальної кількості рядків).
     * 3. Опціональне заповнення даними (при завантаженні пресета).
     * 4. Додавання компонента у візуальний контейнер.
     */
    private void createRow(String query, String header, com.app.extractor.core.search.MatchMode mode) {
        ConfigRow row = new ConfigRow(); 

        row.setOnDelete(() -> {
            if (fieldsContainer.getChildren().size() > 1) {
                fieldsContainer.getChildren().remove(row);
            }
        });

        if (query != null) row.setData(query, header, mode);
        fieldsContainer.getChildren().add(row);
    }

    /**
     * Виклик діалогового вікна для вибору папки з вхідними документами.
     */
    @FXML
    public void handleSelectSource() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Оберіть папку з документами");
        File selected = chooser.showDialog(rootNode.getScene().getWindow());
        if (selected != null) sourcePathField.setText(selected.getAbsolutePath());
    }

    /**
     * Виклик діалогового вікна для вибору шляху збереження Excel звіту.
     */
    @FXML
    public void handleSelectOutput() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selected = chooser.showSaveDialog(rootNode.getScene().getWindow());
        if (selected != null) outputPathField.setText(selected.getAbsolutePath());
    }

    /**
     * Зберігає поточні налаштування інтерфейсу у файл JSON.
     * Кроки:
     * 1. Вибір місця збереження через FileChooser.
     * 2. Агрегація даних з усіх полів UI у модель AppConfig.
     * 3. Запис моделі на диск та оновлення реєстру пресетів.
     */
    @FXML
    public void handleSaveConfig() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("config/"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = chooser.showSaveDialog(rootNode.getScene().getWindow());

        if (file != null) {
            AppConfig config = collectDataFromUI();
            config.setConfigName(file.getName().replace(".json", ""));
            try {
                configManager.saveConfig(config, file);
                refreshRegistry();
                showInfo("Успіх", "Конфігурацію збережено в реєстр.");
            } catch (IOException e) {
                showError("Помилка збереження", e.getMessage());
            }
        }
    }

    /**
     * Завантаження конфігурації з файлу, обраного користувачем вручну.
     */
    @FXML
    public void handleOpenConfig() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("config/"));
        File file = chooser.showOpenDialog(rootNode.getScene().getWindow());
        if (file != null) {
            try {
                AppConfig loaded = configManager.loadConfig(file);
                applyConfigToUI(loaded);
            } catch (IOException e) {
                showError("Помилка відкриття", e.getMessage());
            }
        }
    }

    /**
     * Запуск основного процесу обробки документів.
     * Кроки:
     * 1. Збір поточної конфігурації.
     * 2. Валідація заповнення критичних шляхів.
     * 3. Запуск Orchestrator у фоновому потоці (Background Thread) для збереження чутливості UI.
     * 4. Передача статусів виконання в UI потік через Platform.runLater.
     */
    @FXML
    public void handleRun() {
        AppConfig config = collectDataFromUI();
        if (config.getSourcePath().isEmpty() || config.getOutputPath().isEmpty()) {
            showError("Помилка", "Будь ласка, заповніть шляхи до файлів.");
            return;
        }

        new Thread(() -> {
            try {
                orchestrator.run(config, status -> Platform.runLater(() -> System.out.println(status)));
                Platform.runLater(() -> showInfo("Обробка завершена", "Дані успішно записані в Excel."));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Критична помилка", e.getMessage()));
            }
        }).start();
    }

    /**
     * Перемикання візуальної теми додатка (Світла/Темна) шляхом заміни CSS файлів.
     */
    @FXML
    public void handleToggleTheme() {
        ObservableList<String> sheets = rootNode.getStylesheets();
        String dark = getClass().getResource("/css/dark.css").toExternalForm();
        String light = getClass().getResource("/css/light.css").toExternalForm();

        if (sheets.contains(dark)) {
            sheets.remove(dark);
            sheets.add(light);
            themeBtn.setText("🌙 Темна");
        } else {
            sheets.remove(light);
            sheets.add(dark);
            themeBtn.setText("☀️ Світла");
        }
    }

    /**
     * Збирає дані з усіх текстових полів та динамічних рядків у модель AppConfig.
     */
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

    /**
     * Очищує поточні поля та заповнює їх даними з об'єкта AppConfig.
     */
    private void applyConfigToUI(AppConfig config) {
        sourcePathField.setText(config.getSourcePath());
        outputPathField.setText(config.getOutputPath());
        fieldsContainer.getChildren().clear();
        config.getFieldEntries().forEach(e -> createRow(e.query, e.header, e.mode));
    }

    /**
     * Завантажує пресет за іменем, використовуючи шлях із реєстру.
     */
    private void loadPresetByName(String name) {
        String path = registry.get(name);
        if (path != null) {
            try {
                applyConfigToUI(configManager.loadConfig(new File(path)));
            } catch (IOException e) {
                showError("Помилка пресета", "Не вдалося завантажити: " + name);
            }
        }
    }

    /**
     * Синхронізує список імен пресетів у UI із файлом registry.json на диску.
     */
    private void refreshRegistry() {
        this.registry = configManager.loadRegistry();
        presetNames.setAll(registry.keySet());
    }

    /**
     * Відображення інформаційного модального вікна.
     */
    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Відображення модального вікна з описом помилки.
     */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Виникла проблема");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}