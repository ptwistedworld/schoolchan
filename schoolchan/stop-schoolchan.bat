@echo off
REM ---------------------------------------------------------------
REM  SchoolChan - stop every running instance
REM ---------------------------------------------------------------
setlocal
cd /d "%~dp0"
title Stop SchoolChan

echo.
echo  Stopping SchoolChan...
powershell.exe -NoProfile -ExecutionPolicy Bypass -Command ^
  "$root = (Resolve-Path '.').Path; Get-CimInstance Win32_Process -Filter \"Name='java.exe' OR Name='python.exe'\" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like ('*' + $root + '*') } | ForEach-Object { try { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue; Write-Host ('  stopped pid ' + $_.ProcessId) } catch {} }"

echo  Done.
ping -n 2 127.0.0.1 >nul 2>&1
endlocal
