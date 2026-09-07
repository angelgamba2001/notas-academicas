CREATE DATABASE IF NOT EXISTS notas_academicas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE notas_academicas;

CREATE TABLE IF NOT EXISTS estudiante (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)   NOT NULL,
    nota1           DECIMAL(3,1)   NOT NULL,
    nota2           DECIMAL(3,1)   NOT NULL,
    nota3           DECIMAL(3,1)   NOT NULL,
    nota4           DECIMAL(3,1)   NOT NULL,
    promedio        DECIMAL(3,1)   NOT NULL,
    resultado       VARCHAR(50)    NOT NULL,
    fecha_registro  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,

    -- Refuerzo de las validaciones tambien a nivel de base de datos
    CONSTRAINT chk_nota1 CHECK (nota1 BETWEEN 0.0 AND 5.0),
    CONSTRAINT chk_nota2 CHECK (nota2 BETWEEN 0.0 AND 5.0),
    CONSTRAINT chk_nota3 CHECK (nota3 BETWEEN 0.0 AND 5.0),
    CONSTRAINT chk_nota4 CHECK (nota4 BETWEEN 0.0 AND 5.0),
    CONSTRAINT chk_promedio CHECK (promedio BETWEEN 0.0 AND 5.0)
);

-- Datos de ejemplo (opcional, uno por cada categoria de rendimiento)
INSERT INTO estudiante (nombre, nota1, nota2, nota3, nota4, promedio, resultado) VALUES
('Ana Torres',    2.0, 2.5, 3.0, 2.4, 2.5, 'Rendimiento insuficiente (R.I)'),
('Carlos Ruiz',   3.5, 3.8, 3.2, 3.9, 3.6, 'Aprobado (A)'),
('Laura Gómez',   4.2, 4.5, 4.0, 4.3, 4.3, 'Aprobado con sobresaliente (A.S)'),
('Mario Peña',    5.0, 4.8, 4.6, 4.7, 4.8, 'Aprobado con excelente (A.E)');