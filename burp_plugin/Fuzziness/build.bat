@echo off
echo Building SMS Bomb Fuzzer Enhanced...

REM Create build directory
if exist build rmdir /s /q build
mkdir build
mkdir build\burp

REM Compile Java source
echo Compiling Java source...
javac -cp "burp-extender-api-2.3.jar;json-20230227.jar" -d build decompiled_output\burp\BurpExtender.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Extract JSON library classes
echo Extracting JSON library...
cd build
jar xf ..\json-20230227.jar
cd ..

REM Create JAR file
echo Creating JAR file...
cd build
jar cf ..\SMS_Tomato_Fuzzer_v4.1.jar burp\*.class org\json\*.class

cd ..
echo Build completed successfully!
echo Output: SMS_Bomb_Fuzzer_Enhanced.jar
pause
