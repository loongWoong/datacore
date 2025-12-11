@echo off
setlocal enabledelayedexpansion

chcp 936 >nul

echo ==========================================
echo 数据中台核心框架服务停止脚本 (Windows)
echo ==========================================

:: 查找并终止核心框架服务进程
echo 正在查找核心框架服务进程...

:: 终止监控服务
echo 正在停止监控服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Monitoring Service*"

:: 终止网关服务
echo 正在停止网关服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Gateway Service*"

:: 终止审计服务
echo 正在停止审计服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Audit Service*"

:: 终止认证服务
echo 正在停止认证服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Auth Service*"

:: 终止配置中心服务
echo 正在停止配置中心服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Config Service*"

:: 终止服务注册与发现服务
echo 正在停止服务注册与发现服务...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Discovery Service*"

echo ==========================================
echo 核心框架服务已停止
echo ==========================================

pause