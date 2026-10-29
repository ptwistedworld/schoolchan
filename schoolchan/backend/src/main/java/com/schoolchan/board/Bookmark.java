package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookmarks")
public class Bookmark {

    @Id
    private UUID id;

    @Column(name = "session_hash", nullable = false, length = 64)
    private String sessionHash;

    @Column(name = "thread_id", nullable = false)
    private UUID threadId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSessionHash() { return sessionHash; }
    public void setSessionHash(String sessionHash) { this.sessionHash = sessionHash; }
    public UUID getThreadId() { return threadId; }
    public void setThreadId(UUID threadId) { this.threadId = threadId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
