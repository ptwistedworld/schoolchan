package com.schoolchan.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostFormatterTest {

    private final PostFormatter formatter = new PostFormatter();

    @Test
    void formatPlain_escapesHtmlForXss() {
        String out = formatter.formatPlain("<script>alert(1)</script>");
        assertFalse(out.contains("<script>"));
        assertTrue(out.contains("&lt;script&gt;"));
    }

    @Test
    void formatPlain_greentextAndQuoteLinks() {
        String out = formatter.formatPlain(">hello\n>>42");
        assertTrue(out.contains("greentext"));
        assertTrue(out.contains("quote-link"));
        assertTrue(out.contains("&gt;&gt;42"));
    }
}
