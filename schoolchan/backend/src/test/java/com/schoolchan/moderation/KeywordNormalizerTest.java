package com.schoolchan.moderation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeywordNormalizerTest {

    /** Mirrors the WORD match type used by the keyword stage. */
    private boolean matches(String text, String pattern) {
        var textVariants = KeywordNormalizer.variants(text);
        var patternVariants = KeywordNormalizer.variants(pattern);
        for (String p : patternVariants) {
            for (String v : textVariants) {
                if (containsToken(v, p)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsToken(String text, String pattern) {
        if (pattern.isEmpty()) {
            return false;
        }
        int idx = text.indexOf(pattern);
        while (idx >= 0) {
            boolean leftOk = idx == 0 || !Character.isLetterOrDigit(text.charAt(idx - 1));
            int end = idx + pattern.length();
            boolean rightOk = end >= text.length() || !Character.isLetterOrDigit(text.charAt(end));
            if (leftOk && rightOk) {
                return true;
            }
            idx = text.indexOf(pattern, idx + 1);
        }
        return false;
    }

    @Test
    void plainTextMatches() {
        assertTrue(matches("you are a gago", "gago"));
    }

    @Test
    void spacedLettersAreFolded() {
        assertTrue(matches("p u t a n g i n a", "putangina"));
        assertTrue(matches("g a g o", "gago"));
    }

    @Test
    void repeatedLettersAreFolded() {
        assertTrue(matches("gagoooo", "gago"));
        assertTrue(matches("putaaaaangina", "putangina"));
    }

    @Test
    void leetspeakIsFolded() {
        assertTrue(matches("9a90", "gago"));
    }

    @Test
    void accentsAreStrippedNotDeleted() {
        // "putangína" -> "putangina", the accented letter must survive.
        assertTrue(matches("putangína", "putangina"));
        assertTrue(matches("gágō", "gago"));
    }

    @Test
    void confusableCyrillicIsFolded() {
        // Cyrillic "а" (U+0430) and "е" (U+0435) look like Latin a and e.
        assertTrue(matches("gаgo", "gago"));
    }

    @Test
    void scunthorpeCaseIsNotFlagged() {
        // "class" contains "ass"; word-boundary matching must not fire.
        assertFalse(matches("this is a class about mass communication", "ass"));
        assertFalse(matches("assignment submitted", "ment"));
    }

    @Test
    void plainSubstringStillMatchesWhenAskedFor() {
        // CONTAINS style matching (no word boundary) is still possible.
        assertTrue(KeywordNormalizer.normalize("assignment").contains(
                KeywordNormalizer.normalize("sign")));
    }

    @Test
    void spacedWordIsNotMergedWithTheNextLine() {
        // The pipeline screens the original and its translation together,
        // separated by a newline. The spaced word must still be caught.
        assertTrue(matches("p u t a n g i n a\nHello, this is a stub English translation.", "putangina"));
    }
}
