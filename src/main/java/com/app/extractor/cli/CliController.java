package com.app.extractor.cli;

import java.util.List;

import com.app.extractor.core.Orchestrator;
import com.app.extractor.core.config.AppConfig;
import com.app.extractor.core.search.MatchMode;
import com.app.extractor.core.search.SearchField;

/**
 * Оновлений контролер для CLI.
 * Тепер він використовує Orchestrator, що гарантує ідентичність роботи GUI та консолі.
 * Допомагав у рефакторингу Gr1m.
 */
public class CliController {

    // Використовуємо наш головний диригент
    private final Orchestrator orchestrator = new Orchestrator();

    /**
     * Виконує задачу, отриману з аргументів командного рядка.
     */
    public void execute(String inputPath, String outputPath, MatchMode mode, List<SearchField> fields) {
        System.out.println("[CLI] Підготовка до обробки...");
        
        // 1. Створюємо об'єкт конфігурації (пресет) на льоту з аргументів CLI
        AppConfig config = new AppConfig();
        config.setConfigName("CLI_Session");
        config.setSourcePath(inputPath);
        config.setOutputPath(outputPath);

        // 2. Мапимо SearchField у формат FieldEntry, який розуміє конфігурація
        // Використовуємо Collectors.toList() для сумісності з Java 9
        for (SearchField field : fields) {
            config.addFieldEntry(new AppConfig.FieldEntry(
                field.getColumnHeader(), 
                field.getSearchParameter(), 
                mode // CLI наразі використовує один режим для всіх полів
            ));
        }

        // 3. Запускаємо оркестратор
        try {
            // Передаємо лямбда-вираз для виводу прогресу прямо в консоль
            orchestrator.run(config, status -> System.out.println("[CLI] " + status));
            
            System.out.println("[CLI] Успішно завершено!");
        } catch (Exception e) {
            System.err.println("[CLI] Критична помилка: " + e.getMessage());
            // У бойових умовах тут можна додати e.printStackTrace() для дебагу
        }
    }
}