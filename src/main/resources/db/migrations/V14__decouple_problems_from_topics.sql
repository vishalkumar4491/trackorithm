-- Decouple canonical problems from topics.
-- After this, topics/subtopics only reference problems via placement tables.

-- Drop FK and NOT NULL constraints by dropping the column.
ALTER TABLE problems
    DROP CONSTRAINT IF EXISTS problems_topic_id_fkey;

-- Drop old indexes tied to topic_id/order_index if present.
DROP INDEX IF EXISTS idx_problems_topic_order;

-- These columns are now represented by topic_problems/subtopic_problems.
ALTER TABLE problems
    DROP COLUMN IF EXISTS topic_id,
    DROP COLUMN IF EXISTS order_index;

