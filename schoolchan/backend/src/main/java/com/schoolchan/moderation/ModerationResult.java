package com.schoolchan.moderation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "moderation_results")
public class ModerationResult {

    @Id
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ModerationDecision decision;

    @Column(columnDefinition = "TEXT")
    private String reasons;

    @Column(name = "stage_results_json", nullable = false, columnDefinition = "TEXT")
    private String stageResultsJson;

    @Column(name = "total_latency_ms", nullable = false)
    private long totalLatencyMs;

    @Column(name = "baseline_mode", nullable = false)
    private boolean baselineMode;

    @Column(name = "ml_unavailable", nullable = false)
    private boolean mlUnavailable;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public ModerationDecision getDecision() { return decision; }
    public void setDecision(ModerationDecision decision) { this.decision = decision; }
    public String getReasons() { return reasons; }
    public void setReasons(String reasons) { this.reasons = reasons; }
    public String getStageResultsJson() { return stageResultsJson; }
    public void setStageResultsJson(String stageResultsJson) { this.stageResultsJson = stageResultsJson; }
    public long getTotalLatencyMs() { return totalLatencyMs; }
    public void setTotalLatencyMs(long totalLatencyMs) { this.totalLatencyMs = totalLatencyMs; }
    public boolean isBaselineMode() { return baselineMode; }
    public void setBaselineMode(boolean baselineMode) { this.baselineMode = baselineMode; }
    public boolean isMlUnavailable() { return mlUnavailable; }
    public void setMlUnavailable(boolean mlUnavailable) { this.mlUnavailable = mlUnavailable; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
