package com.schoolchan.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RulesTextTest {

    @Test
    void rendersHeadingsAndParagraphs() {
        String html = RulesText.toHtml("# Title\nA line\nstill the same paragraph.\n\n## Second\nAnother line.");

        assertThat(html).contains("<h2>Title</h2>");
        assertThat(html).contains("<h2>Second</h2>");
        assertThat(html).contains("<p>A line still the same paragraph.</p>");
        assertThat(html).contains("<p>Another line.</p>");
    }

    @Test
    void escapesEverythingStaffWrite() {
        String html = RulesText.toHtml("## <script>alert(1)</script>\nBody & <b>bold</b>");

        assertThat(html).doesNotContain("<script>");
        assertThat(html).doesNotContain("<b>bold</b>");
        assertThat(html).contains("&lt;script&gt;");
        assertThat(html).contains("&amp;");
    }

    @Test
    void blankRulesProduceNothing() {
        assertThat(RulesText.toHtml(null)).isEmpty();
        assertThat(RulesText.toHtml("   \n\n  ")).isEmpty();
    }

    @Test
    void hashInsideALineIsNotAHeading() {
        assertThat(RulesText.toHtml("Use C# only in code blocks")).contains("<p>Use C# only in code blocks</p>");
    }
}
