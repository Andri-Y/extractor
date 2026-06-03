package com.app.extractor.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Реалізація процесора для роботи з PDF документами.
 * Клас зберігає стан поточної сесії обробки (папка та файл).
 */
public class PdfProcessor implements DocumentProcessor {

    private String sourceFolder;
    private File currentFile;

    
    // --- Методи керування станом (Getters/Setters) ---

    @Override
    public void setSourceFolder(String path) {
        // Зберігаємо шлях до робочої папки
        this.sourceFolder = path;
    }

    @Override
    public String getSourceFolder() {
        return this.sourceFolder;
    }

    @Override
    public void setCurrentFile(File file) {
        // Встановлюємо файл, з яким працюємо в даний момент
        this.currentFile = file;
    }

    @Override
    public File getCurrentFile() {
        return this.currentFile;
    }

    // --- Реалізація методу listSourceFiles ---
    @Override
    public List<File> listSourceFiles(String folderPath) throws IOException {
        // 1. Створюємо об'єкт файлу для вказаного шляху
        File directory = new File(folderPath);

        // 2. Валідація: перевіряємо чи шлях існує і чи це директорія
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException("Шлях не знайдено або він не є папкою: " + folderPath);
        }

        // 3. Отримуємо список файлів, фільтруємо лише .pdf (ігноруючи регістр)
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        // 4. Перетворюємо масив у список. Якщо папка порожня — повертаємо порожній список
        if (files == null) {
            return Collections.emptyList();
        }

        // 5. Повертаємо список, відсортований за ім'ям для передбачуваності обробки
        return Arrays.stream(files)
                .sorted()
                .collect(Collectors.toList());
    }

    // --- Реалізація методу readContent ---
    @Override
    public String readContent(File file) throws IOException {
        // 1. Перевіряємо, чи файл придатний для читання
        if (file == null || !file.exists() || !file.canRead()) {
            throw new IOException("Файл неможливо прочитати: " + (file != null ? file.getName() : "null"));
        }

        // 2. Використовуємо try-with-resources для автоматичного закриття PDDocument
        // Це запобігає витокам пам'яті (Memory Leaks)
        try (PDDocument document = PDDocument.load(file)) {
            
            // 3. Створюємо об'єкт для вилучення тексту
            PDFTextStripper stripper = new PDFTextStripper();
            
            // 4. Отримуємо текст з усіх сторінок документа
            String text = stripper.getText(document);
            
            // 5. Очищуємо текст від зайвих пробілів на початку/в кінці та повертаємо
            return text != null ? text.trim() : "";
        }
    }
}