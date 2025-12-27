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

-- 更多商品（丰富首页与各分区）
INSERT INTO products (name, description, price, stock, cover_image_url, category)
VALUES
  -- 数码电器
  ('智能手机 A1', '6.5英寸全面屏，5G，128G存储', 1999.00, 120, 'https://picsum.photos/seed/phone-a1/600/400', '数码电器'),
  ('平板电脑 M10', '10.4英寸高刷屏，学习娱乐两用', 1499.00, 80, 'https://picsum.photos/seed/tablet-m10/600/400', '数码电器'),
  ('笔记本电脑 Pro 14', '14寸轻薄本，16G内存，512G SSD', 5499.00, 35, 'https://picsum.photos/seed/laptop-pro14/600/400', '数码电器'),
  ('蓝牙音箱 Mini', '便携设计，重低音增强', 199.00, 300, 'https://picsum.photos/seed/speaker-mini/600/400', '数码电器'),
  ('运动手表 S3', '全天候健康监测，50米防水', 899.00, 150, 'https://picsum.photos/seed/watch-s3/600/400', '数码电器'),
  ('智能路由器 AX6', 'Wi-Fi 6，全屋覆盖', 399.00, 90, 'https://picsum.photos/seed/router-ax6/600/400', '数码电器'),
  ('闪存盘 128G', 'USB3.1 高速传输', 79.00, 500, 'https://picsum.photos/seed/usb-128/600/400', '数码电器'),
  ('移动电源 20000mAh', '双向快充，大容量', 129.00, 240, 'https://picsum.photos/seed/powerbank-20k/600/400', '数码电器'),
  -- 美妆个护
  ('丝绒口红套装', '热门色号，显白不拔干', 299.00, 60, 'https://picsum.photos/seed/lipstick-set/600/400', '美妆个护'),
  ('水润粉底液', '轻薄服帖，持久不暗沉', 229.00, 110, 'https://picsum.photos/seed/foundation/600/400', '美妆个护'),
  ('氨基酸洁面乳', '温和清洁，不紧绷', 59.00, 260, 'https://picsum.photos/seed/cleanser/600/400', '美妆个护'),
  ('保湿面霜', '长效锁水，换季必备', 169.00, 140, 'https://picsum.photos/seed/cream/600/400', '美妆个护'),
  ('修护精华液', '稳定肌肤，提亮肤色', 399.00, 90, 'https://picsum.photos/seed/serum/600/400', '美妆个护'),
  ('清爽身体乳', '快速吸收，不油腻', 89.00, 180, 'https://picsum.photos/seed/bodylotion/600/400', '美妆个护'),
  ('去屑洗发水', '强韧发根，清爽控油', 69.00, 220, 'https://picsum.photos/seed/shampoo/600/400', '美妆个护'),
  ('花果香水', '甜而不腻，清新持久', 329.00, 75, 'https://picsum.photos/seed/perfume/600/400', '美妆个护'),
  -- 家居生活
  ('香薰加湿器', '静音设计，氛围灯光', 129.00, 160, 'https://picsum.photos/seed/humidifier/600/400', '家居生活'),
  ('收纳箱套装', '可叠加设计，坚固耐用', 99.00, 300, 'https://picsum.photos/seed/storage-box/600/400', '家居生活'),
  ('无叶风扇', '清凉静音，安全防护', 499.00, 70, 'https://picsum.photos/seed/fan/600/400', '家居生活'),
  ('折叠台灯', '三档调光，护眼阅读', 79.00, 240, 'https://picsum.photos/seed/desk-lamp/600/400', '家居生活'),
  ('懒人沙发', '回弹饱满，久坐不累', 399.00, 55, 'https://picsum.photos/seed/sofa/600/400', '家居生活'),
  ('床品四件套', '全棉亲肤，舒适透气', 259.00, 120, 'https://picsum.photos/seed/bedding/600/400', '家居生活'),
  ('餐具套装', '12件套，简约美观', 149.00, 180, 'https://picsum.photos/seed/tableware/600/400', '家居生活'),
  ('纯棉毛巾三件套', '柔软吸水，耐洗不掉毛', 49.00, 260, 'https://picsum.photos/seed/towel/600/400', '家居生活'),
  -- 服饰鞋包
  ('纯棉圆领T恤', '简约百搭，舒适透气', 59.00, 400, 'https://picsum.photos/seed/tshirt/600/400', '服饰鞋包'),
  ('直筒牛仔裤', '显瘦显高，不挑身材', 169.00, 180, 'https://picsum.photos/seed/jeans/600/400', '服饰鞋包'),
  ('轻便跑步鞋', '缓震透气，运动出行', 299.00, 130, 'https://picsum.photos/seed/sneakers/600/400', '服饰鞋包'),
  ('大容量帆布包', '耐磨耐用，通勤休闲', 79.00, 210, 'https://picsum.photos/seed/tote/600/400', '服饰鞋包'),
  ('针织开衫', '柔软亲肤，春秋必备', 199.00, 95, 'https://picsum.photos/seed/cardigan/600/400', '服饰鞋包'),
  ('运动短裤', '清爽速干，夏季必备', 89.00, 220, 'https://picsum.photos/seed/shorts/600/400', '服饰鞋包'),
  ('遮阳渔夫帽', '轻便透气，防晒出行', 49.00, 260, 'https://picsum.photos/seed/hat/600/400', '服饰鞋包'),
  ('城市双肩包', '减压肩带，合理分层', 199.00, 150, 'https://picsum.photos/seed/backpack/600/400', '服饰鞋包'),
  -- 季末特惠（促销专区）
  ('蓝牙降噪耳机 Pro', '季末特惠，限时折扣', 299.00, 80, 'https://picsum.photos/seed/sale-headset/600/400', '季末特惠'),
  ('轻薄笔记本 Air 13', '季末特惠，学生办公优选', 4299.00, 40, 'https://picsum.photos/seed/sale-laptop/600/400', '季末特惠'),
  ('柔雾口红礼盒', '季末特惠，买就送小样', 259.00, 60, 'https://picsum.photos/seed/sale-lipstick/600/400', '季末特惠'),
  ('北欧风床品', '季末特惠，套装更划算', 229.00, 70, 'https://picsum.photos/seed/sale-bedding/600/400', '季末特惠'),
  ('轻跑运动鞋', '季末特惠，轻便透气', 239.00, 120, 'https://picsum.photos/seed/sale-sneakers/600/400', '季末特惠'),
  ('护眼台灯 Pro', '季末特惠，学习办公利器', 129.00, 90, 'https://picsum.photos/seed/sale-desk-lamp/600/400', '季末特惠'),
  ('智能手表 青春版', '季末特惠，性价比之选', 699.00, 110, 'https://picsum.photos/seed/sale-watch/600/400', '季末特惠'),
  ('多功能收纳盒', '季末特惠，整洁有序', 39.00, 300, 'https://picsum.photos/seed/sale-storage/600/400', '季末特惠');
