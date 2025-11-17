-- ###################################
--              USERS
-- ###################################

-- Admin user with BCrypt hashed password (password: admin123)
insert into app_user (uuid, name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'System Administrator', 'admin@company.com', '$2a$10$.E7TXjx4EE6MKcx1EJ0q6.1msDkxkzghReD2J5ID6LiiseRin731e', true, now(), now());