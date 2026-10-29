CREATE TABLE IF NOT EXISTS bookmarks (
    id UUID PRIMARY KEY,
    session_hash VARCHAR(64) NOT NULL,
    thread_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_bookmarks_session ON bookmarks (session_hash, created_at DESC);