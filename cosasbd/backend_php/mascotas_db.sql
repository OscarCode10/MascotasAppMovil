-- mascotas_db.sql — Ejecutar en phpMyAdmin (pestaña SQL) o en la consola de MySQL
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS mascotas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mascotas_db;

CREATE TABLE IF NOT EXISTS mascotas (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    especie        VARCHAR(30)  NOT NULL,   -- viene del Spinner (lista desplegable)
    sexo           VARCHAR(10)  NOT NULL,   -- viene del RadioButton
    edad           INT          NOT NULL DEFAULT 0,
    vacunado       TINYINT(1)   NOT NULL DEFAULT 0,  -- CheckBox
    esterilizado   TINYINT(1)   NOT NULL DEFAULT 0,  -- CheckBox
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuario de la app (igual que en la guía con infinity_pol)
CREATE USER IF NOT EXISTS 'umb_movil'@'localhost' IDENTIFIED BY 'movil2026';
GRANT ALL PRIVILEGES ON mascotas_db.* TO 'umb_movil'@'localhost';
FLUSH PRIVILEGES;

-- Datos de prueba
INSERT INTO mascotas (nombre, especie, sexo, edad, vacunado, esterilizado) VALUES
('Firulais', 'Perro', 'Macho', 3, 1, 0),
('Michi',    'Gato',  'Hembra', 2, 1, 1),
('Piolín',   'Ave',   'Macho', 1, 0, 0);
