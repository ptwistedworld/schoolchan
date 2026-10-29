package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A session watching a thread, with the last post number it has seen. */
@Entity
@Table(name = "thread_watch")
public class ThreadWatch {

    @Id
    private UUID id;

    @Column(name = "thread_id", nullable = false)
    private UUID threadId;

    @Column(name = "session_hash", nullable = false, length = 128)
    private String sessionHash;

    @Column(name = "last_seen_post", nullable = false)
    private long lastSeenPost;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getThreadId() { return threadId; }
    public void setThreadId(UUID threadId) { this.threadId = threadId; }
    public String getSessionHash() { return sessionHash; }
    public void setSessionHash(String sessionHash) { this.sessionHash = sessionHash; }
    public long getLastSeenPost() { return lastSeenPost; }
    public void setLastSeenPost(long lastSeenPost) { this.lastSeenPost = lastSeenPost; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
