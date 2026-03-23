-- Tracks which system sheets a user has enrolled in ("My sheets").
-- One row per (user, sheet). Re-enroll clears removed_at.

CREATE TABLE IF NOT EXISTS user_sheet_enrollments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sheet_id UUID NOT NULL REFERENCES sheets(id) ON DELETE CASCADE,

    enrolled_at TIMESTAMP NOT NULL DEFAULT NOW(),
    removed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE(user_id, sheet_id)
);

CREATE INDEX IF NOT EXISTS idx_use_user_active
    ON user_sheet_enrollments(user_id)
    WHERE removed_at IS NULL;

