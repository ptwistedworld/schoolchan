package com.schoolchan.board;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Backlinks make a thread a conversation instead of a list. */
class QuoteIndexTest {

    private static Post post(long number, String text, PostStatus status) {
        Post post = new Post();
        post.setBoardPostNumber(number);
        post.setOriginalText(text);
        post.setStatus(status);
        return post;
    }

    @Test
    void recordsWhoQuotedWhomOldestFirst() {
        QuoteIndex index = QuoteIndex.of(List.of(
                post(1, "opening post", PostStatus.PUBLISHED),
                post(2, "hello >>1", PostStatus.PUBLISHED),
                post(3, "also >>1, and >>2", PostStatus.PUBLISHED)));

        assertThat(index.has(1)).isTrue();
        assertThat(index.backlinks(1)).containsExactly(2L, 3L);
        assertThat(index.backlinks(2)).containsExactly(3L);
        assertThat(index.has(3)).isFalse();
    }

    @Test
    void ignoresSelfQuotesUnknownNumbersAndRepeats() {
        QuoteIndex index = QuoteIndex.of(List.of(
                post(1, ">>1 look at me", PostStatus.PUBLISHED),
                post(2, ">>99 does not exist, >>1 twice >>1", PostStatus.PUBLISHED)));

        assertThat(index.backlinks(1)).containsExactly(2L);
        assertThat(index.has(99)).isFalse();
    }

    @Test
    void aHiddenOrDeletedPostDoesNotRevealItsQuotes() {
        Post hidden = post(2, ">>1", PostStatus.HELD_FOR_REVIEW);
        Post deleted = post(3, ">>1 again", PostStatus.PUBLISHED);
        deleted.setDeletedByAuthor(true);

        QuoteIndex index = QuoteIndex.of(List.of(
                post(1, "opening post", PostStatus.PUBLISHED), hidden, deleted));

        // The numbers still exist (the tombstones keep the line intact), but
        // nothing points at them from a post a reader cannot see.
        assertThat(index.has(1)).isFalse();
    }

    @Test
    void anEmptyThreadIsFine() {
        assertThat(QuoteIndex.of(List.of()).has(1)).isFalse();
    }
}
