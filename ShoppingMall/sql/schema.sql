-- 数据库与表结构（MySQL 8，InnoDB，utf8mb4）
CREATE DATABASE IF NOT EXISTS shopping_mall
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopping_mall;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(50) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  email         VARCHAR(120),
  role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
  enabled       TINYINT(1) NOT NULL DEFAULT 1,
  created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 商品表
CREATE TABLE IF NOT EXISTS products (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  name             VARCHAR(255) NOT NULL,
  description      TEXT,
  price            DECIMAL(12,2) NOT NULL DEFAULT 0,
  stock            INT NOT NULL DEFAULT 0,
  cover_image_url  VARCHAR(512),
  category         VARCHAR(100),
  created_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  INDEX idx_products_name_prefix (name(100)),
  FULLTEXT KEY ft_products_name_desc (name, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 购物车表（用户维度）
CREATE TABLE IF NOT EXISTS cart_items (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity   INT NOT NULL DEFAULT 1,
  CONSTRAINT uk_cart_user_product UNIQUE (user_id, product_id),
  CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
  CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  INDEX idx_cart_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id      BIGINT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  status       ENUM('CREATED','PAID','SHIPPED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'CREATED',
  created_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_orders_user (user_id),
  INDEX idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 订单明细
CREATE TABLE IF NOT EXISTS order_items (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id       BIGINT NOT NULL,
  product_id     BIGINT NOT NULL,
  price_snapshot DECIMAL(12,2) NOT NULL,
  quantity       INT NOT NULL,
  CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
  INDEX idx_order_items_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 浏览日志
CREATE TABLE IF NOT EXISTS browse_logs (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NULL,
  product_id  BIGINT NOT NULL,
  action      VARCHAR(32) NOT NULL DEFAULT 'VIEW',
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_browse_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE SET NULL,
  CONSTRAINT fk_browse_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  INDEX idx_browse_user (user_id),
  INDEX idx_browse_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 购买日志
CREATE TABLE IF NOT EXISTS purchase_logs (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  order_id    BIGINT NOT NULL,
  product_id  BIGINT NULL,
  action      VARCHAR(32) NOT NULL DEFAULT 'PURCHASE',
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_purchase_user   FOREIGN KEY (user_id)  REFERENCES users(id),
  CONSTRAINT fk_purchase_order  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_purchase_product FOREIGN KEY (product_id) REFERENCES products(id),
  INDEX idx_purchase_user (user_id),
  INDEX idx_purchase_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

