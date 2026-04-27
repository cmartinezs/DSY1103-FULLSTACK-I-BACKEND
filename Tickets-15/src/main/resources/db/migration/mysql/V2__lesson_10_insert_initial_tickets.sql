-- V2__lesson_10_insert_initial_tickets.sql
-- Lección 10: tickets iniciales sin referencias a usuarios

INSERT INTO tickets (title, description, status, created_at, estimated_resolution_date) VALUES
  ('Error en login',      'No se puede iniciar sesion con Google', 'NEW',         NOW(), DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
  ('Mejora en dashboard', 'Agregar graficos de estadisticas',       'IN_PROGRESS', NOW(), DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
  ('Documentacion API',   'Falta documentacion de endpoints',       'NEW',         NOW(), DATE_ADD(CURDATE(), INTERVAL 5 DAY));
