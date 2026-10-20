$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $root "ml-service")
pip install -r requirements.txt
pip install transformers torch
$env:MOCK_MODE = "false"
if (-not $env:TEXT_MODEL_ID) { $env:TEXT_MODEL_ID = "vectorsense/toxiscan" }
if (-not $env:TRANSLATE_MODEL_ID) { $env:TRANSLATE_MODEL_ID = "facebook/nllb-200-distilled-600M" }
if (-not $env:IMAGE_MODEL_ID) { $env:IMAGE_MODEL_ID = "schoolchan-image-mock-v1" }
Write-Host "Real text and translation models. First start downloads weights into the Hugging Face cache."
Write-Host "Image scoring stays on the mock until IMAGE_MODEL_ID is a local ONNX file."
python -m uvicorn app.main:app --host 0.0.0.0 --port 8090
