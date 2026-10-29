<#
    SchoolChan one-click launcher.

    Starts the moderation sidecar and the web app, then keeps the console open.
    Closing this console window stops SchoolChan completely.

    Usage:  schoolchan\start-schoolchan.bat
#>

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

function Write-Step($text) {
    Write-Host ""
    Write-Host "==> $text" -ForegroundColor Cyan
}

# --- stop anything already running -------------------------------------------
Write-Step "Stopping any previous SchoolChan instance"
Get-CimInstance Win32_Process -Filter "Name='java.exe' OR Name='python.exe'" -ErrorAction SilentlyContinue |
    Where-Object { $_.CommandLine -like "*$root*" } |
    ForEach-Object {
        try { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue } catch { }
    }

# --- find a JDK --------------------------------------------------------------
$toolsDir = Join-Path (Split-Path -Parent $root) "tools"
$jdk = $null
if (Test-Path (Join-Path $toolsDir "jdk-21")) {
    $jdk = Join-Path $toolsDir "jdk-21"
} else {
    $candidate = Get-ChildItem -Path $toolsDir -Directory -Filter "jdk-21*" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($candidate) { $jdk = $candidate.FullName }
}
if ($jdk -and (Test-Path (Join-Path $jdk "bin\java.exe"))) {
    $env:JAVA_HOME = $jdk
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "    JDK: $jdk" -ForegroundColor DarkGray
} else {
    Write-Host "    No bundled JDK found - using system Java (Java 21 required)." -ForegroundColor Yellow
}

# --- start the moderation sidecar --------------------------------------------
Write-Step "Starting the moderation sidecar (port 8090)"
$ml = Start-Process -FilePath "powershell.exe" `
    -ArgumentList "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", (Join-Path $root "scripts\run-ml.ps1") `
    -WorkingDirectory $root -PassThru -WindowStyle Minimized

Start-Sleep -Seconds 4

# --- start the web app -------------------------------------------------------
Write-Step "Starting SchoolChan (http://localhost:8080)"
Write-Host "    Staff login: http://localhost:8080/staff/login  (admin / change-me-now)" -ForegroundColor DarkGray
Write-Host ""
Write-Host "    Keep this window open. Close it to stop SchoolChan." -ForegroundColor Yellow
Write-Host ""

try {
    & powershell.exe -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root "scripts\run-backend.ps1")
} finally {
    Write-Step "Stopping SchoolChan"
    try { if ($ml -and -not $ml.HasExited) { Stop-Process -Id $ml.Id -Force -ErrorAction SilentlyContinue } } catch { }
    Get-CimInstance Win32_Process -Filter "Name='java.exe' OR Name='python.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -like "*$root*" } |
        ForEach-Object {
            try { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue } catch { }
        }
    Write-Host "    SchoolChan stopped." -ForegroundColor Green
}
