package com.schoolchan.moderation;

<<<<<<< HEAD
=======
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Text normalisation for the keyword/blacklist stage.
 *
 * <p>Handles the obfuscation families that matter for a multilingual school board:
 * leetspeak, repeated characters, spaced-out letters, accents, and Cyrillic /
 * Greek homoglyphs. Normalisation produces a <em>set</em> of variants so a
 * pattern still matches when only one obfuscation is used.
 */
>>>>>>> d5851d3 (Initial commit)
public final class KeywordNormalizer {

    private KeywordNormalizer() {}

<<<<<<< HEAD
=======
    /** Homoglyph folding for Cyrillic and Greek characters that look like Latin. */
    private static final String CONFUSABLES =
            "аеорсухАЕОРСТХ" // Cyrillic a e o p c y x
            + "ѕіјһѕІЈЅ" // Serbian/Cyrillic variants
            + "αβγδεζηθικλμνξορστυφχψω" // Greek
            + "ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΡΣΤΥΦΧΨΩ";

    private static final String CONFUSABLE_REPLACEMENTS =
            "aeopcyxAEOPCTX"
            + "sijhsIJHS"
            + "abgdezetlmnxopstufxopstufx"
            + "ABGDEZETLMNXOPSTUFXOPSTUFX";

    /**
     * Primary normalisation: lowercase, fold homoglyphs, strip accents, map
     * leetspeak, then drop everything that is not a letter or digit.
     */
>>>>>>> d5851d3 (Initial commit)
    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String lower = input.toLowerCase();
        StringBuilder sb = new StringBuilder(lower.length());
        for (char c : lower.toCharArray()) {
<<<<<<< HEAD
            sb.append(mapLeet(c));
=======
            sb.append(mapLeet(foldConfusable(c)));
>>>>>>> d5851d3 (Initial commit)
        }
        return sb.toString().replaceAll("[^a-z0-9]+", "");
    }

<<<<<<< HEAD
=======
    /**
     * All normalised forms of the input. A blacklist pattern matches if any
     * variant contains it, so "s a m a", "sama" and "saaaaama" all collapse to
     * the same token.
     *
     * <p>Variants are produced for the whole string <em>and</em> for each
     * whitespace-separated token. Per-token variants keep word boundaries
     * intact, which is what stops a collapsed token from swallowing the word
     * next to it.
     */
    public static Set<String> variants(String input) {
        Set<String> out = new LinkedHashSet<>();
        if (input == null || input.isBlank()) {
            return out;
        }
        // Work line by line so a spaced-out word cannot merge with the text on
        // the next line (the pipeline screens the original and its translation
        // together, separated by a newline).
        for (String line : input.split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            out.addAll(stringVariants(line));
            for (String token : line.split("\\s+")) {
                if (!token.isBlank()) {
                    out.addAll(stringVariants(token));
                }
            }
        }
        out.removeIf(s -> s == null || s.isEmpty());
        return out;
    }

    private static Set<String> stringVariants(String input) {
        Set<String> out = new LinkedHashSet<>();
        String lower = input.toLowerCase();
        String folded = foldHomoglyphs(lower);

        // 1. plain: strip accents, leet-map, drop separators
        out.add(stripAccents(folded));

        // 2. de-spaced: "s a m a" -> "sama"
        String compact = deSpace(folded);
        out.add(stripAccents(compact));

        // 3. de-repeated: "sammmaa" -> "sama"
        out.add(stripAccents(deRepeat(folded)));
        out.add(stripAccents(deRepeat(compact)));

        // 4. de-spaced + de-repeated + de-leet (the most aggressive form)
        String leet = deLeet(stripAccents(compact));
        out.add(deRepeat(leet));
        out.add(leet);

        // 5. fully collapsed: every run of 2+ identical characters becomes one.
        // This catches "putaaaaangina" but is only used as an extra variant.
        out.add(collapseRuns(compact));
        out.add(collapseRuns(leet));

        out.removeIf(s -> s == null || s.isEmpty());
        return out;
    }

    private static char foldConfusable(char c) {
        int idx = CONFUSABLES.indexOf(c);
        if (idx >= 0) {
            return CONFUSABLE_REPLACEMENTS.charAt(idx);
        }
        return c;
    }

    private static String foldHomoglyphs(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            sb.append(foldConfusable(c));
        }
        return sb.toString();
    }

    /** Removes diacritics but keeps the base letter (é -> e, ñ -> n). */
    private static String stripAccents(String text) {
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /** "s a m a" -> "sama"; single letters are kept so words are not lost. */
    private static String deSpace(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else if (i + 1 < text.length() && Character.isLetterOrDigit(text.charAt(i + 1))) {
                // drop the separator between two word characters
                continue;
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    /** "sammmaa" -> "sama" (collapses runs of 3+ identical characters). */
    private static String deRepeat(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        char last = 0;
        int run = 0;
        for (char c : text.toCharArray()) {
            if (c == last) {
                run++;
                if (run >= 3) {
                    continue;
                }
            } else {
                last = c;
                run = 1;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /** Replaces common digit/symbol leetspeak. */
    private static String deLeet(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            sb.append(mapLeet(c));
        }
        return sb.toString();
    }

    /** "putaaaaangina" -> "putangina" (every run of 2+ collapses to one). */
    private static String collapseRuns(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        char last = 0;
        for (char c : text.toCharArray()) {
            if (c != last) {
                sb.append(c);
                last = c;
            }
        }
        return sb.toString();
    }

>>>>>>> d5851d3 (Initial commit)
    private static char mapLeet(char c) {
        return switch (c) {
            case '0' -> 'o';
            case '1', '!' -> 'i';
            case '3' -> 'e';
            case '4', '@' -> 'a';
            case '5', '$' -> 's';
<<<<<<< HEAD
            case '7' -> 't';
=======
            case '6' -> 'g';
            case '7' -> 't';
            case '9' -> 'g';
            case '+' -> 't';
            case '|' -> 'l';
            case '2' -> 'z';
>>>>>>> d5851d3 (Initial commit)
            default -> c;
        };
    }
}
