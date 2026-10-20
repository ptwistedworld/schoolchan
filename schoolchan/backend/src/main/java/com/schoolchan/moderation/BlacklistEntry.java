package com.schoolchan.moderation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "blacklist_entries")
public class BlacklistEntry {

    @Id
    private UUID id;

    @Column(nullable = false, length = 512)
    private String pattern;

    @Column(name = "match_type", nullable = false, length = 32)
    private String matchType;

    @Column(nullable = false, length = 64)
    private String category;

    @Column(nullable = false)
    private double severity = 0.9;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(length = 32)
    private String language;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
