INSERT
IGNORE INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin) VALUES
(1, 'Festival de Comida Criolla', 'Verano', '2026-12-01', '2026-12-31'),
(2, 'Festival Gourmet Internacional', 'Primavera', '2026-09-15', '2026-10-15'),
(3, 'Festival de Comida Saludable', 'Otoño', '2026-03-01', '2026-04-30'),
(4, 'Street Food Festival', 'Verano', '2026-01-10', '2026-02-28');

INSERT
IGNORE INTO unidad_de_venta (id, nombre_comercial, codigo, superficie, id_festival) VALUES
(1, 'FoodTruck Gourmet Central', 'FT-001', 15, 1),
(2, 'Puesto Criollo 1', 'PD-001', 10, 1);

INSERT
IGNORE INTO staff (id, dni, nombre, apellido, fecha_nacimiento, fecha_ingreso, sueldo, activo, id_unidad_venta) VALUES
(1, 35123456, 'Juan', 'Pérez', '1990-05-15', '2023-01-10', 850000.00, true, 1),
(2, 38987654, 'María', 'Gómez', '1995-08-20', '2023-03-01', 920000.00, true, 2);

INSERT
IGNORE INTO cocinero (id_staff, categoria) VALUES (1, 'CHEF');
INSERT
IGNORE INTO cajero (id_staff, turno_trabajo) VALUES (2, 'NOCHE');

INSERT
IGNORE INTO rol (id, rol) VALUES (1, 'ROLE_EMPLEADO');

INSERT
IGNORE INTO usuario (id, nombre_usuario, password_usuario, usuario_activo, id_staff, id_rol) VALUES
(1, 'juan.perez@epicentro.com', '123456', true, 1, 1),
(2, 'maria.gomez@epicentro.com', '123456', true, 2, 1);