CREATE INDEX IF NOT EXISTS idx_threads_board_status_bumped ON threads (board_id, status, bumped_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_thread_created ON posts (thread_id, created_at);
CREATE INDEX IF NOT EXISTS idx_posts_board_number ON posts (board_id, board_post_number);
CREATE INDEX IF NOT EXISTS idx_moderation_post ON moderation_results (post_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reports_post ON reports (post_id);