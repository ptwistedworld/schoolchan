package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * An anonymous trust tier for a poster's session. It never reveals who a
 * poster is: the tier is derived from how their posts were treated by staff,
 * capped so no single session can dominate, and shown to nobody.
 */
@Entity
@Table(name = "poster_tiers")
public class PosterTier {

    @Id
    private UUID id;

    @Column(name = "session_hash", nullable = false, unique = true, length = 128)
    private String sessionHash;

    @Column(nullable = false, length = 16)
    private String tier = "NEW";

    @Column(nullable = false)
    private double score = 0;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSessionHash() { return sessionHash; }
    public void setSessionHash(String sessionHash) { this.sessionHash = sessionHash; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
