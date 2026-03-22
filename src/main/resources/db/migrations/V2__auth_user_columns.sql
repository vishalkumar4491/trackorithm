-- Auth-related user columns and indexes.
-- Keep this migration additive to avoid rewriting V1.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS phone_number VARCHAR(30);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(30) NOT NULL DEFAULT 'USER';

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_phone_number
    ON users (phone_number)
    WHERE phone_number IS NOT NULL;

-- Case-insensitive uniqueness for username/email.
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_username_lower
    ON users (lower(username))
    WHERE username IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_lower
    ON users (lower(email))
    WHERE email IS NOT NULL;

