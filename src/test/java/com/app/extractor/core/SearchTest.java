package com.app.extractor.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SearchTest {
    private final SearchEngine engine = new SearchEngine();

    @Test
    void testExactMatch() {
        List<String> res = engine.find("Hello World", "Hello", SearchEngine.MatchMode.EXACT);
        assertEquals(1, res.size());
        assertEquals("Hello", res.get(0));
    }

    @Test
    void testRegexMatch() {
        List<String> res = engine.find("ID: 12345", "\\d+", SearchEngine.MatchMode.REGEX);
        assertEquals(1, res.size());
        assertEquals("12345", res.get(0));
    }
}