package com.schoolchan.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the small Markdown subset the rules editor accepts.
 *
 * <p>Staff write the rules as plain text: a line starting with {@code ##}
 * (or {@code #}) is a heading, a blank line starts a new paragraph. Everything
 * is escaped first, so a rules version can never inject HTML into the public
 * page.
 */
public final class RulesText {

    private RulesText() {
    }

    public static String toHtml(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        List<String> paragraph = new ArrayList<>();
        String normalised = body.replace("\r\n", "\n").replace('\r', '\n');
        for (String raw : normalised.split("\n", -1)) {
            String line = raw.strip();
            if (line.isEmpty()) {
                flush(out, paragraph);
                continue;
            }
            if (line.startsWith("## ")) {
                flush(out, paragraph);
                out.append("<h2>").append(escape(line.substring(3))).append("</h2>");
                continue;
            }
            if (line.startsWith("# ")) {
                flush(out, paragraph);
                out.append("<h2>").append(escape(line.substring(2))).append("</h2>");
                continue;
            }
            paragraph.add(line);
        }
        flush(out, paragraph);
        return out.toString();
    }

    private static void flush(StringBuilder out, List<String> paragraph) {
        if (paragraph.isEmpty()) {
            return;
        }
        out.append("<p>").append(escape(String.join(" ", paragraph))).append("</p>");
        paragraph.clear();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
