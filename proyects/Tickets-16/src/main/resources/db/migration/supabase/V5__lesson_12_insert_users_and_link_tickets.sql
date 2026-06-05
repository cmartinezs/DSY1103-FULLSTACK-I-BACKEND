-- V5__lesson_12_insert_users_and_link_tickets.sql
-- Lección 12: inserta usuarios y vincula los tickets existentes

INSERT INTO users (name, email) VALUES
  ('Ana Garcia',   'ana.garcia@empresa.com'),
  ('Carlos Lopez', 'carlos.lopez@empresa.com');

UPDATE tickets SET created_by_id = 1 WHERE title IN ('Error en login', 'Documentacion API');
UPDATE tickets SET created_by_id = 2 WHERE title = 'Mejora en dashboard';
