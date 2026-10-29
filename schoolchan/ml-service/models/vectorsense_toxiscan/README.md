---
license: apache-2.0
language:
- en
- hi
- es
- fr
- de
- it
- ru
- ar
- ja
- zh
tags:
- text-classification
- toxicity
- hate-speech
- content-moderation
- multilingual
- onnx
pipeline_tag: text-classification
library_name: onnxruntime
---

# ToxiScan — multilingual multi-label toxicity & hate-speech moderation

For any input text, ToxiScan returns **per-category probabilities across 8 categories**, plus a
severity level and a policy-driven action (`allow` / `flag` / `review` / `block`). It ships as a
CPU-only **INT8 ONNX** model (~136 MB) that runs fully offline — no GPU, no network.

- **Base model:** `distilbert-base-multilingual-cased`
- **Task:** multi-label text classification (independent sigmoid per category)
- **Categories:** `insult`, `threat`, `identity_hate`, `sexual`, `self_harm`, `harassment`, `profanity`, `spam`
- **Runtime:** `onnxruntime` (CPU), ~17 ms / request

## Results — held-out test set (threshold 0.5)

| Category | Precision | Recall | F1 | Support |
|---|---|---|---|---|
| insult        | 0.86 | 0.88 | 0.87 | 9,336 |
| self_harm     | 0.92 | 0.96 | 0.94 |   982 |
| spam          | 0.99 | 0.97 | 0.98 |   614 |
| sexual        | 0.73 | 0.77 | 0.75 |   388 |
| identity_hate | 0.74 | 0.72 | 0.73 | 1,080 |
| profanity     | 0.70 | 0.73 | 0.71 |   798 |
| harassment    | 0.68 | 0.65 | 0.66 |   476 |
| threat        | 0.69 | 0.56 | 0.61 |   326 |
| **macro-F1**  |      |      | **0.78** | |
| **micro-F1**  | 0.84 | 0.85 | **0.84** | ~13.5k |

Metrics are **in-distribution** (test split drawn from the same sources as training). Out-of-distribution
phrasing may score lower, especially for `threat` and `harassment`. Obfuscation-robustness and HateCheck
bias false-positive rate: pending.

## Quick start (Python + onnxruntime)

```bash
pip install onnxruntime tokenizers huggingface_hub numpy
```

```python
import json, numpy as np, onnxruntime as ort
from huggingface_hub import hf_hub_download
from tokenizers import Tokenizer

REPO = "vectorsense/toxiscan"
sess   = ort.InferenceSession(hf_hub_download(REPO, "toxiscan.onnx"), providers=["CPUExecutionProvider"])
tok    = Tokenizer.from_file(hf_hub_download(REPO, "tokenizer.json"))
labels = json.loads(open(hf_hub_download(REPO, "labels.json"), encoding="utf-8").read())

def classify(text: str) -> dict:
    enc   = tok.encode(text)
    ids   = np.array([enc.ids], dtype=np.int64)
    mask  = np.array([enc.attention_mask], dtype=np.int64)
    feeds = {i.name: (mask if "mask" in i.name else ids) for i in sess.get_inputs()}
    logits = sess.run(None, feeds)[0][0]
    probs  = 1.0 / (1.0 + np.exp(-logits))
    return {lbl: round(float(p), 4) for lbl, p in zip(labels, probs)}

print(classify("i will find you and kill you tonight"))
# {'insult': 0.006, 'threat': 0.943, 'identity_hate': 0.002, ...}
```

## Run it as a REST API (request / response service)

The companion [ToxiScan repo](https://github.com/palarnab) ships a FastAPI service that wraps this
model with policy presets, toxic-span rationales, and target-group detection.

```bash
pip install fastapi "uvicorn[standard]" onnxruntime tokenizers numpy pyyaml
uvicorn service.app:app --port 8000     # auto-loads toxiscan.onnx from ./models
```

```bash
curl -s http://127.0.0.1:8000/v1/classify \
  -H "Content-Type: application/json" \
  -d '{"text":"you are an idiot","options":{"policy":"balanced","explain":true}}'
```

```powershell
# PowerShell
$body = '{"text":"you are an idiot","options":{"policy":"balanced","explain":true}}'
Invoke-RestMethod -Uri http://127.0.0.1:8000/v1/classify -Method Post -ContentType 'application/json' -Body $body
```

Response fields: `toxic`, `severity`, `action`, `overall_score`, `categories` (all 8 scores),
`spans`, `targets`, `normalized_text`, `explanation`, `model_version`, `latency_ms`.
Endpoints: `GET /health`, `POST /v1/classify`, `POST /v1/classify/batch`, `POST /v1/feedback`.

## Policy presets
`strict` / `balanced` / `lenient` set per-category decision thresholds and map severity to an action
(`allow` → `flag` → `review` → `block`). Override any threshold per request via `options.thresholds`.

## Training record
Full provenance lives in this repo under `training/` (best checkpoint, logs, configs, dataset
manifest, benchmark tables), pushed straight from the training box.

## Limitations & licensing
- Trained on mixed-license public corpora; only a redistributable subset is published — see the dataset card.
- In-distribution metrics; treat rare categories (`threat`, `sexual`, `spam`) as weaker in the wild.
- `self_harm` is trained on distress/ideation text and is **not** a crisis-intervention tool.

## Author
Arnab Pal — [LinkedIn](https://www.linkedin.com/in/pal-arnab) &middot; [GitHub](https://github.com/palarnab)
