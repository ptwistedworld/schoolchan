"""Text moderation classification (mock heuristics or optional real models)."""

from __future__ import annotations

import logging
import re
from dataclasses import dataclass
from typing import Any

from app.config import Settings, get_settings

logger = logging.getLogger(__name__)

CATEGORIES = (
    "toxicity",
    "hate",
    "harassment",
    "sexual",
    "threat",
    "self_harm",
)

LABEL_TO_CATEGORY = {
    "toxic": "toxicity",
    "toxicity": "toxicity",
    "severe_toxicity": "toxicity",
    "insult": "harassment",
    "obscene": "sexual",
    "sexual_explicit": "sexual",
    "identity_attack": "hate",
    "identity_hate": "hate",
    "threat": "threat",
    "self_harm": "self_harm",
    "self-harm": "self_harm",
}

TOKEN_SCORES: dict[str, dict[str, float]] = {
    "TOKEN_TOXIC": {"toxicity": 0.92},
    "TOKEN_HATE": {"hate": 0.91},
    "TOKEN_HARASSMENT": {"harassment": 0.90},
    "TOKEN_SEXUAL": {"sexual": 0.88},
    "TOKEN_THREAT": {"threat": 0.93},
    "TOKEN_SELF_HARM": {"self_harm": 0.89},
}

_real_pipeline: Any | None = None
_real_load_attempted = False
_real_load_error: str | None = None


@dataclass
class TextClassifyResult:
    toxicity: float
    hate: float
    harassment: float
    sexual: float
    threat: float
    self_harm: float
    model_id: str

    def as_dict(self) -> dict[str, float | str]:
        return {
            "toxicity": self.toxicity,
            "hate": self.hate,
            "harassment": self.harassment,
            "sexual": self.sexual,
            "threat": self.threat,
            "self_harm": self.self_harm,
            "model_id": self.model_id,
        }


def _baseline_scores() -> dict[str, float]:
    return {cat: 0.02 for cat in CATEGORIES}


def classify_text_mock(text: str, settings: Settings | None = None) -> TextClassifyResult:
    settings = settings or get_settings()
    scores = _baseline_scores()
    upper = text.upper()

    for token, boosts in TOKEN_SCORES.items():
        if token in upper:
            for category, value in boosts.items():
                scores[category] = max(scores[category], value)

    # Mild bump when multiple placeholder tokens appear (still synthetic only).
    token_hits = sum(1 for t in TOKEN_SCORES if t in upper)
    if token_hits >= 2:
        for cat in CATEGORIES:
            scores[cat] = min(1.0, scores[cat] + 0.05)

    return TextClassifyResult(
        **scores,
        model_id=settings.text_model_id,
    )


def _try_load_real_pipeline(settings: Settings) -> bool:
    global _real_pipeline, _real_load_attempted, _real_load_error

    if _real_load_attempted:
        return _real_pipeline is not None

    _real_load_attempted = True
    try:
<<<<<<< HEAD
        from transformers import pipeline  # type: ignore[import-untyped]

=======
>>>>>>> d5851d3 (Initial commit)
        model_name = settings.text_model_id
        if model_name.endswith("-mock-v1"):
            raise ValueError(
                "Set TEXT_MODEL_ID to a Hugging Face model when MOCK_MODE=false"
            )
<<<<<<< HEAD
        _real_pipeline = pipeline(
            "text-classification",
            model=model_name,
            top_k=None,
        )
=======
        _real_pipeline = _load_onnx_classifier(model_name)
>>>>>>> d5851d3 (Initial commit)
        return True
    except Exception as exc:  # noqa: BLE001 — fall back to mock
        _real_load_error = str(exc)
        logger.warning("Text model load failed, using mock: %s", exc)
        return False


<<<<<<< HEAD
=======
def _load_onnx_classifier(model_name: str):
    """Load an ONNX sequence-classification model without PyTorch.

    Returns a callable that maps text to {label: probability}.
    """
    from app.onnx_runtime import download_repo, load_labels, make_session, sigmoid, softmax
    from transformers import AutoTokenizer

    folder = download_repo(model_name)
    model_path = None
    for candidate in ("model.onnx", "onnx/model.onnx", "model_int8.onnx"):
        if (folder / candidate).exists():
            model_path = folder / candidate
            break
    if model_path is None:
        raise FileNotFoundError(f"no ONNX graph found in {model_name}")

    tokenizer = AutoTokenizer.from_pretrained(str(folder))
    session = make_session(model_path)
    input_names = {i.name for i in session.get_inputs()}

    labels = None
    for name in ("labels.json", "id2label.json"):
        if (folder / name).exists():
            labels = load_labels(folder / name)
            break

    def classify(text: str) -> dict[str, float]:
        encoded = tokenizer(
            text[:512],
            return_tensors="np",
            truncation=True,
            max_length=256,
        )
        feed = {}
        for name in input_names:
            if name in encoded:
                feed[name] = encoded[name]
        if "input_ids" not in feed:
            feed["input_ids"] = encoded["input_ids"]
        if "attention_mask" in input_names and "attention_mask" in encoded:
            feed["attention_mask"] = encoded["attention_mask"]
        if "token_type_ids" in input_names and "token_type_ids" in encoded:
            feed["token_type_ids"] = encoded["token_type_ids"]
        outputs = session.run(None, feed)
        logits = outputs[0][0]
        values = [float(v) for v in logits]
        # Multi-label models use sigmoid, single-label models use softmax.
        probs = softmax(values) if len(values) > 1 else [sigmoid(values[0])]
        result = {}
        for i, p in enumerate(probs):
            if labels and i in labels:
                result[labels[i]] = p
            else:
                result[f"label_{i}"] = p
        return result

    return classify


