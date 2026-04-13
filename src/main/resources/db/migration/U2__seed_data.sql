DELETE FROM tasks
WHERE project_id IN (
    SELECT id FROM projects WHERE name = 'Sample Project'
);

DELETE FROM projects
WHERE name = 'Sample Project';

DELETE FROM users
WHERE email = 'test@example.com';