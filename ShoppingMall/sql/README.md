SQL 使用说明

- 一键启动 MySQL 8（端口 3306）：
  cd sql
  docker compose up -d

- 默认数据库：shopping_mall，root/rootpassword

- 如果不使用 Docker，请手动创建数据库：
  CREATE DATABASE shopping_mall CHARACTER SET utf8mb4;

- 本项目 JPA 默认在 dev 环境下使用 `spring.jpa.hibernate.ddl-auto=update` 自动建表。
- 如需手动建表，可参考实体定义或自行编写 schema.sql。

