-- ==============================
-- EXTENSIONS
-- ==============================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==============================
-- ENUMS
-- ==============================
CREATE TYPE auth_provider_enum AS ENUM ('LOCAL', 'GOOGLE', 'GITHUB');
CREATE TYPE user_status_enum AS ENUM ('ACTIVE', 'SUSPENDED');

CREATE TYPE sheet_type_enum AS ENUM ('SYSTEM', 'USER');
CREATE TYPE visibility_enum AS ENUM ('PUBLIC', 'PRIVATE');

CREATE TYPE platform_enum AS ENUM ('LEETCODE', 'GFG', 'CODEFORCES');

CREATE TYPE difficulty_enum AS ENUM ('EASY', 'MEDIUM', 'HARD');

CREATE TYPE problem_status_enum AS ENUM ('TODO', 'IN_PROGRESS', 'DONE', 'REVISION');

CREATE TYPE revision_status_enum AS ENUM ('PENDING', 'COMPLETED', 'SKIPPED');

CREATE TYPE attempt_result_enum AS ENUM ('SUCCESS', 'FAIL');

CREATE TYPE activity_type_enum AS ENUM ('SOLVE', 'REVISE', 'NOTE_UPDATE');

-- ==============================
-- USERS
-- ==============================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255),
                       name VARCHAR(255),
                       username VARCHAR(255),
                       profile_image_url TEXT,

                       auth_provider auth_provider_enum NOT NULL DEFAULT 'LOCAL',
                       provider_id VARCHAR(255),

                       status user_status_enum NOT NULL DEFAULT 'ACTIVE',

                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       last_login_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- ==============================
-- USER SETTINGS
-- ==============================
CREATE TABLE user_settings (
                               user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,

                               timezone VARCHAR(100),
                               preferred_language VARCHAR(50),

                               daily_goal INT DEFAULT 3,

                               email_notifications_enabled BOOLEAN DEFAULT TRUE,
                               reminder_time TIME,

                               created_at TIMESTAMP DEFAULT NOW(),
                               updated_at TIMESTAMP DEFAULT NOW()
);

-- ==============================
-- USER STATS
-- ==============================
CREATE TABLE user_stats (
                            user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,

                            total_problems_solved INT DEFAULT 0,
                            total_time_spent INT DEFAULT 0,

                            current_streak INT DEFAULT 0,
                            max_streak INT DEFAULT 0,
                            last_active_date DATE,

                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW()
);

