@echo off
REM ---------------------------------------------------------------
REM  SchoolChan - one click start
REM  Double-click this file. Close the window to stop SchoolChan.
REM ---------------------------------------------------------------
setlocal
cd /d "%~dp0"
title SchoolChan

echo.
echo  ================================================
echo   SchoolChan - anonymous school discussion board
echo  ================================================
echo.

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\launch.ps1"

echo.
echo  SchoolChan has stopped.
pause
endlocal
