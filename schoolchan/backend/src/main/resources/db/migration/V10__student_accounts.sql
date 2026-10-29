CREATE TABLE IF NOT EXISTS student_accounts (
    id UUID PRIMARY KEY,
    student_id VARCHAR(32) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    display_name VARCHAR(100),
    grade_section VARCHAR(64),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    provisioned_by VARCHAR(64),
    created_at TIMESTAMP NOT NULL
);
CREATE TABLE IF NOT EXISTS student_enrollment_requests (
    id UUID PRIMARY KEY,
    student_id VARCHAR(32) NOT NULL,
    display_name VARCHAR(100),
    grade_section VARCHAR(64),
    requested_by VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL,
    reviewed_by VARCHAR(64),
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_enroll_status ON student_enrollment_requests (status, created_at DESC);