"""Sweep moderation thresholds and report precision/recall tradeoff."""
import argparse, json, sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("fixture")
    args = ap.parse_args()
    rows = [json.loads(l) for l in open(args.fixture) if l.strip()]
    print("threshold\tprecision\trecall\tf1")
    for t in [i/100 for i in range(30, 96, 5)]:
        tp = fp = fn = tn = 0
        for r in rows:
            # synthetic: treat TOKEN_BLOCK as positive prediction above threshold
            pred = 1 if ("TOKEN_BLOCK" in r["text"].upper() and t <= 0.9) else 0
            actual = 1 if r["label"] == "violating" else 0
            if pred and actual: tp += 1
            elif pred and not actual: fp += 1
            elif (not pred) and actual: fn += 1
            else: tn += 1
        prec = tp/(tp+fp) if tp+fp else 0
        rec = tp/(tp+fn) if tp+fn else 0
        f1 = 2*prec*rec/(prec+rec) if prec+rec else 0
        print(f"{t:.2f}\t{prec:.3f}\t{rec:.3f}\t{f1:.3f}")

if __name__ == "__main__":
    main()