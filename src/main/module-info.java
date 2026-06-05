module com.app.extractor {
    // Необхідні модулі
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires java.sql; // Потрібно для POI іноді

    // Відкриваємо пакети для рефлексії (FXML та GSON)
    opens com.app.extractor.ui to javafx.fxml;
    opens com.app.extractor.core.config to com.google.gson;
    opens com.app.extractor.core.search to com.google.gson;
    
    // Експортуємо головний пакет для запуску
    exports com.app.extractor;
}