-- Sheet enhancements:
-- 1) Optional source link for system sheets (avoid copyright issues).
-- 2) Unique sheet name constraints (case-insensitive):
--    - system sheets: unique by name
--    - user sheets: unique per user

ALTER TABLE sheets
    ADD COLUMN IF NOT EXISTS source_url TEXT;

CREATE UNIQUE INDEX IF NOT EXISTS ux_sheets_system_name_lower
    ON sheets (lower(name))
    WHERE type = 'SYSTEM';

CREATE UNIQUE INDEX IF NOT EXISTS ux_sheets_user_owner_name_lower
    ON sheets (created_by, lower(name))
    WHERE type = 'USER';

