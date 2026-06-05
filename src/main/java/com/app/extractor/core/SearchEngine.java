package com.app.extractor.core;

import com.app.extractor.core.search.SearchField;
import com.app.extractor.core.search.SearchStrategy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Головний двигун пошуку.
 * Він отримує налаштовані поля і застосовує до них відповідні стратегії.
 * Розташований у пакеті core, щоб бути доступним для Orchestrator.
 */
public class SearchEngine {

    // Список полів (наборів даних), які ми ініціалізуємо один раз для пресета
    private final List<SearchField> fields;

    /**
     * Конструктор приймає список полів для обробки.
     */
    public SearchEngine(List<SearchField> fields) {
        this.fields = fields;
    }

    /**
     * Метод: execute
     * Реалізація: Проходить по кожному полю та викликає відповідну стратегію пошуку.
     * @param text витягнутий текст документа (PDF або DOCX).
     * @return Мапа, де Ключ — назва стовпця, Значення — список знайдених збігів.
     */
    public Map<String, List<String>> execute(String text) {
        // Використовуємо LinkedHashMap, щоб зберегти порядок стовпців, як у GUI
        Map<String, List<String>> results = new LinkedHashMap<>();

        for (SearchField field : fields) {
            // Отримуємо стратегію безпосередньо через MatchMode, який зберігається у полі
            SearchStrategy strategy = field.getMatchMode().getStrategy();
            
            // Виконуємо пошук за параметром, який задав користувач
            List<String> matches = strategy.find(text, field.getSearchParameter());
            
            // Записуємо результат під відповідним заголовком стовпця
            results.put(field.getColumnHeader(), matches);
        }

        return results;
    }
}