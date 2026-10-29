package com.schoolchan.eval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

=======
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The benchmark harness.
 *
 * <p>Each fixture row is screened twice: once in <em>keyword-only</em> mode (the
 * paper's baseline) and once with the full pipeline. The harness reports the
 * confusion matrix, precision, recall and F1 overall and per language, the
 * McNemar contingency table with an exact two-sided p-value, and the latency
 * of every stage.
 */
>>>>>>> d5851d3 (Initial commit)
@Service
public class EvalService {

    private final ObjectMapper objectMapper;
<<<<<<< HEAD

    public EvalService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EvalService() {
        this(new ObjectMapper());
    }
=======
    private final com.schoolchan.moderation.KeywordStage keywordStage;
    private final com.schoolchan.moderation.MlTextStage mlStage;
    private final com.schoolchan.moderation.DecisionEngine decisionEngine;
    private final com.schoolchan.translation.TranslationClient translationClient;
    private final Map<String, String> translationCache = new java.util.concurrent.ConcurrentHashMap<>();

    @org.springframework.beans.factory.annotation.Autowired
    public EvalService(ObjectMapper objectMapper,
                       org.springframework.beans.factory.ObjectProvider<com.schoolchan.moderation.KeywordStage> keywordStage,
                       org.springframework.beans.factory.ObjectProvider<com.schoolchan.moderation.MlTextStage> mlStage,
                       org.springframework.beans.factory.ObjectProvider<com.schoolchan.moderation.DecisionEngine> decisionEngine,
                       org.springframework.beans.factory.ObjectProvider<com.schoolchan.translation.TranslationClient> translationClient) {
        this.objectMapper = objectMapper;
        this.keywordStage = keywordStage.getIfAvailable();
        this.mlStage = mlStage.getIfAvailable();
        this.decisionEngine = decisionEngine.getIfAvailable();
        this.translationClient = translationClient.getIfAvailable();
    }

    /** For unit tests, without the live moderation stages. */
    public EvalService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.keywordStage = null;
        this.mlStage = null;
        this.decisionEngine = null;
        this.translationClient = null;
    }

    // ------------------------------------------------------------------
    // Statistics
    // ------------------------------------------------------------------
>>>>>>> d5851d3 (Initial commit)

    public Map<String, Object> confusionMatrix(List<String> predicted, List<String> actual) {
        if (predicted.size() != actual.size()) {
            throw new IllegalArgumentException("size mismatch");
        }
        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < predicted.size(); i++) {
            boolean p = isPositive(predicted.get(i));
            boolean a = isPositive(actual.get(i));
            if (p && a) tp++;
            else if (!p && !a) tn++;
            else if (p && !a) fp++;
            else fn++;
        }
        double precision = tp + fp == 0 ? 0 : (double) tp / (tp + fp);
        double recall = tp + fn == 0 ? 0 : (double) tp / (tp + fn);
        double f1 = precision + recall == 0 ? 0 : 2 * precision * recall / (precision + recall);
        double accuracy = predicted.isEmpty() ? 0 : (double) (tp + tn) / predicted.size();
<<<<<<< HEAD
        Map<String, Object> out = new HashMap<>();
=======
        Map<String, Object> out = new LinkedHashMap<>();
>>>>>>> d5851d3 (Initial commit)
        out.put("tp", tp);
        out.put("tn", tn);
        out.put("fp", fp);
        out.put("fn", fn);
        out.put("precision", precision);
        out.put("recall", recall);
        out.put("f1", f1);
        out.put("accuracy", accuracy);
        return out;
    }

