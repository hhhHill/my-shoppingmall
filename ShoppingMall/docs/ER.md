ER关系与索引建议（简化电商网站）

主要实体与关系
- User(用户)
  - 字段：id, username, password_hash, email, role, enabled, created_at
  - 关系：
    - 1:N -> CartItem（一个用户可有多条购物车记录）
    - 1:N -> Order（一个用户可有多个订单）
    - 1:N -> BrowseLog（可为空，未登录也可能产生浏览日志）
    - 1:N -> PurchaseLog

- Product(商品)
  - 字段：id, name, description, price, stock, cover_image_url, category, created_at, updated_at
  - 关系：
    - 1:N -> CartItem
    - 1:N -> OrderItem
    - 1:N -> BrowseLog
    - 0..N -> PurchaseLog（可选）

- CartItem(购物车项)
  - 字段：id, user_id, product_id, quantity
  - 关系：N:1 -> User；N:1 -> Product
  - 约束：user_id+product_id 唯一

- Order(订单)
  - 字段：id, user_id, total_amount, status(CREATED/PAID/SHIPPED/COMPLETED/CANCELLED), created_at, updated_at
  - 关系：N:1 -> User，1:N -> OrderItem

- OrderItem(订单明细)
  - 字段：id, order_id, product_id, price_snapshot, quantity
  - 关系：N:1 -> Order；N:1 -> Product

- BrowseLog(浏览日志)
  - 字段：id, user_id(NULLABLE), product_id, action=VIEW, created_at
  - 关系：N:1 -> Product；N:0..1 -> User

- PurchaseLog(购买日志)
  - 字段：id, user_id, order_id, product_id(可选), action=PURCHASE, created_at
  - 关系：N:1 -> User；N:1 -> Order；N:0..1 -> Product

索引建议
- users
  - UNIQUE(username)

- products
  - 前缀索引 idx_products_name_prefix(name(100))，支持前缀匹配查询
  - FULLTEXT(name, description) 用于全文搜索（MySQL 8 InnoDB 支持）。中文场景可按需调整分词或使用前缀匹配。

- cart_items
  - UNIQUE(user_id, product_id) 保证同一用户同一商品仅一条记录
  - INDEX(user_id)

- orders
  - INDEX(user_id) 用于用户订单查询
  - INDEX(status) 用于按状态过滤

- order_items
  - INDEX(order_id)

- browse_logs
  - INDEX(user_id), INDEX(product_id)

- purchase_logs
  - INDEX(user_id), INDEX(order_id)

