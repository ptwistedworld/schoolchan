-- Lets a tombstone say "deleted by the person who wrote it" instead of
-- "removed by moderation", without changing the status enum.
ALTER TABLE posts ADD COLUMN IF NOT EXISTS deleted_by_author BOOLEAN NOT NULL DEFAULT FALSE;