<<<<<<< HEAD
    public double mcNemar(int b, int c) {
        if (b + c == 0) {
            return 1.0;
        }
        return Math.pow(Math.abs(b - c) - 1.0, 2) / (b + c);
    }

    /**
     * Runs a keyword-only vs hybrid (TOKEN-aware) comparison on a JSONL fixture.
     * Hybrid here uses both TOKEN_BLOCK and TOKEN_ESCALATE_* markers; baseline uses TOKEN_BLOCK only.
     */
    public Map<String, Object> runFixture(Path jsonl) throws IOException {
        List<String> actual = new ArrayList<>();
        List<String> baselinePred = new ArrayList<>();
        List<String> hybridPred = new ArrayList<>();
        int b = 0, c = 0;
        long started = System.nanoTime();
=======
    /** The McNemar statistic with the continuity correction. */
    public double mcNemar(int b, int c) {
        if (b + c == 0) {
            return 0.0;
        }
        double diff = Math.abs(b - c) - 1.0;
        if (diff < 0) {
            diff = 0;
        }
        return (diff * diff) / (b + c);
    }

    /**
     * The exact two-sided McNemar p-value (binomial test on the discordant
     * pairs). This is the recommended test for small samples, and it needs no
     * external statistics library.
     */
    public double mcNemarPValue(int b, int c) {
        int n = b + c;
        if (n == 0) {
            return 1.0;
        }
        int k = Math.min(b, c);
        double tail = 0;
        for (int i = 0; i <= k; i++) {
            tail += binomialCoefficient(n, i) * Math.pow(0.5, n);
        }
        return Math.min(1.0, tail * 2);
    }

    private static double binomialCoefficient(int n, int k) {
        double result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Running the harness
    // ------------------------------------------------------------------

    public record FixtureRow(String id, String text, String label, String subcategory, String language) {}

    public List<FixtureRow> readFixture(Path jsonl) throws IOException {
        List<FixtureRow> rows = new ArrayList<>();
>>>>>>> d5851d3 (Initial commit)
        for (String line : Files.readAllLines(jsonl)) {
            if (line.isBlank()) {
                continue;
            }
            JsonNode node = objectMapper.readTree(line);
<<<<<<< HEAD
            String text = node.path("text").asText("").toUpperCase(Locale.ROOT);
            boolean violating = "violating".equalsIgnoreCase(node.path("label").asText());
            actual.add(violating ? "positive" : "negative");

            boolean baseHit = text.contains("TOKEN_BLOCK");
            boolean hybridHit = baseHit || text.contains("TOKEN_ESCALATE");
            baselinePred.add(baseHit ? "positive" : "negative");
            hybridPred.add(hybridHit ? "positive" : "negative");
=======
            rows.add(new FixtureRow(
                    node.path("id").asText(""),
                    node.path("text").asText(""),
                    node.path("label").asText("non-violating"),
                    node.path("subcategory").asText(""),
                    node.path("language").asText("unknown")));
        }
        return rows;
    }

    /**
     * Screens every fixture row in baseline and hybrid mode and reports the
     * comparison. When the moderation stages are not wired in (unit tests), it
     * falls back to a token scan so the harness still produces a report.
     */
    public Map<String, Object> runFixture(Path jsonl) throws IOException {
        List<FixtureRow> rows = readFixture(jsonl);
        List<String> actual = new ArrayList<>();
        List<String> baselinePred = new ArrayList<>();
        List<String> hybridPred = new ArrayList<>();
        Map<String, List<String>> actualByLanguage = new LinkedHashMap<>();
        Map<String, List<String>> baselineByLanguage = new LinkedHashMap<>();
        Map<String, List<String>> hybridByLanguage = new LinkedHashMap<>();
        List<Long> totalLatencies = new ArrayList<>();
        Map<String, List<Long>> stageLatencies = new LinkedHashMap<>();

        int b = 0;
        int c = 0;

        for (FixtureRow row : rows) {
            String truth = isPositive(row.label()) ? "positive" : "negative";
            actual.add(truth);
            actualByLanguage.computeIfAbsent(row.language(), k -> new ArrayList<>()).add(truth);

            boolean baseHit;
            boolean hybridHit;
            long elapsedMs = 0;
            if (keywordStage != null) {
                long start = System.nanoTime();
                var baseResult = keywordStage.evaluate(null, row.text());
                var baseHitScore = baseResult.getScore();
                baseHit = baseHitScore >= decisionEngineThreshold();
                hybridHit = baseHit;
                elapsedMs = (System.nanoTime() - start) / 1_000_000;
                totalLatencies.add(elapsedMs);
                stageLatencies.computeIfAbsent("keyword", k -> new ArrayList<>()).add(baseResult.getLatencyMs());
                if (mlStage != null) {
                    // Screen the post the way the app does: the original text,
                    // plus its translation when there is one.
                    com.schoolchan.board.Post probe = new com.schoolchan.board.Post();
                    probe.setOriginalText(row.text());
                    probe.setDetectedLanguage(row.language());
                    String textToScreen = row.text();
                    String translation = translateIfPossible(row);
                    if (translation != null) {
                        probe.setTranslatedText(translation);
                        textToScreen = row.text() + "\n" + translation;
                    }
                    var mlResult = mlStage.evaluate(probe, textToScreen);
                    hybridHit = baseHit || mlResult.getScore() >= decisionEngineThreshold();
                    stageLatencies.computeIfAbsent("ml-text", k -> new ArrayList<>()).add(mlResult.getLatencyMs());
                }
            } else {
                String upper = row.text().toUpperCase();
                baseHit = upper.contains("TOKEN_BLOCK");
                hybridHit = baseHit || upper.contains("TOKEN_ESCALATE_THREAT");
            }

            baselinePred.add(baseHit ? "positive" : "negative");
            hybridPred.add(hybridHit ? "positive" : "negative");
            baselineByLanguage.computeIfAbsent(row.language(), k -> new ArrayList<>())
                    .add(baseHit ? "positive" : "negative");
            hybridByLanguage.computeIfAbsent(row.language(), k -> new ArrayList<>())
                    .add(hybridHit ? "positive" : "negative");
>>>>>>> d5851d3 (Initial commit)

            if (baseHit != hybridHit) {
                if (!baseHit && hybridHit) {
                    b++;
                } else {
                    c++;
                }
            }
        }
<<<<<<< HEAD
        long elapsed = (System.nanoTime() - started) / 1_000_000L;
        Map<String, Object> report = new HashMap<>();
=======

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("rows", rows.size());
>>>>>>> d5851d3 (Initial commit)
        report.put("baseline", confusionMatrix(baselinePred, actual));
        report.put("hybrid", confusionMatrix(hybridPred, actual));
        report.put("mcnemar_b", b);
        report.put("mcnemar_c", c);
        report.put("mcnemar_chi_square", mcNemar(b, c));
<<<<<<< HEAD
        report.put("fixture_latency_ms", elapsed);
        return report;
    }

=======
        report.put("mcnemar_p_value", mcNemarPValue(b, c));
        report.put("fixture_latency_ms", totalLatencies.isEmpty() ? 0 : sum(totalLatencies));
        report.put("latency", latencySummary(totalLatencies, stageLatencies));
        report.put("by_language", byLanguage(actualByLanguage, baselineByLanguage, hybridByLanguage));
        return report;
    }

    /** Uses the sidecar to translate a row, when one is available. */
    private String translateIfPossible(FixtureRow row) {
        if (translationClient == null) {
            return null;
        }
        if (row.language() == null || row.language().isBlank() || "en".equalsIgnoreCase(row.language())) {
            return null;
        }
        // The fixture repeats sentences, so cache by text: the translation model
        // is the slowest thing in the harness by far.
        String cached = translationCache.get(row.text());
        if (cached != null) {
            return cached.isEmpty() ? null : cached;
        }
        String result;
        try {
            result = translationClient.translate(row.text())
                    .map(t -> t.translatedText())
                    .filter(s -> s != null && !s.isBlank() && !s.equals(row.text()))
                    .orElse(null);
        } catch (Exception e) {  // noqa: BLE001
            result = null;
        }
        translationCache.put(row.text(), result == null ? "" : result);
        return result;
    }

    private double decisionEngineThreshold() {
        if (decisionEngine == null) {
            return 0.55;
        }
        try {
            var field = decisionEngine.getClass().getDeclaredField("queueThreshold");
            field.setAccessible(true);
            return field.getDouble(decisionEngine);
        } catch (Exception e) {
            return 0.55;
        }
    }

    private Map<String, Object> byLanguage(Map<String, List<String>> actual,
                                          Map<String, List<String>> baseline,
                                          Map<String, List<String>> hybrid) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String language : actual.keySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("language", language);
            row.put("n", actual.get(language).size());
            row.put("baseline", confusionMatrix(baseline.get(language), actual.get(language)));
            row.put("hybrid", confusionMatrix(hybrid.get(language), actual.get(language)));
            rows.add(row);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("rows", rows);
        return out;
    }

    private Map<String, Object> latencySummary(List<Long> totals, Map<String, List<Long>> stages) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("total", stats(totals));
        List<Map<String, Object>> stageRows = new ArrayList<>();
        stages.forEach((name, values) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("stage", name);
            row.putAll(stats(values));
            stageRows.add(row);
        });
        out.put("stages", stageRows);
        return out;
    }

    private Map<String, Object> stats(List<Long> values) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (values.isEmpty()) {
            out.put("n", 0);
            out.put("mean_ms", 0);
            out.put("median_ms", 0);
            out.put("p95_ms", 0);
            out.put("max_ms", 0);
            return out;
        }
        List<Long> sorted = new ArrayList<>(values);
        sorted.sort(Long::compareTo);
        double mean = sorted.stream().mapToLong(Long::longValue).average().orElse(0);
        out.put("n", sorted.size());
        out.put("mean_ms", round(mean));
        out.put("median_ms", sorted.get(sorted.size() / 2));
        out.put("p95_ms", sorted.get(Math.max(0, (int) Math.ceil(0.95 * sorted.size()) - 1)));
        out.put("max_ms", sorted.get(sorted.size() - 1));
        return out;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static long sum(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).sum();
    }

    // ------------------------------------------------------------------
    // Output
    // ------------------------------------------------------------------

