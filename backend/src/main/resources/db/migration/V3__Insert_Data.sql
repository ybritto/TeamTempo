-- ###################################
--              USERS
-- ###################################

-- Admin user with BCrypt hashed password (password: admin123)
insert into app_user (uuid, name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'System Administrator', 'admin@company.com',
        '$2a$10$.E7TXjx4EE6MKcx1EJ0q6.1msDkxkzghReD2J5ID6LiiseRin731e', true, now(), now());

-- ###################################
--              TEAMS
-- ###################################

-- Team 1: Development Team
insert into team (uuid, name, description, start_date, end_date, app_user_id, created_at, updated_at)
values (uuid_generate_v4(),
        'Development Team',
        'Main development team responsible for building and maintaining the application',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '1 year',
        (SELECT key_id FROM app_user WHERE email = 'admin@company.com'),
        now(),
        now());

-- Team 2: Quality Assurance Team
insert into team (uuid, name, description, start_date, end_date, app_user_id, created_at, updated_at)
values (uuid_generate_v4(),
        'Quality Assurance Team',
        'QA team focused on testing and ensuring product quality',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '1 year',
        (SELECT key_id FROM app_user WHERE email = 'admin@company.com'),
        now(),
        now());

-- ###################################
--              PROJECTS
-- ###################################

-- Project 1: Web Application Development (Development Team)
insert into project (uuid, name, description, start_date, end_date, is_active, team_id, created_at, updated_at)
values (uuid_generate_v4(),
        'Web Application Development',
        'Main web application project for building the core platform features and user interface',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '6 months',
        TRUE,
        (SELECT key_id FROM team WHERE name = 'Development Team'),
        now(),
        now());

-- Project 2: API Backend Services (Development Team)
insert into project (uuid, name, description, start_date, end_date, is_active, team_id, created_at, updated_at)
values (uuid_generate_v4(),
        'API Backend Services',
        'RESTful API development and microservices architecture implementation',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '8 months',
        TRUE,
        (SELECT key_id FROM team WHERE name = 'Development Team'),
        now(),
        now());

-- Project 3: Automated Testing Suite (Quality Assurance Team)
insert into project (uuid, name, description, start_date, end_date, is_active, team_id, created_at, updated_at)
values (uuid_generate_v4(),
        'Automated Testing Suite',
        'Comprehensive automated testing framework including unit, integration, and E2E tests',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '4 months',
        TRUE,
        (SELECT key_id FROM team WHERE name = 'Quality Assurance Team'),
        now(),
        now());

-- Project 4: Performance Testing (Quality Assurance Team)
insert into project (uuid, name, description, start_date, end_date, is_active, team_id, created_at, updated_at)
values (uuid_generate_v4(),
        'Performance Testing',
        'Load testing and performance optimization project to ensure system scalability',
        CURRENT_DATE + INTERVAL '1 month',
        CURRENT_DATE + INTERVAL '5 months',
        TRUE,
        (SELECT key_id FROM team WHERE name = 'Quality Assurance Team'),
        now(),
        now());

-- Project 5: Mobile App Development (Development Team)
insert into project (uuid, name, description, start_date, end_date, is_active, team_id, created_at, updated_at)
values (uuid_generate_v4(),
        'Mobile App Development',
        'Cross-platform mobile application development for iOS and Android',
        CURRENT_DATE + INTERVAL '2 months',
        CURRENT_DATE + INTERVAL '10 months',
        TRUE,
        (SELECT key_id FROM team WHERE name = 'Development Team'),
        now(),
        now());

-- ###################################
--        PROJECT CONFIGURATIONS
-- ###################################

-- Project Configuration 1: 2-week sprint with Story Points (Web Application Development)
insert into project_configuration (uuid, iteration_duration, iteration_duration_unit, capacity_unit, forcast_unit, is_active, project_id, created_at, updated_at)
values (uuid_generate_v4(),
        2,
        'WEEKS',
        'STORY_POINTS',
        'MAN_DAYS',
        TRUE,
        (SELECT key_id FROM project WHERE name = 'Web Application Development'),
        now(),
        now());

-- Project Configuration 2: 1-week sprint with T-Shirt sizing (API Backend Services)
insert into project_configuration (uuid, iteration_duration, iteration_duration_unit, capacity_unit, forcast_unit, is_active, project_id, created_at, updated_at)
values (uuid_generate_v4(),
        1,
        'WEEKS',
        'T_SHIRT',
        'MAN_DAYS',
        TRUE,
        (SELECT key_id FROM project WHERE name = 'API Backend Services'),
        now(),
        now());

-- Project Configuration 3: Monthly iteration with Story Points (Automated Testing Suite)
insert into project_configuration (uuid, iteration_duration, iteration_duration_unit, capacity_unit, forcast_unit, is_active, project_id, created_at, updated_at)
values (uuid_generate_v4(),
        1,
        'MONTHS',
        'STORY_POINTS',
        'MAN_DAYS',
        TRUE,
        (SELECT key_id FROM project WHERE name = 'Automated Testing Suite'),
        now(),
        now());

