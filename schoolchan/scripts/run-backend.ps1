$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$jdk = Join-Path (Split-Path -Parent $root) "tools\jdk-21"
if (Test-Path $jdk) {
    $env:JAVA_HOME = $jdk
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}
Set-Location (Join-Path $root "backend")
# Profile "local" uses an H2 file database. Use --spring.profiles.active=dev for Postgres.
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
