package com.app.extractor.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.app.extractor.core.Orchestrator;
import com.app.extractor.core.config.AppConfig;
import com.app.extractor.core.config.ConfigManager;
import com.app.extractor.core.readers.ProcessorFactory;
import com.app.extractor.core.search.MatchMode;
import com.app.extractor.io.FileService;

/**
 * Повноцінний текстовий контролер (TUI).
 * Дозволяє керувати всіма аспектами програми через термінал.
 */
public class CliController {

    // Використовуємо Scanner для зчитування вводу користувача
    private final Scanner input = new Scanner(System.in, "UTF-8");
    private final ConfigManager configManager = new ConfigManager();
    private final FileService fileService = new FileService();
    private final Orchestrator orchestrator = new Orchestrator();
    
    // Поточний стан сесії в консолі (DTO)
    private AppConfig currentConfig = new AppConfig();

    /**
     * Головний цикл текстового меню. Побудований на класичному switch для Java 9.
     */
    public void startInteractiveMode() {
        boolean running = true;
        System.out.println("\n=== ПРОВІДНИК ДАНИХ (CLI VERSION) dev by Gr1m ===");

        while (running) {
            printMainMenu();
            String choice = input.nextLine();

            switch (choice) {
                case "1":
                    listFilesInFolder();
                    break;
                case "2":
                    viewFileContent();
                    break;
                case "3":
                    managePresetsMenu();
                    break;
                case "4":
                    configureSearchFields();
                    break;
                case "5":
                    runFullProcess();
                    break;
                case "0":
                    running = false;
                    System.out.println("Вихід з програми...");
                    break;
                default:
                    System.out.println("❌ Невірний вибір. Спробуйте ще раз.");
                    break;
            }
        }
    }

    /**
     * Відображає головне меню в консолі.
     */
    private void printMainMenu() {
        System.out.println("\n-------------------------------------------");
        System.out.println("            ГОЛОВНЕ МЕНЮ (TUI)             ");
        System.out.println("-------------------------------------------");
        System.out.println(" 1. 📂 Перегляд файлів у папці");
        System.out.println(" 2. 📖 Читання вмісту конкретного файлу");
        System.out.println(" 3. 💾 Робота з пресетами (Завантажити/Зберегти)");
        System.out.println(" 4. ⚙️  Налаштування пошукових запитів");
        System.out.println(" 5. ▶️  ЗАПУСТИТИ ПОВНУ ОБРОБКУ");
        System.out.println(" 0. ❌ Вихід");
        System.out.println("-------------------------------------------");
        System.out.print(" Введіть номер дії: ");
    }

    private void listFilesInFolder() {
        System.out.print("Введіть шлях до папки: ");
        String path = input.nextLine();
        try {
            fileService.setSourcePath(path);
            currentConfig.setSourcePath(path);
            List<File> files = fileService.listFilesByExtensions(Arrays.asList(".pdf", ".docx"));
            
            System.out.println("\nЗнайдено файлів:");
            for (File f : files) {
                System.out.println(" - " + f.getName());
            }
        } catch (FileNotFoundException e) {
            System.out.println("❌ Папку не знайдено: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }
    }

    private void viewFileContent() {
        System.out.print("Введіть повне ім'я файлу (з розширенням): ");
        String fileName = input.nextLine();
        File file = new File(currentConfig.getSourcePath(), fileName);

        ProcessorFactory.getProcessor(fileName).ifPresentOrElse(
            processor -> {
                try {
                    String content = processor.readContent(file);
                    System.out.println("\n--- ВМІСТ ФАЙЛУ ---\n" + content + "\n-------------------");
                } catch (IOException e) { 
                    System.out.println("❌ Помилка читання: " + e.getMessage()); 
                }
            },
            () -> System.out.println("❌ Формат не підтримується.")
        );
    }

    private void managePresetsMenu() {
        System.out.println("\n--- УПРАВЛІННЯ ПРЕСЕТАМИ ---");
        System.out.println("1. Список збережених пресетів");
        System.out.println("2. Завантажити пресет за ім'ям");
        
        String choice = input.nextLine();
        Map<String, String> registry = configManager.loadRegistry();

        if ("1".equals(choice)) {
            for (Map.Entry<String, String> entry : registry.entrySet()) {
                System.out.println("⭐ " + entry.getKey() + " -> " + entry.getValue());
            }
        } else if ("2".equals(choice)) {
            System.out.print("Введіть ім'я пресета: ");
            String name = input.nextLine();
            if (registry.containsKey(name)) {
                try {
                    this.currentConfig = configManager.loadConfig(new File(registry.get(name)));
                    System.out.println("✅ Пресет завантажено.");
                } catch (IOException e) { 
                    System.out.println("❌ Помилка: " + e.getMessage()); 
                }
            } else {
                System.out.println("❌ Пресет не знайдено.");
            }
        }
    }

    private void configureSearchFields() {
        System.out.println("\n--- НАЛАШТУВАННЯ ПОШУКУ ---");
        for (int i = 0; i < currentConfig.getFieldEntries().size(); i++) {
            AppConfig.FieldEntry e = currentConfig.getFieldEntries().get(i);
            System.out.println((i + 1) + ". " + e.header + " | " + e.query + " [" + e.mode + "]");
        }
        
        System.out.println("\n1. Додати нове поле");
        System.out.println("2. Очистити всі поля");
        
        String choice = input.nextLine();
        if ("1".equals(choice)) {
            System.out.print("Заголовок стовпця: ");
            String h = input.nextLine();
            System.out.print("Пошуковий запит (String або Regex): ");
            String q = input.nextLine();
            System.out.println("Режим (1. EXACT, 2. PARTIAL, 3. REGEX): ");
            String m = input.nextLine();
            
            MatchMode mode = MatchMode.EXACT;
            if ("2".equals(m)) mode = MatchMode.PARTIAL;
            else if ("3".equals(m)) mode = MatchMode.REGEX;
            
            currentConfig.addFieldEntry(new AppConfig.FieldEntry(h, q, mode));
            System.out.println("✅ Поле додано.");
        } else if ("2".equals(choice)) {
            currentConfig.getFieldEntries().clear();
            System.out.println("✅ Поля очищено.");
        }
    }

    private void runFullProcess() {
        if (currentConfig.getFieldEntries().isEmpty()) {
            System.out.println("❌ Помилка: Список полів для пошуку порожній.");
            return;
        }
        System.out.print("Введіть шлях до вихідного Excel (.xlsx): ");
        String out = input.nextLine();
        currentConfig.setOutputPath(out);

        try {
            orchestrator.run(currentConfig, status -> System.out.println("[ПРОГРЕС] " + status));
            System.out.println("🚀 ОБРОБКА ЗАВЕРШЕНА УСПІШНО!");
        } catch (Exception e) {
            System.out.println("❌ Критична помилка під час обробки: " + e.getMessage());
        }
    }
}