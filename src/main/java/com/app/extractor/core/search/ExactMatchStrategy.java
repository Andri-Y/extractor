package com.app.extractor.core.search;

import java.util.ArrayList;
import java.util.List;

class ExactMatchStrategy implements SearchStrategy {
    @Override
    public List<String> find(String text, String query) {
        List<String> results = new ArrayList<>();
        // Якщо текст містить точну копію запиту, додаємо її в результати
        if (text != null && query != null && !query.isEmpty() && text.contains(query)) {
            results.add(query);
        }
        return results;
    }
}