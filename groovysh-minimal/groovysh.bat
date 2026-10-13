@echo off
rem Launches groovysh using only the runtime and libraries bundled in this folder.
setlocal
set "JAVA_HOME=%~dp0jre"
set "GROOVY_HOME=%~dp0"
set "CLASSPATH="
call "%~dp0bin\groovysh.bat" %*
exit /B %ERRORLEVEL%
