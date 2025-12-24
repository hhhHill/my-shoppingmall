简化电商网站（Java Spring Boot + Vue3）

学号：__________   姓名：__________

项目简介：
- 后端：Java 17 + Spring Boot 3.x + Maven + Spring Security + JWT + Spring Validation + Spring Data JPA + Swagger/OpenAPI
- 前端：Vue 3 + Vite + Vue Router + Pinia + Axios + Element Plus
- 数据库：MySQL 8
- 角色：用户(User)、管理员(Admin)（管理员通过 users.role 字段区分）
- 功能范围：用户注册/登录/注销、商品列表/详情/搜索、购物车、下单/订单查询；管理员商品CRUD、订单管理、销售统计、用户管理、浏览/购买日志

目录结构：
- backend/  后端 Spring Boot
- frontend/ 前端 Vue3 + Vite
- sql/      数据库 SQL 脚本
- docs/     文档（接口说明等）

快速开始
1) 准备环境
- Java 17、Maven 3.8+
- Node.js 18+、npm 9+
- MySQL 8（建议使用 sql/docker-compose.yml 一键启动）

2) 启动数据库（可选，推荐）
- 进入 sql/ 目录，运行：
  docker compose up -d
- 或手动创建数据库：
  - 创建数据库：shopping_mall
  - 用户/密码（示例）：root / rootpassword

3) 配置后端
- 复制 backend/src/main/resources/application-dev.yml，根据本机 MySQL 修改 url、username、password。
- 或直接使用 sql/docker-compose.yml 对应的默认配置。

4) 启动后端
- 在 backend/ 目录执行：
  mvn spring-boot:run -Dspring-boot.run.profiles=dev
- 接口文档（启动后）：http://localhost:8080/swagger-ui/index.html

5) 启动前端
- 在 frontend/ 目录执行：
  npm install
  cp .env.example .env.local  # Windows手动复制
  # 修改 .env.local 中的 VITE_API_BASE（默认 http://localhost:8080）
  npm run dev
- 访问：http://localhost:5173

6) 验收标准
- 访问前端首页能看到商品列表页面（无数据时显示为空状态）。

接口说明（简要）
- 用户认证
  - POST /api/auth/register  注册
  - POST /api/auth/login     登录（返回JWT）
  - GET  /api/auth/me        当前用户
- 商品
  - GET  /api/products       商品列表（支持 q 关键字）
  - GET  /api/products/{id}  商品详情
- 购物车（需登录）
  - GET  /api/cart
  - POST /api/cart           添加
  - PUT  /api/cart/{id}      修改数量
  - DELETE /api/cart/{id}    删除
- 订单（需登录）
  - GET  /api/orders
  - POST /api/orders         下单
- 管理员（需 ADMIN 角色）
  - /api/admin/products  商品CRUD
  - /api/admin/orders    订单管理
  - /api/admin/users     用户管理
  - /api/admin/stats     销售统计

部署说明（概述）
- 后端：设置 application-prod.yml，打包 mvn clean package，然后以 JVM 参数 --spring.profiles.active=prod 运行。
- 前端：npm run build 生成 dist/，部署到 Nginx/静态服务器，并将 API 反向代理到后端。

更多细节见 docs/ 目录与代码注释。

