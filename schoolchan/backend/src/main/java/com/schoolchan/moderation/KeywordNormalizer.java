package com.schoolchan.moderation;

public final class KeywordNormalizer {

    private KeywordNormalizer() {}

    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String lower = input.toLowerCase();
        StringBuilder sb = new StringBuilder(lower.length());
        for (char c : lower.toCharArray()) {
            sb.append(mapLeet(c));
        }
        return sb.toString().replaceAll("[^a-z0-9]+", "");
    }

    private static char mapLeet(char c) {
        return switch (c) {
            case '0' -> 'o';
            case '1', '!' -> 'i';
            case '3' -> 'e';
            case '4', '@' -> 'a';
            case '5', '$' -> 's';
            case '7' -> 't';
            default -> c;
        };
    }
}
