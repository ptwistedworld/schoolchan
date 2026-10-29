package com.schoolchan.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A small automated accessibility audit. It is not a substitute for a real
 * screen-reader pass, but it catches the mistakes that are easy to make and
 * expensive to miss: missing labels, missing language, and images with no
 * alternative text.
 */
@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class AccessibilityAuditTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String[] PUBLIC_PAGES = {
            "/", "/rules", "/about", "/boards/general", "/boards/general/catalog", "/search?q=school"
    };

    @Test
    void everyPageDeclaresItsLanguage() throws Exception {
        for (String path : PUBLIC_PAGES) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("lang=\"en\"")));
        }
    }

    @Test
    void everyPageHasAVisibleTitle() throws Exception {
        for (String path : PUBLIC_PAGES) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("<title")));
        }
    }

    @Test
    void everyImageHasAlternativeText() throws Exception {
        for (String path : PUBLIC_PAGES) {
            String html = mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            String[] chunks = html.split("<img");
            for (int i = 1; i < chunks.length; i++) {
                String chunk = chunks[i];
                int end = chunk.indexOf('>');
                if (end < 0) {
                    continue;
                }
                String imgTag = chunk.substring(0, end);
                if (!imgTag.contains("alt=")) {
                    throw new AssertionError("an <img> on " + path + " has no alt attribute: " + imgTag);
                }
            }
        }
    }

    @Test
    void everyFormControlHasALabelOrAnAriaLabel() throws Exception {
        for (String path : PUBLIC_PAGES) {
            String html = mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            String[] chunks = html.split("<input");
            for (int i = 1; i < chunks.length; i++) {
                String chunk = chunks[i];
                int end = chunk.indexOf('>');
                if (end < 0) {
                    continue;
                }
                String tag = chunk.substring(0, end);
                if (tag.contains("type=\"hidden\"") || tag.contains("type=\"submit\"")
                        || tag.contains("type=\"checkbox\"") || tag.contains("type=\"radio\"")
                        || tag.contains("aria-hidden=\"true\"")) {
                    continue;
                }
                boolean labelled = tag.contains("aria-label=") || tag.contains("id=")
                        || html.contains("for=\"" + attr(tag, "id") + "\"")
                        || wrappedInLabel(html, html.indexOf("<input" + tag));
                if (!labelled) {
                    throw new AssertionError("an <input> on " + path + " has no label: " + tag);
                }
            }
        }
    }

    @Test
    void pagesDoNotLeakPrivateData() throws Exception {
        for (String path : PUBLIC_PAGES) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(not(containsString("sc_session"))))
                    .andExpect(content().string(not(containsString("127.0.0.1"))));
        }
    }

    private static String attr(String tag, String name) {
        int i = tag.indexOf(name + "=\"");
        if (i < 0) {
            return "";
        }
        int start = i + name.length() + 2;
        int end = tag.indexOf('"', start);
        return end < 0 ? "" : tag.substring(start, end);
    }

    /** True when the input sits inside an unclosed &lt;label&gt; element. */
    private static boolean wrappedInLabel(String html, int position) {
        if (position < 0) {
            return false;
        }
        String before = html.substring(0, position);
        int lastOpen = before.lastIndexOf("<label");
        int lastClose = before.lastIndexOf("</label>");
        return lastOpen > lastClose;
    }
}
