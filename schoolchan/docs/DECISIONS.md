# Architecture Decision Records

## ADR-001: Build tool — Gradle (Kotlin DSL)

- **Decision:** Use Gradle with Kotlin DSL and the Spring Boot plugin.
- **Alternatives:** Maven.
- **Why:** First-class Spring Boot support, faster incremental builds, wrapper for zero local install of Gradle itself.
- **Date:** 2026-10-20

## ADR-002: Text toxicity — ToxiScan + Tagalog RoBERTa boost

- **Decision:** Primary classifier `vectorsense/toxiscan` (INT8 ONNX, ~136 MB). Secondary Tagalog hate model `mapsoriano/roberta-tagalog-base-philippine-elections-2016-2022-hate-speech` as boost when language is Tagalog/Taglish.
- **Alternatives:** Horizon-Labs multilingual-toxicity-small; gravitee DistilBERT toxicity.
- **Why:** Multi-label categories including threat/self_harm; CPU-friendly; Tagalog specialist improves Filipino coverage. Cebuano remains weak → rely on translation ensemble (Part 7).
- **Date:** 2026-10-20

## ADR-003: NSFW — OpenNSFW ONNX

- **Decision:** `yahoo-open-nsfw-onnx` / OpenNSFW2 (~24 MB).
- **Alternatives:** Falconsai ViT (~75 MB); NudeNet (AGPL — avoided for license friction).
- **Why:** Small, fast on CPU, permissive licensing.
- **Date:** 2026-10-20

## ADR-004: Video — ffmpeg frame sampling

- **Decision:** Sample first/middle/last + N configurable frames; max NSFW score wins.
- **Why:** No separate video model; clear failure if ffmpeg missing.
- **Date:** 2026-10-20

## ADR-005: Translation — NLLB-200 distilled 600M via CTranslate2

- **Decision:** `facebook/nllb-200-distilled-600M` with `tgl_Latn` / `ceb_Latn` → `eng_Latn`.
- **Alternatives:** Opus-MT (more permissive license, weaker Cebuano).
- **Why:** Confirmed Tagalog and Cebuano coverage. **License CC-BY-NC** — academic/local only; swap before commercial deploy.
- **Date:** 2026-10-20

## ADR-006: Language detection — lingua-py

- **Decision:** Use lingua-py in the sidecar; low-confidence / short text → skip or mark UNKNOWN.
- **Alternatives:** fastText lid.176 (heavier).
- **Date:** 2026-10-20

## ADR-007: Link screening — offline heuristics

- **Decision:** Local domain blocklist + typosquat/homograph/shortener/IP-literal checks. External reputation behind `LinkReputationClient` with no-op default.
- **Date:** 2026-10-20

## ADR-008: Test database — Testcontainers PostgreSQL; H2 fallback profile

- **Decision:** Integration tests prefer Testcontainers. Unit/smoke may use `test` profile with H2 if Docker unavailable.
- **Why:** Dialect fidelity when Docker present; still runnable without it for basic context tests.
- **Date:** 2026-10-20

## ADR-009: Moderation decision thresholds

- Escalation category ≥ 0.85 or keyword escalation → `ESCALATE`
- Max score ≥ 0.90 → `BLOCK`
- Max in [0.55, 0.90) → `QUEUE_FOR_REVIEW`
- Else → `PUBLISH`
- Reputation boost capped at +0.15
- Text sync; ML unavailable → `QUEUE`
- Media async behind quarantine
- All values overridable via `schoolchan.moderation.*` config
- **Date:** 2026-10-20

## ADR-010: Reputation model

- Session reliability ∈ [0.1, 2.0], start 1.0, decay over time
- No single session outweighs the cap
- Votes collapse UI only; never sole moderation signal
- **Date:** 2026-10-20

## ADR-011: Package base — `com.schoolchan`

- Java base package `com.schoolchan` with domain packages under it.
- **Date:** 2026-10-20
