-- V6__lesson_13_create_ticket_history_table.sql
-- Lección 13: historial de cambios de tickets (microservicios + auditoría)

CREATE TABLE ticket_history (
    id                      BIGSERIAL    PRIMARY KEY,
    ticket_id               BIGINT       NOT NULL,
    previous_status         VARCHAR(20),
    new_status              VARCHAR(20),
    previous_assigned_email VARCHAR(150),
    new_assigned_email      VARCHAR(150),
    changed_at              TIMESTAMP    NOT NULL,
    comment                 VARCHAR(255),

    CONSTRAINT fk_ticket_history_ticket
        FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE
);

CREATE INDEX idx_ticket_history_ticket ON ticket_history(ticket_id);
