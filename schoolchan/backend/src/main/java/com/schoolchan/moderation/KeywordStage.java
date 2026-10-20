package com.schoolchan.moderation;

import com.schoolchan.board.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeywordStage implements ModerationStage {

    private final BlacklistEntryRepository repository;

    public KeywordStage(BlacklistEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public String name() {
        return "keyword";
    }

    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.nanoTime();
        String normalizedText = KeywordNormalizer.normalize(combinedText);
        double max = 0.0;
        StageResult result = new StageResult(name(), 0, 0);
        List<BlacklistEntry> entries = repository.findByEnabledTrue();
        for (BlacklistEntry entry : entries) {
            String pattern = KeywordNormalizer.normalize(entry.getPattern());
            if (pattern.isEmpty()) {
                continue;
            }
            if (matches(normalizedText, combinedText, entry.getMatchType(), pattern, entry.getPattern())) {
                max = Math.max(max, entry.getSeverity());
                result.withReason(entry.getCategory() + ":" + entry.getPattern());
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        StageResult out = new StageResult(name(), max, ms);
        out.getReasons().addAll(result.getReasons());
        return out;
    }

    private boolean matches(
            String normalizedText, String rawText, String matchType, String normalizedPattern, String rawPattern) {
        if ("WORD".equalsIgnoreCase(matchType)) {
            return containsToken(normalizedText, normalizedPattern);
        }
        if ("REGEX".equalsIgnoreCase(matchType)) {
            return rawText.matches("(?is).*" + rawPattern + ".*");
        }
        return normalizedText.contains(normalizedPattern)
                || rawText.toUpperCase().contains(rawPattern.toUpperCase());
    }

    private boolean containsToken(String normalizedText, String normalizedPattern) {
        int idx = normalizedText.indexOf(normalizedPattern);
        while (idx >= 0) {
            boolean leftOk = idx == 0 || !Character.isLetterOrDigit(normalizedText.charAt(idx - 1));
            int end = idx + normalizedPattern.length();
            boolean rightOk = end >= normalizedText.length() || !Character.isLetterOrDigit(normalizedText.charAt(end));
            if (leftOk && rightOk) {
                return true;
            }
            idx = normalizedText.indexOf(normalizedPattern, idx + 1);
        }
        return false;
    }
}
