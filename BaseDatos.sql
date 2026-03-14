-- ==========================================================
-- 1. CREACIÓN DE ESTRUCTURA (DDL)
-- ==========================================================

-- Tabla Base: Persona
CREATE TABLE persona (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    genero VARCHAR(20),
    edad INT,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion VARCHAR(250),
    telefono VARCHAR(20),
    email VARCHAR(100) UNIQUE
);

-- Tabla Hija: Cliente (Relación de Herencia)
CREATE TABLE cliente (
    id INT PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    cliente_id VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

-- Tabla: Cuenta
CREATE TABLE cuenta (
    id SERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    tipo_cuenta VARCHAR(50) NOT NULL, -- 'Ahorros' o 'Corriente'
    saldo_inicial DECIMAL(15, 2) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    cliente_id INT NOT NULL REFERENCES cliente(id)
);

-- Tabla: Movimiento
CREATE TABLE movimiento (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(50) NOT NULL, -- 'Retiro' o 'Deposito'
    valor DECIMAL(15, 2) NOT NULL,
    saldo_disponible DECIMAL(15, 2) NOT NULL,
    cuenta_id INT NOT NULL REFERENCES cuenta(id)
);

-- ==========================================================
-- 2. DATOS DE PRUEBA (DML)
-- ==========================================================

-- Clientes
-- Nota: El ID del Cliente debe coincidir con el ID de su Persona
INSERT INTO persona (id, nombre, apellido, genero, edad, identificacion, direccion, telefono, email) 
VALUES (1, 'Jose', 'Lema', 'Masculino', 30, '123456789', 'Otavalo sn y principal', '098254785', 'jose.lema@mail.com');
INSERT INTO cliente (id, cliente_id, password, estado) 
VALUES (1, 'C001', '1234', TRUE);

INSERT INTO persona (id, nombre, apellido, genero, edad, identificacion, direccion, telefono, email) 
VALUES (2, 'Marian', 'Arandi', 'Femenino', 28, '987654321', 'Amazonas y NN.UU.', '097548965', 'marian.arandi@mail.com');
INSERT INTO cliente (id, cliente_id, password, estado) 
VALUES (2, 'C002', '5678', TRUE);

INSERT INTO persona (id, nombre, apellido, genero, edad, identificacion, direccion, telefono, email) 
VALUES (3, 'Juan', 'Osorio', 'Masculino', 35, '456123789', '13 de Junio y Equinoccial', '098874587', 'juan.osorio@mail.com');
INSERT INTO cliente (id, cliente_id, password, estado) 
VALUES (3, 'C003', '1245', TRUE);

-- Cuentas
INSERT INTO cuenta (id, numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES (1, '478758', 'Ahorros', 2000.00, TRUE, 1);
INSERT INTO cuenta (id, numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES (2, '225487', 'Corriente', 100.00, TRUE, 2);
INSERT INTO cuenta (id, numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES (3, '495878', 'Ahorros', 0.00, TRUE, 3);
INSERT INTO cuenta (id, numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES (4, '496825', 'Ahorros', 540.00, TRUE, 2);

-- ==========================================================
-- 3. SINCRONIZACIÓN DE SECUENCIAS
-- ==========================================================
SELECT setval(pg_get_serial_sequence('persona', 'id'), (SELECT MAX(id) FROM persona));
SELECT setval(pg_get_serial_sequence('cuenta', 'id'), (SELECT MAX(id) FROM cuenta));
SELECT setval(pg_get_serial_sequence('movimiento', 'id'), COALESCE((SELECT MAX(id) FROM movimiento), 1));