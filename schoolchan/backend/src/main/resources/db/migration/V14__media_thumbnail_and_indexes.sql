-- Faster lookups for the board page, the catalog and the media queue.
--
-- The board page used to load every post of every thread just to show the
-- first one, and every attachment of every thread. These indexes back the
-- queries that replaced that.
CREATE INDEX IF NOT EXISTS idx_posts_thread_status_number ON posts (thread_id, status, board_post_number);
CREATE INDEX IF NOT EXISTS idx_media_post ON media (post_id);
CREATE INDEX IF NOT EXISTS idx_media_post_status ON media (post_id, quarantine_status);
CREATE INDEX IF NOT EXISTS idx_moderation_created ON moderation_results (created_at);

-- A small copy of each image, used for the board previews. Storing it means a
-- board page never has to load the full-size file.
ALTER TABLE media ADD COLUMN IF NOT EXISTS thumbnail_name VARCHAR(255);
