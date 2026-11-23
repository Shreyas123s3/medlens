@echo off
setlocal
set APP_HOME=%~dp0
if not exist "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" (
  where gradle >nul 2>nul
  if errorlevel 1 (
    echo Gradle 8.9 is required to bootstrap the wrapper. See README.md for the cloud build.
    exit /b 1
  )
  gradle -p "%APP_HOME%" wrapper --gradle-version 8.9 --distribution-type bin
  if errorlevel 1 exit /b %errorlevel%
)
call "%APP_HOME%gradlew.bat" %*
endlocal
