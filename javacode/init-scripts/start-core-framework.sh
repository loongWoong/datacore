#!/bin/bash

# ==========================================
# 数据中台核心框架服务启动脚本 (Linux)
# ==========================================

echo "=========================================="
echo "数据中台核心框架服务启动脚本 (Linux)"
echo "=========================================="

# 设置工作目录
cd "$(dirname "$0")/core-framework"

# 检查是否在正确的目录
if [ ! -d "discovery-service" ]; then
    echo "错误: 未找到核心框架服务目录"
    echo "请确保在javacode目录下运行此脚本"
    exit 1
fi

# 启动服务注册与发现 (Nacos)
echo "正在启动服务注册与发现..."
cd discovery-service
gnome-terminal --title="Discovery Service" -- mvn spring-boot:run &
sleep 10

# 启动配置中心
echo "正在启动配置中心..."
cd ../config-service
gnome-terminal --title="Config Service" -- mvn spring-boot:run &
sleep 10

# 启动认证服务
echo "正在启动认证服务..."
cd ../auth-service
gnome-terminal --title="Auth Service" -- mvn spring-boot:run &
sleep 10

# 启动审计服务
echo "正在启动审计服务..."
cd ../audit-service
gnome-terminal --title="Audit Service" -- mvn spring-boot:run &
sleep 10

# 启动网关服务
echo "正在启动网关服务..."
cd ../gateway-service
gnome-terminal --title="Gateway Service" -- mvn spring-boot:run &
sleep 10

# 启动监控服务
echo "正在启动监控服务..."
cd ../monitoring-service
gnome-terminal --title="Monitoring Service" -- mvn spring-boot:run &

echo "=========================================="
echo "核心框架服务启动完成"
echo "请等待各服务完全启动后再启动业务服务"
echo "=========================================="