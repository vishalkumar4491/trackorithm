-- Make sheet tags globally reusable by name (case-insensitive).
-- This prevents duplicate tags like "DSA" created by many users.

DROP INDEX IF EXISTS ux_sheet_tags_system_name_lower;
DROP INDEX IF EXISTS ux_sheet_tags_user_owner_name_lower;

CREATE UNIQUE INDEX IF NOT EXISTS ux_sheet_tags_name_lower
    ON sheet_tags (lower(name));

