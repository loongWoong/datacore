# 调度服务 (Scheduler Service)

## 项目概述

调度服务是数据中台的重要基础设施服务，负责管理和执行各种定时任务和调度作业。该服务基于Spring Boot框架构建，集成了XXL-JOB分布式调度框架，提供高可用、高可靠的分布式任务调度能力。

## 技术架构

- **框架**: Spring Boot 3.x + Spring Cloud Alibaba
- **调度框架**: XXL-JOB 2.4.x
- **数据库**: MySQL 8.x
- **注册中心**: Nacos
- **配置中心**: Nacos
- **接口文档**: SpringDoc Swagger UI
- **缓存**: Redis
- **ORM框架**: MyBatis Plus
- **分页插件**: PageHelper

## 功能模块

### 1. 任务管理
- 任务创建、修改、删除
- 任务启停控制
- 任务详情查询
- 任务列表分页查询

### 2. 执行器管理
- 执行器注册与发现
- 执行器信息维护

### 3. 日志监控
- 任务执行日志记录
- 日志查询与分析
- 执行统计报告

### 4. 配置管理
- 调度参数配置
- 执行策略配置

## 项目结构

```
scheduler-service
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.dataplatform.scheduler
│   │   │       ├── SchedulerServiceApplication.java    # 启动类
│   │   │       ├── config                             # 配置类
│   │   │       ├── controller                         # 控制器层
│   │   │       ├── dto                                # 数据传输对象
│   │   │       ├── entity                             # 实体类
│   │   │       ├── mapper                             # 数据访问层
│   │   │       └── service                            # 业务逻辑层
│   │   └── resources
│   │       ├── mapper                                 # MyBatis XML映射文件
│   │       └── application.yml                        # 应用配置文件
│   └── test                                           # 测试代码
└── pom.xml                                            # Maven配置文件
```

## 核心实体类

1. **JobInfo** - 任务信息表 (xxl_job_info)
2. **JobLog** - 调度日志表 (xxl_job_log)
3. **JobRegistry** - 执行器注册表 (xxl_job_registry)
4. **JobLogReport** - 任务日志报告表 (xxl_job_log_report)
5. **JobLogGlue** - 任务GLUE日志表 (xxl_job_logglue)

## API接口

### 任务管理接口
- `POST /api/scheduler/jobs` - 创建任务
- `GET /api/scheduler/jobs` - 获取任务列表
- `GET /api/scheduler/jobs/{id}` - 获取任务详情
- `PUT /api/scheduler/jobs/{id}` - 更新任务
- `DELETE /api/scheduler/jobs/{id}` - 删除任务
- `POST /api/scheduler/jobs/{id}/start` - 启动任务
- `POST /api/scheduler/jobs/{id}/stop` - 停止任务

### 执行器管理接口
- `POST /api/scheduler/jobgroups` - 创建执行器
- `GET /api/scheduler/jobgroups` - 获取执行器列表
- `GET /api/scheduler/jobgroups/{id}` - 获取执行器详情
- `PUT /api/scheduler/jobgroups/{id}` - 更新执行器
- `DELETE /api/scheduler/jobgroups/{id}` - 删除执行器

### 日志管理接口
- `GET /api/scheduler/logs` - 获取日志列表
- `GET /api/scheduler/logs/{id}` - 获取日志详情

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scheduler_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 2000ms
```

## 部署说明

1. 创建MySQL数据库 `scheduler_db`
2. 执行数据库初始化脚本
3. 修改 `application.yml` 中的数据库和Redis连接配置
4. 编译打包: `mvn clean package`
5. 运行服务: `java -jar scheduler-service-1.0.0.jar`

## 访问地址

- API接口: http://localhost:8085
- API文档: http://localhost:8085/swagger-ui.html