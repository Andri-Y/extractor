package com.app.extractor.core.config;

import java.util.ArrayList;
import java.util.List;

import com.app.extractor.core.search.MatchMode;

public class AppConfig {
    private String configName = "";
    private String sourcePath = "";
    private String outputPath = "";
    private List<FieldEntry> fieldEntries = new ArrayList<>();

    public static class FieldEntry {
        // Змінюємо імена полів, щоб вони збігалися з викликами в MainController
        public String header;
        public String query;
        public MatchMode mode;

        public FieldEntry(String header, String query, MatchMode mode) {
            this.header = header;
            this.query = query;
            this.mode = mode;
        }
    }

    // Геттери/Сеттери (переконайся, що вони відповідають іменам полів)
    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }
    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }
    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
    public List<FieldEntry> getFieldEntries() { return fieldEntries; }
    public void addFieldEntry(FieldEntry entry) { this.fieldEntries.add(entry); }
}