package com.schoolchan.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpoilerParserTest {

    @Test
    void parse_withTag_marksSpoilerAndStripsMarkers() {
        SpoilerParser.Result r = SpoilerParser.parse("[spoiler=Anime]ending[/spoiler]");
        assertTrue(r.spoiler());
        assertEquals("Anime", r.label());
        assertEquals("ending", r.text());
    }

    @Test
    void parse_plainText_notSpoiler() {
        SpoilerParser.Result r = SpoilerParser.parse("hello");
        assertFalse(r.spoiler());
        assertEquals("hello", r.text());
    }
}
