$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
<<<<<<< HEAD
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
=======
$mlDir = Join-Path $root "ml-service"
Set-Location $mlDir

# The real models need Python 3.11 or 3.12 (onnxruntime has no 3.13+ wheels)
# and live in their own environment so the mock path stays light.
$venv = Join-Path $mlDir ".venv-real"
if (-not (Test-Path (Join-Path $venv "Scripts\python.exe"))) {
    Write-Host "The real-model environment is missing. Run scripts\setup-real-models.ps1 first." -ForegroundColor Yellow
    exit 1
}
$python = Join-Path $venv "Scripts\python.exe"

$env:MOCK_MODE = "false"
$env:TEXT_MODEL_ID = "vectorsense/toxiscan"
$env:IMAGE_MODEL_ID = "onnx-community/nsfw_image_detection-ONNX"
$env:TRANSLATE_MODEL_ID = "facebook/nllb-200-distilled-600M"
$env:ML_MODEL_CACHE = Join-Path $mlDir "models"
# bfloat16 halves the translation model's memory. Set TRANSLATE_DTYPE=float32
# if you have 6 GB or more free.
$env:TRANSLATE_DTYPE = "bfloat16"
$env:TRANSLATE_MAX_NEW_TOKENS = "96"

Write-Host ""
Write-Host "Real moderation models:"
Write-Host "  text      $env:TEXT_MODEL_ID   (verified: apache-2.0, ~136 MB)"
Write-Host "  image     $env:IMAGE_MODEL_ID  (OpenNSFW2 export, ~90 MB)"
Write-Host "  translate $env:TRANSLATE_MODEL_ID (CC-BY-NC, ~2.5 GB, lazy loaded)"
Write-Host ""
Write-Host "Weights cache: $env:ML_MODEL_CACHE"
Write-Host "Memory: about 3 GB free for the translation model (bfloat16)."
Write-Host "The first request for a model downloads it; after that it works offline."
Write-Host ""

& $python -m uvicorn app.main:app --host 0.0.0.0 --port 8090
>>>>>>> d5851d3 (Initial commit)
