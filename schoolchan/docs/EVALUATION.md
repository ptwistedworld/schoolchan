# Evaluation

## Hybrid vs keyword-only baseline

On Windows, from the `schoolchan` folder:

```powershell
.\scripts\run-eval.ps1
```

On macOS or Linux: `make eval`.

Both start the board once with `schoolchan.eval.run=true` on the embedded H2 profile, compare `data/fixtures/eval-sample.jsonl`, write the files below, and exit.

Outputs under `eval/results/<timestamp>/`:

- `accuracy.json` — hybrid confusion matrix (tp/tn/fp/fn, precision, recall, F1, accuracy)
- `mcnemar.json` — contingency b, c, and chi-square
- `latency.md` — wall time of that fixture comparison

The same numbers are available while the app is running, for an admin, at `/staff/api/eval/confusion` and `/staff/api/eval/mcnemar`.

1. **Baseline** in the report is keyword/token hits on `TOKEN_BLOCK` only.
2. **Hybrid** also counts `TOKEN_ESCALATE`.

## Dataset schema

CSV/JSONL: `id, text, image_path, label, subcategory, language`

Labels: `violating` | `non-violating`. Use only licensed research sets or synthetic fixtures. **Do not commit explicit imagery.**

## ISO/IEC 25010 traceability

| Characteristic | Features |
| --- | --- |
| Functional suitability | Board, bump/archive, moderation decisions, translation, spoilers |
| Performance efficiency | Latency logging, load probe, indexes |
| Usability | Thymeleaf board UI, rules, clear block messages |
| Security | Anonymity, CSRF, encrypted metadata, audit, rate limits |

## Threshold tuning

`scripts/threshold_sweep.py` sweeps block/queue thresholds and prints precision/recall tradeoffs.
