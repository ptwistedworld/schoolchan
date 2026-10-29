package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "threads")
public class ThreadEntity {

    @Id
    private UUID id;

    @Column(name = "board_id", nullable = false)
    private UUID boardId;

    @Column(length = 200)
    private String subject;

<<<<<<< HEAD
=======
    @Column(length = 32)
    private String tag;

>>>>>>> d5851d3 (Initial commit)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ThreadStatus status = ThreadStatus.ACTIVE;

    @Column(name = "bumped_at", nullable = false)
    private Instant bumpedAt = Instant.now();

    @Column(name = "reply_count", nullable = false)
    private int replyCount;

    @Column(name = "image_count", nullable = false)
    private int imageCount;

    @Column(nullable = false)
    private boolean locked;

    @Column(nullable = false)
    private boolean pinned;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBoardId() { return boardId; }
    public void setBoardId(UUID boardId) { this.boardId = boardId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
<<<<<<< HEAD
=======
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
>>>>>>> d5851d3 (Initial commit)
    public ThreadStatus getStatus() { return status; }
    public void setStatus(ThreadStatus status) { this.status = status; }
    public Instant getBumpedAt() { return bumpedAt; }
    public void setBumpedAt(Instant bumpedAt) { this.bumpedAt = bumpedAt; }
    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }
    public int getImageCount() { return imageCount; }
    public void setImageCount(int imageCount) { this.imageCount = imageCount; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
