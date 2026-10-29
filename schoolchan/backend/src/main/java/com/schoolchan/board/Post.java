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
@Table(name = "posts")
public class Post {

    @Id
    private UUID id;

    @Column(name = "thread_id", nullable = false)
    private UUID threadId;

    @Column(name = "board_id", nullable = false)
    private UUID boardId;

    @Column(name = "board_post_number", nullable = false)
    private long boardPostNumber;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "translated_text", columnDefinition = "TEXT")
    private String translatedText;

    @Column(name = "detected_language", length = 32)
    private String detectedLanguage;

    @Column(name = "translation_model", length = 128)
    private String translationModel;

    @Column(name = "translation_confidence")
    private Double translationConfidence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PostStatus status = PostStatus.PENDING_SCREENING;

    @Column(nullable = false)
    private boolean spoiler;

    @Column(name = "spoiler_label", length = 128)
    private String spoilerLabel;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private boolean sage;

<<<<<<< HEAD
=======
    /**
     * True when the author removed their own post, so the tombstone can say
     * "deleted by the person who wrote it" instead of "removed by moderation".
     */
    @Column(name = "deleted_by_author", nullable = false)
    private Boolean deletedByAuthor = Boolean.FALSE;

    /**
     * True when the post has been flagged for a person to look at. The post is
     * still published: the board never removes anything on its own.
     */
    @Column(name = "flagged")
    private Boolean flagged;

    /** Why it was flagged, and who flagged it. */
    @Column(name = "flag_reason", length = 500)
    private String flagReason;

    /** "filter", "student", "moderator" or "escalation". */
    @Column(name = "flag_source", length = 32)
    private String flagSource;

>>>>>>> d5851d3 (Initial commit)
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getThreadId() { return threadId; }
    public void setThreadId(UUID threadId) { this.threadId = threadId; }
    public UUID getBoardId() { return boardId; }
    public void setBoardId(UUID boardId) { this.boardId = boardId; }
    public long getBoardPostNumber() { return boardPostNumber; }
    public void setBoardPostNumber(long boardPostNumber) { this.boardPostNumber = boardPostNumber; }
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(String translatedText) { this.translatedText = translatedText; }
    public String getDetectedLanguage() { return detectedLanguage; }
    public void setDetectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; }
    public String getTranslationModel() { return translationModel; }
    public void setTranslationModel(String translationModel) { this.translationModel = translationModel; }
    public Double getTranslationConfidence() { return translationConfidence; }
    public void setTranslationConfidence(Double translationConfidence) { this.translationConfidence = translationConfidence; }
    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }
    public boolean isSpoiler() { return spoiler; }
    public void setSpoiler(boolean spoiler) { this.spoiler = spoiler; }
    public String getSpoilerLabel() { return spoilerLabel; }
    public void setSpoilerLabel(String spoilerLabel) { this.spoilerLabel = spoilerLabel; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public boolean isSage() { return sage; }
    public void setSage(boolean sage) { this.sage = sage; }
<<<<<<< HEAD
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
=======
    public boolean isDeletedByAuthor() { return Boolean.TRUE.equals(deletedByAuthor); }
    public void setDeletedByAuthor(boolean deletedByAuthor) { this.deletedByAuthor = deletedByAuthor; }
    public boolean isFlagged() { return Boolean.TRUE.equals(flagged); }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public String getFlagReason() { return flagReason; }
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    public String getFlagSource() { return flagSource; }
    public void setFlagSource(String flagSource) { this.flagSource = flagSource; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** True when a machine translation exists that differs from the original text. */
    @jakarta.persistence.Transient
    public boolean isTranslated() {
        if (translatedText == null || translatedText.isBlank()) {
            return false;
        }
        if (detectedLanguage == null || detectedLanguage.isBlank() || "en".equalsIgnoreCase(detectedLanguage)) {
            return false;
        }
        return !translatedText.equals(originalText);
    }
>>>>>>> d5851d3 (Initial commit)
}
