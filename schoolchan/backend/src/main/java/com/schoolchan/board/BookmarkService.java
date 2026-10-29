package com.schoolchan.board;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ThreadRepository threadRepository;
    private final BoardRepository boardRepository;

    public BookmarkService(
            BookmarkRepository bookmarkRepository,
            ThreadRepository threadRepository,
            BoardRepository boardRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.threadRepository = threadRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional
    public boolean toggle(String sessionHash, UUID threadId) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        var existing = bookmarkRepository.findBySessionHashAndThreadId(sessionHash, threadId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setSessionHash(sessionHash);
        bookmark.setThreadId(threadId);
        bookmarkRepository.save(bookmark);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(String sessionHash, UUID threadId) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        return bookmarkRepository.findBySessionHashAndThreadId(sessionHash, threadId).isPresent();
    }

    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> listFor(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return List.of();
        }
        java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
        for (Bookmark b : bookmarkRepository.findBySessionHashOrderByCreatedAtDesc(sessionHash)) {
            ThreadEntity t = threadRepository.findById(b.getThreadId()).orElse(null);
            if (t == null) {
                continue;
            }
            Board board = boardRepository.findById(t.getBoardId()).orElse(null);
            out.add(java.util.Map.of(
                    "id", t.getId(),
                    "subject", t.getSubject() == null ? "(no subject)" : t.getSubject(),
                    "replies", t.getReplyCount(),
                    "boardSlug", board == null ? "general" : board.getSlug(),
                    "boardName", board == null ? "Board" : board.getName(),
                    "bumpedAt", t.getBumpedAt()));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public long count(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return 0;
        }
        return bookmarkRepository.countBySessionHash(sessionHash);
    }

    @Transactional
    public void clear(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return;
        }
        bookmarkRepository.deleteAll(bookmarkRepository.findBySessionHashOrderByCreatedAtDesc(sessionHash));
    }
}
