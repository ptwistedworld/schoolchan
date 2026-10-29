# Evaluation

## Hybrid vs keyword-only baseline

On Windows, from the `schoolchan` folder:

```powershell
.\scripts\run-eval.ps1
```

On macOS or Linux: `make eval`.

<<<<<<< HEAD
Both start the board once with `schoolchan.eval.run=true` on the embedded H2 profile, compare `data/fixtures/eval-sample.jsonl`, write the files below, and exit.
=======
Both start the board once with `schoolchan.eval.run=true` on the embedded H2 profile,
compare `data/fixtures/eval-sample.jsonl`, write the files below, and exit.
>>>>>>> d5851d3 (Initial commit)

Outputs under `eval/results/<timestamp>/`:

- `accuracy.json` — hybrid confusion matrix (tp/tn/fp/fn, precision, recall, F1, accuracy)
<<<<<<< HEAD
- `mcnemar.json` — contingency b, c, and chi-square
- `latency.md` — wall time of that fixture comparison

The same numbers are available while the app is running, for an admin, at `/staff/api/eval/confusion` and `/staff/api/eval/mcnemar`.

1. **Baseline** in the report is keyword/token hits on `TOKEN_BLOCK` only.
2. **Hybrid** also counts `TOKEN_ESCALATE`.
=======
- `mcnemar.json` — contingency b, c, chi-square and the **exact two-sided p-value**
- `report.json` — everything, including the baseline and per-language tables
- `latency.md` — per-stage latency (n, mean, median, p95, max)
- `by-language.md` — accuracy for English, Tagalog and Cebuano, side by side

The same numbers are available while the app is running, for an admin, at
`/staff/api/eval/confusion` and `/staff/api/eval/mcnemar`.

## Measured result (2026-10-21)

Run with the **full real stack** — ToxiScan, OpenNSFW2 and NLLB-200 (`scripts/run-ml-real.ps1`) —
against the 312-row synthetic fixture, which mixes English, Tagalog and Cebuano and includes rows
that only the language model can catch.

| Measure | Value |
| --- | --- |
| Rows | 312 (120 violating) |
| Baseline (keywords only) accuracy | 0.86 |
| Hybrid accuracy | 0.88 |
| Hybrid precision / recall / F1 | 0.78 / 0.97 / 0.87 |
| McNemar b / c | 64 / 0 |
| Chi-square | 62.02 |
| **Exact two-sided p-value** | **1.08e-19** |
| Keyword stage latency (mean) | 2.4 ms |
| ML stage latency (mean / p95) | 31.1 ms / 38 ms |

The hybrid pipeline is significantly better than keywords alone (p < 0.001): the language model
catches 64 rows the keyword list misses.

**Per language**

| Language | n | Baseline accuracy | Hybrid accuracy | Hybrid F1 |
| --- | --- | --- | --- | --- |
| en | 128 | 0.88 | 0.91 | 0.89 |
| ceb | 92 | 0.87 | **0.96** | 0.95 |
| tl | 92 | 0.83 | 0.78 | 0.76 |

**What the numbers say**

- **Cebuano improves a lot** once the text is screened through its NLLB translation (0.87 → 0.96).
  This is the clearest evidence for the translation-ensemble design.
- **Tagalog gets slightly worse** (0.83 → 0.78). The English-only classifier still picks up
  hostility in some Filipino translations that a Filipino reader would not read as abusive.
  The honest conclusion for the paper: translation helps Cebuano, but a native Filipino
  classifier is needed for Filipino. That is the limitation to disclose.
- The remaining false positives are mostly mild Filipino sentences whose translations read
  more bluntly than the original.

**What this changed in the code**

Running the real models exposed two real bugs, both now fixed:

1. The ML stage was scoring the original Tagalog **and** the translation. ToxiScan is
   English-only, so the raw Filipino produced garbage scores and false positives. It now
   scores the **translation only** when one exists, the original when the text is English,
   and skips anything else with the reason `ml-skipped: needs translation`.
2. The NLLB source-language detection missed some Filipino sentences (`May extra ba kayong
   kopya ng aklat?`), so they were sent to the model as English and came back untranslated.
   The detector now uses the same marker sets as the mock, plus extra cues.
