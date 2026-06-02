package com.app.extractor.core;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Сервіс для пошуку даних у тексті за різними стратегіями.
 */
public class SearchEngine {
    
    public enum MatchMode {
        EXACT("Точне"),
        PARTIAL("Часткове"),
        REGEX("Regex точне"),
        REGEX_PARTIAL("Regex часткове");

        private final String label;
        MatchMode(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public List<String> find(String text, String query, MatchMode mode) {
        if (text == null || query == null || query.isEmpty()) return Collections.emptyList();
        
        Set<String> results = new LinkedHashSet<>();
        
        switch (mode) {
            case EXACT:
                if (text.contains(query)) results.add(query);
                break;
            case PARTIAL:
                Arrays.stream(text.split("\\s+"))
                      .filter(word -> word.contains(query))
                      .forEach(results::add);
                break;
            case REGEX:
                extractByRegex(text, query, results, false);
                break;
            case REGEX_PARTIAL:
                extractByRegex(text, query, results, true);
                break;
        }
        return new ArrayList<>(results);
    }

    private void extractByRegex(String text, String regex, Set<String> res, boolean partial) {
        try {
            Pattern p = Pattern.compile(partial ? ".*" + regex + ".*" : regex);
            Matcher m = p.matcher(text);
            while (m.find()) res.add(m.group());
        } catch (PatternSyntaxException ignored) {}
    }
}