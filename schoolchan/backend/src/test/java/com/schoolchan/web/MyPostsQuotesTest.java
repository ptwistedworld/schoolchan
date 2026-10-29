package com.schoolchan.web;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import com.schoolchan.session.AnonymousSessionFilter;
import com.schoolchan.session.SessionHashService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The second tab on the personal page: replies that quoted something this
 * browser wrote. The tests use the same cookie the filter would issue, so the
 * session hash on the posts and the one on the request are the same value.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MyPostsQuotesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private SessionHashService sessionHashService;

    private Cookie cookieFor(String token) {
        return new Cookie(AnonymousSessionFilter.COOKIE_NAME, token);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void quotedYouTabListsTheReplyAndMyPostNumbers() throws Exception {
        String token = "test-quotes-browser-token";
        String sessionHash = sessionHashService.hashToken(token);
        Post op = boardService.createThread("general", "Quoted you check",
                "The post that will be quoted.", false, false, sessionHash, "10.50.0.1");
        Post reply = boardService.reply("general", op.getThreadId(),
                ">>" + op.getBoardPostNumber() + " TOKEN_QUOTED_YOU", false, false,
                "sess-quotes-someone-else", "10.50.0.2");

        mockMvc.perform(get("/my-posts").param("tab", "quotes").cookie(cookieFor(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TOKEN_QUOTED_YOU")))
                .andExpect(content().string(containsString("Quoted you")))
                .andExpect(content().string(containsString("your No." + op.getBoardPostNumber())))
                .andExpect(content().string(containsString("threads/" + op.getThreadId())))
                .andExpect(content().string(containsString("#p" + reply.getBoardPostNumber())));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void anEmptyInboxExplainsHowQuotesWork() throws Exception {
        String token = "test-quotes-empty-token";

        mockMvc.perform(get("/my-posts").param("tab", "quotes").cookie(cookieFor(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No one has quoted you yet")));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void theDefaultTabStillShowsMyPosts() throws Exception {
        String token = "test-quotes-default-token";
        String sessionHash = sessionHashService.hashToken(token);
        boardService.createThread("general", "Default tab check",
                "TOKEN_DEFAULT_TAB", false, false, sessionHash, "10.50.1.1");

        mockMvc.perform(get("/my-posts").cookie(cookieFor(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TOKEN_DEFAULT_TAB")))
                .andExpect(content().string(not(containsString("No one has quoted you yet"))));
    }
}