>>>>>>> d5851d3 (Initial commit)
def classify_text_real(text: str, settings: Settings) -> TextClassifyResult | None:
    if not _try_load_real_pipeline(settings):
        return None

    assert _real_pipeline is not None
    scores = _baseline_scores()
    try:
        raw = _real_pipeline(text[:512])
<<<<<<< HEAD
        if isinstance(raw, list) and raw and isinstance(raw[0], list):
            raw = raw[0]
        for item in raw or []:
            label = str(item.get("label", "")).lower().replace(" ", "_")
            score = float(item.get("score", 0.0))
            mapped = LABEL_TO_CATEGORY.get(label)
            if mapped is None:
                for cat in CATEGORIES:
                    if cat in label or label in cat:
                        mapped = cat
                        break
            if mapped:
                scores[mapped] = max(scores[mapped], score)
=======
        for label, score in (raw or {}).items():
            key = str(label).lower().replace(" ", "_")
            mapped = LABEL_TO_CATEGORY.get(key)
            if mapped is None:
                for cat in CATEGORIES:
                    if cat in key or key in cat:
                        mapped = cat
                        break
            if mapped:
                scores[mapped] = max(scores[mapped], float(score))
>>>>>>> d5851d3 (Initial commit)
    except Exception as exc:  # noqa: BLE001
        logger.warning("Real text classify failed: %s", exc)
        return None

    return TextClassifyResult(**scores, model_id=settings.text_model_id)


def classify_text(text: str, settings: Settings | None = None) -> TextClassifyResult:
    settings = settings or get_settings()
    if settings.mock_mode:
        return classify_text_mock(text, settings)

    real = classify_text_real(text, settings)
    if real is not None:
        return real
    return classify_text_mock(text, settings)


def get_text_classifier_health() -> dict[str, str | bool | None]:
    settings = get_settings()
    return {
        "mock_mode": settings.mock_mode,
        "model_id": settings.text_model_id,
        "real_model_loaded": _real_pipeline is not None,
        "real_load_error": _real_load_error,
    }


def looks_like_tagalog_stub(text: str) -> bool:
    """Heuristic for MOCK_MODE translation tests (no real harmful content)."""
    lower = text.lower()
    markers = (
        r"\bpo\b",
        r"\bnga\b",
        r"\bmaganda\b",
        r"\bsalamat\b",
        r"\bkumusta\b",
<<<<<<< HEAD
        r"ng\b",
    )
    hits = sum(1 for p in markers if re.search(p, lower))
    return hits >= 2 or ("po" in lower and "nga" in lower)
=======
        r"\bako\b",
        r"\bikaw\b",
        r"\bkayo\b",
        r"\binyong\b",
        r"\bathin\b",
        r"\bnatin\b",
        r"\bmga\b",
        r"\bang\b",
        r"\bhindi\b",
        r"\bito ay\b",
        r"\bsa\b",
        r"\bpara sa\b",
        r"\bmatanda\b",
        r"\bkapag\b",
        r"\bnang\b",
        r"\bganda\b",
        r"\bnito\b",
        r"\btong\b",
        r"\bnatin\b",
        r"\bnila\b",
        r"\bnamin\b",
        r"\binyo\b",
        r"\bdito\b",
        r"\bbakit\b",
        r"\bpaano\b",
        r"\bkailan\b",
        r"\bsaan\b",
        r"\bano\b",
        r"\bsino\b",
        r"\bkayo po\b",
        r"\bsalamat\b",
        r"\bopisina\b",
        r"\btrabaho\b",
        r"\bniyo\b",
        r"\bninyo\b",
        r"\bkung\b",
        r"\bba\b",
        r"\bnakita\b",
        r"\bnalaman\b",
        r"\bsinabi\b",
        r"\bsinasabi\b",
        r"\bnaiintindihan\b",
        r"\bkapag\b",
        r"\bhabang\b",
        r"\bupat\b",
        r"\bkasi\b",
        r"\btapos\b",
        r"\bpero\b",
        r"\bopo\b",
        r"\bkya\b",
        r"\bdiba\b",
    )
    hits = sum(1 for p in markers if re.search(p, lower))
    return hits >= 2


CEBUANO_MARKERS = (
    r"\bkaayo\b",
    r"\bkaayo kay\b",
    r"\bmaayo\b",
    r"\bsugdan\b",
    r"\bka\b",
    r"\bmao\b",
    r"\bni\b",
    r"\bong\b",
    r"\buna\b",
    r"\bndi\b",
    r"\bwala\b",
    r"\bayo\b",
    r"\bngan\b",
    r"\bkaibigan\b",
    r"\bkinhigpit\b",
    r"\bkausahan\b",
    r"\bman\b",
    r"\bili\b",
    r"\bhatag\b",
    r"\bduha\b",
)


def looks_like_cebuano_stub(text: str) -> bool:
    """Heuristic for Cebuano text in MOCK_MODE."""
    lower = text.lower()
    hits = sum(1 for p in CEBUANO_MARKERS if re.search(p, lower))
    return hits >= 2
>>>>>>> d5851d3 (Initial commit)
