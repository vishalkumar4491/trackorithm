-- Tags are global; deleting a user should not delete shared tags.
-- Keep creator as audit only.

ALTER TABLE sheet_tags
    DROP CONSTRAINT IF EXISTS sheet_tags_created_by_fkey;

ALTER TABLE sheet_tags
    ADD CONSTRAINT sheet_tags_created_by_fkey
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;

