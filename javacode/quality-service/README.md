# 数据质量服务

## 项目概述

数据质量服务是数据中台的重要组成部分，负责定义、执行和监控数据质量规则，确保数据的完整性、准确性、一致性和可靠性。该服务集成了Quartz调度框架用于定时执行质量检查任务，提供全面的数据质量管理功能。

## 技术栈

- **框架**：Spring Boot 3.x
- **调度框架**：Quartz 2.3.x
- **数据库**：MySQL 8.x
- **缓存**：Redis 7.x
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

## 项目结构

```
quality-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dataplatform/quality/
│   │   │       ├── QualityServiceApplication.java     # 启动类
│   │   │       ├── config/                          # 配置类
│   │   │       ├── controller/                      # 控制器层
│   │   │       ├── service/                         # 服务层
│   │   │       ├── mapper/                          # 数据访问层
│   │   │       ├── entity/                          # 实体类
│   │   │       └── dto/                            # 数据传输对象
│   │   └── resources/
│   │       ├── application.yml                      # 应用配置
│   │       └── mapper/                              # MyBatis映射文件
│   └── test/                                        # 测试代码
└── pom.xml                                          # Maven配置文件
```

## 功能模块

1. **质量规则管理**：支持质量规则的增删改查
2. **检查任务管理**：支持定时检查任务的创建和调度
3. **质量报告生成**：提供质量报告查询和趋势分析功能

## 接口文档

项目启动后，可以通过以下地址访问Swagger接口文档：
```
http://localhost:8082/swagger-ui.html
```

## 如何运行

1. 确保已安装 JDK 17 和 Maven
2. 启动 MySQL 和 Redis 服务
3. 创建 MySQL 数据库 `quality_db`
4. 修改 `application.yml` 中的数据库和Redis配置
5. 在项目根目录执行以下命令启动服务：
   ```bash
   mvn spring-boot:run
   ```
6. 服务启动后，可通过 `http://localhost:8082` 访问

## 主要接口

### 质量规则接口
- `POST /api/quality/rules` - 创建质量规则
- `GET /api/quality/rules` - 获取规则列表
- `GET /api/quality/rules/{id}` - 获取规则详情
- `PUT /api/quality/rules/{id}` - 更新规则
- `DELETE /api/quality/rules/{id}` - 删除规则

### 检查任务接口
- `POST /api/quality/jobs` - 创建检查任务
- `GET /api/quality/jobs` - 获取任务列表
- `POST /api/quality/jobs/{id}/trigger` - 触发任务执行
- `POST /api/quality/jobs/{id}/toggle` - 启用/禁用任务

### 质量报告接口
- `GET /api/quality/reports` - 获取质量报告列表
- `GET /api/quality/reports/trend/{tableName}` - 获取表的质量趋势