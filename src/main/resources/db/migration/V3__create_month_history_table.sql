-- ============================================================
-- V3__create_month_history_table.sql
-- Location: src/main/resources/db/migration/
-- ============================================================

CREATE TABLE IF NOT EXISTS month_history (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             year_value INT NOT NULL,
                                             month_value INT NOT NULL,
                                             snapshot_json TEXT NOT NULL,
                                             snapshot_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             description VARCHAR(500),
                                             total_shifts INT,
                                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             deleted BOOLEAN NOT NULL DEFAULT FALSE,

                                             CONSTRAINT uk_year_month UNIQUE (year_value, month_value),
                                             CONSTRAINT chk_year CHECK (year_value >= 2020 AND year_value <= 2100),
                                             CONSTRAINT chk_month CHECK (month_value >= 1 AND month_value <= 12),

                                             INDEX idx_year_month (year_value, month_value),
                                             INDEX idx_deleted (deleted),
                                             INDEX idx_snapshot_date (snapshot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;