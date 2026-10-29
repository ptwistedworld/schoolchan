package com.schoolchan.web;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The composer must send what the student typed.
 *
 * <p>There was a bug where the "clear the composer on submit" script emptied
 * the textarea. The browser builds the request body <em>after</em> the submit
 * event has finished, so the message went out empty and every post arrived
 * blank. This test guards the fix.
 */
class ComposerScriptTest {

    private static String readJs() throws Exception {
        Path path = Path.of("src/main/resources/static/js/board.js");
        return Files.readString(path);
    }

    @Test
    void theSubmitHandlerDoesNotEmptyTheTextarea() throws Exception {
        String js = readJs();
        int submitHandler = js.indexOf("document.addEventListener(\"submit\"");
        assertTrue(submitHandler > 0, "the submit handler should exist");

        // The handler runs until the next top-level comment block.
        int end = js.indexOf("window.addEventListener(\"pageshow\"", submitHandler);
        assertTrue(end > submitHandler, "the submit handler should be followed by the pageshow handler");
        String handler = js.substring(submitHandler, end);

        assertFalse(handler.contains("ta.value = \"\""),
                "the submit handler must not empty the textarea: the browser builds the "
                        + "request body after this handler runs, so the post would go out blank");
        assertTrue(handler.contains("data-cleared"),
                "the submit handler should still mark the composer as submitted");
    }

    @Test
    void theComposerStillClearsARestoredPage() throws Exception {
        String js = readJs();
        assertTrue(js.contains("window.addEventListener(\"pageshow\""),
                "a restored page should clear the composer so a reply is not shown twice");
        assertTrue(js.contains("if (!event.persisted) return;"),
                "only a page restored from the browser cache should be cleared");
    }
}
