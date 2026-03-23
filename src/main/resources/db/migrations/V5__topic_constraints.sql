-- Production constraints for topics:
-- 1) Unique topic name per sheet (case-insensitive)
-- 2) Unique order_index per sheet

CREATE UNIQUE INDEX IF NOT EXISTS ux_topics_sheet_name_lower
    ON topics (sheet_id, lower(name));

CREATE UNIQUE INDEX IF NOT EXISTS ux_topics_sheet_order_index
    ON topics (sheet_id, order_index);

ALTER TABLE topics
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

