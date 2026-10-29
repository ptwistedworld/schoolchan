package com.schoolchan.board;

import java.util.ArrayList;
import java.util.List;

/**
 * The part of a thread that is rendered by default.
 *
 * <p>4chan keeps long threads readable by showing the opening post and the most
 * recent replies, with a link to reveal the rest. The same slice is used here:
 * the opening post is always visible, the newest {@code limit} posts follow it,
 * and everything between them is counted so the page can say how many replies
 * are hidden. The full list is still available with {@code ?all=1}.
 */
public record ThreadPreview(List<Post> visible, int omitted) {

    public static ThreadPreview of(List<Post> posts, int limit) {
        if (posts == null || posts.isEmpty()) {
            return new ThreadPreview(List.of(), 0);
        }
        // One post over the limit is not worth hiding; show the whole thread.
        if (limit <= 0 || posts.size() <= limit + 1) {
            return new ThreadPreview(posts, 0);
        }
        List<Post> visible = new ArrayList<>(limit + 1);
        visible.add(posts.get(0));
        visible.addAll(posts.subList(posts.size() - limit, posts.size()));
        return new ThreadPreview(visible, posts.size() - visible.size());
    }
}
