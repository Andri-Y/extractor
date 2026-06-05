package com.app.extractor.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class RegexMatchStrategy implements SearchStrategy {
    @Override
    public List<String> find(String text, String query) {
        List<String> results = new ArrayList<>();
        if (text == null || query == null || query.isEmpty()) return results;

        try {
            // Компілюємо патерн. Використовуємо CASE_INSENSITIVE за потреби, 
            // але тут лишаємо за замовчуванням для точності.
            Pattern pattern = Pattern.compile(query);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                // Додаємо саме ту частину тексту, яку знайшов Regex
                results.add(matcher.group());
            }
        } catch (PatternSyntaxException e) {
            // Якщо користувач ввів невалідний Regex, повертаємо порожній список.
            // У бойовій версії тут можна додати логування.
        }
        return results;
    }
}