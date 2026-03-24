-- Subtopics under topics (optional tier).

CREATE TABLE IF NOT EXISTS subtopics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    topic_id UUID NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_subtopics_topic_name_lower
    ON subtopics (topic_id, lower(name));

CREATE UNIQUE INDEX IF NOT EXISTS ux_subtopics_topic_order_index
    ON subtopics (topic_id, order_index);

CREATE INDEX IF NOT EXISTS idx_subtopics_topic_order
    ON subtopics (topic_id, order_index);

