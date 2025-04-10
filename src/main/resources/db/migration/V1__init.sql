CREATE TABLE usuario (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE cliente (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    usuario_id BIGINT UNIQUE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE profesional (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100),
    usuario_id BIGINT UNIQUE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE disponibilidad (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    dia VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    profesional_id BIGINT,
    FOREIGN KEY (profesional_id) REFERENCES profesional(id)
);

CREATE TABLE turno (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    cliente_id BIGINT,
    disponibilidad_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    CONSTRAINT fk_turno_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_turno_disponibilidad FOREIGN KEY (disponibilidad_id) REFERENCES disponibilidad(id),
    CONSTRAINT unique_turno UNIQUE (disponibilidad_id, fecha, hora)
);


