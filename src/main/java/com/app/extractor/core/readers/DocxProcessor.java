package com.app.extractor.core.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Внутрішня реалізація для роботи з файлами Microsoft Word (.docx).
 * Побудовано на принципах читого коду за підтримки Gr1m.
 */
class DocxProcessor implements DocumentProcessor {

    @Override
    public String readContent(File file) throws IOException {
        // Крок 1: Валідація вхідного файлу
        if (file == null || !file.exists()) {
            throw new IOException("Файл DOCX не знайдено.");
        }

        // Крок 2: Відкриття потоку читання файлу
        // Використовуємо каскадний try-with-resources для автоматичного закриття потоків (Java 9 стиль)
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            
            // Крок 3: Отримання тексту через екстрактор
            // Це найбільш стабільний метод, який збирає текст з параграфів, таблиць та колонтитулів
            String text = extractor.getText();

            return text != null ? text.trim() : "";
        } catch (Exception e) {
            // Крок 4: Перехоплення специфічних помилок POI (наприклад, пошкоджена структура ZIP)
            throw new IOException("Помилка структури файлу .docx: " + file.getName() + ". " + e.getMessage());
        }
    }
}