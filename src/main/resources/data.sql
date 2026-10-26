/*-- COSTOS

-- =========================================================
-- costo_superficie       = $500 por m2
-- costo_montaje          = $10
-- plus_electricidad      = $2000
-- sueldo_base            = $100000
-- antiguedad_cajero      = $5000 por año
-- plus_cocinero          = $150000
-- plus_ayudante          = $100000
-- plus_lavaplatos        = $50000

INSERT IGNORE INTO costo
(
    id,
    costo_superficie,
    costo_montaje,
    plus_electricidad,
    sueldo_base,
    antiguedad_cajero,
    plus_cocinero,
    plus_ayudante,
    plus_lavaplatos
)
VALUES
(1, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
(2, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
(3, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
(4, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000);

INSERT
IGNORE INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin, id_costo) VALUES
(1, 'Festival de Comida Criolla', 'Verano', '2026-10-01', '2026-11-5',1),
(2, 'Festival Gourmet Internacional', 'Primavera', '2026-09-15', '2026-10-15',2),
(3, 'Festival de Comida Saludable', 'Otoño', '2026-03-01', '2026-04-30',3),
(4, 'Street Food Festival', 'Verano', '2026-01-10', '2026-02-28',4);

INSERT
IGNORE INTO unidad_de_venta (id, nombre_comercial, codigo, superficie, id_festival) VALUES
(1, 'FoodTruck Gourmet Central', 'FT-001', 15, 1),
(2, 'Puesto Criollo 1', 'PD-001', 10, 1);

INSERT
IGNORE INTO pedido (id, fecha, total, id_unidad_de_venta) VALUES
(1, '2026-12-05', 45000.50, 1),
(2, '2026-12-10', 32000.00, 1),
(3, '2026-12-15', 18500.00, 2),
(4, '2026-12-20', 95000.00, 2);

INSERT
IGNORE INTO staff (id, dni, nombre, apellido, fecha_nacimiento, fecha_ingreso, sueldo, activo, id_unidad_venta) VALUES
(1, 35123456, 'Juan', 'Pérez', '1990-05-15', '2023-01-10', 850000.00, true, 1),
(2, 38987654, 'María', 'Gómez', '1995-08-20', '2023-03-01', 920000.00, true, 1);

INSERT
IGNORE INTO cocinero (id_staff, categoria) VALUES (1, 'COCINERO');
INSERT
IGNORE INTO cajero (id_staff, turno_trabajo) VALUES (2, 'NOCHE');

-- ROLES
INSERT INTO `Rol` (`rol`)
SELECT 'ADMIN'
    WHERE NOT EXISTS (
    SELECT 1 FROM `Rol` WHERE `rol` = 'ADMIN'
);
INSERT INTO `Rol` (`rol`)
SELECT 'RESPONSABLE'
    WHERE NOT EXISTS (
    SELECT 1 FROM `Rol` WHERE `rol` = 'RESPONSABLE'
);
INSERT INTO `Rol` (`rol`)
SELECT 'EMPLEADO'
    WHERE NOT EXISTS (
    SELECT 1 FROM `Rol` WHERE `rol` = 'EMPLEADO'
);

-- ============================================
-- USUARIO ADMIN
-- usuario: admin
-- contraseña: admin123
-- ============================================

INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'admin@gmail.com','$2y$10$wA6BQOeiHqer/VwuER2rse.nFrN4A7q/0gALCT4QmxTqWrMvOiNDe',true,NULL,
(SELECT id FROM `Rol` WHERE `rol` = 'ADMIN' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'admin@gmail.com');

-- ============================================
-- USUARIO RESPONSABLE
-- usuario: responsable
-- contraseña: responsable123
-- ============================================

INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'responsable@gmail.com','$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',true,NULL,
(SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'responsable@gmail.com');

-- ============================================
-- USUARIO EMPLEADO COCINERO
-- contraseña: empleado123
-- ============================================

INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'empleado@gmail.com','$2y$10$/tEyJqFNxSlMJNYQ1oEv9.gb.VsIxJxDIW9mFLriPsaTj3F/Foata',true,1,
(SELECT id FROM `Rol` WHERE `rol` = 'EMPLEADO' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'empleado@gmail.com');

-- ============================================
-- USUARIO EMPLEADO CAJERO
-- contraseña: empleado123
-- ============================================
INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'empleado1@gmail.com','$2y$10$/tEyJqFNxSlMJNYQ1oEv9.gb.VsIxJxDIW9mFLriPsaTj3F/Foata',true,2,
      (SELECT id FROM `Rol` WHERE `rol` = 'EMPLEADO' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'empleado1@gmail.com');

-- ============================================
-- USUARIO EMPLEADO SIN STAFF ASOCIADO PARA PRUEBA DE ERRORES
-- contraseña: empleado123
-- ============================================
INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'empleado2@gmail.com','$2y$10$/tEyJqFNxSlMJNYQ1oEv9.gb.VsIxJxDIW9mFLriPsaTj3F/Foata',true,null,
      (SELECT id FROM `Rol` WHERE `rol` = 'EMPLEADO' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'empleado2@gmail.com');

INSERT
IGNORE INTO usuario (id, nombre_usuario, password_usuario, usuario_activo, id_staff, id_rol) VALUES
(1, 'juan.perez@epicentro.com', '123456', true, 1, 1),
(2, 'maria.gomez@epicentro.com', '123456', true, 2, 1);

-- ============================================
-- RESPONSABLES DE LAS UNIDADES DE VENTA
-- usuario: responsable1@gmail.com -> unidad FT-001 (FoodTruck Gourmet Central)
-- usuario: responsable2@gmail.com -> unidad PD-001 (Puesto Criollo 1)
-- contraseña de ambos: responsable123
-- ============================================

INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'responsable1@gmail.com','$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',true,NULL,
(SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario`WHERE `nombre_usuario` = 'responsable1@gmail.com');

INSERT INTO `Usuario`(`nombre_usuario`,`password_usuario`,`usuario_activo`,`id_staff`,`id_rol`)
SELECT'responsable2@gmail.com','$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',true,NULL,
(SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM `Usuario`WHERE `nombre_usuario` = 'responsable2@gmail.com');

-- Cada usuario queda como responsable de su unidad
UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable1@gmail.com' LIMIT 1)
WHERE `codigo` = 'FT-001' AND `id_usuario_responsable` IS NULL;

UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable2@gmail.com' LIMIT 1)
WHERE `codigo` = 'PD-001' AND `id_usuario_responsable` IS NULL;
 */

