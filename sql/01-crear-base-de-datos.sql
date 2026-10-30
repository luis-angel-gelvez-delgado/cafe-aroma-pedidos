-- Ejecutar UNA vez como usuario root (MySQL Workbench o MySQL Command Line Client).
-- Reemplaza <TU_CONTRASEÑA> por una contraseña que elijas. NO la subas a Git.

CREATE DATABASE IF NOT EXISTS cafe_aroma
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usuario propio de la aplicación (no usamos root): solo tiene permisos sobre cafe_aroma.
CREATE USER IF NOT EXISTS 'cafe_app'@'localhost' IDENTIFIED BY '<TU_CONTRASEÑA>';
GRANT ALL PRIVILEGES ON cafe_aroma.* TO 'cafe_app'@'localhost';
FLUSH PRIVILEGES;

-- Las tablas y el menú los crea la aplicación al arrancar (schema.sql y data.sql).
