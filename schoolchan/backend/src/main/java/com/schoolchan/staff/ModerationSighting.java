package com.schoolchan.staff;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A staff member acknowledging that they have seen an escalation case. */
@Entity
@Table(name = "moderation_sightings")
public class ModerationSighting {

    @Id
    private UUID id;

    @Column(name = "escalation_id", nullable = false)
    private UUID escalationId;

    @Column(name = "staff_name", nullable = false, length = 64)
    private String staffName;

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEscalationId() { return escalationId; }
    public void setEscalationId(UUID escalationId) { this.escalationId = escalationId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
