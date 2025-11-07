-- ============================================================
-- V5__insert_initial_data.sql
-- Location: src/main/resources/db/migration/
-- ============================================================

-- Insert initial concierges
INSERT INTO concierges (name, color, active, created_at, updated_at, deleted)
VALUES
    ('Alice', 'BLUE', TRUE, NOW(), NOW(), FALSE),
    ('Bob', 'PURPLE', TRUE, NOW(), NOW(), FALSE),
    ('Carol', 'GREEN', TRUE, NOW(), NOW(), FALSE)
ON DUPLICATE KEY UPDATE name=name;

-- Insert admin user (password: admin123 - will be BCrypt encoded in Phase 4)
-- For now, using plain text (TEMPORARY - will be updated in Phase 4)
INSERT INTO users (username, password, role, enabled, created_at, updated_at, deleted)
VALUES
    ('admin', 'admin123', 'ADMIN', TRUE, NOW(), NOW(), FALSE)
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample shift assignments for current month (November 2025)
INSERT INTO shift_assignments (shift_date, concierge_id, shift_type, created_at, updated_at, deleted)
SELECT '2025-11-01', id, 'FULL_DAY', NOW(), NOW(), FALSE FROM concierges WHERE name = 'Alice'
UNION ALL
SELECT '2025-11-02', id, 'FULL_DAY', NOW(), NOW(), FALSE FROM concierges WHERE name = 'Bob'
UNION ALL
SELECT '2025-11-03', id, 'FULL_DAY', NOW(), NOW(), FALSE FROM concierges WHERE name = 'Carol'
UNION ALL
SELECT '2025-11-04', id, 'FULL_DAY', NOW(), NOW(), FALSE FROM concierges WHERE name = 'Alice'
UNION ALL
SELECT '2025-11-05', id, 'FULL_DAY', NOW(), NOW(), FALSE FROM concierges WHERE name = 'Bob'
UNION ALL
SELECT '2025-11-06', id, 'FULL_DAY', NOW(), NOW(), FALSE FROM concierges WHERE name = 'Carol'
ON DUPLICATE KEY UPDATE shift_date=shift_date;

-- ============================================================
-- VERIFICATION QUERIES (Run these manually to check)
-- ============================================================

-- Check concierges
-- SELECT * FROM concierges;

-- Check shift assignments
-- SELECT
--     s.id, s.shift_date, c.name as concierge_name, s.shift_type, s.deleted
-- FROM shift_assignments s
-- JOIN concierges c ON s.concierge_id = c.id
-- ORDER BY s.shift_date;

-- Check users
-- SELECT * FROM users;

-- Count shifts per concierge
-- SELECT
--     c.name,
--     COUNT(s.id) as total_shifts
-- FROM concierges c
-- LEFT JOIN shift_assignments s ON c.id = s.concierge_id AND s.deleted = FALSE
-- GROUP BY c.id, c.name;

-- ============================================================
-- ROLLBACK SCRIPTS (if needed)
-- ============================================================

-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS month_history;
-- DROP TABLE IF EXISTS shift_assignments;
-- DROP TABLE IF EXISTS concierges;