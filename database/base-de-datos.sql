-- ============================================================
-- BASE DE DATOS: notas_academicas
-- Proyecto: Notas Académicas + Programación Académica
-- MySQL 8.0
-- Usuario: root
-- Contraseña: root
-- ============================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS notas_academicas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE notas_academicas;

-- ============================================================
-- LIMPIAR TABLAS EXISTENTES
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS horario;
DROP TABLE IF EXISTS hora_extra;
DROP TABLE IF EXISTS docente_asignatura;
DROP TABLE IF EXISTS estudiante;
DROP TABLE IF EXISTS docente;
DROP TABLE IF EXISTS asignatura;
DROP TABLE IF EXISTS curso;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. TABLA ESTUDIANTE
-- ============================================================

CREATE TABLE estudiante (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,

    nota1 DECIMAL(3,1) NOT NULL,
    nota2 DECIMAL(3,1) NOT NULL,
    nota3 DECIMAL(3,1) NOT NULL,
    nota4 DECIMAL(3,1) NOT NULL,

    promedio DECIMAL(3,1) NOT NULL,
    resultado VARCHAR(50) NOT NULL,

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT chk_estudiante_nota1
        CHECK (nota1 BETWEEN 0.0 AND 5.0),

    CONSTRAINT chk_estudiante_nota2
        CHECK (nota2 BETWEEN 0.0 AND 5.0),

    CONSTRAINT chk_estudiante_nota3
        CHECK (nota3 BETWEEN 0.0 AND 5.0),

    CONSTRAINT chk_estudiante_nota4
        CHECK (nota4 BETWEEN 0.0 AND 5.0),

    CONSTRAINT chk_estudiante_promedio
        CHECK (promedio BETWEEN 0.0 AND 5.0)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 2. TABLA CURSO
-- ============================================================

CREATE TABLE curso (
    id_curso INT NOT NULL AUTO_INCREMENT,

    grado VARCHAR(20) NOT NULL,
    nombre_curso VARCHAR(50) NOT NULL,
    jornada VARCHAR(20) NOT NULL,
    num_estudiantes INT NOT NULL DEFAULT 0,

    PRIMARY KEY (id_curso),

    CONSTRAINT chk_curso_estudiantes
        CHECK (num_estudiantes >= 0)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 3. TABLA ASIGNATURA
-- ============================================================

CREATE TABLE asignatura (
    id_asignatura INT NOT NULL AUTO_INCREMENT,

    nombre VARCHAR(50) NOT NULL,
    intensidad_horaria INT NOT NULL,

    PRIMARY KEY (id_asignatura),

    CONSTRAINT chk_asignatura_horas
        CHECK (intensidad_horaria > 0)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 4. TABLA DOCENTE
-- ============================================================

CREATE TABLE docente (
    id_docente INT NOT NULL AUTO_INCREMENT,

    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    disponibilidad VARCHAR(100) DEFAULT NULL,
    horas_maximas_semanales INT NOT NULL DEFAULT 20,

    PRIMARY KEY (id_docente),

    CONSTRAINT chk_docente_horas
        CHECK (horas_maximas_semanales > 0)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 5. RELACIÓN DOCENTE - ASIGNATURA
-- ============================================================

CREATE TABLE docente_asignatura (
    id_docente INT NOT NULL,
    id_asignatura INT NOT NULL,

    PRIMARY KEY (id_docente, id_asignatura),

    CONSTRAINT fk_docente_asignatura_docente
        FOREIGN KEY (id_docente)
        REFERENCES docente(id_docente)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_docente_asignatura_asignatura
        FOREIGN KEY (id_asignatura)
        REFERENCES asignatura(id_asignatura)
        ON DELETE CASCADE
        ON UPDATE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 6. TABLA HORAS EXTRA
-- ============================================================

CREATE TABLE hora_extra (
    id_hora_extra INT NOT NULL AUTO_INCREMENT,

    id_docente INT NOT NULL,
    fecha DATE NOT NULL,
    horas DECIMAL(3,1) NOT NULL,

    PRIMARY KEY (id_hora_extra),

    CONSTRAINT fk_hora_extra_docente
        FOREIGN KEY (id_docente)
        REFERENCES docente(id_docente)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT chk_hora_extra_horas
        CHECK (horas > 0)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 7. TABLA HORARIO
-- ============================================================

CREATE TABLE horario (
    id_horario INT NOT NULL AUTO_INCREMENT,

    id_curso INT NOT NULL,
    id_asignatura INT NOT NULL,
    id_docente INT NOT NULL,

    dia ENUM(
        'Lunes',
        'Martes',
        'Miercoles',
        'Jueves',
        'Viernes'
    ) NOT NULL,

    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,

    PRIMARY KEY (id_horario),

    CONSTRAINT fk_horario_curso
        FOREIGN KEY (id_curso)
        REFERENCES curso(id_curso)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_horario_asignatura
        FOREIGN KEY (id_asignatura)
        REFERENCES asignatura(id_asignatura)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_horario_docente
        FOREIGN KEY (id_docente)
        REFERENCES docente(id_docente)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT chk_horario_horas
        CHECK (hora_fin > hora_inicio)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- ------------------------------------------------------------
-- ESTUDIANTES
-- ------------------------------------------------------------

INSERT INTO estudiante
(nombre, nota1, nota2, nota3, nota4, promedio, resultado)
VALUES
('Juan Pérez', 4.0, 4.5, 3.8, 4.2, 4.1, 'Aprobado'),
('María Gómez', 3.5, 3.8, 4.0, 3.9, 3.8, 'Aprobado'),
('Carlos Rodríguez', 2.0, 2.5, 2.8, 2.2, 2.4, 'Reprobado');


-- ------------------------------------------------------------
-- CURSOS
-- ------------------------------------------------------------

INSERT INTO curso
(grado, nombre_curso, jornada, num_estudiantes)
VALUES
('10°', 'Décimo A', 'Mañana', 30),
('10°', 'Décimo B', 'Mañana', 28),
('11°', 'Once A', 'Tarde', 25),
('11°', 'Once B', 'Tarde', 27);


-- ------------------------------------------------------------
-- ASIGNATURAS
-- ------------------------------------------------------------

INSERT INTO asignatura
(nombre, intensidad_horaria)
VALUES
('Matemáticas', 5),
('Programación', 4),
('Inglés', 3),
('Física', 4),
('Ciencias Sociales', 3);


-- ------------------------------------------------------------
-- DOCENTES
-- ------------------------------------------------------------

INSERT INTO docente
(nombre, apellido, disponibilidad, horas_maximas_semanales)
VALUES
('Carlos', 'Gómez', 'Lunes a Viernes', 20),
('Ana', 'Martínez', 'Lunes a Viernes', 20),
('Luis', 'Rodríguez', 'Lunes, Miércoles y Viernes', 16),
('Laura', 'Pérez', 'Martes a Jueves', 18);


-- ------------------------------------------------------------
-- DOCENTE - ASIGNATURA
-- ------------------------------------------------------------

INSERT INTO docente_asignatura
(id_docente, id_asignatura)
VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 5),
(3, 4),
(4, 2);


-- ------------------------------------------------------------
-- HORARIOS
-- ------------------------------------------------------------

INSERT INTO horario
(id_curso, id_asignatura, id_docente, dia, hora_inicio, hora_fin)
VALUES
(1, 1, 1, 'Lunes', '07:00:00', '09:00:00'),
(1, 2, 1, 'Martes', '07:00:00', '09:00:00'),
(1, 3, 2, 'Miercoles', '07:00:00', '08:30:00'),
(2, 1, 1, 'Jueves', '07:00:00', '09:00:00'),
(3, 4, 3, 'Lunes', '13:00:00', '15:00:00'),
(4, 2, 4, 'Martes', '13:00:00', '15:00:00');


-- ------------------------------------------------------------
-- HORAS EXTRA
-- ------------------------------------------------------------

INSERT INTO hora_extra
(id_docente, fecha, horas)
VALUES
(1, '2026-09-01', 2.0),
(2, '2026-09-02', 1.5),
(3, '2026-09-03', 2.0);


-- ============================================================
-- COMPROBACIÓN
-- ============================================================

SELECT 'ESTUDIANTES' AS tabla, COUNT(*) AS registros
FROM estudiante

UNION ALL

SELECT 'CURSOS', COUNT(*)
FROM curso

UNION ALL

SELECT 'ASIGNATURAS', COUNT(*)
FROM asignatura

UNION ALL

SELECT 'DOCENTES', COUNT(*)
FROM docente

UNION ALL

SELECT 'DOCENTE_ASIGNATURA', COUNT(*)
FROM docente_asignatura

UNION ALL

SELECT 'HORARIOS', COUNT(*)
FROM horario

UNION ALL

SELECT 'HORAS_EXTRA', COUNT(*)
FROM hora_extra;


-- ============================================================
-- VERIFICAR TABLAS
-- ============================================================

SHOW TABLES;