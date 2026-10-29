package com.schoolchan.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * A provisioned student account. The student ID number is the login name.
 *
 * <p>This account is a <em>gate</em> only: nothing here is ever attached to a
 * post. Posts remain labelled "Anonymous" and the anonymous session hash still
 * drives votes, bookmarks and rate limiting, so enabling student login does
 * not make posts attributable.
 */
@Entity
@Table(name = "student_accounts")
public class StudentAccount {

    @Id
    private UUID id;

    @Column(name = "student_id", nullable = false, unique = true, length = 32)
    private String studentId;

    @Column(name = "password_hash", nullable = false, length = 128)
    private String passwordHash;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "grade_section", length = 64)
    private String gradeSection;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "provisioned_by", length = 64)
    private String provisionedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getGradeSection() { return gradeSection; }
    public void setGradeSection(String gradeSection) { this.gradeSection = gradeSection; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
    public String getProvisionedBy() { return provisionedBy; }
    public void setProvisionedBy(String provisionedBy) { this.provisionedBy = provisionedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
