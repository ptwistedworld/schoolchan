"""SchoolChan ML sidecar FastAPI application."""

from __future__ import annotations

import logging
from typing import Annotated

from fastapi import FastAPI, File, Query, UploadFile
from pydantic import BaseModel, Field

from app import __version__
from app.config import Settings, get_settings
from app.image_classifier import classify_image, get_image_classifier_health
from app.text_classifier import classify_text, get_text_classifier_health
from app.translator import get_translator_health, translate

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="SchoolChan ML Sidecar",
    description="Text/image moderation and translation for SchoolChan",
    version=__version__,
)


class TextClassifyRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=10000)


class TextClassifyResponse(BaseModel):
    toxicity: float
    hate: float
    harassment: float
    sexual: float
    threat: float
    self_harm: float
    model_id: str


class ImageClassifyResponse(BaseModel):
    nsfw_score: float
    model_id: str


class TranslateRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=10000)


class TranslateResponse(BaseModel):
    translated_text: str
    detected_language: str
    model_id: str


def _build_health_payload(settings: Settings) -> dict:
    if not settings.mock_mode:
        # Probe lazy loaders so /health reports fallback warnings.
        from app.image_classifier import _try_load_real_model
        from app.text_classifier import _try_load_real_pipeline
        from app.translator import _try_load_real_translator

        _try_load_real_pipeline(settings)
        _try_load_real_model(settings)
        _try_load_real_translator(settings)

    text_h = get_text_classifier_health()
    image_h = get_image_classifier_health()
    trans_h = get_translator_health()

    warnings: list[str] = []
    if not settings.mock_mode:
        for name, h in (
            ("text", text_h),
            ("image", image_h),
            ("translate", trans_h),
        ):
            if not h.get("real_model_loaded") and h.get("real_load_error"):
                warnings.append(
                    f"{name}: real model unavailable, using mock — {h['real_load_error']}"
                )

    return {
        "status": "ok",
        "version": __version__,
        "mock_mode": settings.mock_mode,
        "services": {
            "text": text_h,
            "image": image_h,
            "translate": trans_h,
        },
        "warnings": warnings,
    }


@app.get("/health")
def health() -> dict:
    settings = get_settings()
    return _build_health_payload(settings)


@app.post("/v1/text/classify", response_model=TextClassifyResponse)
def text_classify(body: TextClassifyRequest) -> TextClassifyResponse:
    result = classify_text(body.text)
    return TextClassifyResponse(**result.as_dict())


@app.post("/v1/image/classify", response_model=ImageClassifyResponse)
async def image_classify(
    file: Annotated[UploadFile, File(...)],
    path_hint: Annotated[str | None, Query()] = None,
) -> ImageClassifyResponse:
    data = await file.read()
    result = classify_image(
        data,
        filename=file.filename,
        path_hint=path_hint,
    )
    return ImageClassifyResponse(**result.as_dict())


@app.post("/v1/translate", response_model=TranslateResponse)
def translate_endpoint(body: TranslateRequest) -> TranslateResponse:
    result = translate(body.text)
    return TranslateResponse(**result.as_dict())
