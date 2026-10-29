package com.schoolchan.web;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import com.schoolchan.media.MediaEntity;
import com.schoolchan.media.MediaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The hover preview of a quoted reply has to show that reply's picture, not
 * just its text. The fragment is what the board's JavaScript drops into the
 * page, so the image URL has to be in the fragment itself.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PreviewImageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MediaRepository mediaRepository;

    @Test
    void quotedReplyPreviewIncludesItsPicture() throws Exception {
        Post op = boardService.createThread("general", "Preview image check",
                "Opening post for the preview check.", false, false, "preview-op", "10.1.1.1");
        Post reply = boardService.reply("general", op.getThreadId(),
                "A reply that carries a picture.", false, false, "preview-reply", "10.1.1.2");

        MediaEntity media = new MediaEntity();
        media.setId(UUID.randomUUID());
        media.setPostId(reply.getId());
        media.setOriginalFilename("picture.png");
        media.setStoredName("preview-check.png");
        media.setThumbnailName("preview-check.png.thumb.jpg");
        media.setContentType("image/png");
        media.setSizeBytes(1234L);
        media.setQuarantineStatus("RELEASED");
        mediaRepository.save(media);

        mockMvc.perform(get("/boards/general/preview/" + reply.getBoardPostNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/media/preview-check.png.thumb.jpg")))
                .andExpect(content().string(containsString("A reply that carries a picture.")));
    }

    @Test
    void aPostWithoutAPictureHasNoImageInItsPreview() throws Exception {
        Post op = boardService.createThread("general", "Preview without image",
                "Opening post with no picture at all.", false, false, "preview-empty", "10.1.1.3");

        mockMvc.perform(get("/boards/general/preview/" + op.getBoardPostNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("preview-thumb"))));
    }
}
