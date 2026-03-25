-- 1. Drop the unique index (since it's an index, not a constraint)
DROP INDEX IF EXISTS ux_subtopics_topic_order_index;

-- 2. You might also want to drop the constraint just in case Hibernate auto-generated one
ALTER TABLE subtopics DROP CONSTRAINT IF EXISTS ux_subtopics_topic_order_index;

-- 3. Add the rule back as a true DEFERRABLE CONSTRAINT
ALTER TABLE subtopics ADD CONSTRAINT ux_subtopics_topic_order_index
    UNIQUE (topic_id, order_index)
    DEFERRABLE INITIALLY DEFERRED;