package com.app.extractor.core.readers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Фабрика для управління та отримання обробників документів.
 * Реалізує паттерн "Flyweight" (Пристосуванець), зберігаючи екземпляри обробників в одному екземплярі.
 */
public class ProcessorFactory {

    // Мапа-реєстр: Розширення -> Відповідний обробник
    private static final Map<String, DocumentProcessor> registry = new HashMap<>();

    static {
        // Реєструємо наявні обробники. 
        // Оскільки вони не мають стану (stateless), одного екземпляра на всю програму достатньо.
        registry.put("pdf", new PdfProcessor());
        registry.put("docx", new DocxProcessor());
    }

    /**
     * Метод: getProcessor
     * Реалізація: Аналізує розширення файлу та повертає відповідний процесор.
     * @param fileName ім'я файлу (напр. "invoice.pdf").
     * @return Optional, що містить процесор або порожній, якщо формат не підтримується.
     */
    public static Optional<DocumentProcessor> getProcessor(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return Optional.empty();
        }

        // Витягуємо розширення після останньої крапки
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // Повертаємо процесор через Optional для безпечної обробки в UI/CLI
        return Optional.ofNullable(registry.get(extension));
    }
    
    /**
     * Метод для перевірки підтримки формату перед початком обробки.
     */
    public static boolean isSupported(String fileName) {
        return getProcessor(fileName).isPresent();
    }
}