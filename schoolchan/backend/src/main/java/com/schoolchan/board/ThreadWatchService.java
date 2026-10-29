package com.schoolchan.board;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Watching a thread. The watcher is the anonymous session, never a person.
 * The count of unseen replies is the only thing this produces.
 */
@Service
public class ThreadWatchService {

    private final ThreadWatchRepository watches;
    private final ThreadRepository threads;
    private final PostRepository posts;

    public ThreadWatchService(ThreadWatchRepository watches, ThreadRepository threads, PostRepository posts) {
        this.watches = watches;
        this.threads = threads;
        this.posts = posts;
    }

    @Transactional
    public boolean toggle(String sessionHash, UUID threadId) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        var existing = watches.findByThreadIdAndSessionHash(threadId, sessionHash);
        if (existing.isPresent()) {
            watches.delete(existing.get());
            return false;
        }
        ThreadWatch watch = new ThreadWatch();
        watch.setId(UUID.randomUUID());
        watch.setThreadId(threadId);
        watch.setSessionHash(sessionHash);
        watch.setLastSeenPost(0);
        watches.save(watch);
        return true;
    }

    @Transactional
    public boolean isWatching(String sessionHash, UUID threadId) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        return watches.findByThreadIdAndSessionHash(threadId, sessionHash).isPresent();
    }

    /** Marks everything up to the given post number as seen. */
    @Transactional
    public void markSeen(String sessionHash, UUID threadId, long postNumber) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return;
        }
        ThreadWatch watch = watches.findByThreadIdAndSessionHash(threadId, sessionHash)
                .orElseGet(() -> {
                    ThreadWatch w = new ThreadWatch();
                    w.setId(UUID.randomUUID());
                    w.setThreadId(threadId);
                    w.setSessionHash(sessionHash);
                    return w;
                });
        if (postNumber > watch.getLastSeenPost()) {
            watch.setLastSeenPost(postNumber);
            watches.save(watch);
        }
    }

    /** How many replies arrived since the watcher last looked. */
    @Transactional(readOnly = true)
    public long unreadCount(String sessionHash, UUID threadId) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return 0;
        }
        var watch = watches.findByThreadIdAndSessionHash(threadId, sessionHash).orElse(null);
        long lastSeen = watch == null ? 0 : watch.getLastSeenPost();
        return posts.findByThreadIdOrderByCreatedAtAsc(threadId).stream()
                .filter(p -> p.getStatus() == PostStatus.PUBLISHED)
                .filter(p -> p.getBoardPostNumber() > lastSeen)
                .count();
    }

    @Transactional(readOnly = true)
    public long totalUnread(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return 0;
        }
        long total = 0;
        for (ThreadWatch w : watches.findBySessionHashOrderByCreatedAtDesc(sessionHash)) {
            total += unreadCount(sessionHash, w.getThreadId());
        }
        return total;
    }

    @Transactional(readOnly = true)
    public List<ThreadWatch> watchedThreads(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return List.of();
        }
        return watches.findBySessionHashOrderByCreatedAtDesc(sessionHash);
    }
}
