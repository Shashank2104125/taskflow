-- Fixed IDs (important)
-- User
INSERT INTO users (id, name, email, password)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Test User',
    'test@example.com',
    '$2a$10$Dow1p6v2vZl8Q0sP0XQ8QeF9uWcYQz8rF7kRXKuX3sI5Yucs5cjG6'
);

-- Project
INSERT INTO projects (id, name, description, owner_id)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'Sample Project',
    'Seeded project for testing',
    '11111111-1111-1111-1111-111111111111'
);

-- Tasks
INSERT INTO tasks (title, description, status, priority, project_id, assignee_id, creator_id)
VALUES
(
    'Task 1',
    'Todo task',
    'todo',
    'low',
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111'
),
(
    'Task 2',
    'In progress task',
    'in_progress',
    'medium',
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111'
),
(
    'Task 3',
    'Done task',
    'done',
    'high',
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111'
);