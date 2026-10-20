package com.schoolchan.staff;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "poster_metadata")
public class PosterMetadata {

    @Id
    private UUID id;

    @Column(name = "post_id", nullable = false, unique = true)
    private UUID postId;

    @Column(name = "ip_encrypted", nullable = false)
    private byte[] ipEncrypted;

    @Column(name = "session_hash_encrypted", nullable = false)
    private byte[] sessionHashEncrypted;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public byte[] getIpEncrypted() { return ipEncrypted; }
    public void setIpEncrypted(byte[] ipEncrypted) { this.ipEncrypted = ipEncrypted; }
    public byte[] getSessionHashEncrypted() { return sessionHashEncrypted; }
    public void setSessionHashEncrypted(byte[] sessionHashEncrypted) { this.sessionHashEncrypted = sessionHashEncrypted; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
