package com.schoolchan.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Which replies quoted which post.
 *
 * <p>On an imageboard a thread is a graph, not a list: {@code >>14} points at
 * post 14, but until now post 14 never showed who answered it. This builds the
 * reverse index in one pass so the thread page can render "→ 149, 151" next to
 * the quoted post.
 *
 * <p>Only posts that a reader can actually read contribute links: a hidden
 * (held/blocked/deleted) post must not reveal that it quoted something. The
 * target only has to exist in the thread - pointing at a tombstone is fine,
 * because the tombstones keep the number line intact.
 */
public final class QuoteIndex {

    private static final Pattern QUOTE = Pattern.compile(">>(\\d{1,9})");

    private final Map<Long, List<Long>> byTarget;

    private QuoteIndex(Map<Long, List<Long>> byTarget) {
        this.byTarget = byTarget;
    }

    public static QuoteIndex of(Collection<Post> posts) {
        Map<Long, List<Long>> byTarget = new HashMap<>();
        if (posts == null || posts.isEmpty()) {
            return new QuoteIndex(byTarget);
        }
        Set<Long> numbers = new HashSet<>();
        for (Post post : posts) {
            numbers.add(post.getBoardPostNumber());
        }
        for (Post post : posts) {
            if (post.getStatus() != PostStatus.PUBLISHED || post.isDeletedByAuthor()) {
                continue;
            }
            String text = post.getOriginalText();
            if (text == null || text.isEmpty()) {
                continue;
            }
            Matcher matcher = QUOTE.matcher(text);
            Set<Long> seen = new HashSet<>();
            while (matcher.find()) {
                long target = Long.parseLong(matcher.group(1));
                if (target == post.getBoardPostNumber()
                        || !numbers.contains(target)
                        || !seen.add(target)) {
                    continue;
                }
                byTarget.computeIfAbsent(target, key -> new ArrayList<>())
                        .add(post.getBoardPostNumber());
            }
        }
        byTarget.values().forEach(Collections::sort);
        return new QuoteIndex(byTarget);
    }

    /** The numbers of the posts that quoted {@code postNumber}, oldest first. */
    public List<Long> backlinks(long postNumber) {
        return byTarget.getOrDefault(postNumber, List.of());
    }

    public boolean has(long postNumber) {
        return byTarget.containsKey(postNumber);
    }
}
