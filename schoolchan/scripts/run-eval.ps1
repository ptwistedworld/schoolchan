$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$jdk = Join-Path (Split-Path -Parent $root) "tools\jdk-21"
if (Test-Path $jdk) {
    $env:JAVA_HOME = $jdk
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}
Set-Location (Join-Path $root "backend")
Write-Host "Writes accuracy.json, mcnemar.json, and latency.md under schoolchan/eval/results/<timestamp>/"
.\gradlew.bat bootRun --args="--spring.profiles.active=local --schoolchan.eval.run=true"
