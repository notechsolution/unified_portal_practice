# Unified Portal

一个前后端一体的演示项目，展示了现代Web应用的完整开发流程。

## 项目简介

这是一个完整的全栈项目，前端使用Vue.js 3.x生态，后端使用Spring Boot 3.x框架，数据库采用MongoDB。

## 技术栈

### 前端 (front目录)
- **Vue.js 3.x** - 渐进式JavaScript框架
- **Element Plus** - 基于Vue 3的组件库
- **Pinia** - Vue 3的状态管理库
- **Vite** - 新一代前端构建工具
- **Vue Router** - 官方路由管理器
- **Axios** - HTTP客户端

### 后端 (backend目录)
- **Spring Boot 3.x** - Java企业级应用框架
- **Spring Security** - 安全认证框架
- **MongoDB** - NoSQL文档数据库
- **JWT** - JSON Web Token认证
- **Lombok** - Java代码简化工具
- **Maven** - 项目管理工具

## 项目结构

```
unified_portal/
├── front/                          # 前端项目
│   ├── src/
│   │   ├── api/                   # API接口
│   │   ├── components/            # 可复用组件
│   │   ├── router/                # 路由配置
│   │   ├── stores/                # Pinia状态管理
│   │   ├── utils/                 # 工具函数
│   │   ├── views/                 # 页面组件
│   │   ├── App.vue               # 根组件
│   │   └── main.js               # 入口文件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js            # Vite配置
│
├── backend/                       # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/backend/
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── controller/   # 控制器层
│   │   │   │   ├── dto/          # 数据传输对象
│   │   │   │   ├── model/        # 数据模型
│   │   │   │   ├── repository/   # 数据访问层
│   │   │   │   ├── security/     # 安全相关
│   │   │   │   ├── service/      # 业务逻辑层
│   │   │   │   └── UnifiedPortalApplication.java
│   │   │   └── resources/
│   │   │       ├── static/       # 前端打包文件(构建后生成)
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml                   # Maven配置
│
└── README.md
```

## 功能特性

- ✅ 用户认证与授权 (JWT)
- ✅ 前后端分离开发
- ✅ RESTful API设计
- ✅ 响应式UI界面
- ✅ 角色权限管理
- ✅ 前端路由守卫
- ✅ HTTP请求拦截
- ✅ 统一错误处理

## 开发环境要求

- **Node.js**: 16.0+
- **Java**: 17+
- **Maven**: 3.6+
- **MongoDB**: 4.4+

## 快速开始

### 1. 启动MongoDB

确保MongoDB服务正在运行：

```bash
# Windows
net start MongoDB

# Linux/Mac
sudo systemctl start mongod
```

### 2. 后端开发

```bash
# 进入后端目录
cd backend

# 编译项目
mvn clean install

# 运行Spring Boot应用
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 3. 前端开发

```bash
# 进入前端目录
cd front

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端开发服务器将在 `http://localhost:5173` 启动

## 生产部署

### 方式一：前后端一体部署

```bash
# 1. 构建前端
cd front
npm install
npm run build
# 前端文件会自动打包到 backend/src/main/resources/static

# 2. 构建并运行后端
cd ../backend
mvn clean package
java -jar target/unified-portal-backend-1.0.0.jar
```

访问 `http://localhost:8080` 即可使用完整应用

### 方式二：分离部署

前端使用Nginx等Web服务器部署，后端独立部署，需要配置CORS和反向代理。

## API接口

### 认证接口

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/current` - 获取当前用户
- `POST /api/auth/logout` - 退出登录

### 用户接口

- `GET /api/user/list` - 获取用户列表
- `GET /api/user/{id}` - 获取用户详情
- `PUT /api/user/{id}` - 更新用户信息
- `DELETE /api/user/{id}` - 删除用户

## 配置说明

### 后端配置 (application.yml)

```yaml
spring:
  data:
    mongodb:
      host: localhost        # MongoDB地址
      port: 27017           # MongoDB端口
      database: unified_portal  # 数据库名

jwt:
  secret: your-secret-key   # JWT密钥(生产环境请修改)
  expiration: 86400000      # Token过期时间(毫秒)
```

### 前端配置 (vite.config.js)

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端API地址
      changeOrigin: true
    }
  }
},
build: {
  outDir: '../backend/src/main/resources/static'  // 打包输出目录
}
```

## 开发说明

### 前端开发

1. **添加新页面**：在 `front/src/views/` 创建Vue组件
2. **配置路由**：在 `front/src/router/index.js` 添加路由配置
3. **API调用**：在 `front/src/api/` 创建API方法
4. **状态管理**：在 `front/src/stores/` 创建Pinia store

### 后端开发

1. **数据模型**：在 `model/` 包创建实体类
2. **数据访问**：在 `repository/` 包创建Repository接口
3. **业务逻辑**：在 `service/` 包创建Service类
4. **接口暴露**：在 `controller/` 包创建Controller类

## 常见问题

### 1. 前端无法访问后端API

检查后端CORS配置和前端代理配置是否正确。

### 2. JWT Token验证失败

确保前端请求头正确携带 `Authorization: Bearer <token>`。

### 3. MongoDB连接失败

检查MongoDB服务是否启动，配置的地址和端口是否正确。

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request！

## 联系方式

如有问题，请通过GitHub Issues联系。
