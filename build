# SchoolChan: Coding Agent Build Prompt (Local-First, Multi-Part)

**How to use this file**

1. Paste the **MASTER PROMPT** into your agent once, at the start. Save it in the repo as `AGENTS.md` (or `CLAUDE.md`) so it persists across sessions.
2. Feed the **Parts one at a time**, starting with Part 0. Each Part ends with a stop-and-report gate. Do not start the next Part until you've reviewed the report and answered the agent's questions.
3. When a Part's design decisions change something, tell the agent to update `docs/PLAN.md` and `docs/DECISIONS.md` before moving on.

The Parts follow the capstone's own sprint order (core board, keyword filter, NLP, image/video, translation, reputation, spoilers), plus the cross-cutting pieces the paper requires (access windows, escalation, admin tools, benchmark harness).

---

# MASTER PROMPT (paste once)

You are the lead engineer building **SchoolChan**, an anonymous, account-free, non-ephemeral discussion board for a school community (St. Joseph's Academy of Las Piñas, Senior High School). It is a capstone project. Build it **locally first**: everything must run on a developer laptop with no paid services and no cloud accounts. Deployment comes later and is out of scope for now.

## 1. Product summary (source of truth: the capstone paper)

SchoolChan is an imageboard-style forum with these defining properties:

- **Anonymous, account-free.** No registration, password, or profile. Every post is labeled "Anonymous". Users never see each other's identity.
- **Non-ephemeral.** Unlike 4chan, threads are never deleted for inactivity. When a thread is pushed off the active board it moves to a **permanent, publicly browsable archive**.
- **Bump system.**
  - Each reply to a thread "bumps" it to the top of its board (updates its bump timestamp).
  - Each board has a **fixed maximum number of active threads**. When a new thread is created and the cap is reached, the thread with the oldest bump is archived.
  - Each thread has a **bump limit** (default 300, configurable per board in the 300–500 range). After the limit, replies no longer bump it; it sinks naturally and is eventually archived.
  - Each thread also has an **image limit** (configurable per board).
  - Bump limit, image limit, and active-thread cap are admin-configurable parameters, not hardcoded.
- **Comment voting.** Readers can upvote/downvote individual comments. This is separate from bumping (bumping is driven by posting activity, not votes).
- **Layered moderation pipeline** (the central engineering problem):
  1. Keyword and blacklist matching
  2. NLP/ML text classification (toxicity, hate speech; must handle English, Filipino/Tagalog, Cebuano, and Taglish code-switching)
  3. Image and video screening (explicit/NSFW content)
  4. Scam and malicious-link detection
  5. Community reporting with **reputation-weighted** learning (confirmed reports improve future detection)
  6. Decision by confidence: high confidence = block; medium = queue for human review; low = publish
- **Automatic translation** of posts into a common language (English) for moderation and readability, **always retaining the original text**. Supported languages: Filipino (Tagalog), Cebuano, English.
- **Spoiler tagging.** Users can voluntarily tag a post as a spoiler; spoiler content is blurred (text and media) until the viewer clicks to reveal.
- **Access windows.** Admins define time windows (e.g., recess, lunch, after-class) when the board is open. Outside windows, the board is either unavailable or read-only (admin-configurable). Enforced at the application level.
- **Rules and conditions page** reachable from the board, covering acceptable use and the escalation pathway.
- **Rotating quote** shown each time the app is opened.
- **Clean bulletin-board UI.** Minimal clutter, clear thread ordering, straightforward posting controls. Responsive website, not a native app.
- **Moderators/admins are anonymous to users**, are the only people who can see IP/session metadata, and are themselves open to public criticism on the board. The code of conduct prohibits retaliation and any use of IP/session data beyond abuse investigation and escalation.
- **Safety escalation.** Posts that read as credible bomb threats or suicidal intent are routed to a restricted escalation queue so admins can forward the post plus IP to school authorities (administration / guidance office). The system does not contact anyone automatically; it produces an escalation package for a human to act on. This pathway must be disclosed in the rules page.
- **Evaluation support.** The paper evaluates (a) accuracy and response time of the hybrid pipeline versus a keyword-only baseline (McNemar's test) and (b) ISO/IEC 25010 quality (functional suitability, performance efficiency, usability, security). The app must therefore include a **benchmark harness** and **per-stage latency logging**.

## 2. Hard constraints

- **Local-first and free.** No paid APIs, no required cloud keys. Anything that would normally use a paid or keyed service (e.g., Google Safe Browsing, hosted translation) must sit behind an interface with a **local/offline default implementation**, and an optional adapter can be added later.
- **Backend language: Java** (the paper commits to a Java backend). Use **Java 21 + Spring Boot 3.x**. ML and media-analysis work may live in a **separate Python sidecar service** (FastAPI) called over HTTP by the Java backend, because the relevant models are Python-native. Keep the sidecar optional behind a client interface so the board still runs (with ML stages marked "unavailable, degraded") if the sidecar is down.
- **Frontend: server-rendered** (Thymeleaf) with **htmx** for interactivity and light vanilla JS. No SPA framework. Keep the look plain bulletin-board: small, fast, readable.
- **Database: PostgreSQL via Docker Compose** for the main app, with an **H2 in-memory profile** for quick tests. Use **Flyway** migrations. Never edit an applied migration; add a new one.
- **One command to run.** `docker compose up -d` for infra and a documented command for the app and sidecar. Provide a `Makefile` or scripts (`make up`, `make run`, `make test`, `make seed`).
- **Secrets and config** via environment variables with a documented `.env.example`. Nothing sensitive committed.
- **No real harmful content.** For testing, use only placeholder strings, synthetic fixtures, or published academic benchmark datasets that are clearly licensed for research. Never generate, download, or commit real explicit imagery, real threats, or real abuse content. Use mocks and tiny synthetic fixtures in automated tests.

## 3. Privacy, security, and ethics requirements (non-negotiable)

- **Anonymity toward users is absolute.** No API response, HTML, or log visible to ordinary users may expose IP, session ID, or any stable identifier of a poster. Do not show poster IDs (no 4chan-style per-thread IDs unless I explicitly ask).
- **IP/session metadata** is collected **only** for abuse investigation and escalation. Store it in a separate restricted table, encrypted at rest at the field level (AES-GCM with a key from env), visible only to authenticated moderator/admin roles, with every access written to an **append-only audit log**. Define a retention period (configurable, default 90 days) with a scheduled purge job, except for records attached to an open escalation.
- **Moderator/admin auth.** Staff accounts are the *only* accounts in the system (ordinary users still have none). Use Spring Security, BCrypt, role-based access (MODERATOR, ADMIN), CSRF protection, login rate limiting. Seed a first admin from env vars.
- **Fail-safe media handling.** Uploaded images/videos are held in a **quarantine** state until screened. Anything flagged explicit is never served to users. Strip EXIF metadata on all uploads. Validate file type by content (magic bytes), not extension; enforce size limits; randomize stored filenames; serve uploads from a non-executable path.
- **Abuse resistance.** Rate-limit posting per session and per IP, add a simple honeypot field, enforce max lengths, and escape/sanitize all output (prevent XSS). Parameterized queries only.
- **Data Privacy Act of 2012 (RA 10173).** Keep data minimal, document what is stored and why in `docs/PRIVACY.md`, and surface the key points on the rules page. Note in docs that this is a design-level measure and not a substitute for the school's legal review.
- **Automated flags are a starting point, not a verdict.** Only high-confidence results auto-block; borderline results go to human review. Record the reason and scores for every moderation decision so it can be explained and audited.

## 4. Working protocol (how you must operate)

1. **Plan before you code.** For each Part, first write or update the relevant section of `docs/PLAN.md`, list assumptions, and list any questions for me. If a question is blocking, ask it and wait. If not, state your assumption, proceed, and record it in `docs/DECISIONS.md`.
2. **Small, verifiable steps.** Work in small commits with clear messages. After each meaningful step, run the build and tests.
3. **Tests are part of "done".** Every Part ships with unit tests for new logic and at least one integration test for its main flow. Do not report a Part complete if `make test` fails.
4. **Stop-and-report gate.** At the end of each Part, stop and give me:
   - what was built (short),
   - how to run and manually verify it (exact commands, URLs, what I should see),
   - test results,
   - deviations from the plan and why,
   - open questions and risks,
   - a proposed scope for the next Part.
   Then **wait for my go-ahead**. Do not start the next Part on your own.
5. **Do not silently expand scope.** If you think something extra is needed, propose it in the report instead of building it.
6. **Be honest about limits.** If a model, library, or approach doesn't work well (for example, poor Cebuano accuracy), say so plainly in the report and propose alternatives. Never fake a passing result. Verify any model, package name, or version actually exists before depending on it. Do not guess at model IDs or APIs from memory.
7. **Keep docs alive.** Maintain `README.md` (quickstart), `docs/PLAN.md`, `docs/DECISIONS.md`, `docs/ARCHITECTURE.md`, `docs/PRIVACY.md`, and `docs/API.md` as the project grows.

## 5. Target repository layout (adjust if you have a good reason, and record it)

```
schoolchan/
  AGENTS.md
  README.md
  Makefile
  docker-compose.yml
  .env.example
  docs/                  (PLAN, DECISIONS, ARCHITECTURE, PRIVACY, API, EVALUATION)
  backend/               (Spring Boot, Java 21, Gradle or Maven; pick one and record why)
    src/main/java/.../
      board/             (boards, threads, posts, bump, archive)
      access/            (access windows)
      moderation/        (pipeline, stages, decisions, review queue, escalation)
      reputation/        (reports, votes, reputation weighting)
      translation/
      media/             (upload, quarantine, serving)
      staff/             (auth, roles, audit log)
      eval/              (benchmark harness, latency metrics)
      web/               (controllers, templates, static)
    src/main/resources/db/migration/
    src/test/
  ml-service/            (Python FastAPI sidecar: text classifier, NSFW, translation)
    app/  tests/  requirements.txt  Dockerfile
  data/
    blacklists/  quotes/  fixtures/
```

## 6. Definition of done for the whole project

A school admin can run the stack locally, define access windows and board limits, and students (simulated) can post text/images/video anonymously across boards. Every post passes through the moderation pipeline with explainable results. Threads bump, cap, and archive correctly. Posts are translated with originals preserved. Reports and votes feed a reputation system that measurably affects future filtering. Spoilers blur. Moderators have a review queue and a restricted escalation queue. The benchmark harness can run the hybrid pipeline and a keyword-only baseline on a labeled dataset and export accuracy, a McNemar contingency table, and latency statistics.

Acknowledge that you've read this, restate the plan in your own words in under 15 lines, and wait for **Part 0**.

---

# PART 0: Planning and Architecture (no feature code yet)

**Goal:** Produce a plan I can review and approve before any feature work.

**Do this:**

1. Read the Master Prompt and restate the system in your own words, flagging anything ambiguous or contradictory.
2. Produce `docs/ARCHITECTURE.md` with:
   - a component diagram (Mermaid) covering backend, DB, ML sidecar, media storage, moderation pipeline stages, and staff dashboard,
   - the **post lifecycle** as a Mermaid sequence diagram: submit, access-window check, rate limit, quarantine, pipeline stages, decision (block / queue / publish), translation, bump, archive,
   - the **thread lifecycle** state machine: active, sinking (past bump limit), archived,
   - the data model as an ER diagram (boards, threads, posts, media, moderation_results, reports, votes, reputation entries, blacklist entries, access_windows, staff, audit_log, escalations, poster_metadata, quotes, site_settings).
3. Produce `docs/PLAN.md` with the Part-by-Part roadmap below, refined with estimates and risks.
4. Propose **concrete technology choices** for each of these, with 2 candidates and a recommendation each, constrained by "free, runs locally on a laptop, CPU-only acceptable":
   - multilingual toxicity/hate-speech text model (must cover English, Tagalog, Cebuano, Taglish; verify the model exists and its license and size before recommending),
   - NSFW image classifier,
   - video screening approach (frame sampling plus the image classifier),
   - translation model (must cover Tagalog and Cebuano to English; verify language coverage),
   - language detection,
   - malicious-link detection offline approach,
   - build tool (Gradle vs Maven).
   For each, note expected size, speed on CPU, licensing, and known weaknesses (for example, Taglish).
5. Define the **moderation decision policy**: score ranges, how stage scores combine, thresholds for block/queue/publish, how reputation shifts thresholds, and which categories exist (hate/toxicity, explicit, scam link, self-harm/violence-threat escalation, spoiler suggestion). Keep all thresholds in config, not code.
6. Define the **reputation model** concretely (see Part 8 for the intent) and explain how it prevents any single user from gaining outsized influence, as the paper's Slashdot-based design requires.
7. List all open questions for me, grouped as "blocking" and "can assume".

**Acceptance:** Docs written, diagrams render, choices justified. No application code beyond an empty repo skeleton and `AGENTS.md`.

**Stop and report. Wait for my approval and answers.**

---

# PART 1: Scaffold, Infrastructure, and Core Data Model

**Goal:** A running skeleton with the database, migrations, config, and test harness.

**Build:**

- Repo layout from section 5, `AGENTS.md` populated from the Master Prompt.
- `docker-compose.yml` (PostgreSQL), `.env.example`, Makefile targets (`up`, `down`, `run`, `test`, `seed`, `lint`).
- Spring Boot app with profiles: `dev` (Postgres), `test` (H2 or Testcontainers; pick one and record why).
- Flyway migrations for the core schema: boards, threads, posts, media, site_settings, quotes. (Moderation, reputation, and staff tables come in their own Parts, but design IDs and foreign keys now so they slot in cleanly.)
- Typed configuration classes (`@ConfigurationProperties`) for board limits, thresholds, and feature flags, with sensible defaults.
- Base Thymeleaf layout (header, footer, rules link, placeholder for the rotating quote), static assets, a minimal CSS file with a plain bulletin-board look. Support light/dark via `prefers-color-scheme`.
- Health endpoint, structured logging (never log post content at INFO; never log IPs outside the restricted store), global error pages.
- CI-style local check: `make test` runs unit tests; add a smoke test that boots the context.

**Acceptance:** `make up && make run` shows a styled empty home page; `make test` passes; migrations apply cleanly on a fresh DB.

**Stop and report.**

---

# PART 2: Core Board (Sprint 1): Posting, Threading, Bump, Archive

**Goal:** A fully working anonymous board with no moderation yet.

**Build:**

- Board index page listing boards; board page showing active threads ordered by `bumped_at` desc; thread page with posts in order.
- Create thread (subject optional, body required, optional image upload placeholder; real media handling comes in Part 6, so use a stub that accepts and stores images in a plain safe directory for now), and reply.
- All posts labeled "Anonymous". Post numbers per board (e.g., `>>123`) with **quote-linking** (`>>123` creates a link and a hover preview via htmx) and basic greentext (`>text`). Escape all output; allow no raw HTML.
- **Bump logic:** reply bumps the thread unless the thread's post count has reached its `bump_limit`; support `sage` (reply without bump) as a checkbox. Respect `image_limit`.
- **Active-thread cap:** when creating a thread on a full board, archive the thread with the oldest `bumped_at` in the same transaction.
- **Archive:** archived threads are read-only, never deleted, browsable at `/{board}/archive` with pagination and basic search by subject. Threads past the bump limit stay active and sink until the cap pushes them out.
- **Comment voting:** up/down per post, one vote per session per post (anonymous session cookie, no account). Voting does **not** affect bump order. Show score on each post.
- Anonymous session identifier: a random token in an HttpOnly, SameSite cookie, used only for vote de-duplication and rate limiting. Store only a **salted hash** server-side. The session token must never be rendered into pages.
- Pagination, reply count, last-bump display, and a "catalog" view (grid of thread cards) if cheap to do.
- Seed script: 3 sample boards (for example, General, Academics, Events) with configurable limits, and fake threads for demos.

**Tests:** bump ordering, bump-limit stops bumping, sage, cap triggers archive of the correct thread, archived threads reject new replies, vote de-dup, XSS escaping, quote-link parsing.

**Acceptance:** I can create threads and replies in a browser, watch bump and cap/archive behavior with small limits set via config, and browse the archive.

**Stop and report.**

---

# PART 3: Access Windows, Rules Page, Rotating Quote, Admin Settings

**Goal:** The time-gated, school-specific shell.

**Build:**

- **Access windows:** table of windows (day-of-week set, start/end time, label like "Recess", timezone default `Asia/Manila`). A request-time filter evaluates "is the board open now". Two admin-selectable modes for closed hours: **unavailable** (friendly closed page showing the next opening time) or **read-only** (browse allowed, posting/voting disabled). Server-side enforcement on every write endpoint, not just UI hiding. Include a global **admin override** ("open now" / "close now") with an expiry.
- **Rules and conditions page** at `/rules` with clearly written sections: acceptable use, prohibited content, how moderation works (automated plus human review), reputation and reports, the **escalation pathway** (what triggers it, that the post and its IP may be forwarded to school authorities, and that this is limited to genuine safety risks), moderator conduct (no retaliation, no misuse of IP data), data retention, and Data Privacy Act notes. Make the text editable by admins from the settings area (versioned), with a plain-language draft that I can hand to the school for review.
- **Rotating quote:** a curated quotes table and loader (seed ~30 appropriate, school-friendly, public-domain or original quotes; do not copy copyrighted quote collections). Show a different quote on each app open (pick randomly on the home page load, avoid immediate repeats per session).
- **Staff login** scaffolding (Spring Security) and a minimal admin settings page for: access windows, closed-hours mode, board limits (active thread cap, bump limit within 300–500, image limit), rules text, and quotes. Full moderator tooling arrives in Part 10; here we just need the settings surface and auth.
- Ensure staff routes are separated (`/staff/**`) and ordinary routes never require auth.

**Tests:** window evaluation across edge cases (midnight, overlapping windows, timezone), closed-mode behavior for GET and POST, override expiry, role access, validation of limit ranges.

**Acceptance:** I can define a window that excludes "now" and see the board close or go read-only; I can edit rules and limits as admin.

**Stop and report.**

---

# PART 4: Moderation Pipeline Framework + Keyword/Blacklist + Link Screening (Sprint 2)

**Goal:** The pluggable pipeline backbone and the first two stages.

**Build:**

- **Pipeline architecture:** a `ModerationStage` interface (`name`, `supports(content type)`, `evaluate(content) -> StageResult{score, category, evidence, latencyMs}`), an orchestrator that runs stages in a configured order (cheap stages first, short-circuit on definitive block), and a **Decision Engine** applying thresholds from config to produce `BLOCK`, `QUEUE_FOR_REVIEW`, `PUBLISH`, or `ESCALATE`. Persist a `moderation_result` row for every submission with all stage results, final decision, and reasons. Record **per-stage and total latency**.
- **Post state machine:** `PENDING_SCREENING` → `PUBLISHED` | `HELD_FOR_REVIEW` | `BLOCKED`. Posts are not visible to others until screened. Decide and document whether screening is synchronous (default for text, with a timeout and a configurable fail-open or fail-closed policy; recommend and justify your default) and whether media screening is async.
- **Stage 1: Keyword/blacklist.** Database-backed blacklist entries (exact word, phrase, and regex types) with category and severity, normalization (case folding, accent stripping, leetspeak/obfuscation handling like `h4te`, repeated characters, spaced-out letters, Unicode confusables), and word-boundary correctness so it avoids the Scunthorpe problem. Provide starter lists for English, Tagalog, and Cebuano slurs/profanity/threat phrases **as placeholder-quality seed files that I will review**; do not fabricate an exhaustive list. Admin can add/remove entries (UI comes in Part 10; for now, a CSV import command is enough).
- **Stage 2: Link screening.** Extract URLs; check against a local blocklist (importable list of known scam/phishing/malware domains; make the importer accept a standard hosts/domain-list format), detect lookalike/typosquat domains of a configurable protected list (for example, the school's own domain), URL shorteners (flag for review), IP-literal URLs, suspicious TLDs, punycode/homograph tricks, and excessive links. Put the external-reputation lookup behind an interface with a no-op default so a keyed provider can be added later.
- **Escalation categories** are recognized here only by keyword/phrase patterns for now (for example, explicit threat or self-harm phrases); the full escalation queue arrives in Part 10. For now such a post becomes `HELD_FOR_REVIEW` with category `ESCALATION_CANDIDATE` and is hidden.
- **Keyword-only baseline mode:** a config flag that makes the pipeline use **only Stage 1** so the evaluation in Part 11 can compare it fairly. Build this switch into the orchestrator now.

**Tests:** normalization cases, Scunthorpe cases, obfuscation cases, URL edge cases, orchestrator short-circuit, decision thresholds, latency recorded, baseline-mode isolation.

**Acceptance:** Posting a blacklisted word is blocked with a user-friendly, non-revealing message; a borderline link is held; a clean post publishes; the DB shows full results per submission.

**Stop and report.**

---

# PART 5: NLP/ML Text Classification Sidecar (Sprint 3)

**Goal:** Contextual text moderation for English, Filipino, Cebuano, and Taglish.

**Build:**

- **ML sidecar** (`ml-service/`, FastAPI) with `POST /v1/text/classify` returning per-category scores (for example: toxicity, hate, harassment/bullying, sexual content, threat/violence, self-harm) and the model identifier/version. Include `/health` and a warm-up on startup, batch support, and a request timeout contract. Use the model(s) chosen and approved in Part 0 and **verify each model actually loads and runs** before relying on it; if the chosen model doesn't cover a category, say so and propose a fallback instead of pretending.
- **Java client** with timeouts, retry policy, and circuit breaker; on failure, follow the configured degraded-mode policy and mark the moderation result `ML_UNAVAILABLE` so it's visible in staff tools.
- **Stage 3 in the pipeline:** call the sidecar (using the *translated-to-English text when available* once Part 7 lands; for now use original text), map outputs into the common `StageResult`, and feed the Decision Engine. Make thresholds per category and configurable.
- **Taglish handling:** run the classifier on the original text and, later, on the translation; take the max/ensemble per config. Document the approach and its known weaknesses.
- **Calibration notes:** include a small script that runs the classifier over a tiny synthetic fixture file and prints score distributions, so thresholds can be tuned later with real benchmark data in Part 11.
- Dockerfile and Makefile targets for the sidecar; `make run-ml`. Document RAM/CPU needs and first-run model download behavior (offline after first download, cache directory configurable).

**Tests:** sidecar unit tests with a mocked model; Java client tests against a WireMock/stub; pipeline integration test showing ML escalation of a held post; degraded-mode test with sidecar down.

**Acceptance:** With the sidecar running, a clearly toxic synthetic test string is held or blocked per thresholds and the staff-visible result shows scores; with the sidecar stopped, the board still works in the documented degraded mode.

**Stop and report.**

---

# PART 6: Image and Video Screening + Safe Media Handling (Sprint 4)

**Goal:** Safe uploads with explicit-content screening.

**Build:**

- **Upload pipeline:** accept JPEG/PNG/GIF/WebP images and a limited set of video formats (MP4/WebM). Magic-byte validation, size and dimension limits, EXIF stripping, re-encoding or sanitizing where practical, randomized filenames, thumbnails, and storage in a non-web-executable directory abstracted behind a `MediaStore` interface (local disk now; object storage later).
- **Quarantine:** media is stored in quarantine and served only after it passes screening. While pending, the post shows a "media pending review" placeholder.
- **Image stage:** call the sidecar `POST /v1/image/classify` (NSFW/explicit classifier chosen in Part 0; verify it runs on CPU). Return scores per category and an overall explicit score.
- **Video stage:** server-side frame sampling with `ffmpeg` (document the dependency; fail clearly if missing), classify sampled frames (configurable count, plus first/middle/last frames), take the maximum explicit score, and enforce max duration and file size.
- **Decision rules:** high explicit score = block and delete the media file (keep only a non-viewable audit record: hash, scores, timestamp); medium = hold for human review where staff can view through a **blurred-by-default** preview with click-to-reveal; low = release.
- **Perceptual-hash blocklist:** compute a perceptual hash for every blocked/confirmed-violating image and auto-hold or block future near-duplicates. (This is the "learning from confirmed reports" hook that Part 8 will use.)
- **Image limit enforcement** per thread from Part 2 now uses real counts.
- No explicit test images. Use synthetic or benign fixtures and **mock the classifier** in tests; provide a manual test note explaining how I can safely verify with benign images only.

**Tests:** magic-byte spoofing rejected, oversize rejected, EXIF stripped, quarantine never served, decision-threshold behavior with a mocked classifier, phash near-duplicate matching with benign images, ffmpeg frame-sampling on a tiny generated video.

**Acceptance:** A benign image uploads, is screened, and appears. A mocked-explicit result blocks and removes the file. A spoofed file type is rejected.

**Stop and report.**

---

# PART 7: Translation Layer (Sprint 5)

**Goal:** Normalize multilingual posts to English without losing the original.

**Build:**

- **Language detection** (handle Tagalog, Cebuano, English, and mixed Taglish; record detected language and confidence; treat low confidence/short text sensibly).
- **Translation service** in the sidecar (`POST /v1/translate`) using the model approved in Part 0 (verify Tagalog and Cebuano coverage), exposed via a Java `TranslationService` interface so the engine can be swapped.
- **Storage:** every post keeps `original_text` (immutable), `detected_language`, `translated_text` (English), `translation_model`, and `translation_confidence`. The UI shows the **original by default** with a "Show English translation" toggle (htmx), and clearly labels machine translation as approximate.
- **Pipeline integration:** moderation stages that benefit from English (the ML text stage, optionally keyword matching against an English list) run on **both** original and translated text per config. Record which text triggered the flag.
- **Caching and performance:** cache translations by content hash; skip translation for text already detected as English; time-box translation so it can't stall posting (async fallback: publish after other stages pass, then attach the translation when ready, and document this behavior).
- **Translation of post quotes** (`>>123` references) must be preserved untouched.

**Tests:** detection across fixtures in each language, mixed-language handling, original immutability, cache hit, timeout fallback, UI toggle.

**Acceptance:** Posting a Tagalog sentence stores the original, shows an English translation toggle, and the moderation record shows which version was screened.

**Stop and report.**

---

# PART 8: Reporting, Voting Integration, and Reputation Learning (Sprint 6)

**Goal:** The community-driven feedback loop that adapts filtering.

**Build:**

- **User reports:** any visitor can report a post (reason categories plus optional note); one report per session per post; rate-limited. Reports go to the review queue (UI in Part 10).
- **Reputation model (anonymous-friendly):** since there are no accounts, reputation attaches to the **hashed anonymous session identity** (and, as a weak secondary signal, a hashed network identifier), never displayed to anyone. Each session has a reporter-reliability score that rises when its reports are **confirmed by staff** and falls when they're rejected, with decay over time, a floor and a cap, and a rule that **no single session can ever outweigh the cap** (per the Slashdot-style design in the paper). A post's "report weight" is the reliability-weighted sum of its reports; crossing a configurable threshold auto-holds the post for review.
- **Closing the loop:** when staff confirm a violation:
  - adjust reporter reliability up, and adjust rejected reporters down,
  - add the post's normalized text patterns to a **learned-blacklist candidate** table (staff-approvable, never auto-activated without a confidence rule you define and document),
  - add the media perceptual hash to the blocklist (from Part 6),
  - store the confirmed post (text embedding or tokenized form) as a **labeled example** in a `training_examples` table to support periodic review and threshold re-tuning,
  - apply a **similarity boost**: new posts similar to confirmed violations (via near-duplicate text matching or embedding cosine similarity from the sidecar) get a score boost in the Decision Engine.
- **Poster-side signal (careful):** posters whose posts are repeatedly confirmed as violations get a hashed-session **strike level** that tightens thresholds and rate limits for that session (temporary, expiring). Make the anti-abuse and fairness tradeoffs explicit in `docs/DECISIONS.md` (false-positive harm, shared devices, session resets).
- **Votes** from Part 2 feed a *minor* quality signal only (heavily downvoted comments collapse, like Slashdot thresholds), never moderation decisions on their own.
- Provide a `reputation-simulator` test utility that replays a scripted history of reports/confirmations to show how scores evolve, so the behavior can be explained at the capstone defense.

**Tests:** reliability updates, cap enforcement, decay, threshold auto-hold, learned-pattern creation, similarity boost, strike expiry, report de-dup and rate limiting.

**Acceptance:** Several test sessions report a post, staff confirm it (via a temporary API/CLI if the UI isn't ready), reputation shifts, and a near-duplicate new post is now scored higher or held.

**Stop and report.**

---

# PART 9: Spoiler Tagging and Auto-Blur (Sprint 7)

**Goal:** Voluntary spoiler protection without removal.

**Build:**

- A **"Spoiler" checkbox** on post/reply forms plus inline syntax (for example `[spoiler]...[/spoiler]`) with a safe parser.
- Spoiler posts and spoiler-marked media render **blurred** (CSS blur plus content not present in the visible DOM text until revealed, so copy/paste and screen readers don't leak; reveal by click via htmx or JS, with keyboard accessibility and `aria` labels). Optional spoiler topic label (for example, "Anime S2 finale").
- **Suggested spoiler (optional, behind a flag):** if the pipeline detects likely spoiler language (for example keywords like "spoiler", "ending", "dies" in context of a thread flagged as discussing a show), prompt the poster to tag it, never forcing. Keep it simple and clearly non-authoritative.
- Spoiler flags must survive translation (translated text is also blurred) and must apply to thumbnails of media.
- Staff can add/remove the spoiler tag on any post.

**Tests:** parser edge cases (nesting, unclosed tags, escaped content), no leakage of hidden text in server HTML for blurred sections, accessibility attributes, media thumbnail blur.

**Acceptance:** Tagged text and images are blurred until clicked; the page source doesn't expose spoiler text in plain visible form.

**Stop and report.**

---

# PART 10: Moderator/Admin Dashboard, Review Queue, and Escalation

**Goal:** The human side of the pipeline.

**Build:**

- **Staff dashboard** (`/staff`): counts (held, reported, escalated), recent decisions, system health (sidecar up, degraded modes, ffmpeg present).
- **Review queue:** list of held and reported posts with full context (original, translation, stage scores and evidence, reasons, thread context, report summary). Actions: approve, block (with category), delete, tag spoiler, move thread, lock/pin thread, **confirm violation** (triggers the learning loop from Part 8), **reject report**. Media shown **blurred by default** with click-to-reveal.
- **Blacklist/keyword management UI:** add/edit/disable entries, import/export CSV, test-a-string tool showing how normalization and matching work, view and approve learned-pattern candidates.
- **Restricted escalation queue** (ADMIN-only, or a configured `GUIDANCE_LIAISON` role): posts in escalation categories (credible threat, self-harm) are hidden from the public immediately and routed here. Provide an **escalation package** view: post content, original and translated, context, timestamps, and the **IP/session metadata (decrypted only on explicit click, with the access written to the audit log and a required reason field)**. Provide a one-click **export to PDF or printable page** for handing to school authorities, and a status workflow (new, under review, forwarded, closed-false-positive, closed-actioned) with notes. The system never contacts anyone automatically.
- **Audit log:** append-only record of every staff action and every metadata access; viewable by admins; no edit or delete endpoints. Include an "accountability" view so that moderator conduct is itself reviewable.
- **Staff management** (ADMIN): create/disable moderators, force password reset, role assignment. Moderators are not named on the public board.
- **Retention jobs:** scheduled purge for poster metadata per policy; a dry-run mode and logging of what was purged.
- A visible **"Moderator" capcode option** is optional and off by default; if you implement it, a post made by staff is labeled generically ("Moderator") without identity, and staff posts remain subject to the same public criticism (no hiding or locking of threads criticizing moderators by that reason alone; add a note in the rules and a guard in code that prevents deleting a thread solely tagged as "moderator criticism" without admin-level reason logging).

**Tests:** role-based access matrix, escalation flow end to end, audit entries for every sensitive action, decrypted metadata access logging, retention purge dry-run, CSRF checks.

**Acceptance:** I can walk a held post through review, confirm it, see reputation and blocklists update, and take an escalated post through the full workflow with an audit trail.

**Stop and report.**

---

# PART 11: Evaluation and Benchmark Harness

**Goal:** Produce the numbers Chapter 4 of the paper needs.

**Build:**

- An **`eval` module** (CLI plus staff-only page) that loads a **labeled dataset** (CSV/JSONL with `id, text/image_path/url, label (violating/non-violating), subcategory, language`) and runs every item through:
  - **Baseline:** keyword/blacklist-only mode (Part 4 flag),
  - **Hybrid:** full pipeline (keyword + links + ML + media + reputation features disabled/neutral for a clean comparison; make that configurable and documented).
- Output per run: confusion matrix, accuracy, precision, recall, F1 (overall and per category and per language), **McNemar's test** contingency table (b and c discordant counts), statistic, and p-value (implement with a standard stats library and cross-check with a small Python script), plus **latency stats** (mean, median, p95, max per stage and total) in seconds as the paper specifies.
- Export results to CSV/JSON/Markdown tables and simple charts (PNG) into `eval/results/<timestamp>/`.
- A **dataset loader guide** (`docs/EVALUATION.md`) explaining how to obtain approved public benchmark datasets (hate-speech, profanity, NSFW) listed in Chapter 2's sources, their licenses, how to map their labels to this schema, and a stratified sampling script (safe / borderline-or-spoiler / violating). **Do not download or commit explicit imagery.** For images, provide the loader and a tiny synthetic/benign fixture set so the code path is tested, and document how I should run the real image benchmark in a controlled environment.
- A **threshold tuning notebook or script** that sweeps thresholds and reports the precision/recall tradeoff, so we can choose defensible defaults.
- A **performance-efficiency probe** for ISO/IEC 25010: a simple load script (for example, k6 or a Java/Python tool) that measures page response times and posting latency under N concurrent simulated users, and writes a summary table.
- Map software features to ISO/IEC 25010 characteristics (functional suitability, performance efficiency, usability, security) in `docs/EVALUATION.md` as a traceability table to help the questionnaire write-up.

**Tests:** statistics functions against known examples, harness reproducibility with a fixed random seed.

**Acceptance:** `make eval` on the sample dataset produces the full report folder; the McNemar output matches the Python cross-check.

**Stop and report.**

---

# PART 12: Hardening, Usability, and Handover

**Goal:** Make it demo-ready and defensible.

**Build/do:**

1. **Security pass:** review against OWASP Top 10 for this app (XSS, CSRF, SSRF in link handling, file upload, auth, rate limits, header hardening such as CSP, X-Content-Type-Options, Referrer-Policy). Add dependency vulnerability scanning to `make lint` or a separate target. Write findings and fixes into `docs/SECURITY.md`.
2. **Anonymity audit:** automated test that crawls every public route and asserts no IP, session token, or staff identifier leaks in HTML, headers, or JSON; manual checklist in docs.
3. **Usability pass:** keyboard navigation, mobile layout, accessible contrast, readable typography, clear error and blocked-post messages (non-revealing about which rule triggered), loading states, empty states. Test at phone widths.
4. **Performance pass:** DB indexes for board listing, bump ordering, archive search; N+1 query check; caching where it helps; baseline numbers recorded.
5. **Resilience:** behavior with sidecar down, DB hiccup, disk full, and clock edge cases (access windows at boundaries); clear staff-visible warnings.
6. **Seed and demo mode:** `make demo` creates boards, sample threads (benign, synthetic), sample reports, and sample reputation history so I can demo the full flow at the defense. Include a demo script (`docs/DEMO.md`) with a click-by-click walkthrough mapped to the paper's objectives 1–7.
7. **Docs:** final `README.md` quickstart, architecture diagram refresh, `docs/API.md`, a **known-limitations section** that mirrors the paper's delimitations (novel or context-dependent harms, application-level access-window enforcement, language scope limited to Filipino/Cebuano/English, translation approximations, false positives and negatives), and a short **deployment-readiness note** listing what must change before putting this on a real server (HTTPS, secrets management, backups, reverse proxy, real IP header handling, school legal review). Do not deploy anything.
8. Final full test run, coverage summary, and a **release checklist**.

**Acceptance:** Clean `make up && make seed && make run && make test`, all docs current, demo script works start to finish.

**Final report:** summary of what exists, what's weak, what I should disclose as limitations, and recommended next steps for deployment.

---

# Appendix: Handy follow-up prompts

- **Re-plan a Part:** "Before building Part N, propose 2 alternative designs for [X], with tradeoffs, and recommend one."
- **Review mode:** "Stop coding. Review everything built so far against the Master Prompt's constraints and list every violation, gap, or risk, ranked by severity."
- **Fix mode:** "Tests for Part N are failing. Diagnose root cause first, explain it, then fix it. Don't change tests to make them pass unless the test itself is wrong, and tell me if that's the case."
- **Scope control:** "Don't add anything beyond this Part's scope. Put ideas in a `docs/BACKLOG.md` instead."
- **Capstone alignment check:** "Map what's implemented to each of the paper's 7 research objectives and flag anything still missing."
