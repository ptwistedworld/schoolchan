package com.schoolchan.web;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import com.schoolchan.media.MediaEntity;
import com.schoolchan.media.MediaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The catalog sorts and filters on the server; the reader tools are markup. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "STUDENT")
class CatalogPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MediaRepository mediaRepository;

    @Test
    void catalogShowsAnOpExcerptAndTheReaderTools() throws Exception {
        Post op = boardService.createThread("general", "ALPHA_CATALOG",
                "ALPHA_CATALOG opening text for the excerpt.", false, false,
                "sess-catalog-alpha", "10.40.0.1");

        mockMvc.perform(get("/boards/general/catalog"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ALPHA_CATALOG")))
                .andExpect(content().string(containsString("ALPHA_CATALOG opening text")))
                .andExpect(content().string(containsString("catalog-excerpt")))
                .andExpect(content().string(containsString("data-filter-text=")))
                .andExpect(content().string(containsString("data-thread=\"" + op.getThreadId() + "\"")))
                .andExpect(content().string(containsString("id=\"readerTools\"")))
                .andExpect(content().string(containsString("id=\"hiddenBar\"")))
                .andExpect(content().string(containsString("sort=replies")));
    }

    @Test
    void hasPicturesOnlyKeepsThreadsWithAReleasedImage() throws Exception {
        Post withPicture = boardService.createThread("general", "PICTURE_CATALOG",
                "A thread that has a picture.", false, false,
                "sess-catalog-picture", "10.40.1.1");
        boardService.createThread("general", "NO_PICTURE_CATALOG",
                "A thread without any picture.", false, false,
                "sess-catalog-nopicture", "10.40.1.2");

        MediaEntity media = new MediaEntity();
        media.setId(UUID.randomUUID());
        media.setPostId(withPicture.getId());
        media.setOriginalFilename("catalog.png");
        media.setStoredName("catalog-check.png");
        media.setThumbnailName("catalog-check.png.thumb.jpg");
        media.setContentType("image/png");
        media.setSizeBytes(2048L);
        media.setQuarantineStatus("RELEASED");
        mediaRepository.save(media);

        mockMvc.perform(get("/boards/general/catalog").param("images", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PICTURE_CATALOG")))
                .andExpect(content().string(not(containsString("NO_PICTURE_CATALOG"))))
                .andExpect(content().string(containsString("All threads")));
    }

    @Test
    void anUnknownSortIsHarmless() throws Exception {
        mockMvc.perform(get("/boards/general/catalog").param("sort", "sideways"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bump")));
    }

    @Test
    void boardListHasAHideButtonAndFilterText() throws Exception {
        Post op = boardService.createThread("general", "HIDE_BUTTON_CHECK",
                "A thread that can be hidden in this browser.", false, false,
                "sess-catalog-hide", "10.40.2.1");

        mockMvc.perform(get("/boards/general"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-hide-thread=\"" + op.getThreadId() + "\"")))
                .andExpect(content().string(containsString("data-filter-text=")))
                .andExpect(content().string(containsString("id=\"readerTools\"")))
                .andExpect(content().string(containsString("id=\"hiddenBar\"")));
    }
}
