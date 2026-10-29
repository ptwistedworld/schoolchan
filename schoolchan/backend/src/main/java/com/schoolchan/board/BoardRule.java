package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A moderation rule that applies to one board only. */
@Entity
@Table(name = "board_rules")
public class BoardRule {

    @Id
    private UUID id;

    @Column(name = "board_id", nullable = false)
    private UUID boardId;

    @Column(nullable = false, length = 512)
    private String pattern;

    @Column(name = "match_type", nullable = false, length = 32)
    private String matchType = "WORD";

    @Column(nullable = false, length = 64)
    private String category = "OTHER";

    @Column(nullable = false)
    private double severity = 0.6;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(length = 32)
    private String language;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBoardId() { return boardId; }
    public void setBoardId(UUID boardId) { this.boardId = boardId; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getSeverity() { return severity; }
    public void setSeverity(double severity) { this.severity = severity; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
