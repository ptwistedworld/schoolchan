package com.schoolchan.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * "Quoted you" is session-scoped, same-board, and never exposes a reply that
 * the rest of the board cannot see.
 */
@SpringBootTest
@ActiveProfiles("test")
class QuoteInboxServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private QuoteInboxService inbox;

    @Autowired
    private PostRepository postRepository;

    @Test
    void oneReplyCanQuoteTwoOfMyPostsAndAppearsOnce() {
        String me = "sess-inbox-me-one";
        Post op = boardService.createThread("general", "Inbox thread one",
                "My opening post for the inbox.", false, false, me, "10.20.0.1");
        Post mine = boardService.reply("general", op.getThreadId(),
                "My second post.", false, false, me, "10.20.0.2");
        Post other = boardService.reply("general", op.getThreadId(),
                ">>" + op.getBoardPostNumber() + " and >>" + mine.getBoardPostNumber()
                        + " TOKEN_INBOX_REPLY", false, false, "sess-inbox-other-one", "10.20.0.3");

        List<QuoteInboxService.QuoteHit> hits = inbox.forSession(me);

        assertThat(hits).filteredOn(hit -> hit.reply().getId().equals(other.getId()))
                .singleElement()
                .satisfies(hit -> {
                    assertThat(hit.yourNumbers()).containsExactly(
                            op.getBoardPostNumber(), mine.getBoardPostNumber());
                    assertThat(hit.thread().getId()).isEqualTo(op.getThreadId());
                    assertThat(hit.board().getSlug()).isEqualTo("general");
                });
    }

    @Test
    void myOwnQuotesDoNotFillMyInbox() {
        String me = "sess-inbox-me-self";
        Post op = boardService.createThread("general", "Inbox thread self",
                "Opening post about self quotes.", false, false, me, "10.20.1.1");
        boardService.reply("general", op.getThreadId(),
                ">>" + op.getBoardPostNumber() + " talking to myself", false, false, me, "10.20.1.2");

        assertThat(inbox.forSession(me)).isEmpty();
    }

    @Test
    void aHiddenReplyIsNotRevealedByTheInbox() {
        String me = "sess-inbox-me-hidden";
        Post op = boardService.createThread("general", "Inbox thread hidden",
                "Opening post for a hidden reply.", false, false, me, "10.20.2.1");
        Post reply = boardService.reply("general", op.getThreadId(),
                ">>" + op.getBoardPostNumber() + " a reply that will be removed", false, false,
                "sess-inbox-other-hidden", "10.20.2.2");
        reply.setStatus(PostStatus.BLOCKED);
        postRepository.save(reply);

        assertThat(inbox.forSession(me)).isEmpty();
    }

    @Test
    void aSessionWithNoPostsHasAnEmptyInbox() {
        assertThat(inbox.forSession("sess-inbox-empty")).isEmpty();
        assertThat(inbox.forSession(null)).isEmpty();
    }
}
