package com.schoolchan.board;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpoilerParser {

    private static final Pattern TAG =
            Pattern.compile("(?is)\\[spoiler(?:=([^\\]]+))?\\](.*?)\\[/spoiler\\]");

    private SpoilerParser() {}

    public static Result parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new Result("", false, null);
        }
        Matcher m = TAG.matcher(raw);
        if (!m.find()) {
            return new Result(raw, false, null);
        }
        String label = m.group(1) != null ? m.group(1).trim() : null;
        String cleaned = TAG.matcher(raw).replaceAll("$2");
        return new Result(cleaned, true, label);
    }

    public record Result(String text, boolean spoiler, String label) {}
}
