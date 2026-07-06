@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __ MVNW_CMD=%~dp0mvnw.cmd
@SET MAVEN_PROJECTBASEDIR=%__MVNW_SAVE_MAVEN_PROJECTBASEDIR__%

@CALL :init_wrapperJar
@IF ERRORLEVEL 1 GOTO error
@CALL :init_mvn_cmd
@IF ERRORLEVEL 1 GOTO error
@CALL :mvn_exec
@IF ERRORLEVEL 1 GOTO error
@GOTO end

:mvn_exec
@CALL %MVNW_CMD% %*
@GOTO :EOF

:init_wrapperJar
@SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
@IF NOT EXIST %WRAPPER_JAR% (
  @CALL :downloadWrapperJar
)
@GOTO :EOF

:downloadWrapperJar
@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
  @IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)
@powershell -Command "&{$webclient=New-Object System.Net.WebClient; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')}"
@GOTO :EOF

:init_mvn_cmd
@SET MVNW_CMD=java -jar %WRAPPER_JAR%
@GOTO :EOF

:error
@EXIT /B 1

:end
@EXIT /B 0
