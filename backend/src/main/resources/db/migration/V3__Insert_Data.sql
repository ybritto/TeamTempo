-- ###################################
--              USERS
-- ###################################

-- Admin user with BCrypt hashed password (password: admin123)
insert into app_user (uuid, name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'System Administrator', 'admin@company.com', '$2a$10$.E7TXjx4EE6MKcx1EJ0q6.1msDkxkzghReD2J5ID6LiiseRin731e', true, now(), now());

-- ###################################
--              TEAMS
-- ###################################

-- Team 1: Development Team
insert into team (uuid, name, description, start_date, end_date, app_user_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'Development Team',
    'Main development team responsible for building and maintaining the application',
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '1 year',
    (SELECT key_id FROM app_user WHERE email = 'admin@company.com'),
    now(),
    now()
);

-- Team 2: Quality Assurance Team
insert into team (uuid, name, description, start_date, end_date, app_user_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'Quality Assurance Team',
    'QA team focused on testing and ensuring product quality',
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '1 year',
    (SELECT key_id FROM app_user WHERE email = 'admin@company.com'),
    now(),
    now()
);