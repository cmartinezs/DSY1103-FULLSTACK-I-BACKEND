-- V1__lesson_10_create_tickets_table.sql
-- Lección 10: esquema inicial de tickets (JPA + H2/MySQL)

CREATE TABLE tickets (
    id                          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    title                       VARCHAR(255),
    description                 VARCHAR(255),
    status                      VARCHAR(255),
    created_at                  DATETIME(6),
    estimated_resolution_date   DATE,
    effective_resolution_date   DATETIME(6),

    INDEX idx_tickets_status (status)
);
