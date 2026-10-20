package com.schoolchan.eval;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvalServiceTest {

    private final EvalService evalService = new EvalService();

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
}
