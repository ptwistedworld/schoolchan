"""Run the real models once and print the scores. Proves the real path works.

Usage:
    .venv-real\\Scripts\\python scripts\\smoke_real_models.py
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.config import Settings  # noqa: E402
from app.image_classifier import classify_image  # noqa: E402
from app.text_classifier import classify_text  # noqa: E402

TEXT_MODEL = "vectorsense/toxiscan"
IMAGE_MODEL = "onnx-community/nsfw_image_detection-ONNX"

BENIGN = "Good morning everyone, see you at the school fair later."
TOXIC = "You are a worthless idiot and everyone hates you."


def main() -> int:
    settings = Settings()
    settings.mock_mode = False
    settings.text_model_id = TEXT_MODEL
    settings.image_model_id = IMAGE_MODEL

    print("Text model:", TEXT_MODEL)
    for text in (BENIGN, TOXIC):
        result = classify_text(text, settings)
        print(f"  {text[:50]!r}")
        print(f"    -> {result.as_dict()}")

    print("\nImage model:", IMAGE_MODEL)
    # A tiny grey PNG, so this is a "does it run" check, not a safety claim.
    import io

    from PIL import Image

    buf = io.BytesIO()
    Image.new("RGB", (64, 64), (128, 128, 128)).save(buf, format="PNG")
    result = classify_image(buf.getvalue(), filename="tiny.png", settings=settings)
    print(f"    -> {result.as_dict()}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
