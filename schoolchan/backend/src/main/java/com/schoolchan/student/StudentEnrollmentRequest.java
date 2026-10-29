package com.schoolchan.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * A request from an existing student to onboard a newly enrolled student.
 * The request stays pending until a staff member approves it, at which point
 * an account is provisioned with a temporary password.
 */
@Entity
@Table(name = "student_enrollment_requests")
public class StudentEnrollmentRequest {

    @Id
    private UUID id;

    @Column(name = "student_id", nullable = false, length = 32)
    private String studentId;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "grade_section", length = 64)
    private String gradeSection;

    @Column(name = "requested_by", nullable = false, length = 32)
    private String requestedBy;

    @Column(nullable = false, length = 16)
    private String status = "PENDING";

    @Column(name = "reviewed_by", length = 64)
    private String reviewedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getGradeSection() { return gradeSection; }
    public void setGradeSection(String gradeSection) { this.gradeSection = gradeSection; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
