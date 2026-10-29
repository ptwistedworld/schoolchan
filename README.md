# schoolchan

Workspace for **SchoolChan**, an anonymous, account-free discussion board built as a local-first capstone project.

| Path | What it is |
| --- | --- |
| `build` | The multi-part coding-agent build prompt used to create SchoolChan |
| `schoolchan/` | The application: Spring Boot backend, FastAPI moderation sidecar, docs, and scripts. Start with [schoolchan/README.md](schoolchan/README.md) |
| `tools/generate_schoolchan.py` | Script that generates the SchoolChan source tree |
| `tools/jdk-21/`, `tools/gradle-8.10.2/` | **Not in this repo.** Local toolchains you need to download (see below) |

## Requirements

The JDK and Gradle are too large to keep in Git, so they are excluded. Download them before you build or run SchoolChan.

### 1. Java 21 (required)

1. Download **Eclipse Temurin JDK 21** (Windows x64, `.zip`) from https://adoptium.net/temurin/releases/?version=21
2. Extract it so the JDK lives at `tools/jdk-21/`. The folder should contain `bin/java.exe` directly, so rename the extracted folder (for example `jdk-21.0.x+y`) to `jdk-21`.

The scripts in `schoolchan/scripts/` and the `schoolchan/Makefile` look for `tools/jdk-21` and use it automatically. If Java 21 is already installed and on your `PATH`, you can skip this step.

Check it:

```powershell
.\tools\jdk-21\bin\java -version
```

### 2. Gradle 8.10.2 (optional)

The backend ships with the Gradle wrapper (`schoolchan/backend/gradlew.bat`), which downloads Gradle 8.10.2 automatically the first time it runs. You only need a manual download if you are offline or your network blocks that download:

1. Download https://services.gradle.org/distributions/gradle-8.10.2-bin.zip
2. Extract it to `tools/gradle-8.10.2/`

### 3. Python 3.11+ (for the moderation sidecar)

```powershell
python --version
pip install -r schoolchan\ml-service\requirements.txt
```

## Expected layout after setup

```
md/
├── md
├── README.md
├── schoolchan/
└── tools/
    ├── generate_schoolchan.py
    ├── jdk-21/            (downloaded)
    └── gradle-8.10.2/     (downloaded, optional)
```

## Run it

See [schoolchan/docs/LOCAL.md](schoolchan/docs/LOCAL.md). In short, from the `schoolchan` folder in two terminals:

```powershell
.\scripts\run-ml.ps1
.\scripts\run-backend.ps1
```

Then open http://localhost:8080.

## GitHub upload checklist

Upload the source and docs. Do **not** upload generated or machine-specific files; the `.gitignore` files already exclude them.

### Upload these
- `schoolchan/` application source, templates, static assets, docs, scripts, and tests
- `schoolchan/backend/gradle/wrapper/gradle-wrapper.jar` and `gradlew` / `gradlew.bat`
- `schoolchan/.env.example`
- `schoolchan/docker-compose.yml`, `Makefile`, `AGENTS.md`
- `schoolchan/.github/` if present
- `tools/generate_schoolchan.py` (optional helper)
- `README.md`, `.gitignore`, `.github/`

### Skip these (already generated or downloaded locally)
- `tools/jdk-21/` — download Eclipse Temurin JDK 21 and extract to `tools/jdk-21/`
- `tools/gradle-8.10.2/` — optional; the Gradle wrapper downloads Gradle automatically
- `schoolchan/backend/build/` and `schoolchan/backend/.gradle/`
- `schoolchan/backend/data/` — H2 database and media; the app seeds sample content on first run
- `schoolchan/ml-service/.venv/`, `ml-service/.venv-real/`, `__pycache__/`
- `schoolchan/eval/results/`
- `.env`, `*.pem`, `*.key`, IDE files, OS junk, cookies, and build artifacts

### Required folder names after clone
| Dependency | Folder | Name inside it |
| --- | --- | --- |
| Java 21 | `tools/jdk-21/` | `bin/java.exe` (or `bin/java`) |
| Gradle 8.10.2 (optional) | `tools/gradle-8.10.2/` | `bin/gradle` |
| Python environment | created by setup script | `schoolchan/ml-service/.venv/` or `.venv-real/` |
| Database | created on first run | `schoolchan/backend/data/schoolchan.mv.db` |
| Media uploads | created on first run | `schoolchan/backend/data/media/` |
| Environment file | copy from example | `schoolchan/.env` |

If Java 21 is already installed and on `PATH`, the `tools/jdk-21` folder is not needed.
