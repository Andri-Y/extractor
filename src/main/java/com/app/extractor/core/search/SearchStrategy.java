package com.app.extractor.core.search;

import java.util.List;

/**
 * Інтерфейс стратегії пошуку. Кожна реалізація відповідає за свій тип співпадіння.
 */
public interface SearchStrategy {
    /**
     * @param text Повний текст документа.
     * @param query Пошуковий запит (рядок або регулярний вираз).
     * @return Список знайдених результатів.
     */
    List<String> find(String text, String query);
}