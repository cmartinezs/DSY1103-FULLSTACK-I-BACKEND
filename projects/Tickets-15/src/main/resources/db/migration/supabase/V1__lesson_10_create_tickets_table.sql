-- V1__lesson_10_create_tickets_table.sql
-- Lección 10: esquema inicial de tickets (JPA + H2/PostgreSQL)

CREATE TABLE tickets (
    id                          BIGSERIAL   PRIMARY KEY,
    title                       VARCHAR(255),
    description                 VARCHAR(255),
    status                      VARCHAR(255),
    created_at                  TIMESTAMP,
    estimated_resolution_date   DATE,
    effective_resolution_date   TIMESTAMP
);

CREATE INDEX idx_tickets_status ON tickets(status);
