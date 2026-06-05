-- V3__lesson_12_create_users_table.sql
-- Lección 12: tabla de usuarios

CREATE TABLE users (
    id      BIGSERIAL    PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NOT NULL UNIQUE
);

CREATE INDEX idx_users_email ON users(email);
