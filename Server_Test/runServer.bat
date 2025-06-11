@echo off
cd /d "%~dp0"
echo [INFO] 編譯 Plush_OJ_Server_Test.java...
javac -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test.java
if errorlevel 1 (
    echo [ERROR] 編譯失敗，請檢查程式碼。
    pause
    exit /b 1
)
echo [INFO] 啟動 Plush_OJ_Server_Test...
java -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test
pause