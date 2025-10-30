@echo off
setlocal enabledelayedexpansion

:: === CONFIGURACIÓN ===
set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAFX_HOME=C:\LearnDevs\JAVA\javafx-sdk-17.0.13
set PROJECT_ROOT=C:\LearnDevs\JAVA\ProgramaGestionFx
set SRC_DIR=%PROJECT_ROOT%\src
set RES_DIR=%PROJECT_ROOT%\resources
set LIB_DIR=%PROJECT_ROOT%\lib
set OUT_DIR=%PROJECT_ROOT%\out
set INPUT_DIR=%PROJECT_ROOT%\build\input
set DIST_DIR=%PROJECT_ROOT%\dist
set APP_NAME=MiAplicacion
set MAIN_CLASS=Home.Main

set APP_IMAGE_DIR=%DIST_DIR%\%APP_NAME%

:: === LIMPIAR CARPETAS DE COMPILACIÓN ===
echo Limpiando carpetas de compilación...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
if exist "%INPUT_DIR%" rmdir /s /q "%INPUT_DIR%"
if exist "%APP_IMAGE_DIR%" rmdir /s /q "%APP_IMAGE_DIR%"
mkdir %OUT_DIR%
mkdir %INPUT_DIR%
if not exist %DIST_DIR% mkdir %DIST_DIR%

:: === COMPILAR TODOS LOS .JAVA ===
echo Compilando todos los archivos Java...
set FILES=
for /R %SRC_DIR% %%f in (*.java) do (
    set FILES=!FILES! %%f
)
if "!FILES!"=="" (
    echo No se encontraron archivos Java
    pause
    exit /b 1
)
"%JAVA_HOME%\bin\javac" -encoding UTF-8 ^
  --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "%LIB_DIR%\*;%SRC_DIR%" ^
  -d %OUT_DIR% !FILES!
if %ERRORLEVEL% neq 0 (
    echo Error en la compilacion
    pause
    exit /b %ERRORLEVEL%
)

:: === COPIAR RECURSOS Y LIBRERÍAS AL INPUT ===
echo Copiando recursos al input...
xcopy /E /I /Y %RES_DIR% %OUT_DIR% >nul
echo Copiando librerías JasperReports al input...
xcopy /Y %LIB_DIR%\*.jar %INPUT_DIR% >nul

:: === GENERAR JAR EN INPUT_DIR ===
echo Generando JAR ejecutable en input...
"%JAVA_HOME%\bin\jar" --create --file %INPUT_DIR%\%APP_NAME%.jar --main-class %MAIN_CLASS% -C %OUT_DIR% .
if %ERRORLEVEL% neq 0 (
    echo Error creando el JAR
    pause
    exit /b %ERRORLEVEL%
)

:: === CREAR APP-IMAGE --- USANDO INPUT_DIR ===
echo Generando app-image portable...
"%JAVA_HOME%\bin\jpackage" ^
  --input %INPUT_DIR% ^
  --main-jar %APP_NAME%.jar ^
  --main-class %MAIN_CLASS% ^
  --name %APP_NAME% ^
  --type app-image ^
  --dest %DIST_DIR% ^
  --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml ^
  --icon %ICON_FILE%
if %ERRORLEVEL% neq 0 (
    echo Error creando el app-image
    pause
    exit /b %ERRORLEVEL%
)

:: === FIN ===
echo.
echo ================================================
echo   Proceso terminado.
echo   JAR ejecutable: %INPUT_DIR%\%APP_NAME%.jar
echo   App portable:   %APP_IMAGE_DIR%
echo ================================================
pause
endlocal
