# B2B 餐饮供应链协作平台

一个基于 Spring Boot + Vue3 的多角色协作系统，支持供应商、采购商和管理员之间的业务协作。

---

## 🚀 项目功能

- 用户认证（JWT）
- 供应商发布商品
- 采购商下单
- 管理员管理用户/商品
- 订单管理与状态流转

---

## 🛠 技术栈

### 后端
- Spring Boot
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Redis

### 前端
- Vue 3
- Axios
- Element Plus（如果你用了）

---

## 📦 项目结构
b2b-project/
├── backend/ # 后端代码
├── frontend/ # 前端代码
├── database/ # 数据库脚本
└── README.md


---

## ⚙️ 环境要求

- JDK 17
- Node.js 16+
- MySQL 8.x
- Redis（可选）

---

## 🧩 项目启动步骤

### 1️⃣ 创建数据库
```sql
CREATE DATABASE b2b DEFAULT CHARACTER SET utf8mb4;

2️⃣ 执行 SQL
db/schema.sql

3️⃣ 配置数据库连接
application-example.yml → application.yml

4️⃣ 启动后端
cd backend
mvn spring-boot:run

5️⃣ 启动前端
cd frontend
npm install
npm run dev

📄 接口文档

启动后访问：
http://localhost:8080/swagger-ui.html

项目包含用户认证,权限控制,JWT校验,日志系统,异常处理等基础功能,适合用来学习和扩展.