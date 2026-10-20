package com.schoolchan.reputation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TextSimilarityTest {

    @Test
    void jaccard_identicalTokens_isOne() {
        assertTrue(TextSimilarity.jaccard("campus bulletin notice today", "campus bulletin notice today") > 0.99);
    }
}
