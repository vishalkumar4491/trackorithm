-- One editorial/solution reference per problem, reused across all sheets that include the problem.
-- Admin-managed.

CREATE TABLE IF NOT EXISTS problem_editorials (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

    -- Optional: written editorial content (markdown/plain text).
    content TEXT,

    -- Optional: external references
    youtube_url TEXT,
    reference_url TEXT,

    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE(problem_id)
);

CREATE INDEX IF NOT EXISTS idx_problem_editorials_problem
    ON problem_editorials(problem_id);

