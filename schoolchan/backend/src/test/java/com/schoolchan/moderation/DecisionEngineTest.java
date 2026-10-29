package com.schoolchan.moderation;

<<<<<<< HEAD
import com.schoolchan.config.SchoolChanProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
=======
import com.schoolchan.board.SiteSettingRepository;
import com.schoolchan.config.SchoolChanProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
>>>>>>> d5851d3 (Initial commit)

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
<<<<<<< HEAD

class DecisionEngineTest {

=======
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DecisionEngineTest {

    @Mock
    private SiteSettingRepository siteSettingRepository;

    private SchoolChanProperties props;
    private ModerationPolicy policy;
>>>>>>> d5851d3 (Initial commit)
    private DecisionEngine engine;

    @BeforeEach
    void setUp() {
<<<<<<< HEAD
        SchoolChanProperties props = new SchoolChanProperties();
        engine = new DecisionEngine(props);
    }

    @Test
    void decide_highScore_blocks() {
=======
        props = new SchoolChanProperties();
        lenient().when(siteSettingRepository.findById(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.empty());
        policy = new ModerationPolicy(siteSettingRepository, props);
        engine = new DecisionEngine(props, policy);
    }

    /**
     * The board never removes anything on its own. A high score flags the post
     * for a person to look at.
     */
    @Test
    void decide_highScore_flagsInsteadOfBlocking() {
        ModerationDecision d = engine.decide(List.of(new StageResult("keyword", 0.95, 1)), 0);
        assertEquals(ModerationDecision.QUEUE_FOR_REVIEW, d);
    }

    @Test
    void decide_highScore_blocksOnlyWhenAutoBlockIsOn() {
        policy.setAutoBlockEnabled(true);
>>>>>>> d5851d3 (Initial commit)
        ModerationDecision d = engine.decide(List.of(new StageResult("keyword", 0.95, 1)), 0);
        assertEquals(ModerationDecision.BLOCK, d);
    }

    @Test
<<<<<<< HEAD
    void decide_mlUnavailable_queuesByDefault() {
        ModerationDecision d =
                engine.decide(List.of(new StageResult("ml-text", 0, 1, true)), 0);
        assertEquals(ModerationDecision.QUEUE_FOR_REVIEW, d);
=======
    void decide_lowScore_publishes() {
        ModerationDecision d = engine.decide(List.of(new StageResult("keyword", 0.1, 1)), 0);
        assertEquals(ModerationDecision.PUBLISH, d);
    }

    /** A filter that could not run never holds a post. */
    @Test
    void decide_mlUnavailable_publishes() {
        ModerationDecision d =
                engine.decide(List.of(new StageResult("ml-text", 0, 1, true)), 0);
        assertEquals(ModerationDecision.PUBLISH, d);
    }

    @Test
    void decide_escalationAlwaysEscalates() {
        ModerationDecision d = engine.decide(
                List.of(new StageResult("ml-text", 0.99, 1).withReason("threat=0.99")), 0);
        assertEquals(ModerationDecision.ESCALATE, d);
>>>>>>> d5851d3 (Initial commit)
    }
}
