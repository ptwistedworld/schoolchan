package com.schoolchan.reputation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class TextSimilarity {

    private static final Set<String> STOP = Set.of(
            "the", "and", "for", "that", "with", "this", "from", "have", "your", "ang", "mga", "nga");

    private TextSimilarity() {}

    public static double jaccard(String left, String right) {
        Set<String> a = tokens(left);
        Set<String> b = tokens(right);
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return (double) intersection.size() / union.size();
    }

    public static Set<String> tokens(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(t -> t.length() >= 4)
                .filter(t -> !STOP.contains(t))
                .collect(java.util.stream.Collectors.toSet());
    }
}
