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

-- ###################################
--              PROJECTS
-- ###################################

-- Project 1: Web Application Development (Development Team)
insert into project (uuid, name, description, start_date, end_date, team_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'Web Application Development',
    'Main web application project for building the core platform features and user interface',
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '6 months',
    (SELECT key_id FROM team WHERE name = 'Development Team'),
    now(),
    now()
);

-- Project 2: API Backend Services (Development Team)
insert into project (uuid, name, description, start_date, end_date, team_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'API Backend Services',
    'RESTful API development and microservices architecture implementation',
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '8 months',
    (SELECT key_id FROM team WHERE name = 'Development Team'),
    now(),
    now()
);

-- Project 3: Automated Testing Suite (Quality Assurance Team)
insert into project (uuid, name, description, start_date, end_date, team_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'Automated Testing Suite',
    'Comprehensive automated testing framework including unit, integration, and E2E tests',
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '4 months',
    (SELECT key_id FROM team WHERE name = 'Quality Assurance Team'),
    now(),
    now()
);

-- Project 4: Performance Testing (Quality Assurance Team)
insert into project (uuid, name, description, start_date, end_date, team_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'Performance Testing',
    'Load testing and performance optimization project to ensure system scalability',
    CURRENT_DATE + INTERVAL '1 month',
    CURRENT_DATE + INTERVAL '5 months',
    (SELECT key_id FROM team WHERE name = 'Quality Assurance Team'),
    now(),
    now()
);

-- Project 5: Mobile App Development (Development Team)
insert into project (uuid, name, description, start_date, end_date, team_id, created_at, updated_at)
values (
    uuid_generate_v4(),
    'Mobile App Development',
    'Cross-platform mobile application development for iOS and Android',
    CURRENT_DATE + INTERVAL '2 months',
    CURRENT_DATE + INTERVAL '10 months',
    (SELECT key_id FROM team WHERE name = 'Development Team'),
    now(),
    now()
);