<#
    SchoolChan database backup.

    Copies the H2 database file to backend/data/backups/ with a timestamp.
    Run this before experimenting with the database.

    Usage:  schoolchan\scripts\backup-db.ps1
#>

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$dataDir = Join-Path $root "backend\data"
$backupDir = Join-Path $dataDir "backups"

if (-not (Test-Path $dataDir)) {
    Write-Host "No database found at $dataDir - nothing to back up yet."
    exit 0
}

if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
}

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$copied = 0
Get-ChildItem -Path $dataDir -Filter "schoolchan*.mv.db" -ErrorAction SilentlyContinue | ForEach-Object {
    $target = Join-Path $backupDir "$($_.BaseName)-$stamp.mv.db"
    Copy-Item $_.FullName $target -Force
    Write-Host "  saved $($_.Name) -> $target"
    $copied++
}
if ($copied -eq 0) {
    Write-Host "No H2 database file found yet."
} else {
    Write-Host "Backup complete. Copies are in $backupDir"
}
