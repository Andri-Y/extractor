package com.app.extractor.core.search;

/**
 * Enum MatchMode, який володіє своєю стратегією.
 */
public enum MatchMode {
    EXACT(new ExactMatchStrategy()),
    PARTIAL(new PartialMatchStrategy()),
    REGEX(new RegexMatchStrategy()),
    REGEX_PARTIAL(new RegexPartialStrategy());

    private final SearchStrategy strategy;

    MatchMode(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public SearchStrategy getStrategy() {
        return strategy;
    }
}