-- ###################################
--              USERS
-- ###################################

-- Admin user with BCrypt hashed password (password: admin123)
insert into app_user (uuid, full_name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'System Administrator', 'admin@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', true, now(), now());

-- Test user with BCrypt hashed password (password: user123)
insert into app_user (uuid, full_name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'Test User', 'test@company.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', true, now(), now());

-- Manager user with BCrypt hashed password (password: manager123)
insert into app_user (uuid, full_name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'Team Manager', 'manager@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', true, now(), now());

-- Developer user with BCrypt hashed password (password: dev123)
insert into app_user (uuid, full_name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'Developer User', 'dev@company.com', '$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm', true, now(), now());

-- Disabled user for testing (password: disabled123)
insert into app_user (uuid, full_name, email, password, enabled, created_at, updated_at)
values (uuid_generate_v4(), 'Disabled User', 'disabled@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', false, now(), now());