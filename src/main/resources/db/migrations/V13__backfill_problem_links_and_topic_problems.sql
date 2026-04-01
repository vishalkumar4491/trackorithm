-- Backfill from the old design (problems.topic_id + problems.order_index) into placement tables.
-- Safe to run once; ignores conflicts if rerun.

INSERT INTO topic_problems(topic_id, problem_id, order_index)
SELECT p.topic_id, p.id, p.order_index
FROM problems p
WHERE p.topic_id IS NOT NULL
ON CONFLICT DO NOTHING;

INSERT INTO problem_links(problem_id, platform, external_id, canonical_url, title_on_platform, difficulty_on_platform)
SELECT p.id, p.platform, p.external_problem_id, p.problem_url, p.title, p.difficulty
FROM problems p
WHERE p.problem_url IS NOT NULL
ON CONFLICT DO NOTHING;

