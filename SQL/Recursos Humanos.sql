USE SistemaRRHH;

SELECT * 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'RecursosHumanos' 
  AND TABLE_NAME = 'Usuario';

SELECT TABLE_SCHEMA, TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES;

SELECT * FROM Usuarios;

CREATE TABLE RecursosHumanos.Empleado(
id INT PRIMARY KEY,
nombre VARCHAR(50) NOT NULL,
apellido VARCHAR(50) NOT NULL,
fechaContratacion DATE NOT NULL,
cargo VARCHAR(50) NOT NULL,
salario MONEY NOT NULL,
departamento VARCHAR(50) NOT NULL
);

INSERT INTO RecursosHumanos.Empleado (id, nombre, apellido, fechaContratacion, cargo, salario, departamento) 
VALUES 
(1, 'Juan', 'Pérez', '2020-01-15', 'Gerente', 75000.00, 'Administración'),
(2, 'María', 'Gómez', '2019-07-30', 'Analista', 55000.00, 'Finanzas'),
(3, 'Carlos', 'Martínez', '2021-03-22', 'Desarrollador', 60000.00, 'Tecnología'),
(4, 'Ana', 'Rodríguez', '2018-11-10', 'Diseñadora', 48000.00, 'Marketing'),
(5, 'Luis', 'Fernández', '2022-05-05', 'Consultor', 70000.00, 'Consultoría');


SELECT * FROM RecursosHumanos.Empleado;
CREATE TABLE RecursosHumanos.Cat_Departamentos(
id INT PRIMARY KEY,
nombre VARCHAR(50) NOT NULL);

INSERT INTO RecursosHumanos.Cat_Departamentos (id, nombre) VALUES
(1,'Dirección Ejecutiva'),
(2,'Secretaría'),
(3,'Unidad de Auditoría Interna'),
(4,'Recursos Humanos'),
(5,'Tecnología de la Información'),
(6,'Finanzas'),
(7,'Marketing'),
(8,'Operaciones'),
(9,'Ventas'),
(10,'Logística');
SELECT * FROM RecursosHumanos.Cat_Departamentos;

SELECT e.id, e.nombre, e.apellido, e.fechaContratacion, e.cargo, e.salario, d.nombre AS departamento
FROM RecursosHumanos.Empleado e
JOIN RecursosHumanos.Cat_Departamentos d ON e.idDepartamento = d.id;


ALTER TABLE RecursosHumanos.Empleado
ADD idDepartamento INT;

ALTER TABLE RecursosHumanos.Empleado
ADD CONSTRAINT FK_Departamento
FOREIGN KEY (idDepartamento) REFERENCES RecursosHumanos.Cat_Departamentos(id);

ALTER TABLE RecursosHumanos.Empleado
DROP COLUMN departamento;

UPDATE RecursosHumanos.Empleado SET idDepartamento = 6 WHERE id = 6;

CREATE TABLE RecursosHumanos.Cat_Usuario(
id INT PRIMARY KEY,
tipo VARCHAR(20) NOT NULL
);

INSERT INTO RecursosHumanos.Cat_Usuario(id, tipo)
VALUES (1, 'Administrador'), (2, 'Reclutador'), (3, 'Operador');
SELECT * FROM RecursosHumanos.Cat_Usuario;
UPDATE RecursosHumanos.Cat_Usuario SET tipo = 'Reclutador' WHERE id = 2;

CREATE TABLE RecursosHumanos.Usuario(
id INT PRIMARY KEY,
nombre VARCHAR(50) NOT NULL,
nombreCompleto VARCHAR(100) NOT NULL,
correo VARCHAR(100) NOT NULL,
telefono VARCHAR(8),
contraseña VARCHAR(64) NOT NULL,
tipoUsuario INT NOT NULL,
FOREIGN KEY (tipoUsuario) REFERENCES RecursosHumanos.Cat_Usuario(id)
);
SELECT * FROM RecursosHumanos.Usuario;

ALTER TABLE RecursosHumanos.Usuario ADD contraseña_temp VARBINARY(MAX);
ALTER TABLE RecursosHumanos.Usuario DROP COLUMN contraseña;
EXEC sp_rename 'RecursosHumanos.Usuario.contraseña_temp', 'contraseña', 'COLUMN';

