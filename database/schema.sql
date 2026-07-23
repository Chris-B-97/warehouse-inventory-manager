CREATE DATABASE IF NOT EXISTS warehouse_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE warehouse_manager;

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    aisle CHAR(1) NOT NULL,
    side TINYINT UNSIGNED NOT NULL,
    position SMALLINT UNSIGNED NOT NULL,
    x_coordinate INT NOT NULL DEFAULT 0,
    y_coordinate INT NOT NULL DEFAULT 0,
    max_capacity INT UNSIGNED NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_location_aisle CHECK (aisle IN ('A','B','C','D','E')),
    CONSTRAINT chk_location_side CHECK (side IN (1,2)),
    CONSTRAINT chk_location_position CHECK (position > 0),
    CONSTRAINT chk_location_capacity CHECK (max_capacity > 0),
    UNIQUE KEY uq_location_coordinates (aisle, side, position),
    INDEX idx_location_code (code),
    INDEX idx_location_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(100),
    quantity INT UNSIGNED NOT NULL DEFAULT 0,
    minimum_stock INT UNSIGNED NOT NULL DEFAULT 0,
    location_id BIGINT UNSIGNED NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_location FOREIGN KEY (location_id) REFERENCES locations(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_product_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_product_minimum CHECK (minimum_stock >= 0),
    INDEX idx_product_name (name),
    INDEX idx_product_category (category),
    INDEX idx_product_location (location_id),
    INDEX idx_product_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNSIGNED NOT NULL,
    movement_type ENUM('IN','OUT','ADJUSTMENT','TRANSFER') NOT NULL,
    quantity INT UNSIGNED NOT NULL,
    previous_quantity INT UNSIGNED NOT NULL,
    new_quantity INT UNSIGNED NOT NULL,
    location_id BIGINT UNSIGNED NULL,
    reason VARCHAR(255) NOT NULL,
    reference VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movement_location FOREIGN KEY (location_id) REFERENCES locations(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_movement_quantity CHECK (quantity >= 0),
    INDEX idx_movement_product_date (product_id, created_at),
    INDEX idx_movement_type_date (movement_type, created_at),
    INDEX idx_movement_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
