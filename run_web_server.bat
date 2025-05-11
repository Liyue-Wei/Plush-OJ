@echo off
title Python 簡易 Web 伺服器（區域網可連）
echo.
echo 啟動中... 請稍候
echo ----------------------------

REM 啟動 Python 的 HTTP 伺服器，綁定到所有網卡
python -m http.server 8000 --bind 0.0.0.0

pause
