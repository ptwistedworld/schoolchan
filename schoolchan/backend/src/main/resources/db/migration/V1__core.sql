CREATE TABLE boards (
    id UUID PRIMARY KEY,
    slug VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    active_thread_cap INT NOT NULL DEFAULT 50,
    bump_limit INT NOT NULL DEFAULT 300,
    image_limit INT NOT NULL DEFAULT 150,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE threads (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL REFERENCES boards(id),
    subject VARCHAR(200),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    bumped_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    reply_count INT NOT NULL DEFAULT 0,
    image_count INT NOT NULL DEFAULT 0,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_threads_board_bump ON threads(board_id, status, bumped_at DESC);

CREATE TABLE posts (
    id UUID PRIMARY KEY,
    thread_id UUID NOT NULL REFERENCES threads(id),
    board_id UUID NOT NULL REFERENCES boards(id),
    board_post_number BIGINT NOT NULL,
    original_text TEXT NOT NULL,
    translated_text TEXT,
    detected_language VARCHAR(32),
    translation_model VARCHAR(128),
    translation_confidence DOUBLE PRECISION,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING_SCREENING',
    spoiler BOOLEAN NOT NULL DEFAULT FALSE,
    spoiler_label VARCHAR(128),
    score INT NOT NULL DEFAULT 0,
    sage BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (board_id, board_post_number)
);
CREATE INDEX idx_posts_thread ON posts(thread_id, created_at);

CREATE TABLE media (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id),
    original_filename VARCHAR(255),
    stored_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    size_bytes BIGINT NOT NULL,
    quarantine_status VARCHAR(32) NOT NULL DEFAULT 'QUARANTINED',
    phash VARCHAR(64),
    width INT,
    height INT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE site_settings (
    setting_key VARCHAR(128) PRIMARY KEY,
    setting_value TEXT NOT NULL
);

CREATE TABLE quotes (
    id UUID PRIMARY KEY,
    body TEXT NOT NULL,
    attribution VARCHAR(255)
);

CREATE TABLE votes (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id),
    session_hash VARCHAR(128) NOT NULL,
    vote_value SMALLINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (post_id, session_hash)
);

