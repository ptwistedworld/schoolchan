package com.schoolchan.board;

import com.schoolchan.reputation.TextSimilarity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Warns a poster that they are about to repeat something. Nothing is blocked:
 * the poster decides. It is a courtesy, and it cuts down on accidental spam.
 */
@Component
public class DuplicateDetector {

    /** Above this similarity, the poster is told it looks like a repeat. */
    public static final double THRESHOLD = 0.75;

    private final PostRepository posts;

    public DuplicateDetector(PostRepository posts) {
        this.posts = posts;
    }

    /**
     * Returns the subject of the most similar recent post in the same board,
     * or null when the text looks new.
     */
    public String findSimilar(UUID boardId, String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        double best = 0;
        String bestSubject = null;
        List<Post> recent = posts.findByBoardId(boardId);
        int checked = 0;
        for (Post p : recent) {
            if (checked++ > 300) {
                break;
            }
            if (p.getStatus() != PostStatus.PUBLISHED) {
                continue;
            }
            double sim = TextSimilarity.jaccard(text, p.getOriginalText());
            if (sim > best) {
                best = sim;
                bestSubject = p.getId() == null ? null : subjectOf(p);
            }
        }
        return best >= THRESHOLD ? bestSubject : null;
    }

    private String subjectOf(Post post) {
        // Posts do not carry the subject; the thread does. The caller shows a
        // generic message, so returning the matched text is enough.
        String text = post.getOriginalText();
        if (text == null) {
            return null;
        }
        return text.length() > 60 ? text.substring(0, 60) + "…" : text;
    }
}
