@echo off
chcp 65001 >nul

echo ==================================================
echo DBT血缘采集测试SQL执行脚本
echo ==================================================

echo 正在连接数据库并执行测试SQL...

REM 执行SQL文件
for %%f in (*.sql) do (
    if not "%%f"=="README.md" (
        if not "%%f"=="run_test_sql.bat" (
            echo 正在执行: %%f
            mysql -h 192.168.22.212 -P 3306 -u root -p123456 dataplatform < "%%f"
            if %errorlevel% equ 0 (
                echo 成功执行: %%f
            ) else (
                echo 执行失败: %%f
            )
            echo.
        )
    )
)

echo ==================================================
echo 所有测试SQL执行完成
echo ==================================================

pause