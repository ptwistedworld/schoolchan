CREATE TABLE IF NOT EXISTS staff_notes (
    id UUID PRIMARY KEY,
    post_id UUID REFERENCES posts(id),
    thread_id UUID REFERENCES threads(id),
    author VARCHAR(64) NOT NULL,
    body VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_staff_notes_thread ON staff_notes (thread_id, created_at DESC);