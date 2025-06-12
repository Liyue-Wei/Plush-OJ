@echo off
cd /d "%~dp0"
echo [INFO] Compiling Plush_OJ_Server_Test.java...
javac -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test.java
if errorlevel 1 (
    echo [ERROR] Compile Error, Terminated...
    pause
    exit /b 1
)
echo [INFO] Starting Plush_OJ_Server_Test...
java -cp ".;sqlite-jdbc-3.49.1.0.jar;json-20250517.jar" Plush_OJ_Server_Test
pause