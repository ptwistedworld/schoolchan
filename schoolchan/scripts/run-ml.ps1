$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $root "ml-service")
$env:MOCK_MODE = "true"
python -m uvicorn app.main:app --host 0.0.0.0 --port 8090
