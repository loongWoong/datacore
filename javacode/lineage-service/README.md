# 数据溯源服务

## 项目概述

数据溯源服务是数据中台的重要组成部分，负责管理和展示数据在各个处理环节之间的血缘关系。该服务通过解析dbt模型和SQL语句，自动构建数据血缘图谱，并提供可视化的展示功能。

## 技术栈

- **框架**：Spring Boot 3.x
- **图数据库**：Neo4j 5.x
- **关系数据库**：MySQL 8.x
- **缓存**：Redis 7.x
- **SQL解析**：JSqlParser
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

## 项目结构

```
lineage-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dataplatform/lineage/
│   │   │       ├── LineageServiceApplication.java     # 启动类
│   │   │       ├── config/                          # 配置类
│   │   │       ├── controller/                      # 控制器层
│   │   │       ├── service/                         # 服务层
│   │   │       ├── repository/                      # 数据访问层
│   │   │       ├── entity/                          # 实体类
│   │   │       └── dto/                             # 数据传输对象
│   │   └── resources/
│   │       ├── application.yml                      # 应用配置
│   │       └── mapper/                              # MyBatis映射文件
│   └── test/                                        # 测试代码
└── pom.xml                                          # Maven配置文件
```

## 功能模块

1. **血缘关系构建**：支持dbt模型和SQL语句的血缘关系自动构建
2. **血缘关系查询**：支持上下游血缘关系查询和可视化展示
3. **图谱展示**：提供血缘图谱的可视化展示功能

## 接口文档

项目启动后，可以通过以下地址访问Swagger接口文档：
```
http://localhost:8081/swagger-ui.html
```

## 如何运行

1. 确保已安装 JDK 17 和 Maven
2. 启动 Neo4j、MySQL 和 Redis 服务
3. 创建 MySQL 数据库 `lineage_db`
4. 修改 `application.yml` 中的数据库和Redis配置
5. 在项目根目录执行以下命令启动服务：
   ```bash
   mvn spring-boot:run
   ```
6. 服务启动后，可通过 `http://localhost:8081` 访问

## 主要接口

### 血缘查询接口
- `GET /api/lineage/graph` - 获取血缘关系图
- `GET /api/lineage/upstream/{tableName}` - 获取上游血缘关系
- `GET /api/lineage/downstream/{tableName}` - 获取下游血缘关系

### 血缘构建接口
- `POST /api/lineage/build/dbt` - 触发dbt血缘构建
- `POST /api/lineage/build/relationship` - 手动添加血缘关系