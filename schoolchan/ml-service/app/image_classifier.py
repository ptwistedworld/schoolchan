"""Image NSFW scoring (mock heuristics or optional real models)."""

from __future__ import annotations

import io
import logging
from dataclasses import dataclass
from typing import Any

from app.config import Settings, get_settings

logger = logging.getLogger(__name__)

NSFW_TOKEN = "TOKEN_NSFW"

_real_session: Any | None = None
_real_load_attempted = False
_real_load_error: str | None = None


@dataclass
class ImageClassifyResult:
    nsfw_score: float
    model_id: str

    def as_dict(self) -> dict[str, float | str]:
        return {"nsfw_score": self.nsfw_score, "model_id": self.model_id}


def classify_image_mock(
    *,
    filename: str | None = None,
    path_hint: str | None = None,
    settings: Settings | None = None,
) -> ImageClassifyResult:
    settings = settings or get_settings()
    combined = " ".join(filter(None, [filename or "", path_hint or ""])).upper()
    score = 0.87 if NSFW_TOKEN in combined else 0.01
    return ImageClassifyResult(nsfw_score=score, model_id=settings.image_model_id)


def _try_load_real_model(settings: Settings) -> bool:
    global _real_session, _real_load_attempted, _real_load_error

    if _real_load_attempted:
        return _real_session is not None

    _real_load_attempted = True
    try:
        import onnxruntime as ort  # type: ignore[import-untyped]

        model_path = settings.image_model_id
        if model_path.endswith("-mock-v1"):
            raise ValueError(
                "Set IMAGE_MODEL_ID to an ONNX model path when MOCK_MODE=false"
            )
        _real_session = ort.InferenceSession(model_path)
        return True
    except Exception as exc:  # noqa: BLE001
        _real_load_error = str(exc)
        logger.warning("Image model load failed, using mock: %s", exc)
        return False


def classify_image_real(
    image_bytes: bytes,
    settings: Settings,
    filename: str | None = None,
    path_hint: str | None = None,
) -> ImageClassifyResult | None:
    if not _try_load_real_model(settings):
        return None

    try:
        from PIL import Image
        import numpy as np

        img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        img = img.resize((224, 224))
        arr = np.array(img, dtype=np.float32) / 255.0
        arr = np.transpose(arr, (2, 0, 1))[np.newaxis, ...]

        session = _real_session
        assert session is not None
        input_name = session.get_inputs()[0].name
        outputs = session.run(None, {input_name: arr})
        score = float(outputs[0].flatten()[0])
        score = max(0.0, min(1.0, score))
        return ImageClassifyResult(nsfw_score=score, model_id=settings.image_model_id)
    except Exception as exc:  # noqa: BLE001
        logger.warning("Real image classify failed: %s", exc)
        return classify_image_mock(filename=filename, path_hint=path_hint, settings=settings)


def classify_image(
    image_bytes: bytes,
    *,
    filename: str | None = None,
    path_hint: str | None = None,
    settings: Settings | None = None,
) -> ImageClassifyResult:
    settings = settings or get_settings()
    if settings.mock_mode:
        return classify_image_mock(
            filename=filename, path_hint=path_hint, settings=settings
        )

    real = classify_image_real(
        image_bytes,
        settings,
        filename=filename,
        path_hint=path_hint,
    )
    if real is not None:
        return real
    return classify_image_mock(
        filename=filename, path_hint=path_hint, settings=settings
    )


def get_image_classifier_health() -> dict[str, str | bool | None]:
    settings = get_settings()
    return {
        "mock_mode": settings.mock_mode,
        "model_id": settings.image_model_id,
        "real_model_loaded": _real_session is not None,
        "real_load_error": _real_load_error,
    }
