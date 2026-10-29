package com.schoolchan.board;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** The catalog can be ordered four ways without touching the database. */
class CatalogOrderTest {

    private static ThreadEntity thread(String subject, boolean pinned, String created, String bumped,
                                       int replies, int images) {
        ThreadEntity thread = new ThreadEntity();
        thread.setSubject(subject);
        thread.setPinned(pinned);
        thread.setCreatedAt(Instant.parse(created));
        thread.setBumpedAt(Instant.parse(bumped));
        thread.setReplyCount(replies);
        thread.setImageCount(images);
        return thread;
    }

    private static List<String> subjects(List<ThreadEntity> threads) {
        return threads.stream().map(ThreadEntity::getSubject).toList();
    }

    @Test
    void bumpOrderPutsPinnedThreadsFirstThenTheMostRecentlyBumped() {
        var old = thread("old", false, "2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z", 1, 0);
        var recent = thread("recent", false, "2026-01-01T00:00:00Z", "2026-01-03T00:00:00Z", 1, 0);
        var pinned = thread("pinned", true, "2026-01-01T00:00:00Z", "2026-01-01T00:00:00Z", 1, 0);

        assertThat(subjects(CatalogOrder.sorted(List.of(old, recent, pinned), "bump")))
                .containsExactly("pinned", "recent", "old");
    }

    @Test
    void newestUsesCreationTimeNotBumpTime() {
        var old = thread("old", false, "2026-01-01T00:00:00Z", "2026-01-09T00:00:00Z", 9, 0);
        var fresh = thread("fresh", false, "2026-01-08T00:00:00Z", "2026-01-08T00:00:00Z", 0, 0);

        assertThat(subjects(CatalogOrder.sorted(List.of(old, fresh), "newest")))
                .containsExactly("fresh", "old");
    }

    @Test
    void repliesAndImagesOrderByTheirCounts() {
        var few = thread("few", false, "2026-01-01T00:00:00Z", "2026-01-05T00:00:00Z", 2, 1);
        var many = thread("many", false, "2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z", 8, 0);
        var pictures = thread("pictures", false, "2026-01-01T00:00:00Z", "2026-01-01T00:00:00Z", 1, 4);

        assertThat(subjects(CatalogOrder.sorted(List.of(few, many, pictures), "replies")))
                .containsExactly("many", "few", "pictures");
        assertThat(subjects(CatalogOrder.sorted(List.of(few, many, pictures), "images")))
                .containsExactly("pictures", "few", "many");
    }

    @Test
    void anUnknownSortFallsBackToBump() {
        assertThat(CatalogOrder.normalize("sideways")).isEqualTo("bump");
        assertThat(CatalogOrder.normalize(null)).isEqualTo("bump");
        assertThat(CatalogOrder.normalize("newest")).isEqualTo("newest");
    }
}
