module com.app.extractor {
    // Бібліотеки графічного інтерфейсу
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // Потрібно для роботи системних вікон вибору файлів

    // Бібліотеки для роботи з документами
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires java.sql; // Важливо: Apache POI часто потребує цей модуль для внутрішніх потреб

    // Бібліотека для роботи з JSON конфігураціями
    requires com.google.gson;

    // --- ДОЗВОЛИ НА РЕФЛЕКСІЮ (Runtime Access) ---

    // Дозволяємо JavaFX "заходити" в UI пакети для ініціалізації контролерів
    opens com.app.extractor.ui to javafx.fxml;

    // Дозволяємо GSON читати та записувати дані в наші моделі конфігурації
    opens com.app.extractor.core.config to com.google.gson;
    
    // Дозволяємо GSON бачити перелік стратегій пошуку (Enum MatchMode)
    opens com.app.extractor.core.search to com.google.gson;

    // --- ЕКСПОРТ ПАКЕТІВ ---

    // Експортуємо головний пакет, щоб JVM могла знайти клас Main
    exports com.app.extractor;
}