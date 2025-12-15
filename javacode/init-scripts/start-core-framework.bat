@echo off
setlocal enabledelayedexpansion

chcp 936 >nul

echo ==========================================
echo 数据中台核心框架服务启动脚本 (Windows)
echo ==========================================

:: 设置工作目录
cd /d "%~dp0\core-framework"

:: 检查是否在正确的目录
if not exist "discovery-service" (
    echo 错误: 未找到核心框架服务目录
    echo 请确保在javacode目录下运行此脚本
    pause
    exit /b 1
)

:: 启动服务注册与发现 (Nacos)
echo 正在启动服务注册与发现...
cd discovery-service
start "Discovery Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

:: 启动配置中心
echo 正在启动配置中心...
cd ..\config-service
start "Config Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

:: 启动认证服务
echo 正在启动认证服务...
cd ..\auth-service
start "Auth Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

:: 启动审计服务
echo 正在启动审计服务...
cd ..\audit-service
start "Audit Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

:: 启动网关服务
echo 正在启动网关服务...
cd ..\gateway-service
start "Gateway Service" cmd /k "mvn spring-boot:run"
timeout /t 10 /nobreak >nul

:: 启动监控服务
echo 正在启动监控服务...
cd ..\monitoring-service
start "Monitoring Service" cmd /k "mvn spring-boot:run"

echo ==========================================
echo 核心框架服务启动完成
echo 请等待各服务完全启动后再启动业务服务
echo ==========================================

pause