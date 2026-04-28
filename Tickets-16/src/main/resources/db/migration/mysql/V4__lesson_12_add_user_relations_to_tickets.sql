-- V4__lesson_12_add_user_relations_to_tickets.sql
-- Lección 12: agrega FK created_by_id y assigned_to_id a tickets

ALTER TABLE tickets
    ADD COLUMN created_by_id    BIGINT,
    ADD COLUMN assigned_to_id   BIGINT,
    ADD CONSTRAINT fk_tickets_created_by
        FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_tickets_assigned_to
        FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_tickets_created_by  ON tickets(created_by_id);
CREATE INDEX idx_tickets_assigned_to ON tickets(assigned_to_id);
