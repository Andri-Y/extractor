package com.app.extractor.core.search;

public class SearchField {
    private final String columnHeader;
    private final String searchParameter;
    private final MatchMode matchMode; // Додаємо поле

    // Виправлений конструктор на 3 параметри
    public SearchField(String columnHeader, String searchParameter, MatchMode matchMode) {
        this.columnHeader = columnHeader;
        this.searchParameter = searchParameter;
        this.matchMode = matchMode;
    }

    public String getColumnHeader() { return columnHeader; }
    public String getSearchParameter() { return searchParameter; }
    public MatchMode getMatchMode() { return matchMode; } // Додаємо геттер для SearchEngine
}