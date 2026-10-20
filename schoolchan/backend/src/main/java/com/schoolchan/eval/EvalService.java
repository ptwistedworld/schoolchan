package com.schoolchan.eval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EvalService {

    private final ObjectMapper objectMapper;

    public EvalService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EvalService() {
        this(new ObjectMapper());
    }

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
        Map<String, Object> out = new HashMap<>();
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
        for (String line : Files.readAllLines(jsonl)) {
            if (line.isBlank()) {
                continue;
            }
            JsonNode node = objectMapper.readTree(line);
            String text = node.path("text").asText("").toUpperCase(Locale.ROOT);
            boolean violating = "violating".equalsIgnoreCase(node.path("label").asText());
            actual.add(violating ? "positive" : "negative");

            boolean baseHit = text.contains("TOKEN_BLOCK");
            boolean hybridHit = baseHit || text.contains("TOKEN_ESCALATE");
            baselinePred.add(baseHit ? "positive" : "negative");
            hybridPred.add(hybridHit ? "positive" : "negative");

            if (baseHit != hybridHit) {
                if (!baseHit && hybridHit) {
                    b++;
                } else {
                    c++;
                }
            }
        }
        long elapsed = (System.nanoTime() - started) / 1_000_000L;
        Map<String, Object> report = new HashMap<>();
        report.put("baseline", confusionMatrix(baselinePred, actual));
        report.put("hybrid", confusionMatrix(hybridPred, actual));
        report.put("mcnemar_b", b);
        report.put("mcnemar_c", c);
        report.put("mcnemar_chi_square", mcNemar(b, c));
        report.put("fixture_latency_ms", elapsed);
        return report;
    }

    public void writeReport(Map<String, Object> report, Path directory) throws IOException {
        Files.createDirectories(directory);
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(directory.resolve("accuracy.json").toFile(), report.get("hybrid"));
        Map<String, Object> mcnemar = new HashMap<>();
        mcnemar.put("b", report.get("mcnemar_b"));
        mcnemar.put("c", report.get("mcnemar_c"));
        mcnemar.put("chi_square", report.get("mcnemar_chi_square"));
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(directory.resolve("mcnemar.json").toFile(), mcnemar);
        String latency = "# Fixture latency\n\n"
                + "Total comparison time: " + report.get("fixture_latency_ms") + " ms\n";
        Files.writeString(directory.resolve("latency.md"), latency);
    }

    private boolean isPositive(String label) {
        return "positive".equalsIgnoreCase(label) || "violating".equalsIgnoreCase(label);
    }
}
