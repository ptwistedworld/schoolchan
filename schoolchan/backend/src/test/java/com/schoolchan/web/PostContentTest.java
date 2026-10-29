package com.schoolchan.web;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A post with no words must never reach the board, and a moderator must be
 * able to read the whole of what they are judging.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostContentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Test
    @WithMockUser(roles = "STUDENT")
    void anEmptyReplyIsRefusedRatherThanPosted() throws Exception {
        String thread = threadId();
        mockMvc.perform(post("/boards/general/threads/{id}/reply", thread)
                        .with(csrf())
                        .param("text", "   ")
                        .param("sage", "false")
                        .param("spoiler", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/general/threads/" + thread));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void aReplyKeepsItsText() throws Exception {
        String thread = threadId();
        String text = "A real reply with words in it.";
        mockMvc.perform(post("/boards/general/threads/{id}/reply", thread)
                        .with(csrf())
                        .param("text", text)
                        .param("sage", "false")
                        .param("spoiler", "false"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/boards/general/threads/{id}", thread))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(text)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void theReviewQueueShowsTheWholePost() throws Exception {
        boardService.createThread("general", "Content check",
                "TOKEN_BLOCK the whole text must be visible to staff",
                false, false, "session-for-content-check", "10.9.9.9");

        mockMvc.perform(get("/staff/review?view=queue"))
                .andExpect(status().isOk())
                // the text itself
                .andExpect(content().string(containsString("TOKEN_BLOCK the whole text must be visible to staff")))
                // and the context a moderator needs
                .andExpect(content().string(containsString("Post text")))
                .andExpect(content().string(containsString("open in the thread")));
    }

    private String threadId() {
        Post op = boardService.createThread("general", "Composer check",
                "A thread to reply to.", false, false, "session-composer-check", "10.9.9.8");
        return op.getThreadId().toString();
    }
}
