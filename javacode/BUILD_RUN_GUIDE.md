# 数据中台项目构建与运行指南

## 目录结构说明

```
javacode/
├── asset-service/          # 数据资产服务
├── core-framework/         # 核心框架服务
│   ├── auth-service/       # 认证服务
│   ├── audit-service/      # 审计服务
│   ├── discovery-service/  # 服务注册与发现
│   ├── config-service/     # 配置中心
│   ├── gateway-service/    # 网关服务
│   ├── monitoring-service/ # 监控服务
│   └── common/            # 公共模块
├── lineage-service/        # 数据血缘服务
├── metadata-service/       # 元数据服务
├── quality-service/        # 数据质量服务
├── report-service/         # 报表服务
├── scheduler-service/      # 调度服务
└── init-scripts/          # 初始化脚本
```

## 环境准备

### 1. Java环境
- JDK 8 或更高版本
- Maven 3.6 或更高版本

### 2. 数据库
- MySQL 5.7 或更高版本
- Redis 5.0 或更高版本

### 3. 中间件
- Nacos 2.0 或更高版本

## 数据库初始化

### 1. 创建数据库
执行初始化SQL脚本:
```bash
mysql -u root -p < init-scripts/datacore_init.sql
```

### 2. 验证数据库
确认以下数据库和表已创建:
- 数据库: `datacore`
- 表: users, roles, permissions, audit_logs 等18个表

## 项目构建

### 1. 构建公共模块
```bash
cd core-framework/common
mvn clean install
```

> **注意**: 如果遇到构建错误，请确保:
> 1. Java版本为1.8或更高
> 2. common模块的pom.xml文件中已正确排除Spring Boot插件
> 3. BusinessException类包含getCode()方法
> 4. 已添加slf4j依赖

### 2. 构建核心框架服务
```bash
# 构建认证服务
cd ../auth-service
mvn clean install

# 构建审计服务
cd ../audit-service
mvn clean install

# 构建服务注册与发现
cd ../discovery-service
mvn clean install

# 构建配置中心
cd ../config-service
mvn clean install

# 构建网关服务
cd ../gateway-service
mvn clean install

# 构建监控服务
cd ../monitoring-service
mvn clean install
```

### 3. 构建业务服务
```bash
# 返回上级目录
cd ../../..

# 构建元数据服务
cd metadata-service
mvn clean install

# 构建数据血缘服务
cd ../lineage-service
mvn clean install

# 构建数据质量服务
cd ../quality-service
mvn clean install

# 构建数据资产服务
cd ../asset-service
mvn clean install

# 构建调度服务
cd ../scheduler-service
mvn clean install

# 构建报表服务
cd ../report-service
mvn clean install
```

## Nacos配置

### 1. 启动Nacos服务器
```bash
# 下载并启动Nacos
# 访问 http://localhost:8848/nacos
# 默认账号密码: nacos/nacos
```

### 2. 配置共享配置
1. 登录Nacos控制台
2. 点击"配置管理" -> "配置列表"
3. 点击"加号"按钮创建新配置
4. 填写配置信息：
   - Data ID: `application.yaml`
   - Group: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 使用之前创建的共享配置文件内容
5. 点击"发布"

## 服务启动顺序

### 1. 启动基础设施服务
```bash
# 1. 启动Nacos (如果未启动)
# 2. 启动MySQL
# 3. 启动Redis
```

### 2. 启动核心框架服务
```bash
# 启动顺序很重要，请按以下顺序启动:

# 1. 服务注册与发现 (端口: 8848)
cd core-framework/discovery-service
java -jar target/discovery-service.jar

# 2. 配置中心 (端口: 8888)
cd ../config-service
java -jar target/config-service.jar

# 3. 认证服务 (端口: 8001)
cd ../auth-service
java -jar target/auth-service.jar --spring.application.name=auth-service --server.port=8001

# 4. 审计服务 (端口: 8002)
cd ../audit-service
java -jar target/audit-service.jar --spring.application.name=audit-service --server.port=8002

# 5. 网关服务 (端口: 9000)
cd ../gateway-service
java -jar target/gateway-service.jar --spring.application.name=gateway-service --server.port=9000

# 6. 监控服务 (端口: 9090)
cd ../monitoring-service
java -jar target/monitoring-service.jar --spring.application.name=monitoring-service --server.port=9090
```

### 3. 启动业务服务
```bash
# 业务服务启动顺序相对灵活，可根据需要调整:

# 元数据服务 (端口: 8010)
cd ../../metadata-service
java -jar target/metadata-service.jar --spring.application.name=metadata-service --server.port=8010

# 数据血缘服务 (端口: 8020)
cd ../lineage-service
java -jar target/lineage-service.jar --spring.application.name=lineage-service --server.port=8020

# 数据质量服务 (端口: 8030)
cd ../quality-service
java -jar target/quality-service.jar --spring.application.name=quality-service --server.port=8030

# 数据资产服务 (端口: 8040)
cd ../asset-service
java -jar target/asset-service.jar --spring.application.name=asset-service --server.port=8040

# 调度服务 (端口: 8050)
cd ../scheduler-service
java -jar target/scheduler-service.jar --spring.application.name=scheduler-service --server.port=8050

# 报表服务 (端口: 8060)
cd ../report-service
java -jar target/report-service.jar --spring.application.name=report-service --server.port=8060
```

## 验证服务

### 1. 检查服务注册
访问Nacos控制台: http://localhost:8848/nacos
确认所有服务都已成功注册

### 2. 测试API网关
通过网关访问各服务API:
```bash
# 访问认证服务
curl http://localhost:9000/api/auth/users

# 访问元数据服务
curl http://localhost:9000/api/metadata/entities

# 访问资产服务
curl http://localhost:9000/api/asset/assets
```

### 3. 查看监控信息
访问监控服务: http://localhost:9090/actuator

## 常见问题及解决方案

### 1. 端口冲突
如果遇到端口冲突，请修改启动命令中的端口号:
```bash
java -jar service.jar --server.port=新端口号
```

### 2. 数据库连接失败
检查以下配置:
- MySQL服务是否启动
- 数据库用户名密码是否正确
- Nacos中数据库配置是否正确

### 3. 服务无法注册到Nacos
检查以下配置:
- Nacos服务是否启动
- Nacos地址配置是否正确
- 网络连接是否正常

### 4. 服务启动缓慢
可能是以下原因:
- 首次启动需要初始化数据库表
- 网络连接较慢
- 系统资源不足

## 性能优化建议

### 1. JVM参数调优
```bash
java -Xms512m -Xmx2g -jar service.jar
```

### 2. 数据库连接池优化
在Nacos配置中调整数据库连接池参数

### 3. 缓存策略
合理使用Redis缓存热点数据

## 备份与恢复

### 1. 数据库备份
```bash
mysqldump -u root -p datacore > datacore_backup.sql
```

### 2. 配置备份
定期导出Nacos中的配置

## 安全建议

### 1. 修改默认密码
- 修改Nacos默认密码
- 修改MySQL root密码
- 修改Redis密码

### 2. 网络安全
- 限制外部访问端口
- 使用HTTPS协议
- 配置防火墙规则

### 3. 应用安全
- 启用JWT认证
- 配置CSRF防护
- 定期更新依赖包