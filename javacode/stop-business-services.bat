@echo off
setlocal enabledelayedexpansion

chcp 936 >nul

echo ==========================================
echo 数据中台业务服务停止脚本 (Windows)
echo ==========================================

:: 查找并终止业务服务进程
echo 正在查找业务服务进程...

:: 终止报表服务
echo 正在停止报表服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Report Service*"

:: 终止调度服务
echo 正在停止调度服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Scheduler Service*"

:: 终止数据资产服务
echo 正在停止数据资产服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Asset Service*"

:: 终止数据质量服务
echo 正在停止数据质量服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Quality Service*"

:: 终止数据血缘服务
echo 正在停止数据血缘服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Lineage Service*"

:: 终止元数据服务
echo 正在停止元数据服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Metadata Service*"

echo ==========================================
echo 业务服务已停止
echo ==========================================

pause