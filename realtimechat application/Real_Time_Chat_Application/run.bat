@echo off
:menu
cls
echo ========================================
echo       REAL TIME CHAT RUN MENU
echo ========================================
echo 1. Run Server
echo 2. Run Client
echo 3. Exit
echo.
set /p choice="Enter your choice (1-3): "

if "%choice%"=="1" goto server
if "%choice%"=="2" goto client
if "%choice%"=="3" goto exit

:server
start cmd /k "run-server.bat"
goto menu

:client
start cmd /k "run-client.bat"
goto menu

:exit
exit
