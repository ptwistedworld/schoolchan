-- The entity treats a missing value as "not deleted by the author", so the
-- column should say the same: never null, default false. A fresh database
-- got this from V12; an older one needs it applied now.
UPDATE posts SET deleted_by_author = FALSE WHERE deleted_by_author IS NULL;
ALTER TABLE posts ALTER COLUMN deleted_by_author SET DEFAULT FALSE;
ALTER TABLE posts ALTER COLUMN deleted_by_author SET NOT NULL;
