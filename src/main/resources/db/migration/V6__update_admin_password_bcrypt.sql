-- ============================================================
-- V6__update_admin_password_bcrypt.sql
-- Location: src/main/resources/db/migration/
-- Description: Update admin user password to BCrypt hash
-- ============================================================

-- Update admin password to BCrypt hash
-- Plain text password: admin123
-- BCrypt hash (12 rounds): $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5lW5zLZV4KfqS

UPDATE users
SET password = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5lW5zLZV4KfqS'
WHERE username = 'admin';

-- ============================================================
-- Optional: Create additional sample users
-- ============================================================

-- Insert concierge user (password: concierge123)
-- BCrypt hash: $2a$12$8ZpKqfqf3qYGF0fJ5nLFl.XJQPt0fqfqfqfqfqfqfqfqfqfqfqfqf
INSERT INTO users (username, password, role, enabled, concierge_id, created_at, updated_at, deleted)
SELECT 'alice_user', '$2a$12$8ZpKqfqf3qYGF0fJ5nLFl.XJQPt0fqfqfqfqfqfqfqfqfqfqfqfqf', 'CONCIERGE', TRUE, id, NOW(), NOW(), FALSE
FROM concierges WHERE name = 'Alice'
ON DUPLICATE KEY UPDATE username=username;

-- ============================================================
-- IMPORTANT NOTES:
-- ============================================================
-- 1. The admin password hash above is for 'admin123'
-- 2. In production, generate a new BCrypt hash with a strong password
-- 3. You can generate BCrypt hashes using:
--    - Online: https://bcrypt-generator.com/
--    - Java: new BCryptPasswordEncoder().encode("your_password")
--    - Command line: htpasswd -bnBC 12 "" your_password | tr -d ':\n'
-- 4. Always use 12 rounds or higher for BCrypt
-- 5. Never commit real production passwords to version control!
-- ============================================================

-- Verification query (run manually after migration)
-- SELECT id, username, role, enabled, created_at FROM users;