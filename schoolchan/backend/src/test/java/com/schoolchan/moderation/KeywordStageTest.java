package com.schoolchan.moderation;

import com.schoolchan.board.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeywordStageTest {

    @Mock
    private BlacklistEntryRepository repository;

<<<<<<< HEAD
=======
    @Mock
    private com.schoolchan.board.BoardRuleRepository boardRules;

>>>>>>> d5851d3 (Initial commit)
    private KeywordStage stage;

    @BeforeEach
    void setUp() {
<<<<<<< HEAD
        stage = new KeywordStage(repository);
=======
        org.mockito.Mockito.lenient()
                .when(boardRules.findByBoardIdAndEnabledTrue(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of());
        stage = new KeywordStage(repository, boardRules);
>>>>>>> d5851d3 (Initial commit)
    }

    @Test
    void evaluate_withTokenBlock_scoresHigh() {
        BlacklistEntry entry = entry("TOKEN_BLOCK", "CONTAINS", 0.95);
        when(repository.findByEnabledTrue()).thenReturn(List.of(entry));

        StageResult result = stage.evaluate(new Post(), "please stop TOKEN_BLOCK now");

        assertTrue(result.getScore() >= 0.95);
        assertTrue(result.getReasons().stream().anyMatch(r -> r.contains("TOKEN_BLOCK")));
    }

    @Test
    void evaluate_classic_doesNotWordMatchAss() {
        BlacklistEntry entry = entry("ASS", "WORD", 0.9);
        when(repository.findByEnabledTrue()).thenReturn(List.of(entry));

        StageResult result = stage.evaluate(new Post(), "classic literature club");

        assertEquals(0.0, result.getScore());
    }

    private static BlacklistEntry entry(String pattern, String matchType, double severity) {
        BlacklistEntry e = new BlacklistEntry();
        e.setId(UUID.randomUUID());
        e.setPattern(pattern);
        e.setMatchType(matchType);
        e.setCategory("TEST");
        e.setSeverity(severity);
        e.setEnabled(true);
        return e;
    }
}
