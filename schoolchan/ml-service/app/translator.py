"""Language detection and translation to English."""

from __future__ import annotations

import logging
<<<<<<< HEAD
=======
import os
>>>>>>> d5851d3 (Initial commit)
import re
from dataclasses import dataclass
from typing import Any

from app.config import Settings, get_settings
<<<<<<< HEAD
from app.text_classifier import looks_like_tagalog_stub
=======
from app.text_classifier import looks_like_cebuano_stub, looks_like_tagalog_stub
>>>>>>> d5851d3 (Initial commit)

logger = logging.getLogger(__name__)

TAGALOG_STUB_ENGLISH = (
    "Hello, this is a stub English translation for SchoolChan mock mode."
)

<<<<<<< HEAD
=======
# Phrase-level glosses used by MOCK_MODE so the feature is visibly working.
# The real NLLB path replaces these entirely.
TAGALOG_PHRASES: dict[str, str] = {
    "magandang umaga": "Good morning",
    "magandang gabi": "Good evening",
    "salamat": "Thank you",
    "kumusta kayo": "How are you",
    "kumusta ka": "How are you",
    "ako'y": "I am",
    "ako ay": "I am",
    "nakatulong": "helped",
    "tulong": "help",
    "hindi": "not",
    "oo": "yes",
    "hindi po": "no",
    "maganda": "nice",
    "ganda": "beauty",
    "topic": "topic",
    "nito": "this",
    "ito ay": "this is",
    "pagsubok": "test",
    "automatic translation": "automatic translation",
    "pagsubok ng automatic translation": "test of automatic translation",
    "sa inyong lahat": "to all of you",
    "inyong lahat": "all of you",
    "ang ganda": "how beautiful",
    "may grupo": "there is a group",
    "grupo tayo": "we have a group",
    "mag-aral": "to study",
    "para mag-aral": "to study",
    "ng calculus": "of Calculus",
    "tuwing sabado": "every Saturday",
    "ako'y pupunta": "I will go",
    "pupunta": "will go",
    "nga una kay kaayo": "first because you are good",
    "una": "first",
    "kayo": "you",
    "natin": "us",
    "namin": "our",
    "nila": "them",
    "sila": "they",
    "ito": "this",
    "iyon": "that",
    "dito": "here",
    "doon": "there",
    "bakit": "why",
    "paano": "how",
    "kailan": "when",
    "saan": "where",
    "ano": "what",
    "sino": "who",
    "trabaho": "work",
    "opisina": "office",
    "isang": "a",
    "ng": "of",
    "na": "now",
    "po": "",
    "ay": "is",
    "nang": "and",
    "kay": "to",
    "yung": "the",
    "nasa": "in",
    "kapag": "when",
    "tuwing": "every",
    "ako": "I",
    "usapin": "topic",
    "kumusta": "How are you",
    "araw": "day",
    "nga": "indeed",
    "masarap": "delicious",
    "pagkain": "food",
    "kaibigan": "friend",
    "mga": "the",
    "ang": "the",
    "at": "and",
    "para sa": "for",
}

CEBUANO_PHRASES: dict[str, str] = {
    "unsa kaayo": "hello / how are you",
    "maayo kaayo": "very good",
    "maayo": "good",
    "kaayo": "good",
    "sugdan": "competition",
    "mga kaibigan": "friends",
    "kaibigan": "friend",
    "gikan": "from",
    "sa team": "on the team",
    "ako gikan": "I am from",
    "akong team": "my team",
    "bolleyball": "volleyball",
    "kinhigpit": "training",
    "kausahan": "schedule",
    "man": "also",
    "bili": "buy",
    "hatag": "give",
    "duha": "two",
    "kayo": "you",
    "nato": "us",
    "nila": "them",
    "silat": "we",
    "niya": "his/her",
    "ni": "of",
    "mao": "is/are",
    "walay": "there is no",
    "wala": "none",
    "butas": "the problem",
    "maayo kaayo ka": "very good",
    "unsa": "what",
    "nga": "so/that",
    "sa sports day": "at sports day",
    "registration": "registration",
    "ako": "I",
    "sa": "at",
    "nga": "so",
    "unsa": "what",
    "nako": "my",
    "nimo": "your",
}


