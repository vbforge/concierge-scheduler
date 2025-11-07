-- ============================================================
-- V4__create_users_table.sql
-- Location: src/main/resources/db/migration/
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(20) NOT NULL DEFAULT 'CONCIERGE',
                                     enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                     concierge_id BIGINT,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     deleted BOOLEAN NOT NULL DEFAULT FALSE,

                                     CONSTRAINT fk_user_concierge FOREIGN KEY (concierge_id)
                                         REFERENCES concierges(id) ON DELETE SET NULL,

                                     INDEX idx_username (username),
                                     INDEX idx_role (role),
                                     INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;