package com.schoolchan.media;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media")
public class MediaEntity {

    @Id
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "stored_name", nullable = false)
    private String storedName;

<<<<<<< HEAD
=======
    /** A small copy used by the board previews, when one could be made. */
    @Column(name = "thumbnail_name")
    private String thumbnailName;

>>>>>>> d5851d3 (Initial commit)
    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "quarantine_status", nullable = false, length = 32)
    private String quarantineStatus = "QUARANTINED";

    @Column(length = 64)
    private String phash;

    private Integer width;
    private Integer height;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
<<<<<<< HEAD
=======
    public String getThumbnailName() { return thumbnailName; }
    public void setThumbnailName(String thumbnailName) { this.thumbnailName = thumbnailName; }
>>>>>>> d5851d3 (Initial commit)
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getQuarantineStatus() { return quarantineStatus; }
    public void setQuarantineStatus(String quarantineStatus) { this.quarantineStatus = quarantineStatus; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getPhash() { return phash; }
    public void setPhash(String phash) { this.phash = phash; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