def _gloss(text: str, phrases: dict[str, str]) -> str:
    """Word-boundary-aware phrase substitution. Best-effort mock gloss."""
    out = text
    matched = 0
    for phrase in sorted(phrases.keys(), key=len, reverse=True):
        pattern = re.compile(r"\b" + re.escape(phrase) + r"\b", re.IGNORECASE)
        out, n = pattern.subn(phrases[phrase], out)
        matched += n
    if matched == 0:
        return ""
    return out

>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======
    # Cebuano first: several markers ("mga", "kaayo") also appear in Tagalog.
    if looks_like_cebuano_stub(text):
        return "ceb"
>>>>>>> d5851d3 (Initial commit)
    if looks_like_tagalog_stub(text):
        return "tl"
    if re.search(r"[\u3040-\u30ff\u4e00-\u9fff]", text):
        return "ja"
    return "en"


def translate_mock(text: str, settings: Settings | None = None) -> TranslateResult:
    settings = settings or get_settings()
    detected = _detect_language_mock(text)
    if detected == "tl":
<<<<<<< HEAD
        return TranslateResult(
            translated_text=TAGALOG_STUB_ENGLISH,
            detected_language="tl",
            model_id=settings.translate_model_id,
        )
=======
        gloss = _gloss(text, TAGALOG_PHRASES)
        translated = gloss if gloss else TAGALOG_STUB_ENGLISH
        return TranslateResult(
            translated_text=translated,
            detected_language="tl",
            model_id=settings.translate_model_id,
        )
    if detected == "ceb":
        gloss = _gloss(text, CEBUANO_PHRASES)
        translated = gloss if gloss else (
            "Hello, friends! This is a stub English translation for SchoolChan mock mode (Cebuano)."
        )
        return TranslateResult(
            translated_text=translated,
            detected_language="ceb",
            model_id=settings.translate_model_id,
        )
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
            model = AutoModelForSeq2SeqLM.from_pretrained(model_name)
=======

            # NLLB-600M in float32 needs about 2.5 GB of weights before the
            # activations. bfloat16 halves that and runs fine on a CPU, which
            # is what a school laptop has.
            dtype = None
            if os.getenv("TRANSLATE_DTYPE", "bfloat16").lower() == "bfloat16":
                try:
                    import torch

                    dtype = torch.bfloat16
                except Exception:  # noqa: BLE001
                    dtype = None
            model = AutoModelForSeq2SeqLM.from_pretrained(
                model_name,
                torch_dtype=dtype,
                low_cpu_mem_usage=True,
            )
            model.eval()
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
    lower = text.lower()
    if any(word in lower for word in ("unsa", "kaayo", "wala", "ngano")):
        return "ceb_Latn"
    if looks_like_tagalog_stub(text) or any(word in lower for word in ("ang", "mga", "hindi", "ako")):
=======
    """Pick the NLLB source language for a piece of text.

    Getting this wrong is expensive: a Filipino sentence that is treated as
    English comes back untranslated and the English-only classifier then
    scores raw Tagalog, which produces false positives.
    """
    if looks_like_cebuano_stub(text):
        return "ceb_Latn"
    if looks_like_tagalog_stub(text):
        return "tgl_Latn"
    lower = text.lower()
    # Extra cues the shared heuristics do not cover.
    tagalog_cues = (" ang ", " mga ", " ay ", " nang ", " kung ", " pero ", " dahil ",
                    " ito ", " iyon ", " hindi ", " naman ", " sana ", " salamat",
                    " magandang", " kumusta", " po ", " ba ")
    cebuano_cues = (" kaayo", " maayo", " unsa ", " kinsa ", " ngano ", " unsay",
                    " mahimong", " kung ", " apan ", " pero ", " mga ", " walay",
                    " adto", " diri", " naa ", " gayud", " usab")
    if any(cue in lower for cue in cebuano_cues):
        return "ceb_Latn"
    if any(cue in lower for cue in tagalog_cues):
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
            generated = model.generate(**inputs, forced_bos_token_id=bos, max_length=256)
=======
            # A short cap keeps the memory use down on a laptop.
            max_new = int(os.getenv("TRANSLATE_MAX_NEW_TOKENS", "96"))
            generated = model.generate(
                **inputs,
                forced_bos_token_id=bos,
                max_new_tokens=max_new,
                num_beams=1,
            )
>>>>>>> d5851d3 (Initial commit)
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
