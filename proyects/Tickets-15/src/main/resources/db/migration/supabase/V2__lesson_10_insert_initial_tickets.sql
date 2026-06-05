-- V2__lesson_10_insert_initial_tickets.sql
-- Lección 10: tickets iniciales sin referencias a usuarios

INSERT INTO tickets (title, description, status, created_at, estimated_resolution_date) VALUES
  ('Error en login',      'No se puede iniciar sesion con Google', 'NEW',         NOW(), CURRENT_DATE + INTERVAL '5 days'),
  ('Mejora en dashboard', 'Agregar graficos de estadisticas',       'IN_PROGRESS', NOW(), CURRENT_DATE + INTERVAL '5 days'),
  ('Documentacion API',   'Falta documentacion de endpoints',       'NEW',         NOW(), CURRENT_DATE + INTERVAL '5 days');
