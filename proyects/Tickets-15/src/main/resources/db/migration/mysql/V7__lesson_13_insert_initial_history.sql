-- V7__lesson_13_insert_initial_history.sql
-- Lección 13: historial inicial que refleja los cambios de estado ya ocurridos

-- Ticket 2 (Mejora en dashboard) pasó de NEW a IN_PROGRESS
INSERT INTO ticket_history (ticket_id, previous_status, new_status, changed_at, comment) VALUES
  (2, 'NEW', 'IN_PROGRESS', NOW(), 'Inicio de desarrollo del dashboard');
