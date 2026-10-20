package com.schoolchan.reputation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reputation_entries")
public class ReputationEntry {

    @Id
    private UUID id;

    @Column(name = "session_hash", nullable = false, unique = true, length = 128)
    private String sessionHash;

    @Column(nullable = false)
    private double reliability = 1.0;

    @Column(name = "strike_level", nullable = false)
    private int strikeLevel;

    @Column(name = "strike_expires_at")
    private Instant strikeExpiresAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSessionHash() { return sessionHash; }
    public void setSessionHash(String sessionHash) { this.sessionHash = sessionHash; }
    public double getReliability() { return reliability; }
    public void setReliability(double reliability) { this.reliability = reliability; }
    public int getStrikeLevel() { return strikeLevel; }
    public void setStrikeLevel(int strikeLevel) { this.strikeLevel = strikeLevel; }
    public Instant getStrikeExpiresAt() { return strikeExpiresAt; }
    public void setStrikeExpiresAt(Instant strikeExpiresAt) { this.strikeExpiresAt = strikeExpiresAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
