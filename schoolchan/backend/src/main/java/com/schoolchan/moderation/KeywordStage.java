package com.schoolchan.moderation;

import com.schoolchan.board.Post;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import java.util.List;
=======
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
>>>>>>> d5851d3 (Initial commit)

@Component
public class KeywordStage implements ModerationStage {

    private final BlacklistEntryRepository repository;
<<<<<<< HEAD

    public KeywordStage(BlacklistEntryRepository repository) {
        this.repository = repository;
=======
    private final com.schoolchan.board.BoardRuleRepository boardRules;

    public KeywordStage(BlacklistEntryRepository repository,
                        com.schoolchan.board.BoardRuleRepository boardRules) {
        this.repository = repository;
        this.boardRules = boardRules;
>>>>>>> d5851d3 (Initial commit)
    }

    @Override
    public String name() {
        return "keyword";
    }

    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.nanoTime();
<<<<<<< HEAD
=======
        java.util.Set<String> textVariants = KeywordNormalizer.variants(combinedText);
>>>>>>> d5851d3 (Initial commit)
        String normalizedText = KeywordNormalizer.normalize(combinedText);
        double max = 0.0;
        StageResult result = new StageResult(name(), 0, 0);
        List<BlacklistEntry> entries = repository.findByEnabledTrue();
        for (BlacklistEntry entry : entries) {
<<<<<<< HEAD
            String pattern = KeywordNormalizer.normalize(entry.getPattern());
            if (pattern.isEmpty()) {
                continue;
            }
            if (matches(normalizedText, combinedText, entry.getMatchType(), pattern, entry.getPattern())) {
=======
            java.util.Set<String> patternVariants = KeywordNormalizer.variants(entry.getPattern());
            if (patternVariants.isEmpty()) {
                continue;
            }
            if (matches(textVariants, normalizedText, combinedText, entry.getMatchType(), patternVariants, entry.getPattern())) {
>>>>>>> d5851d3 (Initial commit)
                max = Math.max(max, entry.getSeverity());
                result.withReason(entry.getCategory() + ":" + entry.getPattern());
            }
        }
<<<<<<< HEAD
=======
        // Board-specific rules sit alongside the global list.
        if (post != null && post.getBoardId() != null) {
            for (var rule : boardRules.findByBoardIdAndEnabledTrue(post.getBoardId())) {
                java.util.Set<String> patternVariants = KeywordNormalizer.variants(rule.getPattern());
                if (patternVariants.isEmpty()) {
                    continue;
                }
                if (matches(textVariants, normalizedText, combinedText, rule.getMatchType(),
                        patternVariants, rule.getPattern())) {
                    max = Math.max(max, rule.getSeverity());
                    result.withReason("board:" + rule.getCategory() + ":" + rule.getPattern());
                }
            }
        }
>>>>>>> d5851d3 (Initial commit)
        long ms = (System.nanoTime() - start) / 1_000_000;
        StageResult out = new StageResult(name(), max, ms);
        out.getReasons().addAll(result.getReasons());
        return out;
    }

<<<<<<< HEAD
    private boolean matches(
            String normalizedText, String rawText, String matchType, String normalizedPattern, String rawPattern) {
        if ("WORD".equalsIgnoreCase(matchType)) {
            return containsToken(normalizedText, normalizedPattern);
=======
    /**
     * Explains how a string would be screened. Used by the staff
     * "test a string" tool so moderators can see the normalisation and
     * which entries would fire.
     */
    public List<Map<String, Object>> test(String text) {
        List<Map<String, Object>> hits = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return hits;
        }
        java.util.Set<String> textVariants = KeywordNormalizer.variants(text);
        String normalized = KeywordNormalizer.normalize(text);
        for (BlacklistEntry entry : repository.findAll()) {
            java.util.Set<String> patternVariants = KeywordNormalizer.variants(entry.getPattern());
            if (patternVariants.isEmpty()) {
                continue;
            }
            boolean matched = matches(textVariants, normalized, text, entry.getMatchType(), patternVariants, entry.getPattern());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("pattern", entry.getPattern());
            row.put("matchType", entry.getMatchType());
            row.put("category", entry.getCategory());
            row.put("severity", entry.getSeverity());
            row.put("enabled", entry.isEnabled());
            row.put("matched", matched);
            row.put("normalizedPattern", KeywordNormalizer.normalize(entry.getPattern()));
            row.put("normalizedText", normalized);
            hits.add(row);
        }
        hits.sort((a, b) -> Boolean.compare((Boolean) b.get("matched"), (Boolean) a.get("matched")));
        return hits;
    }

    private boolean matches(
            java.util.Set<String> textVariants,
            String normalizedText,
            String rawText,
            String matchType,
            java.util.Set<String> patternVariants,
            String rawPattern) {
        if ("WORD".equalsIgnoreCase(matchType)) {
            for (String pattern : patternVariants) {
                for (String variant : textVariants) {
                    if (containsToken(variant, pattern)) {
                        return true;
                    }
                }
            }
            return false;
>>>>>>> d5851d3 (Initial commit)
        }
        if ("REGEX".equalsIgnoreCase(matchType)) {
            return rawText.matches("(?is).*" + rawPattern + ".*");
        }
<<<<<<< HEAD
        return normalizedText.contains(normalizedPattern)
                || rawText.toUpperCase().contains(rawPattern.toUpperCase());
=======
        for (String pattern : patternVariants) {
            for (String variant : textVariants) {
                if (variant.contains(pattern)) {
                    return true;
                }
            }
        }
        return rawText.toUpperCase().contains(rawPattern.toUpperCase());
>>>>>>> d5851d3 (Initial commit)
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
