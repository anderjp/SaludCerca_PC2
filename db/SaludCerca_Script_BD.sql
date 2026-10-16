-- =========================================================================
-- SaludCerca - Script de Base de Datos (DDL)
-- Motor: MySQL 8.0
-- Curso: Diseno de Patrones - UTP
-- =========================================================================

CREATE DATABASE IF NOT EXISTS saludcerca
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish2_ci;

USE saludcerca;

-- -------------------------------------------------------------------------
-- Tabla: especialidad
-- -------------------------------------------------------------------------
CREATE TABLE especialidad (
    id_especialidad      INT AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(80) NOT NULL UNIQUE,
    permite_teleconsulta BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Tabla: centro_salud
-- -------------------------------------------------------------------------
CREATE TABLE centro_salud (
    id_centro_salud INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(120) NOT NULL,
    distrito        VARCHAR(80)  NOT NULL,
    telefono        VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Tabla: paciente
-- -------------------------------------------------------------------------
CREATE TABLE paciente (
    id_paciente       INT AUTO_INCREMENT PRIMARY KEY,
    dni               VARCHAR(8)  NOT NULL UNIQUE,
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE NOT NULL,
    email             VARCHAR(120) NOT NULL UNIQUE,
    telefono          VARCHAR(20),
    fecha_registro    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Tabla: medico
-- -------------------------------------------------------------------------
CREATE TABLE medico (
    id_medico         INT AUTO_INCREMENT PRIMARY KEY,
    colegiatura       VARCHAR(20) NOT NULL UNIQUE,
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    email             VARCHAR(120) NOT NULL UNIQUE,
    telefono          VARCHAR(20),
    id_especialidad   INT NOT NULL,
    id_centro_salud   INT NOT NULL,
    estado_verificacion ENUM('Pendiente','Aprobado','Rechazado') NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT fk_medico_especialidad
        FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_medico_centro_salud
        FOREIGN KEY (id_centro_salud) REFERENCES centro_salud(id_centro_salud)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Tabla: cita
-- -------------------------------------------------------------------------
CREATE TABLE cita (
    id_cita         INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente     INT NOT NULL,
    id_medico       INT NOT NULL,
    fecha_hora      DATETIME NOT NULL,
    tipo_atencion   ENUM('Presencial','Teleconsulta') NOT NULL,
    enlace_videollamada VARCHAR(255) NULL COMMENT 'Enlace externo (Zoom/Google Meet) generado solo si tipo_atencion = Teleconsulta',
    estado          ENUM('Pendiente','Confirmada','Cancelada','Completada') NOT NULL DEFAULT 'Pendiente',
    creado_en       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cita_paciente
        FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_cita_medico
        FOREIGN KEY (id_medico) REFERENCES medico(id_medico)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_cita_fecha (fecha_hora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- NOTA: MySQL no permite un CHECK que consulte otra tabla, por lo que la
-- regla "solo se puede agendar Teleconsulta si especialidad.permite_teleconsulta = TRUE"
-- se valida en la capa de aplicacion (CitaService), no en la base de datos.

-- -------------------------------------------------------------------------
-- Tabla: historial_clinico
-- -------------------------------------------------------------------------
CREATE TABLE historial_clinico (
    id_historial    INT AUTO_INCREMENT PRIMARY KEY,
    id_cita         INT NOT NULL UNIQUE,
    diagnostico     TEXT NOT NULL,
    receta          TEXT,
    observaciones   TEXT,
    fecha_registro  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historial_cita
        FOREIGN KEY (id_cita) REFERENCES cita(id_cita)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Tabla: pago  (modulo simulado, sin pasarela real)
-- -------------------------------------------------------------------------
CREATE TABLE pago (
    id_pago         INT AUTO_INCREMENT PRIMARY KEY,
    id_cita         INT NOT NULL UNIQUE,
    monto           DECIMAL(8,2) NOT NULL,
    metodo_pago     VARCHAR(40) NOT NULL DEFAULT 'Simulado',
    estado_pago     ENUM('Pendiente','Pagado','Rechazado') NOT NULL DEFAULT 'Pendiente',
    fecha_pago      DATETIME,
    CONSTRAINT fk_pago_cita
        FOREIGN KEY (id_cita) REFERENCES cita(id_cita)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_pago_monto CHECK (monto >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------------------------------
-- Tabla: notificacion
-- -------------------------------------------------------------------------
CREATE TABLE notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente     INT NOT NULL,
    tipo            VARCHAR(40) NOT NULL,
    mensaje         VARCHAR(255) NOT NULL,
    leido           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_envio     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_paciente
        FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================================
-- Datos de ejemplo (opcional, para pruebas del sistema)
-- =========================================================================
INSERT INTO especialidad (nombre, permite_teleconsulta) VALUES
  ('Nutricion Clinica', TRUE),
  ('Psicologia', TRUE),
  ('Odontologia', FALSE),
  ('Medicina General', TRUE);

INSERT INTO centro_salud (nombre, distrito, telefono) VALUES
  ('Clinica San Felipe', 'San Isidro', '014712000'),
  ('Centro Medico Los Olivos', 'Los Olivos', '014870000');

INSERT INTO paciente (dni, nombres, apellidos, fecha_nacimiento, email, telefono) VALUES
  ('45231890', 'Karla', 'Effio Vargas', '1996-03-14', 'karla.effio@mail.com', '987654321');

INSERT INTO medico (colegiatura, nombres, apellidos, email, telefono, id_especialidad, id_centro_salud, estado_verificacion) VALUES
  ('CMP48213', 'Milagros', 'Castañeda Ruiz', 'mcastaneda@saludcerca.pe', '999888777', 1, 1, 'Aprobado');
