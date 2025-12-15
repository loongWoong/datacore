# 数据源管理服务 (Datasource Service)

## 项目概述

数据源管理服务是数据中台的重要组成部分，负责管理系统中各种数据源的连接信息、配置参数和状态监控。该服务提供统一的数据源管理接口，支持多种数据库类型的连接配置和连接测试功能。

## 技术栈

- **框架**: Spring Boot 3.x + MyBatis Plus
- **数据库**: MySQL 8.x
- **缓存**: Redis 7.x
- **接口文档**: SpringDoc Swagger UI
- **构建工具**: Maven

## 项目结构

```
datasource-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dataplatform/datasource/
│   │   │       ├── DatasourceServiceApplication.java    # 启动类
│   │   │       ├── config/                             # 配置类
│   │   │       ├── controller/                         # 控制器层
│   │   │       ├── service/                            # 服务层
│   │   │       ├── mapper/                             # 数据访问层
│   │   │       ├── entity/                             # 实体类
│   │   │       └── dto/                                # 数据传输对象
│   │   └── resources/
│   │       ├── application.yml                         # 应用配置
│   │       └── mapper/                                # MyBatis映射文件
│   └── test/                                           # 测试代码
└── pom.xml                                             # Maven配置文件
```

## 功能模块

1. **数据源管理**: 数据源的增删改查操作
2. **连接测试**: 支持测试数据源连接是否正常
3. **状态监控**: 数据源状态管理和监控
4. **配置管理**: 数据源连接参数配置

## 数据库设计

### 主要数据表

1. **数据源信息表** (datasource_info)

## API接口

### 数据源管理接口

- `GET /api/datasources` - 获取数据源列表
- `GET /api/datasources/page` - 分页获取数据源列表
- `GET /api/datasources/{id}` - 获取数据源详情
- `POST /api/datasources` - 创建数据源
- `PUT /api/datasources/{id}` - 更新数据源
- `DELETE /api/datasources/{id}` - 删除数据源
- `POST /api/datasources/{id}/test-connection` - 测试数据源连接

## 如何运行

1. 确保已安装 JDK 17 和 Maven
2. 创建 MySQL 数据库 `datacore`
3. 启动 Redis 服务
4. 修改 `application.yml` 中的数据库和Redis配置
5. 在项目根目录执行以下命令启动服务：
   ```bash
   mvn spring-boot:run
   ```
6. 服务启动后，可通过 `http://localhost:8086` 访问

## 接口文档

项目启动后，可以通过以下地址访问Swagger接口文档：
```
http://localhost:8086/swagger-ui.html
```

## 支持的数据源类型

- MySQL
- PostgreSQL
- Oracle
- 其他JDBC兼容数据库

## 安全设计

- JWT Token认证
- RBAC权限控制
- 敏感信息加密存储
- 操作日志记录