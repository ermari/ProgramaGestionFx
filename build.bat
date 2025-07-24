@echo off
echo ===============================
echo ==  Limpiando bin...         ==
echo ===============================
rmdir /s /q bin
mkdir bin

echo ===============================
echo ==  Compilando...            ==
echo ===============================
del sources.txt 2>nul
for /R src %%f in (*.java) do @echo %%f >> sources.txt

javac --module-path "C:\LearnDevs\JAVA\javafx-sdk-17.0.13\lib" --add-modules javafx.controls,javafx.fxml -encoding UTF-8 -d bin -cp "lib/*" @sources.txt

if %ERRORLEVEL% neq 0 (
    echo ⛔ Error durante la compilación. Revisa los errores arriba.
    pause
    exit /b %ERRORLEVEL%
)

del sources.txt

echo ===============================
echo ==  Copiando archivos FXML  ==
echo ===============================
xcopy src\Home\*.fxml bin\Home\ /Y /I
xcopy src\RegistroUsuario\*.fxml bin\RegistroUsuario\ /Y /I
xcopy src\FruitMarket\views\*.fxml bin\FruitMarket\views\ /Y /I
xcopy src\resources\*.* bin\resources\ /E /I /Y
xcopy src\resources\util\css\*.* bin\util\css\ /E /I /Y
xcopy src\Catalogo\*.fxml bin\Catalogo\ /Y /I

xcopy src\Home\Style.css bin\Home\ /Y /I
xcopy src\util\css\formulario.css bin\util\css\ /Y /I /E


echo ===============================
echo ==  Copiando imágenes...    ==
echo ===============================
xcopy src\resources\images\*.* bin\resources\images\ /Y /I

echo ===============================
echo ==  Copiando CSS...         ==
echo ===============================
xcopy src\resources\Home\*.css bin\Home\ /Y /I
xcopy src\resources\util\css\*.css bin\util\css\ /Y /I

echo ===============================
echo ==  Copiando dependencias... ==
echo ===============================
copy "lib\jfoenix-9.0.10.jar" bin\

echo ===============================
echo ==  Extrayendo dependencias ==
echo ===============================
cd bin
jar xf jfoenix-9.0.10.jar
del jfoenix-9.0.10.jar

echo ===============================
echo ==  Generando JAR...        ==
echo ===============================
rem ✅ Eliminar JAR viejo si existe para evitar errores de "file in use"
if exist ..\ProgramaGestionFx.jar (
    del ..\ProgramaGestionFx.jar
)

jar cvfe ..\ProgramaGestionFx.jar Home.Main *

cd ..

echo ===============================
echo ==  Ejecución del JAR...    ==
echo ===============================
java --module-path "C:\LearnDevs\JAVA\javafx-sdk-17.0.13\lib" --add-modules javafx.controls,javafx.fxml -jar ProgramaGestionFx.jar

echo ===============================
echo ==  FIN
pause
