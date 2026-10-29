package com.schoolchan.staff;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Internal staff note attached to a post or a thread. Never shown on the
 * public board and never shown to students.
 */
@Entity
@Table(name = "staff_notes")
public class StaffNote {

    @Id
    private UUID id;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "thread_id")
    private UUID threadId;

    @Column(name = "author", nullable = false, length = 64)
    private String author;

    @Column(nullable = false, length = 2000)
    private String body;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public UUID getThreadId() { return threadId; }
    public void setThreadId(UUID threadId) { this.threadId = threadId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