>>>>>>> d5851d3 (Initial commit)

## Dataset schema

CSV/JSONL: `id, text, image_path, label, subcategory, language`

<<<<<<< HEAD
Labels: `violating` | `non-violating`. Use only licensed research sets or synthetic fixtures. **Do not commit explicit imagery.**
=======
Labels: `violating` | `non-violating`. Use only licensed research sets or synthetic fixtures.
**Do not commit explicit imagery.**

The committed fixture is built by `ml-service/scripts/build_eval_fixture.py`. It is
synthetic: mild everyday insults plus `TOKEN_*` placeholders, so the repository
never holds real abuse, threats or explicit material.

To use a public dataset instead, map its labels to `violating` / `non-violating`,
keep the `language` field, and point the harness at the file. Recommended sources
for the paper (check each licence before use): Hate Speech and Offensive Language
(davidson), Jigsaw Toxic Comment, and the Filipino and Cebuano hate-speech sets
referenced in Chapter 2.
>>>>>>> d5851d3 (Initial commit)

## ISO/IEC 25010 traceability

| Characteristic | Features |
| --- | --- |
| Functional suitability | Board, bump/archive, moderation decisions, translation, spoilers |
<<<<<<< HEAD
| Performance efficiency | Latency logging, load probe, indexes |
| Usability | Thymeleaf board UI, rules, clear block messages |
| Security | Anonymity, CSRF, encrypted metadata, audit, rate limits |
=======
| Performance efficiency | Latency logging, load probe, indexes, per-stage p95 |
| Usability | Thymeleaf board UI, rules, about page, clear block messages, accessibility audit test |
| Security | Anonymity, CSRF, encrypted metadata, audit, rate limits, login throttling |
>>>>>>> d5851d3 (Initial commit)

## Threshold tuning

`scripts/threshold_sweep.py` sweeps block/queue thresholds and prints precision/recall tradeoffs.
<<<<<<< HEAD
=======

## Model verification

`ml-service/scripts/verify_models.py` checks that every configured model id really
exists, its licence, and that the expected files are present. It found a wrong
model id in an earlier draft of ADR-003, so run it before claiming a model works.

`ml-service/scripts/smoke_real_models.py` runs the real models once and prints the
scores. Verified 2026-10-21: benign text scored harassment 0.13, clearly abusive
text 0.9999; a plain image scored nsfw 0.006.

`ml-service/scripts/smoke_translate.py` does the same for the translation model.
Verified 2026-10-21:

| Input | Output |
| --- | --- |
| "Magandang umaga sa inyong lahat!" | "Good morning to all of you!" |
| "Salamat po sa tulong ninyo." | "Thank you for your help." |
| "Unsa kaayo, mga kaibigan!" | "What a great thing, friends!" |

The first translation takes about 30 s (model load), then 1-2 s per post on a CPU.

### Loading NLLB

```powershell
.\scripts\setup-real-models.ps1    # once: creates a Python 3.11/3.12 venv with torch (CPU)
.\scripts\run-ml-real.ps1          # starts the sidecar with all three real models
```

`setup-real-models.ps1` installs `onnxruntime`, `transformers` and the **CPU build of
PyTorch** (the NLLB model needs it), then checks that every model id exists.

**Memory.** NLLB-600M in float32 needs about 2.5 GB of weights before the
activations, which will not fit on a machine with a few gigabytes free. The
translator loads it in **bfloat16** (`TRANSLATE_DTYPE=bfloat16`), which halves that
to about 1.2 GB. Budget **3 GB free**. Set `TRANSLATE_DTYPE=float32` only if you have
6 GB or more.

**Speed.** About 1-2 s per post on a CPU, so the pipeline translates in the
background and attaches the result when it is ready. The posting thread is never
held up.

If the machine cannot spare the memory, leave `TRANSLATE_MODEL_ID` unset: the
sidecar falls back to the mock translation and says so in `/health` under
`warnings`, rather than pretending the translation is real.
>>>>>>> d5851d3 (Initial commit)
