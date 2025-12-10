# 元数据服务

## 项目概述

元数据服务是数据中台的核心基础服务之一，负责管理所有数据资产的元数据信息，包括表结构信息、字段信息、业务属性等。

## 技术栈

- **框架**：Spring Boot 3.x + MyBatis Plus
- **数据库**：MySQL 8.x
- **缓存**：Redis 7.x
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

## 项目结构

```
metadata-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dataplatform/metadata/
│   │   │       ├── MetadataServiceApplication.java    # 启动类
│   │   │       ├── config/                           # 配置类
│   │   │       ├── controller/                       # 控制器层
│   │   │       ├── service/                          # 服务层
│   │   │       ├── mapper/                           # 数据访问层
│   │   │       ├── entity/                           # 实体类
│   │   │       └── dto/                              # 数据传输对象
│   │   └── resources/
│   │       ├── application.yml                       # 应用配置
│   │       └── mapper/                               # MyBatis映射文件
│   └── test/                                         # 测试代码
└── pom.xml                                           # Maven配置文件
```

## 功能模块

1. **表管理**：表元数据的增删改查
2. **字段管理**：字段元数据的增删改查
3. **版本管理**：元数据版本控制

## 接口文档

项目启动后，可以通过以下地址访问Swagger接口文档：
```
http://localhost:8080/swagger-ui.html
```

## 如何运行

1. 确保已安装 JDK 17 和 Maven
2. 创建 MySQL 数据库 `metadata_db`
3. 启动 Redis 服务
4. 修改 `application.yml` 中的数据库和Redis配置
5. 在项目根目录执行以下命令启动服务：
   ```bash
   mvn spring-boot:run
   ```
6. 服务启动后，可通过 `http://localhost:8080` 访问

## 主要接口

### 表管理接口
- `GET /api/metadata/tables` - 获取表列表
- `GET /api/metadata/tables/{id}` - 获取表详情
- `POST /api/metadata/tables` - 创建表元数据

### 字段管理接口
- `GET /api/metadata/columns/table/{tableId}` - 获取表的字段列表
- `POST /api/metadata/columns` - 创建字段元数据

### 版本管理接口
- `GET /api/metadata/versions/table/{tableId}` - 获取表的版本列表
- `POST /api/metadata/versions` - 创建版本元数据