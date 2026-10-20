# Run SchoolChan locally

**Main approach: embedded H2.** The board stores data in a file next to the app. No Docker, no Postgres process, and roughly a few hundred MB of RAM for Java plus the mock ML sidecar. This is what `scripts/run-backend.ps1` starts.

**Secondary: Docker Postgres.** Heavier (Docker Desktop often uses 1–2 GB before the database). Use it only if you want the same engine as a later server deploy. See [Optional: Docker Postgres](#optional-docker-postgres).

## 1. What you need

| Tool | Required? | Notes |
| --- | --- | --- |
| Java 21 | Yes | A portable JDK may already be at `../tools/jdk-21` (one folder above `schoolchan`). |
| Python 3.11+ | Yes for the ML sidecar | `python --version` |
| Docker Desktop | No | Skip it for day-to-day local use. |

From PowerShell, confirm Java if it is already on PATH:

```powershell
java -version
```

If that fails, point at the portable JDK (adjust the path if your folder layout differs):

```powershell
$env:JAVA_HOME = "\Desktop\AI Agents\md\tools\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
```

## 2. Start the ML sidecar (mock models — default)

Open a terminal in the `schoolchan` folder:

```powershell
cd "\Desktop\AI Agents\md\schoolchan"
pip install -r ml-service\requirements.txt
.\scripts\run-ml.ps1
```

Leave that window open. Health check: http://localhost:8090/health

Mock mode is the default (`MOCK_MODE=true`). Posts that contain tokens such as `TOKEN_BLOCK`, `TOKEN_TOXIC`, or `TOKEN_ESCALATE_THREAT` exercise moderation without downloading large models.

Real text and translation weights are a separate start script. See [Optional: real models](#optional-real-models). Image scoring stays on the mock until you point `IMAGE_MODEL_ID` at a local ONNX file.

## 3. Start the board (embedded H2 — main path)

Open a second terminal:

```powershell
cd "\Desktop\AI Agents\md\schoolchan"
.\scripts\run-backend.ps1
```

That script uses Spring profile `local`:

- Database file: `schoolchan/backend/data/schoolchan.mv.db` (created on first run)
- App: http://localhost:8080
- Staff login: http://localhost:8080/staff/login
- Username: `admin`
- Password: `change-me-now` (change via `SCHOOLCHAN_ADMIN_PASSWORD` before a real demo)

First startup downloads Gradle dependencies and can take a few minutes.

## 4. What to click

1. Home lists boards (General, Academics, Events) and a quote.
2. Open `/boards/general` and create a thread.
3. Reply with sage unchecked to bump; check sage to reply without bumping.
4. Post text containing `TOKEN_BLOCK` to see a hold or block.
5. Staff → Review to publish or block. Blocking a post confirms its reports, stores a training example, and may propose a learned keyword under Staff → Settings. Approve that keyword before it is enforced.
6. Staff → Settings to change board caps, closed-hours mode, and rules. Each existing access window is an edit form: change the hours or uncheck **enabled**, then **Save window**. Use **Add window** for a new one.
7. A post containing `TOKEN_ESCALATE_THREAT` lands in Escalations. Open it to see the original text, any English translation, and timestamps. **Print / save as PDF** uses the browser print dialog. **Download text package** saves a `.txt` file that does not include the poster IP. Enter a reason and reveal IP/session only when you need it. That access is written to the audit log.

Weekday hours are seeded as 07:00–22:00 Asia/Manila. Outside that window the board is read-only unless you edit a window so it includes “now”, or add one, under Staff → Settings.

## 5. Tests and benchmark export

```powershell
cd schoolchan\backend
$env:JAVA_HOME = "..\..\tools\jdk-21"
.\gradlew.bat test

cd ..\ml-service
python -m pytest -q
```

If Gradle says a class file is “not a regular file”, OneDrive has locked `backend\build`. Close the app, delete that folder, and run the tests again:

```powershell
cd schoolchan\backend
cmd /c "rmdir /s /q build"
.\gradlew.bat test --offline
```

Benchmark files (accuracy, McNemar, fixture latency) are written by a one-shot run. It uses the same embedded H2 profile, then exits:

```powershell
cd schoolchan
.\scripts\run-eval.ps1
```

Results land in `schoolchan\eval\results\<timestamp>\` as `accuracy.json`, `mcnemar.json`, and `latency.md`. That folder is gitignored. On macOS or Linux the same flag is `make eval`.

## Optional: Docker Postgres

Skip this unless you specifically want PostgreSQL. Docker Desktop is the heavy option.

```powershell
cd schoolchan
docker compose up -d postgres
cd backend
$env:JAVA_HOME = "..\..\tools\jdk-21"
.\gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

`dev` uses Flyway against `localhost:5432` (user/password/db `schoolchan`). Copy `.env.example` if you want to override credentials.

## Optional: real models

Day-to-day local use stays on mocks (`.\scripts\run-ml.ps1`). Real weights are large. NLLB translation weights are CC-BY-NC (academic use). The script does not download an image model.

From the `schoolchan` folder, in place of `run-ml.ps1`:

```powershell
.\scripts\run-ml-real.ps1
```

That sets `MOCK_MODE=false` and loads:

| Setting | Default | Role |
| --- | --- | --- |
| `TEXT_MODEL_ID` | `vectorsense/toxiscan` | Text scores. Hugging Face labels are mapped onto toxicity, hate, harassment, sexual, threat, and self-harm. |
| `TRANSLATE_MODEL_ID` | `facebook/nllb-200-distilled-600M` | Tagalog (`tgl_Latn`) and Cebuano (`ceb_Latn`) into English (`eng_Latn`). |
| `IMAGE_MODEL_ID` | `schoolchan-image-mock-v1` | Stays on the mock. Set this to a local `.onnx` path when you have one. |

The first start downloads text and translation weights into the Hugging Face cache. If a model fails to load, `/health` reports a warning and that stage falls back to the mock. The Java app still runs if the sidecar is stopped; text ML then follows the configured fail policy (queue by default).

Keep the board on the light path in a second terminal:

```powershell
.\scripts\run-backend.ps1
```
