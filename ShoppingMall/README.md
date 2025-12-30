购物商城（Java Spring Boot + Vue3）

网站地址：https://my-tunnel.hill13.online/

作者信息：黄俊超（学号：202330450601）

测试账号：
- 普通用户：用户名 123，密码 123456
- 管理者：用户名 admin，密码 admin

项目简介：
- 简化电商网站，包含用户与管理员角色；支持注册/登录、商品浏览与搜索、购物车、下单与订单查询；后台商品与订单管理、用户管理与统计。
- 后端：Java 17、Spring Boot 3、Spring Security、JWT、Spring Data JPA、Maven
- 前端：Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus
- 数据库：MySQL 8

快速部署：
- 数据库：导入 `sql/schema.sql` 与 `sql/data.sql`
- 后端：在 `backend` 运行开发 `mvn spring-boot:run`；生产 `mvn clean package -DskipTests` 后执行 `java -jar target/*.jar --spring.profiles.active=prod`
- 前端：在 `frontend` 运行开发 `npm install && npm run dev`；生产 `npm install && npm run build` 后部署 `dist` 并将 `/api` 反向代理到后端
- 接口文档（开发）：`http://localhost:8080/swagger-ui/index.html`
- API 基址：生产 `.env.production` 默认为相对路径 `/api`；开发 `.env.development` 默认为 `http://127.0.0.1:8080`
