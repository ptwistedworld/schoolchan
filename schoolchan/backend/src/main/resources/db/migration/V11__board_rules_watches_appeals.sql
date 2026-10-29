CREATE TABLE IF NOT EXISTS board_rules (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL,
    pattern VARCHAR(512) NOT NULL,
    match_type VARCHAR(32) NOT NULL,
    category VARCHAR(64) NOT NULL,
    severity DOUBLE PRECISION NOT NULL DEFAULT 0.6,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    language VARCHAR(32)
);
CREATE INDEX IF NOT EXISTS idx_board_rules_board ON board_rules (board_id, enabled);

CREATE TABLE IF NOT EXISTS thread_watch (
    id UUID PRIMARY KEY,
    thread_id UUID NOT NULL,
    session_hash VARCHAR(128) NOT NULL,
    last_seen_post BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_thread_watch_unique ON thread_watch (thread_id, session_hash);

CREATE TABLE IF NOT EXISTS appeals (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    session_hash VARCHAR(128) NOT NULL,
    reason VARCHAR(1000),
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_appeals_status ON appeals (status, created_at DESC);

CREATE TABLE IF NOT EXISTS poster_tiers (
    id UUID PRIMARY KEY,
    session_hash VARCHAR(128) NOT NULL UNIQUE,
    tier VARCHAR(16) NOT NULL,
    score DOUBLE PRECISION NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS student_reset_codes (
    id UUID PRIMARY KEY,
    student_id VARCHAR(32) NOT NULL,
    code VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_reset_codes_student ON student_reset_codes (student_id, used);

CREATE TABLE IF NOT EXISTS moderation_sightings (
    id UUID PRIMARY KEY,
    escalation_id UUID NOT NULL,
    staff_name VARCHAR(64) NOT NULL,
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_sightings_escalation ON moderation_sightings (escalation_id, created_at DESC);
