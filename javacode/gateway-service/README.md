# 网关服务 (Gateway Service)

## 概述
网关服务是数据中台系统的统一入口，负责路由请求到各个微服务，并处理跨域、鉴权等通用功能。

## 功能特性
- 统一路由转发
- 负载均衡
- 请求/响应过滤
- 跨域处理
- 熔断降级

## 路由配置
- `/api/metadata/**` -> 元数据服务 (端口8080)
- `/api/lineage/**` -> 数据血缘服务 (端口8081)
- `/api/quality/**` -> 数据质量服务 (端口8082)
- `/api/assets/**` -> 数据资产服务 (端口8083)
- `/api/reports/**` -> 报表服务 (端口8084)
- `/api/scheduler/**` -> 调度服务 (端口8085)
- `/**` -> 前端静态资源 (端口3000)

## 启动方式
```bash
mvn spring-boot:run
```
或
```bash
java -jar gateway-service-1.0.0.jar
```

## 访问地址
- 网关地址: http://localhost:9000