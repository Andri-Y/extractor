package com.app.extractor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Точка входу в систему.
 * Розпізнає режим запуску (CLI або GUI).
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Завантаження FXML розмітки
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("PDF/DOCX Data Extractor");
        primaryStage.setScene(new Scene(root));
        
        // Встановлення світлої теми за замовчуванням
        root.getStylesheets().add(getClass().getResource("/css/light.css").toExternalForm());
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            // CLI Режим
            handleCli(args);
        } else {
            // GUI Режим
            launch(args);
        }
    }

    private static void handleCli(String[] args) {
        // Тут логіка парсингу аргументів, яку ми обговорювали раніше
        // Виклик CliController.execute(...)
        System.out.println("Запуск у режимі CLI...");
    }
}