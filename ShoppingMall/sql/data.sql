USE shopping_mall;

-- 管理员账户（密码为BCrypt哈希示例，登录非必需）
INSERT INTO users (username, password_hash, email, role, enabled)
VALUES
  ('admin', '$2a$10$7s8k7QW1e6m8u0c9vX3zIOi0KxR4YgH2Dk7cIhQvXbKXzS0Vb9y6K', 'admin@example.com', 'ADMIN', 1);

-- 商品示例
INSERT INTO products (name, description, price, stock, cover_image_url, category)
VALUES
  ('机械键盘', '87键机械键盘，红轴，RGB背光', 299.00, 50, 'https://picsum.photos/seed/kb/600/400', '外设'),
  ('无线鼠标', '静音按键，蓝牙/2.4G双模', 89.00, 200, 'https://picsum.photos/seed/mouse/600/400', '外设'),
  ('27英寸显示器', '2K分辨率，IPS面板，75Hz', 899.00, 30, 'https://picsum.photos/seed/monitor/600/400', '显示器'),
  ('USB-C 集线器', '7合1，支持4K HDMI，PD充电', 159.00, 100, 'https://picsum.photos/seed/hub/600/400', '配件'),
  ('降噪耳机', '主动降噪，Type-C充电', 399.00, 80, 'https://picsum.photos/seed/headset/600/400', '音频');

