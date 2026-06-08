package com.app.extractor.core.readers;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Внутрішня реалізація для роботи з PDF документами.
 */
class PdfProcessor implements DocumentProcessor {

    @Override
    public String readContent(File file) throws IOException {
        // Крок 1: Валідація вхідного файлу
        if (file == null || !file.exists()) {
            throw new IOException("Файл PDF не знайдено: " + (file != null ? file.getName() : "null"));
        }

        // Крок 2: Використання try-with-resources для надійної роботи з пам'яттю
        // PDDocument має бути закритий після читання, щоб уникнути витоків (Memory Leaks)
        try (PDDocument document = PDDocument.load(file)) {
            
            // Крок 3: Перевірка на зашифрованість документа
            if (document.isEncrypted()) {
                throw new IOException("Файл " + file.getName() + " захищений паролем. Читання неможливе.");
            }

            // Крок 4: Використання стриппера для вилучення тексту
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Крок 5: Повернення очищеного від зайвих пробілів тексту
            return text != null ? text.trim() : "";
        }
    }
}