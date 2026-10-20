# SchoolChan Local Demo

Full install and start steps: [LOCAL.md](LOCAL.md).

## Prerequisites

- Java 21, optional Docker (Postgres). Without Docker, use profile `local` via `scripts/run-backend.ps1`.
- Python 3.11+ for the ML sidecar.

## Start

```powershell
cd schoolchan
.\scripts\run-ml.ps1
.\scripts\run-backend.ps1
```

Staff login: `admin` / `change-me-now`.

## Walkthrough

1. **Home** `http://localhost:8080/` — boards + rotating quote.
2. **Post** on `/boards/general` (weekdays 07:00–22:00 Asia/Manila by default).
3. **Bump / sage** — reply bumps; check sage to reply without bump.
4. **Archive** — set a low active-thread cap as admin/seed, create threads until oldest moves to `/boards/general/archive`.
5. **Moderation** — post body containing `TOKEN_BLOCK` is blocked/held; `TOKEN_ESCALATE_THREAT` escalates.
6. **Spoilers** — check spoiler or use `[spoiler=label]text[/spoiler]`; reveal via `<details>`.
7. **Reports** — report a post; enough weighted reports hold it.
8. **Staff** `/staff/login` → dashboard → review (Publish / Block). A block confirms reports, stores a training example, and may list a learned keyword on Settings. Approve it before it is enforced.
9. **Settings** — edit an existing access window (hours or uncheck enabled) or add one. Closed-hours mode, board caps, and rules are on the same page.
10. **Escalations** — open a case for original text, English when present, and timestamps. Print / save as PDF, or download the text package. Reveal IP only with a reason.
11. **ML** — `run-ml.ps1` uses token scores. `run-ml-real.ps1` loads ToxiScan and NLLB. With the sidecar down, text ML follows the fail policy (queue by default).
12. **Eval** — `.\scripts\run-eval.ps1` writes `accuracy.json`, `mcnemar.json`, and `latency.md` under `eval/results/<timestamp>/`.

## Tests

```powershell
cd backend; .\gradlew.bat test
cd ..\ml-service; python -m pytest -q
```
