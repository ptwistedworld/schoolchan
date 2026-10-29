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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** A post can be pointed at directly, and the thread says who answered it. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PermalinkTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Test
    void aShortLinkLandsOnThePost() throws Exception {
        Post op = boardService.createThread("general", "Permalink check",
                "A post worth linking to.", false, false, "sess-permalink", "10.0.0.1");

        mockMvc.perform(get("/boards/general/p/{n}", op.getBoardPostNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/general/threads/" + op.getThreadId()
                        + "#p" + op.getBoardPostNumber()));
    }

    @Test
    void anUnknownPostNumberIsNotFound() throws Exception {
        mockMvc.perform(get("/boards/general/p/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void theThreadShowsWhoQuotedTheOpeningPost() throws Exception {
        Post op = boardService.createThread("general", "Backlink check",
                "The first post.", false, false, "sess-backlink-op", "10.0.0.2");
        Post reply = boardService.reply("general", op.getThreadId(),
                ">>" + op.getBoardPostNumber() + " an answer", false, false,
                "sess-backlink-reply", "10.0.0.3");

        mockMvc.perform(get("/boards/general/threads/{id}", op.getThreadId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"backlinks\"")))
                .andExpect(content().string(containsString("href=\"#p" + reply.getBoardPostNumber() + "\"")))
                .andExpect(content().string(containsString("data-time=")));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void aNewReplyLandsOnItself() throws Exception {
        Post op = boardService.createThread("general", "Landing check",
                "A thread to answer.", false, false, "sess-landing-op", "10.0.0.4");

        mockMvc.perform(post("/boards/general/threads/{id}/reply", op.getThreadId())
                        .with(csrf())
                        .param("text", "A reply that should be seen straight away.")
                        .param("sage", "false")
                        .param("spoiler", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/boards/general/threads/"
                        + op.getThreadId() + "?notice=*#p*"));
    }
}
