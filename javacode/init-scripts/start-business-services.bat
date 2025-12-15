@echo off
setlocal enabledelayedexpansion

chcp 936 >nul

echo ==========================================
echo 数据中台业务服务启动脚本 (Windows)
echo ==========================================

:: 设置工作目录
cd /d "%~dp0"

:: 检查是否在正确的目录
if not exist "metadata-service" (
    echo 错误: 未找到业务服务目录
    echo 请确保在javacode目录下运行此脚本
    pause
    exit /b 1
)

:: 启动元数据服务
echo 正在启动元数据服务...
cd metadata-service
start "Metadata Service" cmd /k "mvn spring-boot:run"
timeout /t 5 /nobreak >nul

:: 启动数据血缘服务
echo 正在启动数据血缘服务...
cd ..\lineage-service
start "Lineage Service" cmd /k "mvn spring-boot:run"
timeout /t 5 /nobreak >nul

:: 启动数据质量服务
echo 正在启动数据质量服务...
cd ..\quality-service
start "Quality Service" cmd /k "mvn spring-boot:run"
timeout /t 5 /nobreak >nul

:: 启动数据资产服务
echo 正在启动数据资产服务...
cd ..\asset-service
start "Asset Service" cmd /k "mvn spring-boot:run"
timeout /t 5 /nobreak >nul

:: 启动调度服务
echo 正在启动调度服务...
cd ..\scheduler-service
start "Scheduler Service" cmd /k "mvn spring-boot:run"
timeout /t 5 /nobreak >nul

:: 启动报表服务
echo 正在启动报表服务...
cd ..\report-service
start "Report Service" cmd /k "mvn spring-boot:run"

echo ==========================================
echo 业务服务启动完成
echo ==========================================

pause