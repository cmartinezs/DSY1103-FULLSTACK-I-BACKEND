-- V2: Add categories (Lección 12)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

ALTER TABLE tickets ADD COLUMN category_id BIGINT;
ALTER TABLE tickets ADD CONSTRAINT fk_tickets_category
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

CREATE INDEX idx_tickets_category ON tickets(category_id);