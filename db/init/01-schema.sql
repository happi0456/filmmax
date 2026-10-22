USE filmmax;

CREATE TABLE IF NOT EXISTS roles (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(72)  NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    role_id       INT          NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO roles (name) VALUES
    ('DIRECTOR'),
    ('LINE_PRODUCER'),
    ('SCRIPTWRITER'),
    ('CASTING_MANAGER'),
    ('STYLIST'),
    ('EQUIPMENT_MANAGER'),
    ('LOCATION_MANAGER');

CREATE TABLE IF NOT EXISTS scenes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    scene_number  INT          NOT NULL UNIQUE,
    title         VARCHAR(150) NOT NULL,
    description   TEXT,
    location_name VARCHAR(150),
    shoot_date    DATE,
    time_slot     VARCHAR(20),
    created_by    BIGINT       NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scenes_user FOREIGN KEY (created_by) REFERENCES users(id)
);

USE filmmax;

CREATE TABLE IF NOT EXISTS actors (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(120) NOT NULL,
    detail VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS equipment (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(120) NOT NULL,
    detail VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS locations (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(120) NOT NULL,
    detail VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS scene_resources (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    scene_id      BIGINT      NOT NULL,
    resource_type VARCHAR(20) NOT NULL,
    resource_id   BIGINT      NOT NULL,
    CONSTRAINT fk_sr_scene FOREIGN KEY (scene_id) REFERENCES scenes(id) ON DELETE CASCADE,
    CONSTRAINT uq_sr UNIQUE (scene_id, resource_type, resource_id)
);

-- Optional demo data
INSERT INTO actors (name, detail) VALUES ('Ananya Rao', 'Lead, 9800000001'), ('Vikram Shah', 'Supporting, 9800000002');
INSERT INTO equipment (name, detail) VALUES ('ARRI Alexa Mini', 'Camera'), ('Aputure 600d', 'Light');
INSERT INTO locations (name, detail) VALUES ('Colaba Causeway', 'Outdoor, permit needed'), ('Film City Stage 2', 'Indoor studio');

USE filmmax;
CREATE TABLE IF NOT EXISTS costumes (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(120) NOT NULL, detail VARCHAR(255));
CREATE TABLE IF NOT EXISTS shots (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(120) NOT NULL, detail VARCHAR(255));