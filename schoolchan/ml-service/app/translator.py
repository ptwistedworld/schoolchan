"""Language detection and translation to English."""

from __future__ import annotations

import logging
import re
from dataclasses import dataclass
from typing import Any

from app.config import Settings, get_settings
from app.text_classifier import looks_like_tagalog_stub

logger = logging.getLogger(__name__)

TAGALOG_STUB_ENGLISH = (
    "Hello, this is a stub English translation for SchoolChan mock mode."
)

_real_translator: Any | None = None
_real_load_attempted = False
_real_load_error: str | None = None


@dataclass
class TranslateResult:
    translated_text: str
    detected_language: str
    model_id: str

    def as_dict(self) -> dict[str, str]:
        return {
            "translated_text": self.translated_text,
            "detected_language": self.detected_language,
            "model_id": self.model_id,
        }


def _detect_language_mock(text: str) -> str:
    if looks_like_tagalog_stub(text):
        return "tl"
    if re.search(r"[\u3040-\u30ff\u4e00-\u9fff]", text):
        return "ja"
    return "en"


def translate_mock(text: str, settings: Settings | None = None) -> TranslateResult:
    settings = settings or get_settings()
    detected = _detect_language_mock(text)
    if detected == "tl":
        return TranslateResult(
            translated_text=TAGALOG_STUB_ENGLISH,
            detected_language="tl",
            model_id=settings.translate_model_id,
        )
    return TranslateResult(
        translated_text=text,
        detected_language=detected,
        model_id=settings.translate_model_id,
    )


def _try_load_real_translator(settings: Settings) -> bool:
    global _real_translator, _real_load_attempted, _real_load_error

    if _real_load_attempted:
        return _real_translator is not None

    _real_load_attempted = True
    try:
        model_name = settings.translate_model_id
        if model_name.endswith("-mock-v1"):
            raise ValueError(
                "Set TRANSLATE_MODEL_ID to a translation model when MOCK_MODE=false"
            )
        if "nllb" in model_name.lower():
            from transformers import AutoModelForSeq2SeqLM, AutoTokenizer

            tokenizer = AutoTokenizer.from_pretrained(model_name)
            model = AutoModelForSeq2SeqLM.from_pretrained(model_name)
            _real_translator = ("nllb", tokenizer, model)
        else:
            from transformers import pipeline

            _real_translator = ("pipeline", pipeline("translation", model=model_name))
        return True
    except Exception as exc:  # noqa: BLE001
        _real_load_error = str(exc)
        logger.warning("Translator load failed, using mock: %s", exc)
        return False


def _nllb_source(text: str) -> str:
    lower = text.lower()
    if any(word in lower for word in ("unsa", "kaayo", "wala", "ngano")):
        return "ceb_Latn"
    if looks_like_tagalog_stub(text) or any(word in lower for word in ("ang", "mga", "hindi", "ako")):
        return "tgl_Latn"
    return "eng_Latn"


def translate_real(text: str, settings: Settings) -> TranslateResult | None:
    if not _try_load_real_translator(settings):
        return None

    assert _real_translator is not None
    try:
        kind = _real_translator[0]
        if kind == "nllb":
            _, tokenizer, model = _real_translator
            src = _nllb_source(text)
            if src == "eng_Latn":
                return TranslateResult(text, "en", settings.translate_model_id)
            tokenizer.src_lang = src
            inputs = tokenizer(text[:512], return_tensors="pt")
            bos = tokenizer.convert_tokens_to_ids("eng_Latn")
            generated = model.generate(**inputs, forced_bos_token_id=bos, max_length=256)
            translated = tokenizer.batch_decode(generated, skip_special_tokens=True)[0]
            lang = "tl" if src == "tgl_Latn" else "ceb"
            return TranslateResult(translated, lang, settings.translate_model_id)
        pipe = _real_translator[1]
        out = pipe(text[:512])
        translated = out[0]["translation_text"] if out else text
        return TranslateResult(translated, _detect_language_mock(text), settings.translate_model_id)
    except Exception as exc:  # noqa: BLE001
        logger.warning("Real translate failed: %s", exc)
        return None


def translate(text: str, settings: Settings | None = None) -> TranslateResult:
    settings = settings or get_settings()
    if settings.mock_mode:
        return translate_mock(text, settings)

    real = translate_real(text, settings)
    if real is not None:
        return real
    return translate_mock(text, settings)


def get_translator_health() -> dict[str, str | bool | None]:
    settings = get_settings()
    return {
        "mock_mode": settings.mock_mode,
        "model_id": settings.translate_model_id,
        "real_model_loaded": _real_translator is not None,
        "real_load_error": _real_load_error,
    }
