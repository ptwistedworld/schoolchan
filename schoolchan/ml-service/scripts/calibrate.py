"""Calibration helper: run classifier over a fixture and print score distributions."""
import argparse
import json
import statistics
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.text_classifier import classify_text
from app.config import get_settings


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("fixture", help="JSONL with {text, label}")
    args = parser.parse_args()

    scores = {"toxicity": [], "hate": [], "harassment": [], "sexual": [], "threat": [], "self_harm": []}
    with open(args.fixture, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            obj = json.loads(line)
            r = classify_text(obj["text"])
            d = r.as_dict()
            for c in scores:
                scores[c].append(d[c])

    for c, vals in scores.items():
        if vals:
            print(f"{c}: min={min(vals):.3f} max={max(vals):.3f} mean={statistics.mean(vals):.3f} median={statistics.median(vals):.3f}")


if __name__ == "__main__":
    main()