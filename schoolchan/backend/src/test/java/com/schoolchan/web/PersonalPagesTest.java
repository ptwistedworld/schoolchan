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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Two promises the board makes to the people using it:
 *
 * <ul>
 *   <li>the personal pages only exist for a signed-in student, and</li>
 *   <li>a post that the filter flagged is not announced to the person who
 *       wrote it. Only staff see the flag.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonalPagesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Test
    void signedOutVisitorSeesNoPersonalLinks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(">My posts<"))))
                .andExpect(content().string(not(containsString(">Bookmarks"))))
                .andExpect(content().string(containsString("Student sign in")));
    }

    @Test
    void personalPagesSendASignedOutVisitorToSignIn() throws Exception {
        mockMvc.perform(get("/my-posts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/login?next=%2Fmy-posts"));
        mockMvc.perform(get("/bookmarks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/login?next=%2Fbookmarks"));
    }

    @Test
    void aFlaggedPostIsPublishedAndNotAnnouncedToItsAuthor() throws Exception {
        Post post = boardService.createThread("general", "Flag visibility check",
                "TOKEN_BLOCK placeholder for the visibility check",
                false, false, "session-for-visibility-check", "10.9.9.9");

        // It went up.
        mockMvc.perform(get("/boards/general/threads/" + post.getThreadId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TOKEN_BLOCK placeholder for the visibility check")))
                // ...and nothing on the page tells the reader that it was flagged.
                .andExpect(content().string(not(containsString("under review"))))
                .andExpect(content().string(not(containsString("A moderator is looking"))))
                .andExpect(content().string(not(containsString("HARASSMENT:TOKEN_BLOCK"))));
    }
}
