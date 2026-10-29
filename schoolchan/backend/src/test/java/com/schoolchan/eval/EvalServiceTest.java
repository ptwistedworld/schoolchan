package com.schoolchan.eval;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvalServiceTest {

<<<<<<< HEAD
    private final EvalService evalService = new EvalService();
=======
    private final EvalService evalService = new EvalService(new com.fasterxml.jackson.databind.ObjectMapper());
>>>>>>> d5851d3 (Initial commit)

    @Test
    void confusionMatrix_knownExample() {
        Map<String, Object> m = evalService.confusionMatrix(
                List.of("positive", "negative", "positive", "negative"),
                List.of("positive", "negative", "negative", "positive"));
        assertEquals(1, m.get("tp"));
        assertEquals(1, m.get("tn"));
        assertEquals(1, m.get("fp"));
        assertEquals(1, m.get("fn"));
    }

    @Test
    void mcNemar_knownContingency() {
        // b=1, c=5 → (|1-5|-1)^2 / 6 = 9/6 = 1.5
        assertEquals(1.5, evalService.mcNemar(1, 5), 1e-9);
    }
<<<<<<< HEAD
=======

    @Test
    void mcNemarExactPValue_matchesTheBinomialTest() {
        // Exact two-sided p-value for the discordant pairs.
        // b=0, c=10 -> 2 * 0.5^10 = 0.001953125
        assertEquals(0.001953125, evalService.mcNemarPValue(0, 10), 1e-9);
        // No discordant pairs -> p = 1
        assertEquals(1.0, evalService.mcNemarPValue(0, 0), 1e-9);
        // b=3, c=3 -> the exact two-sided p-value is 2 * P(X<=3) with
        // X ~ Binomial(6, 0.5) = 1.3125, capped at 1.
        assertEquals(1.0, evalService.mcNemarPValue(3, 3), 1e-9);
    }
>>>>>>> d5851d3 (Initial commit)
}
