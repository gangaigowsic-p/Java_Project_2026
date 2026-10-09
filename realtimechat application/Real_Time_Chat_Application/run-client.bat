@echo off
echo Compiling client...
javac -d bin -cp "src;lib\*" src\app\Main.java src\app\ChatClient.java src\dao\*.java src\model\*.java src\util\*.java
if %ERRORLEVEL% GEQ 1 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)
echo Starting client...
java -cp "bin;lib\*" app.Main
pause
