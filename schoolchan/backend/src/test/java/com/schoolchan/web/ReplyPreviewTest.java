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

/** A long thread shows the opening post and the newest replies, not all 500. */
@SpringBootTest(properties = "schoolchan.threads.reply-preview-limit=3")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReplyPreviewTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Test
    void olderRepliesAreOneClickAway() throws Exception {
        Post op = boardService.createThread("general", "Preview limit",
                "The opening post.", false, false, "sess-preview-op", "10.1.0.1");
        for (int i = 1; i <= 5; i++) {
            boardService.reply("general", op.getThreadId(), "TOKEN_REPLY_" + i,
                    false, false, "sess-preview-" + i, "10.1.0." + (i + 1));
        }

        mockMvc.perform(get("/boards/general/threads/{id}", op.getThreadId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2 replies omitted")))
                .andExpect(content().string(containsString("Show all")))
                .andExpect(content().string(not(containsString("TOKEN_REPLY_2"))))
                .andExpect(content().string(containsString("TOKEN_REPLY_5")));

        mockMvc.perform(get("/boards/general/threads/{id}", op.getThreadId()).param("all", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TOKEN_REPLY_2")))
                .andExpect(content().string(containsString("Showing every reply")));
    }
}
