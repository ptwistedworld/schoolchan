package com.schoolchan.media;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media_phash_blocklist")
public class MediaPhashBlocklistEntry {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String phash;

    @Column(name = "source_post_id")
    private UUID sourcePostId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPhash() { return phash; }
    public void setPhash(String phash) { this.phash = phash; }
    public UUID getSourcePostId() { return sourcePostId; }
    public void setSourcePostId(UUID sourcePostId) { this.sourcePostId = sourcePostId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
