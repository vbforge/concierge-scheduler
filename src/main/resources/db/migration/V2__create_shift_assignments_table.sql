-- ============================================================
-- V2__create_shift_assignments_table.sql
-- Location: src/main/resources/db/migration/
-- ============================================================

CREATE TABLE IF NOT EXISTS shift_assignments (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 shift_date DATE NOT NULL,
                                                 concierge_id BIGINT NOT NULL,
                                                 shift_type VARCHAR(20) NOT NULL DEFAULT 'FULL_DAY',
                                                 notes VARCHAR(500),
                                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                 deleted BOOLEAN NOT NULL DEFAULT FALSE,

    -- generated columns for YEAR and MONTH to allow indexing by year/month
                                                 shift_year INT GENERATED ALWAYS AS (YEAR(shift_date)) STORED,
                                                 shift_month INT GENERATED ALWAYS AS (MONTH(shift_date)) STORED,

                                                 CONSTRAINT fk_shift_concierge FOREIGN KEY (concierge_id)
                                                     REFERENCES concierges(id) ON DELETE RESTRICT,

                                                 CONSTRAINT uk_shift_date_concierge UNIQUE (shift_date, concierge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes after table creation (explicitly create them)
CREATE INDEX idx_shift_date ON shift_assignments (shift_date);
CREATE INDEX idx_concierge_id ON shift_assignments (concierge_id);
CREATE INDEX idx_deleted ON shift_assignments (deleted);
CREATE INDEX idx_year_month ON shift_assignments (shift_year, shift_month);