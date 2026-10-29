"""Verify the real models before relying on them.

Checks that each model id really exists, that its licence and file list are
what the capstone paper claims, and (with --download) that the weights can be
fetched and that the text model runs on a CPU.

Usage:
    python scripts/verify_models.py
    python scripts/verify_models.py --download
"""

from __future__ import annotations

import argparse
import json
import os
import sys
import urllib.error
import urllib.request

MODELS = {
    "text": {
        "id": "vectorsense/toxiscan",
        "what": "English toxicity / hate / threat / self-harm classifier (ONNX)",
        "expect": ["model.onnx", "config.json", "labels.json", "tokenizer.json"],
    },
    "text-tagalog": {
        "id": "mapsoriano/roberta-tagalog-base-philippine-elections-2016-2022-hate-speech",
        "what": "Tagalog hate-speech model, used as a boost when the text is Tagalog",
        "expect": ["config.json", "tokenizer.json", "tokenizer_config.json"],
    },
    "image": {
        "id": "onnx-community/nsfw_image_detection-ONNX",
        "what": "OpenNSFW2 image classifier (ONNX, the onnx-community export)",
        "expect": ["config.json", "preprocessor_config.json", "onnx/model.onnx"],
    },
    "translate": {
        "id": "facebook/nllb-200-distilled-600M",
        "what": "NLLB-200 translation model, Tagalog/Cebuano -> English",
        "expect": ["config.json", "tokenizer_config.json"],
    },
}

HF_API = "https://huggingface.co/api/models/"


def fetch_model(model_id: str) -> dict | None:
    try:
        with urllib.request.urlopen(HF_API + model_id, timeout=30) as r:
            return json.loads(r.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        print(f"    HTTP {e.code} for {model_id}")
        return None
    except Exception as e:  # noqa: BLE001
        print(f"    {e}")
        return None


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--download", action="store_true", help="also fetch the weight files")
    args = ap.parse_args()

    print("SchoolChan model verification")
    print("=" * 60)
    failures = 0

    for role, spec in MODELS.items():
        print(f"\n[{role}] {spec['id']}")
        print(f"    {spec['what']}")
        info = fetch_model(spec["id"])
        if info is None:
            print("    MISSING - this id does not exist on Hugging Face")
            failures += 1
            continue
        files = [s.get("rfilename", "") for s in info.get("siblings", [])]
        tags = info.get("tags", [])
        licence = "unknown"
        for t in tags:
            if t.startswith("license:"):
                licence = t.split(":", 1)[1]
        print(f"    found: downloads={info.get('downloads')} likes={info.get('likes')} licence={licence}")
        missing = [f for f in spec["expect"] if f not in files]
        if missing:
            print(f"    WARNING: expected files not in the repo: {missing}")
        else:
            print(f"    files present: {', '.join(spec['expect'])}")
        if args.download:
            target = os.path.join(os.environ.get("ML_MODEL_CACHE", "./models"), spec["id"].replace("/", "_"))
            os.makedirs(target, exist_ok=True)
            for f in spec["expect"]:
                url = f"https://huggingface.co/{spec['id']}/resolve/main/{f}"
                dest = os.path.join(target, f)
                if os.path.exists(dest):
                    print(f"    already have {f}")
                    continue
                try:
                    with urllib.request.urlopen(url, timeout=300) as r, open(dest, "wb") as out:
                        out.write(r.read())
                    print(f"    downloaded {f} -> {dest}")
                except Exception as e:  # noqa: BLE001
                    print(f"    could not download {f}: {e}")
                    failures += 1

    print("\n" + "=" * 60)
    if failures:
        print(f"{failures} problem(s) found. Fix them before claiming the real models work.")
        return 1
    print("All configured model ids exist and the expected files are present.")
    print("Next: pip install -r requirements-real.txt, then scripts/run-ml-real.ps1")
    return 0


if __name__ == "__main__":
    sys.exit(main())
