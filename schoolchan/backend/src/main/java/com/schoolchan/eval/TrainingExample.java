package com.schoolchan.eval;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "training_examples")
public class TrainingExample {

    @Id
    private UUID id;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "text_snapshot", nullable = false, columnDefinition = "TEXT")
    private String textSnapshot;

    @Column(nullable = false, length = 64)
    private String label;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getTextSnapshot() { return textSnapshot; }
    public void setTextSnapshot(String textSnapshot) { this.textSnapshot = textSnapshot; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
