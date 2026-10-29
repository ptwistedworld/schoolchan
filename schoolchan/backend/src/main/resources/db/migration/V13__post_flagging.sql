-- Flagging: a post is published and flagged for a person to look at.
-- The board never removes anything on its own.
ALTER TABLE posts ADD COLUMN IF NOT EXISTS flagged BOOLEAN;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS flag_reason VARCHAR(500);
ALTER TABLE posts ADD COLUMN IF NOT EXISTS flag_source VARCHAR(32);