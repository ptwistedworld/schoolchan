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
<<<<<<< HEAD
=======
_real_folder: Any | None = None
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
    global _real_session, _real_load_attempted, _real_load_error
=======
    global _real_session, _real_folder, _real_load_attempted, _real_load_error
>>>>>>> d5851d3 (Initial commit)

    if _real_load_attempted:
        return _real_session is not None

    _real_load_attempted = True
    try:
<<<<<<< HEAD
        import onnxruntime as ort  # type: ignore[import-untyped]

        model_path = settings.image_model_id
        if model_path.endswith("-mock-v1"):
            raise ValueError(
                "Set IMAGE_MODEL_ID to an ONNX model path when MOCK_MODE=false"
            )
        _real_session = ort.InferenceSession(model_path)
=======
        from app.onnx_runtime import download_repo, make_session

        model_id = settings.image_model_id
        if model_id.endswith("-mock-v1"):
            raise ValueError(
                "Set IMAGE_MODEL_ID to a Hugging Face ONNX repo when MOCK_MODE=false"
            )
        folder = download_repo(model_id)
        model_path = None
        for candidate in ("onnx/model.onnx", "model.onnx", "onnx/model_quantized.onnx"):
            if (folder / candidate).exists():
                model_path = folder / candidate
                break
        if model_path is None:
            raise FileNotFoundError(f"no ONNX graph found in {model_id}")
        _real_session = make_session(model_path)
        _real_folder = folder
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        from PIL import Image
        import numpy as np

        img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        img = img.resize((224, 224))
        arr = np.array(img, dtype=np.float32) / 255.0
=======
        import numpy as np
        from PIL import Image

        img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        size, mean, std = _preprocessor_settings(_real_folder)
        img = img.resize(size, Image.BILINEAR)
        arr = np.asarray(img, dtype=np.float32) / 255.0
        arr = (arr - np.asarray(mean, dtype=np.float32)) / np.asarray(std, dtype=np.float32)
>>>>>>> d5851d3 (Initial commit)
        arr = np.transpose(arr, (2, 0, 1))[np.newaxis, ...]

        session = _real_session
        assert session is not None
<<<<<<< HEAD
        input_name = session.get_inputs()[0].name
        outputs = session.run(None, {input_name: arr})
        score = float(outputs[0].flatten()[0])
=======
        input_names = {i.name for i in session.get_inputs()}
        feed = {}
        for name in input_names:
            if name in ("pixel_values", "input"):
                feed[name] = arr
        if not feed:
            feed[session.get_inputs()[0].name] = arr
        outputs = session.run(None, feed)
        logits = outputs[0][0]
        values = [float(v) for v in logits]
        if len(values) > 1:
            # Single-label classification: softmax and take the "nsfw" head.
            m = max(values)
            exps = [pow(2.718281828459045, v - m) for v in values]
            total = sum(exps)
            probs = [e / total for e in exps]
            score = probs[1] if len(probs) > 1 else probs[0]
        else:
            score = values[0]
>>>>>>> d5851d3 (Initial commit)
        score = max(0.0, min(1.0, score))
        return ImageClassifyResult(nsfw_score=score, model_id=settings.image_model_id)
    except Exception as exc:  # noqa: BLE001
        logger.warning("Real image classify failed: %s", exc)
        return classify_image_mock(filename=filename, path_hint=path_hint, settings=settings)


<<<<<<< HEAD
=======
def _preprocessor_settings(folder) -> tuple[tuple[int, int], list[float], list[float]]:
    """Read size / mean / std from the preprocessor config, with sane defaults."""
    import json as _json

    size = (224, 224)
    mean = [0.5, 0.5, 0.5]
    std = [0.5, 0.5, 0.5]
    for name in ("preprocessor_config.json", "config.json"):
        path = folder / name
        if not path.exists():
            continue
        try:
            cfg = _json.loads(path.read_text(encoding="utf-8"))
        except Exception:  # noqa: BLE001
            continue
        raw_size = cfg.get("size")
        if isinstance(raw_size, dict):
            w = int(raw_size.get("width", 224))
            h = int(raw_size.get("height", 224))
            size = (w, h)
        elif isinstance(raw_size, int):
            size = (raw_size, raw_size)
        if cfg.get("image_mean"):
            mean = list(cfg["image_mean"])
        if cfg.get("image_std"):
            std = list(cfg["image_std"])
        break
    return size, mean, std


>>>>>>> d5851d3 (Initial commit)
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
