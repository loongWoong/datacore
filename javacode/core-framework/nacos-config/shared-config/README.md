# Nacos共享配置方案

## 方案说明

为了减少配置复杂度，本项目采用共享配置方案：

1. **共享配置文件**: `application-common.yaml` 包含所有微服务的通用配置
2. **服务特定配置**: 每个服务只保留其特定的配置项

## 配置文件结构

```
shared-config/
├── application-common.yaml     # 共享配置（数据库、Redis、日志等通用配置）
├── auth-service.yaml          # 认证服务特定配置
├── audit-service.yaml         # 审计服务特定配置
├── discovery-service.yaml     # 服务注册与发现特定配置
├── config-service.yaml        # 配置中心特定配置
├── gateway-service.yaml       # 网关服务特定配置
└── monitoring-service.yaml    # 监控服务特定配置
```

## 配置文件说明

### 1. 共享配置文件 (application-common.yaml)
包含所有服务的通用配置：
- 数据库连接配置
- Redis连接配置
- Nacos注册配置
- MyBatis Plus配置
- 日志配置
- JWT配置
- Actuator配置

### 2. 服务特定配置文件
每个服务只包含其特定配置：
- 服务名称
- 服务端口
- 特定框架配置（如JPA、Gateway路由等）
- 特定日志级别

## 如何在Nacos中配置

### 1. 配置共享配置
1. 登录Nacos控制台
2. 点击"配置管理" -> "配置列表"
3. 点击"加号"按钮创建新配置
4. 填写配置信息：
   - Data ID: `application-common.yaml`
   - Group: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 复制 `application-common.yaml` 文件的内容
5. 点击"发布"

### 2. 配置各服务特定配置
按照相同的方式为每个服务创建特定配置：

| 服务名称 | Data ID | 配置文件 |
|---------|---------|---------|
| 认证服务 | `auth-service.yaml` | `auth-service.yaml` |
| 审计服务 | `audit-service.yaml` | `audit-service.yaml` |
| 服务注册与发现 | `discovery-service.yaml` | `discovery-service.yaml` |
| 配置中心 | `config-service.yaml` | `config-service.yaml` |
| 网关服务 | `gateway-service.yaml` | `gateway-service.yaml` |
| 监控服务 | `monitoring-service.yaml` | `monitoring-service.yaml` |

## 微服务配置引用方式

在每个微服务的 `bootstrap.yml` 中，需要配置：

```yaml
spring:
  application:
    name: auth-service  # 对应服务名称
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        shared-configs:
          - data-id: application-common.yaml
            group: DEFAULT_GROUP
```

## 优势

1. **配置简化**: 减少了重复配置项，降低了维护成本
2. **统一管理**: 通用配置集中管理，便于统一修改
3. **灵活扩展**: 新增服务时只需创建特定配置文件
4. **降低出错率**: 减少了配置项重复，降低了配置错误的可能性

## 注意事项

1. 确保共享配置中的数据库、Redis等连接信息适用于所有服务
2. 如果某个服务需要特殊的数据库配置，可以在其特定配置文件中覆盖共享配置
3. 共享配置发布后，所有引用该配置的服务都会自动刷新