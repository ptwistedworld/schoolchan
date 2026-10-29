@echo off
REM ---------------------------------------------------------------
REM  SchoolChan - back up the local database
REM ---------------------------------------------------------------
setlocal
cd /d "%~dp0"
title Backup SchoolChan database

echo.
echo  Backing up the SchoolChan database...
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\backup-db.ps1"
echo.
pause
endlocal