INSERT
IGNORE INTO costo
(id, costo_superficie, costo_montaje, plus_electricidad, sueldo_base, antiguedad_cajero, plus_cocinero, plus_ayudante, plus_lavaplatos)
VALUES
(1, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000);

INSERT
IGNORE INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin, id_costo) VALUES
(1, 'Festival Gastronómico Anual', 'Primavera-Verano', '2026-10-01', '2026-12-31', 1);

INSERT INTO `Rol` (`rol`)
SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM `Rol` WHERE `rol` = 'ADMIN');
INSERT INTO `Rol` (`rol`)
SELECT 'RESPONSABLE' WHERE NOT EXISTS (SELECT 1 FROM `Rol` WHERE `rol` = 'RESPONSABLE');
INSERT INTO `Rol` (`rol`)
SELECT 'EMPLEADO' WHERE NOT EXISTS (SELECT 1 FROM `Rol` WHERE `rol` = 'EMPLEADO');

-- =========================================================
-- USUARIO ADMIN
-- usuario: admin@gmail.com | clave: admin123
-- =========================================================
INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'admin@gmail.com',
       '$2y$10$wA6BQOeiHqer/VwuER2rse.nFrN4A7q/0gALCT4QmxTqWrMvOiNDe',
       true,
       NULL,
       (SELECT id FROM `Rol` WHERE `rol` = 'ADMIN' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'admin@gmail.com');

-- =========================================================
-- 5 USUARIOS RESPONSABLES (Clave: responsable123)
-- =========================================================
INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'responsable1@gmail.com',
       '$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',
       true,
       NULL,
       (SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'responsable1@gmail.com');

INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'responsable2@gmail.com',
       '$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',
       true,
       NULL,
       (SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'responsable2@gmail.com');

INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'responsable3@gmail.com',
       '$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',
       true,
       NULL,
       (SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'responsable3@gmail.com');

INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'responsable4@gmail.com',
       '$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',
       true,
       NULL,
       (SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'responsable4@gmail.com');

INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'responsable5@gmail.com',
       '$2y$10$/S2or68JWDgaBPWrF1SJP.aJ2KKyVv4vn5uX9hIEGZ.HJZQxDDqla',
       true,
       NULL,
       (SELECT id FROM `Rol` WHERE `rol` = 'RESPONSABLE' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'responsable5@gmail.com');

INSERT
IGNORE INTO unidad_de_venta (id, nombre_comercial, codigo, superficie, id_festival) VALUES
(1, 'FoodTruck Gourmet Central', 'FT-001', 15, 1),
(2, 'Puesto Criollo 1', 'PD-001', 10, 1),
(3, 'Burger Station', 'FT-002', 12, 1),
(4, 'Pizza & Beer Stand', 'PD-002', 15, 1),
(5, 'Tacos & Grill', 'FT-003', 10, 1);

UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable1@gmail.com' LIMIT 1)
WHERE `codigo` = 'FT-001';
UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable2@gmail.com' LIMIT 1)
WHERE `codigo` = 'PD-001';
UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable3@gmail.com' LIMIT 1)
WHERE `codigo` = 'FT-002';
UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable4@gmail.com' LIMIT 1)
WHERE `codigo` = 'PD-002';
UPDATE `unidad_de_venta`
SET `id_usuario_responsable` = (SELECT id FROM `Usuario` WHERE `nombre_usuario` = 'responsable5@gmail.com' LIMIT 1)
WHERE `codigo` = 'FT-003';

INSERT
IGNORE INTO staff (id, dni, nombre, apellido, fecha_nacimiento, fecha_ingreso, sueldo, activo, id_unidad_venta) VALUES

(1, 35123456, 'Juan', 'Pérez', '1990-05-15', '2023-01-10', 850000.00, true, 1),
(2, 38987654, 'María', 'Gómez', '1995-08-20', '2023-03-01', 920000.00, true, 1),

(3, 36111222, 'Carlos', 'López', '1992-02-10', '2023-02-15', 850000.00, true, 2),
(4, 37333444, 'Ana', 'Martínez', '1994-11-05', '2023-04-10', 920000.00, true, 2),

(5, 34555666, 'Lucas', 'Fernández', '1989-07-22', '2023-01-20', 850000.00, true, 3),
(6, 39777888, 'Sofia', 'Rodríguez', '1996-09-12', '2023-05-01', 920000.00, true, 3),

(7, 33999000, 'Diego', 'Sánchez', '1988-03-30', '2023-02-01', 850000.00, true, 4),
(8, 40111222, 'Laura', 'Díaz', '1997-12-18', '2023-06-15', 920000.00, true, 4),

(9, 32333444, 'Mateo', 'Álvarez', '1987-10-08', '2023-01-05', 850000.00, true, 5),
(10, 41555666, 'Camila', 'Torres', '1998-04-25', '2023-07-01', 920000.00, true, 5);

INSERT
IGNORE INTO cocinero (id_staff, categoria) VALUES
(1, 'COCINERO'), (3, 'COCINERO'), (5, 'AYUDANTE'), (7, 'LAVAPLATOS'), (9, 'COCINERO');

INSERT
IGNORE INTO cajero (id_staff, turno_trabajo) VALUES
(2, 'NOCHE'), (4, 'MAÑANA'), (6, 'TARDE'), (8, 'NOCHE'), (10, 'MAÑANA');

-- USUARIOS PARA CADA EMPLEADO (Clave: empleado123)
INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'empleado1@gmail.com',
       '$2y$10$/tEyJqFNxSlMJNYQ1oEv9.gb.VsIxJxDIW9mFLriPsaTj3F/Foata',
       true,
       1,
       (SELECT id FROM `Rol` WHERE `rol` = 'EMPLEADO' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'empleado1@gmail.com');

INSERT INTO `Usuario` (`nombre_usuario`, `password_usuario`, `usuario_activo`, `id_staff`, `id_rol`)
SELECT 'empleado2@gmail.com',
       '$2y$10$/tEyJqFNxSlMJNYQ1oEv9.gb.VsIxJxDIW9mFLriPsaTj3F/Foata',
       true,
       2,
       (SELECT id FROM `Rol` WHERE `rol` = 'EMPLEADO' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM `Usuario` WHERE `nombre_usuario` = 'empleado2@gmail.com');

INSERT
IGNORE INTO pedido (id, fecha, total, id_unidad_de_venta) VALUES

(1, '2026-10-15', 12000.00, 1),
(2, '2026-10-15', 15500.00, 1),
(3, '2026-10-15', 8000.00, 2),
(4, '2026-10-15', 22000.00, 2),
(5, '2026-10-15', 18500.00, 3),
(6, '2026-10-15', 9500.00, 3),
(7, '2026-10-15', 14000.00, 4),
(8, '2026-10-15', 31000.00, 4),
(9, '2026-10-15', 11500.00, 5),
(10, '2026-10-15', 27000.00, 5),

(11, '2026-10-16', 13000.00, 1),
(12, '2026-10-16', 16000.00, 1),
(13, '2026-10-16', 7500.00, 2),
(14, '2026-10-16', 21000.00, 2),
(15, '2026-10-16', 19000.00, 3),
(16, '2026-10-16', 10500.00, 3),
(17, '2026-10-16', 15000.00, 4),
(18, '2026-10-16', 28000.00, 4),
(19, '2026-10-16', 12500.00, 5),
(20, '2026-10-16', 25000.00, 5);