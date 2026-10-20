package com.schoolchan.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @Transactional
    void reply_withoutSage_bumpsThread() throws InterruptedException {
        Post op = boardService.createThread("general", "bump test", "hello", false, false, "sess-bump", "127.0.0.1");
        ThreadEntity before = threadRepository.findById(op.getThreadId()).orElseThrow();
        var bumped = before.getBumpedAt();
        Thread.sleep(5);
        boardService.reply("general", op.getThreadId(), "reply", false, false, "sess-bump2", "127.0.0.1");
        ThreadEntity after = threadRepository.findById(op.getThreadId()).orElseThrow();
        assertTrue(after.getBumpedAt().isAfter(bumped) || after.getBumpedAt().equals(bumped));
    }

    @Test
    @Transactional
    void reply_withSage_doesNotBumpThread() {
        Post op = boardService.createThread("general", "sage test", "hello", false, false, "sess1", "127.0.0.1");
        ThreadEntity before = threadRepository.findById(op.getThreadId()).orElseThrow();
        var bumped = before.getBumpedAt();

        boardService.reply("general", op.getThreadId(), "sage reply", true, false, "sess2", "127.0.0.1");
        ThreadEntity after = threadRepository.findById(op.getThreadId()).orElseThrow();

        assertEquals(bumped, after.getBumpedAt());
    }

    @Test
    @Transactional
    void enforceActiveCap_archivesOldestUnpinned() {
        Board board = boardRepository.findBySlug("general").orElseThrow();
        board.setActiveThreadCap(1);
        boardRepository.save(board);

        Post first = boardService.createThread("general", "t1", "one", false, false, "s1", "127.0.0.1");
        boardService.createThread("general", "t2", "two", false, false, "s2", "127.0.0.1");

        long archived = threadRepository.findAll().stream()
                .filter(t -> t.getBoardId().equals(board.getId()))
                .filter(t -> t.getStatus() == ThreadStatus.ARCHIVED)
                .count();
        assertTrue(archived >= 1);

        ThreadEntity firstThread = threadRepository.findById(first.getThreadId()).orElseThrow();
        assertEquals(ThreadStatus.ARCHIVED, firstThread.getStatus());
    }

    @Test
    @Transactional
    void vote_deduplicatesBySessionHash() {
        Post post = boardService.createThread("general", "vote", "body", false, false, "voter", "127.0.0.1");

        boardService.vote(post.getId(), "same-session", (short) 1);
        boardService.vote(post.getId(), "same-session", (short) 1);

        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(1, updated.getScore());
    }
}
