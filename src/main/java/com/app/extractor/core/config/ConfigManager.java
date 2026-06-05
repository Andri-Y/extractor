package com.app.extractor.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Менеджер для збереження та завантаження конфігурацій.
 * Реалізує роботу з реєстром (Ключ-Значення) та JSON файлами.
 */
public class ConfigManager {
    // Шлях до папки конфігурацій в корені проекту
    private static final String CONFIG_DIR = "config/";
    // Файл-індекс для швидкого завантаження списку пресетів
    private static final String REGISTRY_FILE = CONFIG_DIR + "registry.json";
    
    // Об'єкт Gson з "красивим" друком для зручного редагування людиною
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        // Крок 1: Перевірка та створення робочої директорії при ініціалізації
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
        } catch (IOException e) {
            System.err.println("Помилка створення папки конфігурацій");
        }
    }

    /**
     * Метод: saveConfig
     * Реалізація: Зберігає об'єкт AppConfig у файл та оновлює реєстр.
     */
    public void saveConfig(AppConfig config, File targetFile) throws IOException {
        // 1. Серіалізація об'єкта в JSON і запис у вибраний файл
        try (Writer writer = new FileWriter(targetFile)) {
            gson.toJson(config, writer);
        }

        // 2. Оновлення реєстру (Мапа: Ім'я -> Останній шлях)
        Map<String, String> registry = loadRegistry();
        registry.put(config.getConfigName(), targetFile.getAbsolutePath());
        
        // 3. Збереження оновленого реєстру на диск
        try (Writer writer = new FileWriter(REGISTRY_FILE)) {
            gson.toJson(registry, writer);
        }
    }

    /**
     * Метод: loadConfig
     * Реалізація: Читає JSON файл і перетворює його назад в об'єкт Java.
     */
    public AppConfig loadConfig(File file) throws IOException {
        // 1. Створення потоку читання для вказаного файлу
        try (Reader reader = new FileReader(file)) {
            // 2. Десеріалізація JSON в екземпляр AppConfig
            return gson.fromJson(reader, AppConfig.class);
        }
    }

    /**
     * Метод: loadRegistry
     * Реалізація: Повертає мапу всіх відомих конфігурацій (для лівої панелі UI).
     */
    public Map<String, String> loadRegistry() {
        File file = new File(REGISTRY_FILE);
        // 1. Якщо файлу немає, повертаємо порожню мапу (перший запуск)
        if (!file.exists()) return new HashMap<>();

        try (Reader reader = new FileReader(file)) {
            // 2. Використання TypeToken для коректного зчитування Generic Map<String, String>
            return gson.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}