package com.app.extractor.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервіс для управління операціями з файловою системою.
 * Відповідає за валідацію шляхів та пошук файлів у директоріях.
 */
public class FileService {

    // Поточна робоча папка, обрана користувачем у GUI або CLI
    private String sourceFolderPath;

    /**
     * Метод: setSourcePath
     * Реалізація: Перевіряє чи шлях є валідною папкою та встановлює його як активний.
     */
    public void setSourcePath(String path) throws FileNotFoundException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Шлях не може бути порожнім");
        }

        File folder = new File(path);
        
        // Перевірка: чи шлях існує та чи є він саме директорією (не файлом)
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Вказаний шлях не знайдено або він не є папкою: " + path);
        }

        this.sourceFolderPath = path;
    }

    /**
     * Метод: getSourcePath
     * Реалізація: Повертає поточний шлях для відображення в UI.
     */
    public String getSourcePath() {
        return sourceFolderPath;
    }

    /**
     * Метод: listFilesByExtensions
     * Реалізація: Шукає у встановленій папці всі файли, що відповідають списку розширень.
     * @param extensions список розширень (напр. ".pdf", ".docx")
     */
    public List<File> listFilesByExtensions(List<String> extensions) {
        if (sourceFolderPath == null) {
            return Collections.emptyList();
        }

        File directory = new File(sourceFolderPath);
        
        // Отримуємо масив усіх файлів у директорії
        File[] files = directory.listFiles();

        if (files == null) {
            return Collections.emptyList();
        }

        // Крок фільтрації за допомогою Stream API
        return Arrays.stream(files)
                // 1. Залишаємо тільки файли (ігноруємо підпапки)
                .filter(File::isFile)
                // 2. Перевіряємо чи розширення файлу є у списку дозволених (ігноруючи регістр)
                .filter(file -> extensions.stream()
                        .anyMatch(ext -> file.getName().toLowerCase().endsWith(ext.toLowerCase())))
                // 3. Сортуємо для стабільного порядку відображення в UI
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Метод: validateOutputFile
     * Реалізація: Перевіряє можливість створення або запису у вихідний Excel файл.
     */
    public void validateOutputFile(String path) throws IOException {
        File file = new File(path);
        
        // Перевірка: чи не є шлях папкою
        if (file.isDirectory()) {
            throw new IOException("Вихідний шлях має бути файлом .xlsx, а не папкою.");
        }

        // Спроба перевірити права доступу на запис у директорію
        File parent = file.getParentFile();
        if (parent != null && !parent.canWrite()) {
            throw new IOException("Відсутні права на запис у папку: " + parent.getAbsolutePath());
        }
    }
}