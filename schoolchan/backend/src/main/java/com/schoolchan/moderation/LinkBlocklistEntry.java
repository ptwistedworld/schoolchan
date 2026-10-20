package com.schoolchan.moderation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "link_blocklist")
public class LinkBlocklistEntry {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String domain;

    @Column(length = 255)
    private String reason;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