-- ==============================
-- SHEETS
-- ==============================
CREATE TABLE sheets (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        name VARCHAR(255) NOT NULL,
                        description TEXT,

                        type sheet_type_enum NOT NULL,
                        visibility visibility_enum NOT NULL,

                        created_by UUID REFERENCES users(id),
                        created_at TIMESTAMP DEFAULT NOW(),
                        updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_sheets_created_by ON sheets(created_by);

-- ==============================
-- TOPICS
-- ==============================
CREATE TABLE topics (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        sheet_id UUID NOT NULL REFERENCES sheets(id) ON DELETE CASCADE,

                        name VARCHAR(255) NOT NULL,
                        order_index INT NOT NULL,

                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_topics_sheet_order ON topics(sheet_id, order_index);

-- ==============================
-- PROBLEMS
-- ==============================
CREATE TABLE problems (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          topic_id UUID NOT NULL REFERENCES topics(id) ON DELETE CASCADE,

                          title VARCHAR(255) NOT NULL,
                          slug VARCHAR(255) UNIQUE NOT NULL,

                          platform platform_enum NOT NULL,
                          external_problem_id VARCHAR(100),
                          problem_url TEXT NOT NULL,

                          difficulty difficulty_enum NOT NULL,
                          order_index INT NOT NULL,

                          frequency_score INT DEFAULT 0,
                          acceptance_rate FLOAT,

                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_problems_topic_order ON problems(topic_id, order_index);
CREATE INDEX idx_problems_difficulty ON problems(difficulty);

-- ==============================
-- TAGS (SYSTEM)
-- ==============================
CREATE TABLE problem_tags (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE problem_tag_map (
                                 problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
                                 tag_id UUID REFERENCES problem_tags(id) ON DELETE CASCADE,
                                 PRIMARY KEY (problem_id, tag_id)
);

-- ==============================
-- COMPANY TAGS
-- ==============================
CREATE TABLE company_tags (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE problem_company_map (
                                     problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
                                     company_id UUID REFERENCES company_tags(id) ON DELETE CASCADE,
                                     PRIMARY KEY (problem_id, company_id)
);

-- ==============================
-- USER PROBLEM STATUS (CORE)
-- ==============================
CREATE TABLE user_problem_status (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                     user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

                                     status problem_status_enum NOT NULL DEFAULT 'TODO',

                                     attempt_count INT DEFAULT 0,
                                     success_count INT DEFAULT 0,

                                     confidence_level INT CHECK (confidence_level BETWEEN 1 AND 5),

                                     time_spent_seconds INT DEFAULT 0,

                                     first_attempt_at TIMESTAMP,
                                     last_attempt_at TIMESTAMP,
                                     last_solved_at TIMESTAMP,
                                     last_revision_at TIMESTAMP,

                                     last_viewed_at TIMESTAMP,
                                     view_count INT DEFAULT 0,

                                     is_verified BOOLEAN DEFAULT FALSE,
                                     is_bookmarked BOOLEAN DEFAULT FALSE,
                                     source VARCHAR(50),

                                     created_at TIMESTAMP DEFAULT NOW(),
                                     updated_at TIMESTAMP DEFAULT NOW(),

                                     UNIQUE(user_id, problem_id)
);

CREATE INDEX idx_ups_user_status ON user_problem_status(user_id, status);
CREATE INDEX idx_ups_user_last_solved ON user_problem_status(user_id, last_solved_at);

-- ==============================
-- PROBLEM ATTEMPTS (APPEND ONLY)
-- ==============================
CREATE TABLE problem_attempts (
                                  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                  problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

                                  result attempt_result_enum NOT NULL,
                                  time_spent_seconds INT,

                                  submitted_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_attempts_user_problem ON problem_attempts(user_id, problem_id);

-- ==============================
-- NOTES
-- ==============================
CREATE TABLE notes (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                       user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

                       content TEXT,

                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW(),

                       UNIQUE(user_id, problem_id)
);

CREATE INDEX idx_notes_user_problem ON notes(user_id, problem_id);

-- ==============================
-- NOTE VERSIONS
-- ==============================
CREATE TABLE note_versions (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                               note_id UUID NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
                               content TEXT NOT NULL,

                               created_at TIMESTAMP DEFAULT NOW()
);

-- ==============================
-- USER TAGS
-- ==============================
CREATE TABLE user_tags (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

                           name VARCHAR(100) NOT NULL,

                           UNIQUE(user_id, name)
);

CREATE TABLE user_problem_tags (
                                   user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                   problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
                                   tag_id UUID REFERENCES user_tags(id) ON DELETE CASCADE,

                                   PRIMARY KEY (user_id, problem_id, tag_id)
);

-- ==============================
-- REVISION SCHEDULE
-- ==============================
CREATE TABLE revision_schedule (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                   user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

                                   revision_number INT NOT NULL,
                                   scheduled_date DATE NOT NULL,

                                   status revision_status_enum DEFAULT 'PENDING',

                                   created_at TIMESTAMP DEFAULT NOW(),
                                   completed_at TIMESTAMP
);

CREATE INDEX idx_revision_user_date
    ON revision_schedule(user_id, scheduled_date, status);

-- ==============================
-- REVISION HISTORY
-- ==============================
CREATE TABLE revision_history (
                                  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                  problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,

                                  revision_number INT,
                                  confidence_level INT CHECK (confidence_level BETWEEN 1 AND 5),

                                  reviewed_at TIMESTAMP DEFAULT NOW()
);

-- ==============================
-- ACTIVITY LOG
-- ==============================
CREATE TABLE activity_log (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                              user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              action_type activity_type_enum NOT NULL,

                              problem_id UUID,
                              metadata JSONB,

                              created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_activity_user_time ON activity_log(user_id, created_at);

-- ==============================
-- DAILY ACTIVITY
-- ==============================
CREATE TABLE daily_activity (
                                user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                date DATE,

                                problems_solved INT DEFAULT 0,
                                time_spent INT DEFAULT 0,

                                PRIMARY KEY (user_id, date)
);

