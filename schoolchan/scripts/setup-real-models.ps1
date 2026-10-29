<#
    Sets up a Python environment for the REAL moderation models.

    The default local path (run-ml.ps1) uses the mock and needs nothing.
    This script creates a separate virtual environment so the mock path stays
    light.

    Requirements: Python 3.11 or 3.12 (onnxruntime has no wheels for 3.13+).

    Usage:  schoolchan\scripts\setup-real-models.ps1
#>

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$mlDir = Join-Path $root "ml-service"
$venv = Join-Path $mlDir ".venv-real"

function Find-Python {
    # onnxruntime has no wheels for Python 3.13+, so 3.11 and 3.12 only.
    if (Get-Command py -ErrorAction SilentlyContinue) {
        foreach ($v in @("3.12", "3.11")) {
            try {
                & py "-$v" --version *> $null
                if ($LASTEXITCODE -eq 0) { return @("py", "-$v") }
            } catch { }
        }
    }
    foreach ($exe in @("python3.12", "python3.11", "python")) {
        try {
            $version = & $exe -c "import sys; print('%d.%d' % sys.version_info[:2])" 2>$null
            if ($version -match '^(3\.(11|12))\.' ) { return @($exe) }
        } catch { }
    }
    throw "Python 3.11 or 3.12 is required for the real models (onnxruntime has no wheels for 3.13+)."
}

$pythonCmd = Find-Python
$python = $pythonCmd[0]
$pythonArgs = @()
if ($pythonCmd.Count -gt 1) { $pythonArgs = $pythonCmd[1..($pythonCmd.Count - 1)] }
Write-Host "Using: $python $($pythonArgs -join ' ')"
& $python @pythonArgs --version

if (-not (Test-Path $venv)) {
    Write-Host "Creating the virtual environment in $venv ..."
    & $python @pythonArgs -m venv $venv
}

$venvPython = Join-Path $venv "Scripts\python.exe"
if (-not (Test-Path $venvPython)) {
    $venvPython = Join-Path $venv "bin/python"
}

Write-Host "Installing the real model dependencies..."
& $venvPython -m pip install --upgrade pip --quiet
& $venvPython -m pip install -r (Join-Path $mlDir "requirements-real.txt") --quiet
# PyTorch is only needed for the NLLB translation model. The CPU wheel is
# smaller than the default one.
Write-Host "Installing the CPU build of PyTorch (for the translation model)..."
& $venvPython -m pip install torch --index-url https://download.pytorch.org/whl/cpu --quiet
& $venvPython -m pip install -r (Join-Path $mlDir "requirements.txt") --quiet

Write-Host ""
Write-Host "Checking the models are reachable..."
& $venvPython (Join-Path $mlDir "scripts\verify_models.py")

Write-Host ""
Write-Host "Done. Start the real sidecar with:"
Write-Host "    $root\scripts\run-ml-real.ps1"
Write-Host ""
Write-Host "The first start downloads the weights into the model cache"
Write-Host "(ML_MODEL_CACHE, default ml-service\models)."
Write-Host "Budget about 3 GB of free RAM for the translation model."
