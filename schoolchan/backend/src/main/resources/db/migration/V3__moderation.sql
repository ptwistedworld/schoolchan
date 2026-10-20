CREATE TABLE blacklist_entries (
    id UUID PRIMARY KEY,
    pattern VARCHAR(512) NOT NULL,
    match_type VARCHAR(32) NOT NULL,
    category VARCHAR(64) NOT NULL,
    severity DOUBLE PRECISION NOT NULL DEFAULT 0.9,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    language VARCHAR(32)
);

CREATE TABLE link_blocklist (
    id UUID PRIMARY KEY,
    domain VARCHAR(255) NOT NULL UNIQUE,
    reason VARCHAR(255)
);

CREATE TABLE moderation_results (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id),
    decision VARCHAR(32) NOT NULL,
    reasons TEXT,
    stage_results_json TEXT NOT NULL,
    total_latency_ms BIGINT NOT NULL,
    baseline_mode BOOLEAN NOT NULL DEFAULT FALSE,
    ml_unavailable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE learned_blacklist_candidates (
    id UUID PRIMARY KEY,
    pattern VARCHAR(512) NOT NULL,
    source_post_id UUID REFERENCES posts(id),
    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.5,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
