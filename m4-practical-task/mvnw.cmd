@ECHO OFF
SETLOCAL
SET "BASE_DIR=%~dp0"
IF "%BASE_DIR:~-1%"=="\" SET "BASE_DIR=%BASE_DIR:~0,-1%"
SET "WRAPPER_JAR=%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"
SET "WRAPPER_MAIN=org.apache.maven.wrapper.MavenWrapperMain"

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Maven wrapper JAR not found at "%WRAPPER_JAR%"
  EXIT /B 1
)

java "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" -classpath "%WRAPPER_JAR%" %WRAPPER_MAIN% %*
ENDLOCAL
