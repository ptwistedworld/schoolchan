CREATE TABLE IF NOT EXISTS post_sessions (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL UNIQUE,
    session_hash VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_post_sessions_session ON post_sessions (session_hash, created_at DESC);