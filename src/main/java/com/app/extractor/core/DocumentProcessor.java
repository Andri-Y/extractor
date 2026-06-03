package com.app.extractor.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Інтерфейс для керування життєвим циклом обробки документів.
 * Визначає контракт для пошуку файлів та витягування даних.
 */
public interface DocumentProcessor {

    /**
     * Крок 1: Перегляд вмісту папки.
     * @param folderPath шлях до цільової директорії.
     * @return список файлів, що підлягають обробці.
     * Використовуємо List замість масиву для зручнішої роботи зі Stream API.
     */
    List<File> listSourceFiles(String folderPath) throws IOException;

    /**
     * Крок 2: Зчитування вмісту конкретного файлу.
     * @param file об'єкт файлу для аналізу.
     * @return текстовий зміст файлу.
     */
    String readContent(File file) throws IOException;

    /**
     * Методи для керування шляхом до робочої папки.
     * Це дозволяє "запам'ятати" контекст обробки.
     */
    void setSourceFolder(String path);
    String getSourceFolder();

    /**
     * Методи для керування поточним активним файлом.
     * Дозволяють уникнути повторного передавання файлу в кожен метод екстракції.
     */
    void setCurrentFile(File file);
    File getCurrentFile();
}