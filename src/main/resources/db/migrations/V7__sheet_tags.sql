-- Sheet tags:
-- - system tags (admin-curated): is_system = true, created_by NULL
-- - user tags (owner-scoped): is_system = false, created_by = user
-- - sheets can attach tags via sheet_tag_map

CREATE TABLE IF NOT EXISTS sheet_tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    name VARCHAR(80) NOT NULL,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID REFERENCES users(id) ON DELETE CASCADE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_sheet_tags_system_name_lower
    ON sheet_tags (lower(name))
    WHERE is_system = TRUE;

CREATE UNIQUE INDEX IF NOT EXISTS ux_sheet_tags_user_owner_name_lower
    ON sheet_tags (created_by, lower(name))
    WHERE is_system = FALSE;

CREATE TABLE IF NOT EXISTS sheet_tag_map (
    sheet_id UUID NOT NULL REFERENCES sheets(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES sheet_tags(id) ON DELETE CASCADE,

    PRIMARY KEY (sheet_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_sheet_tag_map_sheet
    ON sheet_tag_map(sheet_id);

CREATE INDEX IF NOT EXISTS idx_sheet_tag_map_tag
    ON sheet_tag_map(tag_id);

