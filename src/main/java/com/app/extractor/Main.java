package com.app.extractor;

import com.app.extractor.cli.CliController;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Налаштування кодування для консолі
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (Exception ignored) {}

        if (args.length > 0 && "-cli".equalsIgnoreCase(args[0])) {
            // Запуск консолі
            new CliController().startInteractiveMode();
        } else {
            // Запуск GUI через посередника (GuiApp)
            // Це обходить помилку "runtime components are missing"
            Application.launch(GuiApp.class, args);
        }
    }
}