DROP TABLE RecursosHumanos.Usuario;

INSERT INTO RecursosHumanos.Usuario(id, nombre, nombreCompleto, correo, telefono, contraseña, tipoUsuario)
VALUES (1, 'admin', 'Adminstrador de Sistema', 'admin@rrhh.com', '50000000','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 1)

CREATE TABLE RecursosHumanos.Cat_Contrato(
id INT PRIMARY KEY,
tipo VARCHAR(20) NOT NULL
);
INSERT INTO RecursosHumanos.Cat_Contrato(id, tipo)
VALUES (1, 'Indefinido'), (2, 'Definido'), (3, 'Por obra'), (4, 'Por servicios');
SELECT * FROM RecursosHumanos.Cat_Contrato;


CREATE TABLE RecursosHumanos.Contratacion(
id INT PRIMARY KEY,
idEmpleado INT NOT NULL,
fechaInicio DATE NOT NULL,
tipoContrato INT NOT NULL,
duracion INT NOT NULL,
salario MONEY NOT NULL,
FOREIGN KEY (idEmpleado) REFERENCES RecursosHumanos.Empleado(id),
FOREIGN KEY (tipoContrato) REFERENCES RecursosHumanos.Cat_Contrato(id)
);
SELECT * FROM RecursosHumanos.Contratacion;

SELECT c.id, c.idEmpleado, e.nombre, e.apellido, c.fechaInicio, cc.tipo AS tipoContrato, c.duracion, c.salario
FROM RecursosHumanos.Contratacion c
JOIN RecursosHumanos.Empleado e ON c.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Contrato cc ON c.tipoContrato = cc.id;

SELECT c.id, c.idEmpleado, c.fechaInicio, cc.tipo AS tipoContrato, c.duracion, c.salario
FROM RecursosHumanos.Contratacion c
JOIN RecursosHumanos.Cat_Contrato cc ON c.tipoContrato = cc.id;

CREATE TABLE RecursosHumanos.Cat_Acciones(
id INT PRIMARY KEY,
tipo VARCHAR(20) NOT NULL
);
INSERT INTO RecursosHumanos.Cat_Acciones(id, tipo)
VALUES (1, 'Permiso'), (2, 'Licencia'), (3, 'Vacaciones'), (4, 'Otros');
SELECT * FROM RecursosHumanos.Cat_Acciones;

SELECT ap.id, ap.idEmpleado, e.nombre, e.apellido, ca.tipo AS tipoAccion, ap.fecha, ap.detalle
FROM RecursosHumanos.Accion_Personal ap
JOIN RecursosHumanos.Empleado e ON ap.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Acciones ca ON ap.tipoAccion = ca.id
WHERE ap.id = 1;

SELECT ap.id, ap.idEmpleado, ca.tipo AS tipoAccion, ap.fecha, ap.detalle
FROM RecursosHumanos.Accion_Personal ap
JOIN RecursosHumanos.Cat_Acciones ca ON ap.tipoAccion = ca.id
WHERE ap.idEmpleado = 7;

SELECT ap.id, ap.idEmpleado, ca.tipo AS tipoAccion, ap.fecha, ap.detalle
FROM RecursosHumanos.Accion_Personal ap
JOIN RecursosHumanos.Cat_Acciones ca ON ap.tipoAccion = ca.id
WHERE ap.fecha BETWEEN '2024-01-01' AND '2024-12-31';


CREATE TABLE RecursosHumanos.Accion_Personal(
    id INT IDENTITY(1,1) PRIMARY KEY,
    idEmpleado INT NOT NULL,
    tipoAccion INT NOT NULL,
    fecha DATE NOT NULL,
    detalle VARCHAR(50) NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES RecursosHumanos.Empleado(id),
    FOREIGN KEY (tipoAccion) REFERENCES RecursosHumanos.Cat_Acciones(id)
);

ALTER TABLE RecursosHumanos.Accion_Personal
ALTER COLUMN detalle VARCHAR(255);

DELETE FROM RecursosHumanos.Accion_Personal;
DROP TABLE RecursosHumanos.Accion_Personal;

ALTER TABLE RecursosHumanos.Accion_Personal
ADD id INT IDENTITY(1,1) PRIMARY KEY;

SELECT * FROM RecursosHumanos.Accion_Personal;


CREATE TABLE RecursosHumanos.Cat_Movimientos(
id INT PRIMARY KEY,
tipo VARCHAR(20) NOT NULL
);
INSERT INTO RecursosHumanos.Cat_Movimientos(id, tipo)
VALUES (1, 'Promocion'), (2, 'Transferencia'), (3, 'Cambio de puesto'), (4, 'Ascenso');
SELECT * FROM RecursosHumanos.Cat_Movimientos;

INSERT INTO RecursosHumanos.Cat_Movimientos(id, tipo)
VALUES (5, 'Reasignación');



CREATE TABLE RecursosHumanos.Movimiento_Personal(
id INT IDENTITY(1,1) PRIMARY KEY,
idEmpleado INT NOT NULL,
tipoMovimiento INT NOT NULL,
fecha DATE NOT NULL,
detalle VARCHAR(50) NOT NULL
FOREIGN KEY (idEmpleado) REFERENCES RecursosHumanos.Empleado(id),
FOREIGN KEY (tipoMovimiento) REFERENCES RecursosHumanos.Cat_Movimientos(id)
);
DROP TABLE RecursosHumanos.Movimiento_Personal;
SELECT * FROM RecursosHumanos.Movimiento_Personal;

ALTER TABLE RecursosHumanos.Movimiento_Personal
ALTER COLUMN detalle VARCHAR(255);

UPDATE RecursosHumanos.Movimiento_Personal SET fecha = '2024-09-15' WHERE id = 1;

SELECT u.id, u.nombre, u.nombreCompleto, u.correo, u.telefono, u.contraseña, cu.tipo AS tipoUsuario
FROM RecursosHumanos.Usuario u 
JOIN RecursosHumanos.Cat_Usuario cu ON u.tipoUsuario = cu.id;

DELETE FROM RecursosHumanos.Usuario WHERE id = 8;

SELECT mp.id, mp.idEmpleado, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle
FROM RecursosHumanos.Movimiento_Personal mp
JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id
WHERE mp.idEmpleado = 1;

SELECT mp.id, mp.idEmpleado, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle
FROM RecursosHumanos.Movimiento_Personal mp
JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id
WHERE mp.fecha BETWEEN '1900-01-01' AND '2040-12-31'
ORDER BY mp.fecha DESC

SELECT 
    d.nombre AS Departamento,
    COUNT(e.id) AS CantidadEmpleados
FROM 
    RecursosHumanos.Cat_Departamentos d
LEFT JOIN 
    RecursosHumanos.Empleado e ON d.nombre = e.departamento
GROUP BY 
    d.nombre
ORDER BY 
    d.nombre;

--Consulta de empleados por departamento
SELECT 
    d.nombre AS Departamento,
    COUNT(e.id) AS CantidadEmpleados
FROM 
    RecursosHumanos.Cat_Departamentos d
LEFT JOIN 
    RecursosHumanos.Empleado e ON d.id = e.idDepartamento
GROUP BY 
    d.nombre
ORDER BY 
    d.nombre;

SELECT d.nombre AS Departamento, COUNT(e.id) AS CantidadEmpleados
FROM RecursosHumanos.Cat_Departamentos d
LEFT JOIN RecursosHumanos.Empleado e ON d.id = e.idDepartamento
GROUP BY d.nombre
ORDER BY d.nombre

SELECT d.nombre AS Departamento, COUNT(e.id) AS CantidadEmpleados
FROM RecursosHumanos.Cat_Departamentos d
LEFT JOIN RecursosHumanos.Empleado e ON d.id = e.idDepartamento
WHERE d.id = 1
GROUP BY d.nombre
ORDER BY d.nombre;

--Consulta de Contrataciones recientes
SELECT 
    e.id AS IdEmpleado,
    e.nombre AS Nombre,
    e.apellido AS Apellido,
    e.fechaContratacion AS FechaContratacion,
    c.fechaInicio AS FechaInicio,
    ct.tipo AS TipoContrato,
    c.duracion AS Duracion,
    c.salario AS Salario
FROM 
    RecursosHumanos.Contratacion c
JOIN 
    RecursosHumanos.Empleado e ON c.idEmpleado = e.id
JOIN 
    RecursosHumanos.Cat_Contrato ct ON c.tipoContrato = ct.id
ORDER BY 
    c.fechaInicio ASC;

SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, e.fechaContratacion AS FechaContratacion, c.fechaInicio AS FechaInicio, ct.tipo AS TipoContrato, c.duracion AS Duracion, c.salario AS Salario
FROM RecursosHumanos.Contratacion c
JOIN RecursosHumanos.Empleado e ON c.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Contrato ct ON c.tipoContrato = ct.id
ORDER BY c.fechaInicio DESC;

SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, e.fechaContratacion AS FechaContratacion, c.fechaInicio AS FechaInicio, ct.tipo AS TipoContrato, c.duracion AS Duracion, c.salario AS Salario
FROM RecursosHumanos.Contratacion c
JOIN RecursosHumanos.Empleado e ON c.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Contrato ct ON c.tipoContrato = ct.id
WHERE c.tipoContrato = 4
ORDER BY c.fechaInicio DESC;

--Reporte de vacaciones ausencias
SELECT 
    e.id AS IdEmpleado,
    e.nombre AS Nombre,
    e.apellido AS Apellido,
    a.fecha AS Fecha,
    ca.tipo AS TipoAccion,
    a.detalle AS Detalle
FROM 
    RecursosHumanos.Accion_Personal a
JOIN 
    RecursosHumanos.Empleado e ON a.idEmpleado = e.id
JOIN 
    RecursosHumanos.Cat_Acciones ca ON a.tipoAccion = ca.id
ORDER BY 
    a.fecha DESC;  -- Ordenar por fecha más reciente

SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, a.fecha AS Fecha, ca.tipo AS TipoAccion, a.detalle AS Detalle
FROM RecursosHumanos.Accion_Personal a
JOIN RecursosHumanos.Empleado e ON a.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Acciones ca ON a.tipoAccion = ca.id
ORDER BY a.fecha DESC;

SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, a.fecha AS Fecha, ca.tipo AS TipoAccion, a.detalle AS Detalle
FROM RecursosHumanos.Accion_Personal a
JOIN RecursosHumanos.Empleado e ON a.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Acciones ca ON a.tipoAccion = ca.id
WHERE a.tipoAccion = 3
ORDER BY a.fecha DESC;

SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, a.fecha AS Fecha, ca.tipo AS TipoAccion, a.detalle AS Detalle
FROM RecursosHumanos.Accion_Personal a
JOIN RecursosHumanos.Empleado e ON a.idEmpleado = e.id
JOIN RecursosHumanos.Cat_Acciones ca ON a.tipoAccion = ca.id
WHERE a.tipoAccion = 1 AND (a.fecha BETWEEN '2024-10-25' AND '2024-12-31')
ORDER BY a.fecha DESC;

SELECT * FROM RecursosHumanos.Accion_Personal
UPDATE RecursosHumanos.Accion_Personal SET fecha = '2024-10-27' WHERE id = 5

--Reporte de movimientos
SELECT mp.id AS IdMovimiento, e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, cm.tipo AS TipoMovimiento, mp.fecha AS Fecha, mp.detalle AS Detalle 
FROM RecursosHumanos.Movimiento_Personal mp 
JOIN RecursosHumanos.Empleado e ON mp.idEmpleado = e.id 
JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id 
ORDER BY mp.fecha DESC;

--Reporte de movimientos
SELECT 
    e.id AS idEmpleado,
    e.nombre AS Nombre,
    e.apellido AS Apellido,
    e.cargo AS Cargo,
    e.departamento AS Departamento,
    m.fecha AS FechaMovimiento,
    cm.tipo AS TipoMovimiento,
    m.detalle AS DetalleMovimiento
FROM 
    RecursosHumanos.Movimiento_Personal m
JOIN 
    RecursosHumanos.Empleado e ON m.idEmpleado = e.id
JOIN 
    RecursosHumanos.Cat_Movimientos cm ON m.tipoMovimiento = cm.id
ORDER BY 
    m.fecha DESC;  -- Ordenar por fecha de movimiento, más reciente primero

