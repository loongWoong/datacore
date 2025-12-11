#!/bin/bash

# ==========================================
# 数据中台业务服务启动脚本 (Linux)
# ==========================================

echo "=========================================="
echo "数据中台业务服务启动脚本 (Linux)"
echo "=========================================="

# 设置工作目录
cd "$(dirname "$0")"

# 检查是否在正确的目录
if [ ! -d "metadata-service" ]; then
    echo "错误: 未找到业务服务目录"
    echo "请确保在javacode目录下运行此脚本"
    exit 1
fi

# 启动元数据服务
echo "正在启动元数据服务..."
cd metadata-service
gnome-terminal --title="Metadata Service" -- mvn spring-boot:run &
sleep 5

# 启动数据血缘服务
echo "正在启动数据血缘服务..."
cd ../lineage-service
gnome-terminal --title="Lineage Service" -- mvn spring-boot:run &
sleep 5

# 启动数据质量服务
echo "正在启动数据质量服务..."
cd ../quality-service
gnome-terminal --title="Quality Service" -- mvn spring-boot:run &
sleep 5

# 启动数据资产服务
echo "正在启动数据资产服务..."
cd ../asset-service
gnome-terminal --title="Asset Service" -- mvn spring-boot:run &
sleep 5

# 启动调度服务
echo "正在启动调度服务..."
cd ../scheduler-service
gnome-terminal --title="Scheduler Service" -- mvn spring-boot:run &
sleep 5

# 启动报表服务
echo "正在启动报表服务..."
cd ../report-service
gnome-terminal --title="Report Service" -- mvn spring-boot:run &

echo "=========================================="
echo "业务服务启动完成"
echo "=========================================="