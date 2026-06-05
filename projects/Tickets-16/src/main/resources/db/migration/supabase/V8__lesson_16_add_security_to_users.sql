-- V8: Lección 16 - Agrega campos de seguridad a usuarios
ALTER TABLE users ADD COLUMN password VARCHAR(255);
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE users
SET password = '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m', role = 'USER', active = TRUE
WHERE email = 'ana.garcia@empresa.com';

UPDATE users
SET password = '$2a$10$LAK58ME84bgotvy2eL.eWeobSCHMDsaD3BajXq/swyevMwfw8PW/m', role = 'AGENT', active = TRUE
WHERE email = 'carlos.lopez@empresa.com';

INSERT INTO users (name, email, password, role, active)
SELECT 'Administrador', 'admin@empresa.com', '$2a$10$gT.PsFi3xTq9xc3virQAfesYBesY5g53tQ5R7lgJGqgVdVMH0I8qa', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@empresa.com');

UPDATE tickets
SET assigned_to_id = (SELECT id FROM users WHERE email = 'carlos.lopez@empresa.com')
WHERE title IN ('Error en login', 'Documentacion API');
