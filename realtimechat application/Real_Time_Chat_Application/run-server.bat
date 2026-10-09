@echo off
echo Compiling server...
javac -d bin -cp "src;lib\*" src\app\ChatServer.java src\dao\*.java src\model\*.java src\util\*.java src\app\ClientHandler.java
if %ERRORLEVEL% GEQ 1 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)
echo Starting server...
java -cp "bin;lib\*" app.ChatServer
pause
