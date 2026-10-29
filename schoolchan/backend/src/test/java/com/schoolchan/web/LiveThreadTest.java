package com.schoolchan.web;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The live-thread endpoint hands over only the posts that arrived later. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LiveThreadTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Test
    void postsAfterReturnsOnlyTheNewerReplies() throws Exception {
        Post op = boardService.createThread("general", "Live thread check",
                "Opening post for the live check.", false, false,
                "sess-live-op", "10.60.0.1");
        Post reply = boardService.reply("general", op.getThreadId(),
                "TOKEN_LIVE_REPLY", false, false, "sess-live-reply", "10.60.0.2");

        mockMvc.perform(get("/boards/general/threads/{id}/posts-after/{after}",
                        op.getThreadId(), op.getBoardPostNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TOKEN_LIVE_REPLY")))
                .andExpect(content().string(containsString("class=\"post\"")))
                .andExpect(content().string(not(containsString("Opening post for the live check."))));

        // Nothing newer than the last reply means an empty fragment.
        mockMvc.perform(get("/boards/general/threads/{id}/posts-after/{after}",
                        op.getThreadId(), reply.getBoardPostNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("TOKEN_LIVE_REPLY"))));
    }
}
