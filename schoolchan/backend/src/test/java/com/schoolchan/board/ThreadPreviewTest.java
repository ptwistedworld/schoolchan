package com.schoolchan.board;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** A long thread shows its opening post and the newest replies. */
class ThreadPreviewTest {

    private static List<Post> thread(int posts) {
        List<Post> list = new ArrayList<>();
        for (int i = 1; i <= posts; i++) {
            Post post = new Post();
            post.setBoardPostNumber(i);
            post.setOriginalText("post " + i);
            post.setStatus(PostStatus.PUBLISHED);
            list.add(post);
        }
        return list;
    }

    @Test
    void keepsTheOpeningPostAndTheNewestReplies() {
        ThreadPreview preview = ThreadPreview.of(thread(70), 60);

        assertThat(preview.omitted()).isEqualTo(9);
        assertThat(preview.visible()).hasSize(61);
        assertThat(preview.visible().get(0).getBoardPostNumber()).isEqualTo(1);
        assertThat(preview.visible().get(1).getBoardPostNumber()).isEqualTo(11);
        assertThat(preview.visible().get(60).getBoardPostNumber()).isEqualTo(70);
    }

    @Test
    void onePostOverTheLimitIsNotWorthHiding() {
        ThreadPreview preview = ThreadPreview.of(thread(61), 60);

        assertThat(preview.omitted()).isZero();
        assertThat(preview.visible()).hasSize(61);
    }

    @Test
    void aZeroLimitMeansShowEverything() {
        ThreadPreview preview = ThreadPreview.of(thread(70), 0);

        assertThat(preview.omitted()).isZero();
        assertThat(preview.visible()).hasSize(70);
    }

    @Test
    void anEmptyThreadIsFine() {
        ThreadPreview preview = ThreadPreview.of(List.of(), 60);

        assertThat(preview.visible()).isEmpty();
        assertThat(preview.omitted()).isZero();
    }
}
