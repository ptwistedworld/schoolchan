$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
<<<<<<< HEAD
$jdk = Join-Path (Split-Path -Parent $root) "tools\jdk-21"
if (Test-Path $jdk) {
    $env:JAVA_HOME = $jdk
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}
Set-Location (Join-Path $root "backend")
# Profile "local" uses an H2 file database. Use --spring.profiles.active=dev for Postgres.
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
=======
$toolsDir = Join-Path (Split-Path -Parent $root) "tools"
# Auto-detect JDK 21 folder
$jdk = $null
if (Test-Path (Join-Path $toolsDir "jdk-21")) { $jdk = Join-Path $toolsDir "jdk-21" }
else {
    $candidate = Get-ChildItem -Path $toolsDir -Directory -Filter "jdk-21*" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($candidate) { $jdk = $candidate.FullName }
}
if ($jdk -and (Test-Path (Join-Path $jdk "bin\java.exe"))) {
    $env:JAVA_HOME = $jdk
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "Using JDK: $jdk"
} else {
    Write-Host "No bundled JDK found; using system Java."
}
Set-Location (Join-Path $root "backend")
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
>>>>>>> d5851d3 (Initial commit)
