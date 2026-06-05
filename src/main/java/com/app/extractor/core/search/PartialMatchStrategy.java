package com.app.extractor.core.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PartialMatchStrategy implements SearchStrategy {
    @Override
    public List<String> find(String text, String query) {
        if (text == null || query == null || query.isEmpty()) return new ArrayList<>();

        // Розбиваємо текст на слова по пробілах та переносах рядків
        String[] words = text.split("\\s+");

        // Використовуємо Stream API для фільтрації слів, що містять запит
        return Arrays.stream(words)
                .filter(word -> word.contains(query))
                .distinct() // Прибираємо дублікати для чистоти списку
                .collect(Collectors.toList());
    }
}