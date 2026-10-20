package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "boards")
public class Board {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String slug;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 512)
    private String description;

    @Column(name = "active_thread_cap", nullable = false)
    private int activeThreadCap;

    @Column(name = "bump_limit", nullable = false)
    private int bumpLimit;

    @Column(name = "image_limit", nullable = false)
    private int imageLimit;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getActiveThreadCap() { return activeThreadCap; }
    public void setActiveThreadCap(int activeThreadCap) { this.activeThreadCap = activeThreadCap; }
    public int getBumpLimit() { return bumpLimit; }
    public void setBumpLimit(int bumpLimit) { this.bumpLimit = bumpLimit; }
    public int getImageLimit() { return imageLimit; }
    public void setImageLimit(int imageLimit) { this.imageLimit = imageLimit; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
