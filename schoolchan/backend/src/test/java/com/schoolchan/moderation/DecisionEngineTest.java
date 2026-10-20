package com.schoolchan.moderation;

import com.schoolchan.config.SchoolChanProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecisionEngineTest {

    private DecisionEngine engine;

    @BeforeEach
    void setUp() {
        SchoolChanProperties props = new SchoolChanProperties();
        engine = new DecisionEngine(props);
    }

    @Test
    void decide_highScore_blocks() {
        ModerationDecision d = engine.decide(List.of(new StageResult("keyword", 0.95, 1)), 0);
        assertEquals(ModerationDecision.BLOCK, d);
    }

    @Test
    void decide_mlUnavailable_queuesByDefault() {
        ModerationDecision d =
                engine.decide(List.of(new StageResult("ml-text", 0, 1, true)), 0);
        assertEquals(ModerationDecision.QUEUE_FOR_REVIEW, d);
    }
}
