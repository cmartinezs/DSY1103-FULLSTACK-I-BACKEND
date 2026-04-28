-- V3__lesson_12_create_users_table.sql
-- Lección 12: tabla de usuarios

CREATE TABLE users (
    id      BIGINT      AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NOT NULL UNIQUE,

    INDEX idx_users_email (email)
);
