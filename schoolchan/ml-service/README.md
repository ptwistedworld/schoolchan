# SchoolChan ML Sidecar

FastAPI service for text moderation scores, image NSFW scoring, and translation to English. Designed for local development and container deployment alongside SchoolChan.

## Quick start

```bash
cd schoolchan/ml-service
python -m venv .venv
.venv\Scripts\activate   # Windows
pip install -r requirements.txt
uvicorn app.main:app --port 8090
```

Open `http://127.0.0.1:8090/docs` for interactive API documentation.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/health` | Service status, `mock_mode`, per-service model info, fallback warnings |
| POST | `/v1/text/classify` | JSON `{ "text": "..." }` → category scores |
| POST | `/v1/image/classify` | Multipart file upload → `nsfw_score` |
| POST | `/v1/translate` | JSON `{ "text": "..." }` → English text + detected language |

## Mock mode (default)

`MOCK_MODE` defaults to `true`. Classifiers use **synthetic placeholder tokens only** (never real harmful content):

| Token | Effect |
|-------|--------|
| `TOKEN_TOXIC` | High toxicity score |
| `TOKEN_HATE` | High hate score |
| `TOKEN_HARASSMENT` | High harassment score |
| `TOKEN_SEXUAL` | High sexual score |
| `TOKEN_THREAT` | High threat score |
| `TOKEN_SELF_HARM` | High self_harm score |
| `TOKEN_NSFW` | In image filename or `path_hint` → high NSFW score |

Translation mock: text that looks Tagalog-ish returns a stub English string with `detected_language: "tl"`; otherwise pass-through with `detected_language: "en"`.

## Real models (optional)

Set `MOCK_MODE=false` and point model IDs to real assets:

- `TEXT_MODEL_ID` — Hugging Face text classification model
- `IMAGE_MODEL_ID` — Path to an ONNX model file
- `TRANSLATE_MODEL_ID` — Hugging Face translation model

Install optional dependencies when using real models:

```bash
pip install transformers onnxruntime torch
```

If loading fails, the service falls back to mock behavior and `/health` includes `warnings`.

## Tests

```bash
pytest
```

## Docker

```bash
docker build -t schoolchan-ml .
docker run --rm -p 8090:8090 schoolchan-ml
```

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MOCK_MODE` | `true` | Use keyword/heuristic mocks |
| `TEXT_MODEL_ID` | `schoolchan-text-mock-v1` | Text model id or HF name |
| `IMAGE_MODEL_ID` | `schoolchan-image-mock-v1` | ONNX path or mock id |
| `TRANSLATE_MODEL_ID` | `schoolchan-translate-mock-v1` | Translation model id |
