# Nacos极简配置方案

## 方案说明

为了最大程度减少配置复杂度，本项目采用极简配置方案：

1. **单一配置文件**: 所有微服务共享同一个配置文件 `application.yaml`
2. **无服务特定配置**: 不再为每个服务创建单独的配置
3. **通过启动参数区分服务**: 通过JVM启动参数或环境变量来区分不同服务

## 配置文件结构

```
simple-config/
├── application.yaml     # 所有服务共享的配置文件
└── README.md           # 说明文档
```

## 配置文件说明

### application.yaml
包含所有可能用到的配置项：
- 数据库连接配置
- Redis连接配置
- Nacos注册与配置中心配置
- MyBatis Plus配置
- 日志配置
- JWT配置
- Actuator配置
- JPA配置
- Gateway路由配置
- 监控配置

## 如何在Nacos中配置

### 1. 配置公共配置
1. 登录Nacos控制台
2. 点击"配置管理" -> "配置列表"
3. 点击"加号"按钮创建新配置
4. 填写配置信息：
   - Data ID: `application.yaml`
   - Group: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 复制 `application.yaml` 文件的内容
5. 点击"发布"

## 微服务启动方式

由于所有服务共享同一份配置，需要通过启动参数来区分不同服务：

### 1. 认证服务启动
```bash
java -jar auth-service.jar --spring.application.name=auth-service --server.port=8001
```

### 2. 审计服务启动
```bash
java -jar audit-service.jar --spring.application.name=audit-service --server.port=8002
```

### 3. 服务注册与发现启动
```bash
java -jar discovery-service.jar --spring.application.name=discovery-service --server.port=8848
```

### 4. 配置中心启动
```bash
java -jar config-service.jar --spring.application.name=config-service --server.port=8888
```

### 5. 网关服务启动
```bash
java -jar gateway-service.jar --spring.application.name=gateway-service --server.port=9000
```

### 6. 监控服务启动
```bash
java -jar monitoring-service.jar --spring.application.name=monitoring-service --server.port=9090
```

## 微服务bootstrap.yml配置

在每个微服务的 `bootstrap.yml` 中，只需要简单配置：

```yaml
spring:
  application:
    name: ${SPRING_APPLICATION_NAME:datacore-service}  # 通过环境变量或启动参数设置
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
```

## 优势

1. **极度简化**: 只需要维护一份配置文件
2. **易于管理**: 配置变更一次发布，所有服务都能获取到
3. **降低成本**: 大幅减少Nacos配置项数量
4. **减少出错**: 避免配置项不一致导致的问题

## 注意事项

1. 服务端口和名称必须通过启动参数指定
2. 某些服务特有的配置项（如Gateway路由）在公共配置中定义，但只有对应服务会使用
3. 启动服务时务必指定正确的应用名称和端口
4. 可以通过Spring Profiles来管理不同环境的配置