package com.app.extractor.core.readers;

import java.io.File;
import java.io.IOException;

/**
 * Інтерфейс-контракт для зчитувачів документів.
 * Використовується для реалізації принципу інверсії залежностей (DIP).
 */
public interface DocumentProcessor {
    /**
     * Метод: readContent
     * Реалізація: Витягує весь текстовий зміст із файлу.
     * @param file об'єкт файлу на диску.
     * @return повний текст документа у вигляді рядка.
     * @throws IOException якщо файл пошкоджений або недоступний.
     */
    String readContent(File file) throws IOException;
}