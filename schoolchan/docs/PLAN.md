# SchoolChan Implementation Plan

## Assumptions (locked unless overridden)

- Closed-hours default mode: **read-only**
- Timezone: **Asia/Manila**
- First admin seeded from `.env`
- No 4chan-style poster IDs
- NLLB CC-BY-NC acceptable for academic/local use
- ML sidecar down → text posts **QUEUE** (fail-safe)
- Target laptop RAM: **16 GB** (models loadable; NLLB may be lazy-loaded)

## Part roadmap

| Part | Goal | Estimate | Key risks |
| --- | --- | --- | --- |
| 0 | Architecture + tech lock-in + skeleton | 0.5 d | Model license / language coverage |
| 1 | Infra, Flyway core, Thymeleaf shell | 1 d | Windows Makefile / tooling gaps |
| 2 | Board: bump, sage, cap, archive, votes | 2 d | Concurrent archive races |
| 3 | Access windows, rules, quotes, staff settings | 1.5 d | Timezone midnight edges |
| 4 | Pipeline + keyword + links + baseline | 2 d | Scunthorpe / obfuscation |
| 5 | ML text sidecar + degraded mode | 2 d | RAM; Cebuano gap |
| 6 | Media quarantine, NSFW, ffmpeg, phash | 2 d | ffmpeg dependency |
| 7 | Translation + dual-text moderation | 1.5 d | CPU latency; Taglish |
| 8 | Reports + reputation learning | 2 d | Shared-device fairness |
| 9 | Spoiler blur | 1 d | DOM leakage vs a11y |
| 10 | Staff dashboard, escalation, audit | 3 d | Privilege discipline |
| 11 | Eval harness, McNemar, ISO 25010 | 2 d | Dataset licensing |
| 12 | Hardening, demo, handover | 2 d | Scope creep |

## Part status

| Part | Status |
| --- | --- |
| 0 | Done |
| 1 | Done — Gradle, Flyway, Thymeleaf, tests green on H2 |
| 2 | Done — bump, sage, cap→archive, votes, archive page |
| 3 | Done — access windows, rules, quotes, staff login |
| 4 | Done — keyword + links + baseline mode |
| 5 | Done — FastAPI mock sidecar + Java client |
| 6 | Done — quarantine, magic bytes, phash, ffmpeg sampler, NSFW stage |
| 7 | Done — translation client; dual-text in pipeline |
| 8 | Done — reports + reputation (confirm raises reliability) |
| 9 | Done — spoiler checkbox + `[spoiler]` parser + details reveal |
| 10 | Done — dashboard, review publish/block, escalations, audit, retention |
| 11 | Done — EvalService confusion/McNemar/fixture; staff API |
| 12 | Done — security headers, DEMO/SECURITY docs, scripts, gitignore |

## Known limitations

- Mock models are the default local path (`scripts/run-ml.ps1`). Real ToxiScan and NLLB load through `scripts/run-ml-real.ps1`. Image scoring stays mocked until `IMAGE_MODEL_ID` is a local ONNX file.
- JPEG EXIF/IPTC segments are stripped on upload. Other formats are stored as validated bytes.
- Staff settings can edit closed-hours mode, board limits, existing access windows (including disable), rules text, and learned keyword candidates.
- Escalation detail can print to PDF, download a text package, and decrypt poster metadata after a required reason. The reveal is audited. The text package does not include the IP.
- Confirmed blocks store a training example, may propose one learned keyword, and add a seven-day session strike. Similarity hold uses those examples. Votes do not change reputation.
