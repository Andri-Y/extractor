package com.app.extractor.core;

import com.app.extractor.core.config.AppConfig;
import com.app.extractor.core.readers.DocumentProcessor;
import com.app.extractor.core.readers.ProcessorFactory;
import com.app.extractor.core.search.SearchField;

import com.app.extractor.io.FileService;
import com.app.extractor.io.ExcelWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Головний координатор процесу екстракції.
 * Реалізує паттерн Фасад для спрощення взаємодії між модулями.
 */
public class Orchestrator {

        private final FileService fileService = new FileService();
    private final ExcelWriter excelWriter = new ExcelWriter();

    public void run(AppConfig config, java.util.function.Consumer<String> progressCallback) throws Exception {
        fileService.setSourcePath(config.getSourcePath());
        fileService.validateOutputFile(config.getOutputPath());

        // Виправлений мапінг: використовуємо правильні імена полів з FieldEntry (header, query, mode)
        List<SearchField> searchFields = config.getFieldEntries().stream()
                .map(e -> new SearchField(e.header, e.query, e.mode))
                .collect(Collectors.toList());

        SearchEngine searchEngine = new SearchEngine(searchFields);
        // 3. Отримання списку файлів (підтримуємо PDF та DOCX)
        List<File> files = fileService.listFilesByExtensions(List.of(".pdf", ".docx"));
        
        if (files.isEmpty()) {
            throw new IOException("У вказаній папці не знайдено підтримуваних файлів (.pdf, .docx)");
        }

        // Підготовка структури даних для Excel
        List<String> headers = searchFields.stream().map(SearchField::getColumnHeader).collect(Collectors.toList());
        // Додаємо службовий стовпець з ім'ям файлу
        headers.add(0, "Ім'я файлу");
        
        List<List<String>> tableData = new ArrayList<>();

        // 4. Ітераційна обробка кожного документа
        for (File file : files) {
            if (progressCallback != null) progressCallback.accept("Обробка: " + file.getName());

            // Використовуємо фабрику для отримання потрібного процесора
            Optional<DocumentProcessor> processorOpt = ProcessorFactory.getProcessor(file.getName());
            
            if (processorOpt.isPresent()) {
                try {
                    // Зчитування тексту
                    String content = processorOpt.get().readContent(file);
                    
                    // Пошук даних (двигун використовує стратегії з MatchMode)
                    // Для спрощення беремо перший знайдений результат для кожного поля
                    Map<String, List<String>> rawResults = searchEngine.execute(content);
                    
                    // Формування рядка таблиці
                    List<String> row = new ArrayList<>();
                    row.add(file.getName()); // Перша клітинка - назва файлу
                    
                    for (SearchField field : searchFields) {
                        List<String> matches = rawResults.get(field.getColumnHeader());
                        // Якщо знайдено декілька співпадінь, об'єднуємо їх через коми
                        String result = (matches == null || matches.isEmpty()) ? "" : String.join(", ", matches);
                        row.add(result);
                    }
                    tableData.add(row);
                    
                } catch (IOException e) {
                    if (progressCallback != null) progressCallback.accept("Помилка у файлі " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        // 5. Експорт накопичених даних у Excel
        excelWriter.save(config.getOutputPath(), headers, tableData);
        
        if (progressCallback != null) progressCallback.accept("Завершено успішно. Створено файл: " + config.getOutputPath());
    }
}