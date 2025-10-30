@echo off
setlocal enabledelayedexpansion

:: ============================
:: Rutas de proyecto
:: ============================
set "SRC_DIR=C:\LearnDevs\JAVA\ProgramaGestionFx\src"
set "BIN_DIR=C:\LearnDevs\JAVA\ProgramaGestionFx\bin"
set "LIB_DIR=C:\LearnDevs\JAVA\ProgramaGestionFx\lib"
set "JAVAFX_DIR=C:\LearnDevs\JAVA\javafx-sdk-17.0.13\lib"

:: ============================
:: Limpiar carpeta bin
:: ============================
if exist "%BIN_DIR%" rd /s /q "%BIN_DIR%"
mkdir "%BIN_DIR%"

:: ============================
:: Construir classpath
:: ============================
set "CP="
for %%i in ("%LIB_DIR%\*.jar") do (
    set CP=!CP!;%%i
)
for %%i in ("%JAVAFX_DIR%\*.jar") do (
    set CP=!CP!;%%i
)

:: Quitar el primer ';' inicial
set CP=!CP:~1!

:: ============================
:: Compilar todos los .java
:: ============================
echo Compilando proyecto...
javac --module-path "%JAVAFX_DIR%" --add-modules javafx.controls,javafx.fxml -cp "!CP!" -d "%BIN_DIR%" %SRC_DIR%\**\*.java

if %errorlevel% neq 0 (
    echo.
    echo Error en la compilacion.
    pause
    exit /b %errorlevel%
)

echo.
echo Compilacion exitosa!
pause
