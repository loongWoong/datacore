# 数据中台核心框架模块

## 项目概述

数据中台核心框架模块是基于Spring Boot和Spring Cloud Alibaba构建的企业级微服务架构框架，提供了服务注册与发现、认证授权、RBAC权限控制、审计日志等系统级功能。

## 技术架构

- **框架**: Spring Boot 3.x + Spring Cloud Alibaba
- **服务注册与发现**: Nacos
- **配置中心**: Nacos
- **认证授权**: Spring Security + OAuth2 + JWT
- **权限控制**: RBAC模型（基于角色的访问控制）
- **网关**: Spring Cloud Gateway
- **监控**: Prometheus + Grafana
- **链路追踪**: SkyWalking
- **熔断降级**: Sentinel

## 模块说明

### 1. 认证服务 (auth-service)
- 功能：用户认证、JWT令牌管理、RBAC权限控制
- 端口：8001
- 技术：Spring Security + JWT + Redis

### 2. 审计服务 (audit-service)
- 功能：操作日志记录与查询
- 端口：8002
- 技术：Spring Data JPA + MySQL

### 3. 服务注册与发现 (discovery-service)
- 功能：服务注册与发现
- 端口：8848
- 技术：Nacos

### 4. 配置中心 (config-service)
- 功能：统一配置管理
- 端口：8888
- 技术：Nacos

### 5. 网关服务 (gateway-service)
- 功能：API网关、路由转发、负载均衡
- 端口：9000
- 技术：Spring Cloud Gateway

### 6. 监控服务 (monitoring-service)
- 功能：系统监控、指标收集
- 端口：9090
- 技术：Prometheus + Micrometer

### 7. 公共模块 (common)
- 功能：公共工具类、异常处理、统一响应封装

## 项目结构

```
core-framework
├── auth-service              # 认证服务
├── audit-service             # 审计服务
├── discovery-service         # 服务注册与发现
├── config-service            # 配置中心
├── gateway-service           # 网关服务
├── monitoring-service        # 监控服务
├── common                   # 公共模块
└── pom.xml                  # 父级Maven配置
```

## 核心功能

### 1. 认证授权
- JWT令牌生成与验证
- 用户密码加密存储
- 登录登出功能
- 令牌黑名单管理

### 2. RBAC权限控制
- 用户-角色-权限三级模型
- 基于注解的权限校验
- 动态权限配置

### 3. 审计日志
- 操作日志自动记录
- 请求参数与响应结果记录
- IP地址与用户代理记录
- 执行时间统计

### 4. 服务治理
- 服务自动注册与发现
- 负载均衡
- 熔断降级
- 链路追踪

## 部署说明

1. 启动Nacos服务器
2. 依次启动各服务模块：
   - discovery-service
   - config-service
   - auth-service
   - audit-service
   - gateway-service
   - monitoring-service

## API网关路由

- 认证服务：`/api/auth/**` → auth-service
- 审计服务：`/api/audit/**` → audit-service

## 访问地址

- Nacos控制台：http://localhost:8848/nacos
- 网关服务：http://localhost:9000
- 监控端点：http://localhost:9090/actuator
- Prometheus指标：http://localhost:9090/actuator/prometheus