-- Project Configuration 4: 2-week sprint with Story Points (Performance Testing - inactive)
insert into project_configuration (uuid, iteration_duration, iteration_duration_unit, capacity_unit, forcast_unit, is_active, project_id, created_at, updated_at)
values (uuid_generate_v4(),
        2,
        'WEEKS',
        'STORY_POINTS',
        'MAN_DAYS',
        FALSE,
        (SELECT key_id FROM project WHERE name = 'Performance Testing'),
        now(),
        now());

-- Project Configuration 5: 2-week sprint with Story Points (Mobile App Development)
insert into project_configuration (uuid, iteration_duration, iteration_duration_unit, capacity_unit, forcast_unit, is_active, project_id, created_at, updated_at)
values (uuid_generate_v4(),
        2,
        'WEEKS',
        'STORY_POINTS',
        'MAN_DAYS',
        TRUE,
        (SELECT key_id FROM project WHERE name = 'Mobile App Development'),
        now(),
        now());

-- ###################################
--              ITERATIONS
-- ###################################

-- Iterations for Web Application Development (2-week sprints)
-- Sprint 1: Completed
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Sprint 1',
        (SELECT key_id FROM project WHERE name = 'Web Application Development'),
        CURRENT_DATE - INTERVAL '4 weeks',
        CURRENT_DATE - INTERVAL '2 weeks',
        40,
        35,
        CURRENT_DATE - INTERVAL '4 weeks',
        CURRENT_DATE - INTERVAL '2 weeks',
        38,
        32,
        now(),
        now());

-- Sprint 2: Completed
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Sprint 2',
        (SELECT key_id FROM project WHERE name = 'Web Application Development'),
        CURRENT_DATE - INTERVAL '2 weeks',
        CURRENT_DATE,
        45,
        40,
        CURRENT_DATE - INTERVAL '2 weeks',
        CURRENT_DATE,
        42,
        38,
        now(),
        now());

-- Sprint 3: In Progress
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Sprint 3',
        (SELECT key_id FROM project WHERE name = 'Web Application Development'),
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '2 weeks',
        50,
        45,
        CURRENT_DATE,
        NULL,
        NULL,
        NULL,
        now(),
        now());

-- Sprint 4: Planned
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Sprint 4',
        (SELECT key_id FROM project WHERE name = 'Web Application Development'),
        CURRENT_DATE + INTERVAL '2 weeks',
        CURRENT_DATE + INTERVAL '4 weeks',
        50,
        45,
        NULL,
        NULL,
        NULL,
        NULL,
        now(),
        now());

-- Iterations for API Backend Services (1-week sprints)
-- Week 1: Completed
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Week 1',
        (SELECT key_id FROM project WHERE name = 'API Backend Services'),
        CURRENT_DATE - INTERVAL '3 weeks',
        CURRENT_DATE - INTERVAL '2 weeks',
        20,
        18,
        CURRENT_DATE - INTERVAL '3 weeks',
        CURRENT_DATE - INTERVAL '2 weeks',
        22,
        20,
        now(),
        now());

-- Week 2: Completed
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Week 2',
        (SELECT key_id FROM project WHERE name = 'API Backend Services'),
        CURRENT_DATE - INTERVAL '2 weeks',
        CURRENT_DATE - INTERVAL '1 week',
        25,
        22,
        CURRENT_DATE - INTERVAL '2 weeks',
        CURRENT_DATE - INTERVAL '1 week',
        24,
        21,
        now(),
        now());

-- Week 3: In Progress
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Week 3',
        (SELECT key_id FROM project WHERE name = 'API Backend Services'),
        CURRENT_DATE - INTERVAL '1 week',
        CURRENT_DATE,
        25,
        22,
        CURRENT_DATE - INTERVAL '1 week',
        NULL,
        NULL,
        NULL,
        now(),
        now());

-- Iterations for Automated Testing Suite (Monthly iterations)
-- Month 1: Completed
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Month 1',
        (SELECT key_id FROM project WHERE name = 'Automated Testing Suite'),
        CURRENT_DATE - INTERVAL '1 month',
        CURRENT_DATE,
        100,
        90,
        CURRENT_DATE - INTERVAL '1 month',
        CURRENT_DATE,
        95,
        88,
        now(),
        now());

-- Month 2: Planned
insert into iteration (uuid, name, project_key_id, planned_start_date, planned_end_date, planned_capacity, planned_forecast, actual_start_date, actual_end_date, actual_capacity, actual_forecast, created_at, updated_at)
values (uuid_generate_v4(),
        'Month 2',
        (SELECT key_id FROM project WHERE name = 'Automated Testing Suite'),
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '1 month',
        110,
        100,
        NULL,
        NULL,
        NULL,
        NULL,
        now(),
        now());