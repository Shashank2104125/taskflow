-- Drop indexes (optional, Postgres drops with table but good practice)
DROP INDEX IF EXISTS idx_tasks_creator_id;
DROP INDEX IF EXISTS idx_tasks_assignee_id;
DROP INDEX IF EXISTS idx_tasks_project_id;

DROP INDEX IF EXISTS idx_projects_owner_id;

-- Drop tables in dependency order
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

-- Drop extension (optional, depends on requirement)
DROP EXTENSION IF EXISTS pgcrypto;