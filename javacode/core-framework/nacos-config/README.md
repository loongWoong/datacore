# Nacos配置说明

## 配置文件列表

本目录包含了数据中台核心框架各个微服务在Nacos中的配置文件：

1. `auth-service.yaml` - 认证服务配置
2. `audit-service.yaml` - 审计服务配置
3. `discovery-service.yaml` - 服务注册与发现配置
4. `config-service.yaml` - 配置中心配置
5. `gateway-service.yaml` - 网关服务配置
6. `monitoring-service.yaml` - 监控服务配置

## 如何在Nacos中配置

### 1. 启动Nacos服务器
首先确保Nacos服务器已经启动并运行在默认端口8848上。

### 2. 登录Nacos控制台
打开浏览器访问 `http://localhost:8848/nacos`，使用默认账户密码登录：
- 用户名: nacos
- 密码: nacos

### 3. 创建命名空间（可选）
为了更好地管理配置，建议为项目创建独立的命名空间：
1. 在Nacos控制台左侧导航栏点击"命名空间"
2. 点击"新建命名空间"
3. 输入命名空间ID和名称，例如：`datacore-dev`
4. 点击"确定"

### 4. 配置各个服务
对于每个服务配置文件，按以下步骤操作：

#### 配置auth-service
1. 在Nacos控制台点击"配置管理" -> "配置列表"
2. 点击"加号"按钮创建新配置
3. 填写配置信息：
   - Data ID: `auth-service.yaml`
   - Group: `DEFAULT_GROUP` （或自定义组名）
   - 配置格式: `YAML`
   - 配置内容: 复制 `auth-service.yaml` 文件的内容
4. 点击"发布"

#### 配置其他服务
按照相同的方式为其他服务配置文件创建配置项：
- audit-service.yaml → Data ID: `audit-service.yaml`
- discovery-service.yaml → Data ID: `discovery-service.yaml`
- config-service.yaml → Data ID: `config-service.yaml`
- gateway-service.yaml → Data ID: `gateway-service.yaml`
- monitoring-service.yaml → Data ID: `monitoring-service.yaml`

### 5. 验证配置
配置完成后，可以在Nacos控制台查看和编辑配置。各个微服务启动时会自动从Nacos获取配置。

## 配置项说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://数据库地址:端口/数据库名
    username: 数据库用户名
    password: 数据库密码
```

### Redis配置
```yaml
spring:
  redis:
    host: Redis服务器地址
    port: Redis端口
    database: 数据库索引
```

### Nacos注册配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: Nacos服务器地址:端口
```

## 注意事项

1. 确保Nacos服务器地址在所有配置文件中一致
2. 数据库和Redis连接信息需要根据实际环境进行修改
3. 如果使用了命名空间，需要在微服务的bootstrap.yml中指定namespace
4. 配置发布后，微服务会自动刷新配置，无需重启