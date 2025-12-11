# 数据中台Java重构项目

## 项目概述

本项目是基于Java的企业级数据中台重构项目，采用Spring Boot + Spring Cloud微服务架构，包含以下核心服务：

### 核心框架服务
- **认证服务 (auth-service)**: 用户认证与权限管理
- **审计服务 (audit-service)**: 操作日志与审计跟踪
- **服务注册与发现 (discovery-service)**: 基于Nacos的服务注册与发现
- **配置中心 (config-service)**: 基于Nacos的统一配置管理
- **网关服务 (gateway-service)**: API网关与路由管理
- **监控服务 (monitoring-service)**: 系统监控与健康检查

### 业务服务
- **元数据服务 (metadata-service)**: 元数据管理与维护
- **数据血缘服务 (lineage-service)**: 数据血缘关系分析
- **数据质量服务 (quality-service)**: 数据质量检查与监控
- **数据资产服务 (asset-service)**: 数据资产管理与评估
- **调度服务 (scheduler-service)**: 任务调度与执行管理
- **报表服务 (report-service)**: 业务报表生成与展示

## 构建状态

✅ 所有服务均已成功构建，兼容Java 8环境

### 构建要求
- Java 8 或更高版本
- Maven 3.6 或更高版本
- MySQL 5.7 或更高版本
- Redis 5.0 或更高版本
- Nacos 2.0 或更高版本

### 构建步骤

1. **构建公共模块**
   ```bash
   cd core-framework/common
   mvn clean install
   ```

2. **构建核心框架服务**
   ```bash
   cd ../..
   mvn clean install
   ```

3. **构建业务服务**
   ```bash
   cd ..
   mvn clean install
   ```

## 配置管理

所有服务使用Nacos进行统一配置管理，采用共享配置方案以减少配置复杂度。

## 启动顺序

为确保服务正常运行，请按照以下顺序启动服务:

1. 基础设施服务 (Nacos, MySQL, Redis)
2. 核心框架服务 (按依赖顺序)
3. 业务服务 (可按任意顺序)

## 文档

- [构建与运行指南](BUILD_RUN_GUIDE.md): 详细的构建与运行说明
- [数据库初始化脚本](init-scripts/datacore_init.sql): 数据库初始化SQL脚本
- [Nacos配置](core-framework/nacos-config): Nacos配置文件

## 注意事项

1. 请确保Java版本为1.8或更高
2. 构建前请先构建core-framework/common模块
3. 启动服务前请确保基础设施服务已正常运行
4. 所有服务均支持通过JVM参数指定端口和应用名称