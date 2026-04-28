-- V6__lesson_13_create_ticket_history_table.sql
-- Lección 13: historial de cambios de tickets (microservicios + auditoría)

CREATE TABLE ticket_history (
    id                      BIGINT      AUTO_INCREMENT PRIMARY KEY,
    ticket_id               BIGINT      NOT NULL,
    previous_status         VARCHAR(20),
    new_status              VARCHAR(20),
    previous_assigned_email VARCHAR(150),
    new_assigned_email      VARCHAR(150),
    changed_at              DATETIME(6) NOT NULL,
    comment                 VARCHAR(255),

    CONSTRAINT fk_ticket_history_ticket
        FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,

    INDEX idx_ticket_history_ticket (ticket_id)
);
