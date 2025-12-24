接口说明（概要）

基础说明
- 基础路径：后端默认 http://localhost:8080
- 认证方式：HTTP Header `Authorization: Bearer <JWT>`
- 所有响应采用统一结构：
  {
    "success": true/false,
    "message": "...",
    "data": {}
  }

认证
- POST /api/auth/register
  - 请求：{ username, password, email }
  - 响应：{ success, message }

- POST /api/auth/login
  - 请求：{ username, password }
  - 响应：{ success, data: { token, user: { id, username, role } } }

- GET /api/auth/me  (需登录)
  - 响应：{ success, data: { id, username, role } }

商品
- GET /api/products
  - 参数：q（可选，关键词）
  - 响应：{ success, data: [ { id, name, price, stock, imageUrl, ... } ] }

- GET /api/products/{id}
  - 响应：{ success, data: { ... } }

购物车（需登录）
- GET /api/cart
- POST /api/cart           { productId, quantity }
- PUT  /api/cart/{id}      { quantity }
- DELETE /api/cart/{id}

订单（需登录）
- GET /api/orders
- POST /api/orders         { /* 从购物车下单，简化模型 */ }

管理员（需 ADMIN 角色）
- /api/admin/products  GET/POST/PUT/DELETE
- /api/admin/orders    GET/PUT
- /api/admin/users     GET/PUT
- /api/admin/stats     GET

