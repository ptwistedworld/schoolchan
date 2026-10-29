package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Links a post to the anonymous session that created it, so the poster can
 * see their own pending (held) posts on a "My posts" page. Only the salted
 * session hash is stored — the same value already used for vote de-duplication
 * — and it is never shown to anyone.
 */
@Entity
@Table(name = "post_sessions")
public class PostSession {

    @Id
    private UUID id;

    @Column(name = "post_id", nullable = false, unique = true)
    private UUID postId;

    @Column(name = "session_hash", nullable = false, length = 128)
    private String sessionHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getSessionHash() { return sessionHash; }
    public void setSessionHash(String sessionHash) { this.sessionHash = sessionHash; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
