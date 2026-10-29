"""Check that the real translation model works on Filipino and Cebuano.

Downloads facebook/nllb-200-distilled-600M on first run (about 2.5 GB) and
prints a translation for each sample language.

Usage:
    .venv-real\\Scripts\\python scripts\\smoke_translate.py
"""

from __future__ import annotations

import sys
import time
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.config import Settings  # noqa: E402
from app.translator import translate  # noqa: E402

MODEL = "facebook/nllb-200-distilled-600M"

SAMPLES = [
    ("tl", "Magandang umaga sa inyong lahat! Ito ay isang pagsubok ng automatic translation."),
    ("tl", "Salamat po sa tulong ninyo."),
    ("ceb", "Unsa kaayo, mga kaibigan! Sugdan nga sa sports day registration."),
    ("ceb", "Maayo kaayo! Ako gikan sa team sa bolleyball."),
    ("en", "Good morning everyone."),
]


def main() -> int:
    settings = Settings()
    settings.mock_mode = False
    settings.translate_model_id = MODEL

    print("Translation model:", MODEL)
    print("(the first run downloads about 2.5 GB, then it works offline)\n")
    for expected, text in SAMPLES:
        start = time.time()
        result = translate(text, settings)
        took = time.time() - start
        print(f"[{expected}] {text}")
        print(f"   -> ({result.detected_language}) {result.translated_text}")
        print(f"   took {took:.1f}s\n")
    return 0


if __name__ == "__main__":
    sys.exit(main())
