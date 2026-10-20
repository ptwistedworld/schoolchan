CREATE TABLE reports (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id),
    session_hash VARCHAR(128) NOT NULL,
    reason VARCHAR(64) NOT NULL,
    note VARCHAR(1000),
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (post_id, session_hash)
);

CREATE TABLE reputation_entries (
    id UUID PRIMARY KEY,
    session_hash VARCHAR(128) NOT NULL UNIQUE,
    reliability DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    strike_level INT NOT NULL DEFAULT 0,
    strike_expires_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE training_examples (
    id UUID PRIMARY KEY,
    post_id UUID REFERENCES posts(id),
    text_snapshot TEXT NOT NULL,
    label VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE poster_metadata (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL UNIQUE REFERENCES posts(id),
    ip_encrypted BYTEA NOT NULL,
    session_hash_encrypted BYTEA NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE escalations (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id),
    status VARCHAR(64) NOT NULL DEFAULT 'NEW',
    category VARCHAR(64) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE media_phash_blocklist (
    id UUID PRIMARY KEY,
    phash VARCHAR(64) NOT NULL UNIQUE,
    source_post_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
