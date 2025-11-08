-- ============================================================
-- V7__update_admin_password_bcrypt.sql
-- Location: src/main/resources/db/migration/
-- Description: Update admin user password to BCrypt hash
-- ============================================================

-- Update admin password to BCrypt hash (version 2)
-- Plain text password: admin123
-- BCrypt hash (12 rounds): $2a$12$Gkk0KpxHUo14ClfvcOJYK.tm0FqDU1HZfzOpP24Gn5hgJJj/bRsDa

UPDATE users
SET password = '$2a$12$Gkk0KpxHUo14ClfvcOJYK.tm0FqDU1HZfzOpP24Gn5hgJJj/bRsDa'
WHERE username = 'admin';

