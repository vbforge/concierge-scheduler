-- ============================================================
-- V1__create_concierges_table.sql
-- Location: src/main/resources/db/migration/
-- ============================================================

CREATE TABLE IF NOT EXISTS concierges
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    color      VARCHAR(20)  NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,

    INDEX idx_name (name),
    INDEX idx_active (active),
    INDEX idx_deleted (deleted)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;