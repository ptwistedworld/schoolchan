package com.schoolchan.moderation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learned_blacklist_candidates")
public class LearnedBlacklistCandidate {

    @Id
    private UUID id;

    @Column(nullable = false, length = 512)
    private String pattern;

    @Column(name = "source_post_id")
    private UUID sourcePostId;

    @Column(nullable = false)
    private double confidence = 0.5;

    @Column(nullable = false)
    private boolean approved;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public UUID getSourcePostId() { return sourcePostId; }
    public void setSourcePostId(UUID sourcePostId) { this.sourcePostId = sourcePostId; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