>>>>>>> d5851d3 (Initial commit)
    public void writeReport(Map<String, Object> report, Path directory) throws IOException {
        Files.createDirectories(directory);
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(directory.resolve("accuracy.json").toFile(), report.get("hybrid"));
<<<<<<< HEAD
        Map<String, Object> mcnemar = new HashMap<>();
        mcnemar.put("b", report.get("mcnemar_b"));
        mcnemar.put("c", report.get("mcnemar_c"));
        mcnemar.put("chi_square", report.get("mcnemar_chi_square"));
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(directory.resolve("mcnemar.json").toFile(), mcnemar);
        String latency = "# Fixture latency\n\n"
                + "Total comparison time: " + report.get("fixture_latency_ms") + " ms\n";
        Files.writeString(directory.resolve("latency.md"), latency);
=======
        Map<String, Object> mcnemar = new LinkedHashMap<>();
        mcnemar.put("b", report.get("mcnemar_b"));
        mcnemar.put("c", report.get("mcnemar_c"));
        mcnemar.put("chi_square", report.get("mcnemar_chi_square"));
        mcnemar.put("p_value", report.get("mcnemar_p_value"));
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(directory.resolve("mcnemar.json").toFile(), mcnemar);
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(directory.resolve("report.json").toFile(), report);
        Files.writeString(directory.resolve("latency.md"), latencyMarkdown(report));
        Files.writeString(directory.resolve("by-language.md"), byLanguageMarkdown(report));
    }

    private String latencyMarkdown(Map<String, Object> report) {
        @SuppressWarnings("unchecked")
        Map<String, Object> latency = (Map<String, Object>) report.get("latency");
        @SuppressWarnings("unchecked")
        Map<String, Object> total = (Map<String, Object>) latency.get("total");
        StringBuilder sb = new StringBuilder("# Latency (milliseconds)\n\n");
        sb.append("| stage | n | mean | median | p95 | max |\n|---|---|---|---|---|---|\n");
        sb.append("| total | ").append(total.get("n")).append(" | ").append(total.get("mean_ms"))
                .append(" | ").append(total.get("median_ms")).append(" | ").append(total.get("p95_ms"))
                .append(" | ").append(total.get("max_ms")).append(" |\n");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> stages = (List<Map<String, Object>>) latency.get("stages");
        for (Map<String, Object> s : stages) {
            sb.append("| ").append(s.get("stage")).append(" | ").append(s.get("n")).append(" | ")
                    .append(s.get("mean_ms")).append(" | ").append(s.get("median_ms")).append(" | ")
                    .append(s.get("p95_ms")).append(" | ").append(s.get("max_ms")).append(" |\n");
        }
        return sb.toString();
    }

    private String byLanguageMarkdown(Map<String, Object> report) {
        @SuppressWarnings("unchecked")
        Map<String, Object> byLanguage = (Map<String, Object>) report.get("by_language");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) byLanguage.get("rows");
        StringBuilder sb = new StringBuilder("# Accuracy by language\n\n");
        sb.append("| language | n | baseline acc | hybrid acc | hybrid F1 |\n|---|---|---|---|---|\n");
        for (Map<String, Object> row : rows) {
            @SuppressWarnings("unchecked")
            Map<String, Object> base = (Map<String, Object>) row.get("baseline");
            @SuppressWarnings("unchecked")
            Map<String, Object> hyb = (Map<String, Object>) row.get("hybrid");
            sb.append("| ").append(row.get("language")).append(" | ").append(row.get("n"))
                    .append(" | ").append(round((Double) base.get("accuracy")))
                    .append(" | ").append(round((Double) hyb.get("accuracy")))
                    .append(" | ").append(round((Double) hyb.get("f1"))).append(" |\n");
        }
        return sb.toString();
>>>>>>> d5851d3 (Initial commit)
    }

    private boolean isPositive(String label) {
        return "positive".equalsIgnoreCase(label) || "violating".equalsIgnoreCase(label);
    }
}
