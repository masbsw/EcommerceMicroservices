@ECHO OFF
SETLOCAL

SET WRAPPER_DIR=%~dp0.mvn\wrapper
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
SET WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Downloading Maven wrapper...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$props = Get-Content '%WRAPPER_PROPERTIES%' | Where-Object { $_ -match '^wrapperUrl=' }; " ^
    "$url = ($props -replace '^wrapperUrl=', ''); " ^
    "New-Item -ItemType Directory -Force -Path '%WRAPPER_DIR%' | Out-Null; " ^
    "Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile '%WRAPPER_JAR%'"
  IF ERRORLEVEL 1 (
    ECHO Failed to download Maven wrapper.
    EXIT /B 1
  )
)

java -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%~dp0" org.apache.maven.wrapper.MavenWrapperMain %*
IF ERRORLEVEL 1 EXIT /B 1

ENDLOCAL
