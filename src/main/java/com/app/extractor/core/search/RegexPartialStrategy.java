package com.app.extractor.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexPartialStrategy implements SearchStrategy {
    @Override
    public List<String> find(String text, String query) {
        List<String> results = new ArrayList<>();
        if (text == null || query == null || query.isEmpty()) return results;

        try {
            // Створюємо "м'який" патерн, який шукає будь-які непробільні символи навколо запиту
            String partialRegex = "\\S*" + query + "\\S*";
            Pattern pattern = Pattern.compile(partialRegex);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                results.add(matcher.group());
            }
        } catch (Exception e) {
            // Обробка помилок компіляції виразу
        }
        return results;
    }
}