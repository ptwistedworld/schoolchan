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
        from transformers import pipeline  # type: ignore[import-untyped]

        model_name = settings.text_model_id
        if model_name.endswith("-mock-v1"):
            raise ValueError(
                "Set TEXT_MODEL_ID to a Hugging Face model when MOCK_MODE=false"
            )
        _real_pipeline = pipeline(
            "text-classification",
            model=model_name,
            top_k=None,
        )
        return True
    except Exception as exc:  # noqa: BLE001 — fall back to mock
        _real_load_error = str(exc)
        logger.warning("Text model load failed, using mock: %s", exc)
        return False


def classify_text_real(text: str, settings: Settings) -> TextClassifyResult | None:
    if not _try_load_real_pipeline(settings):
        return None

    assert _real_pipeline is not None
    scores = _baseline_scores()
    try:
        raw = _real_pipeline(text[:512])
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
        r"ng\b",
    )
    hits = sum(1 for p in markers if re.search(p, lower))
    return hits >= 2 or ("po" in lower and "nga" in lower)
