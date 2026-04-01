-- Canonical problem catalog + placements (Option 1):
-- - problems are global/canonical (no direct FK to topic/subtopic)
-- - topic_problems and subtopic_problems place problems into learning structures
-- - problem_links stores alternate platform links per canonical problem

CREATE TABLE IF NOT EXISTS problem_links (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

    platform platform_enum NOT NULL,
    external_id VARCHAR(150),
    canonical_url TEXT NOT NULL,

    title_on_platform VARCHAR(255),
    difficulty_on_platform difficulty_enum,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Dedup links per platform.
CREATE UNIQUE INDEX IF NOT EXISTS ux_problem_links_platform_external
    ON problem_links(platform, external_id)
    WHERE external_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_problem_links_platform_url
    ON problem_links(platform, canonical_url);

CREATE INDEX IF NOT EXISTS idx_problem_links_problem
    ON problem_links(problem_id);

-- Placement under topics (for two-tier sheets).
CREATE TABLE IF NOT EXISTS topic_problems (
    topic_id UUID NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    order_index INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY(topic_id, problem_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_topic_problems_order
    ON topic_problems(topic_id, order_index);

CREATE INDEX IF NOT EXISTS idx_topic_problems_problem
    ON topic_problems(problem_id);

-- Placement under subtopics (for three-tier sheets).
CREATE TABLE IF NOT EXISTS subtopic_problems (
    subtopic_id UUID NOT NULL REFERENCES subtopics(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    order_index INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY(subtopic_id, problem_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_subtopic_problems_order
    ON subtopic_problems(subtopic_id, order_index);

CREATE INDEX IF NOT EXISTS idx_subtopic_problems_problem
    ON subtopic_problems(problem_id);

-- Problem lifecycle fields (admin review, listing).
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'problem_state_enum') THEN
        CREATE TYPE problem_state_enum AS ENUM ('PENDING', 'ACTIVE', 'REJECTED');
    END IF;
END $$;

ALTER TABLE problems
    ADD COLUMN IF NOT EXISTS state problem_state_enum NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS is_listed BOOLEAN NOT NULL DEFAULT FALSE;

-- Search acceleration for title (optional; safe if extension available)
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_problems_title_trgm
    ON problems USING gin (title gin_trgm_ops);

