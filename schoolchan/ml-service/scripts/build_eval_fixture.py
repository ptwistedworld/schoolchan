"""Build a labelled evaluation fixture for the SchoolChan benchmark harness.

The fixture is synthetic and safe: it contains no real abuse, threats or
explicit content. Text is assembled from harmless fragments plus the
``TOKEN_*`` placeholders the pipeline understands, so the harness can measure
the pipeline without putting harmful material in the repository.

Each line is JSON:
    {"id": "1", "text": "...", "label": "violating|non-violating",
     "subcategory": "...", "language": "en|tl|ceb"}

Usage:
    python scripts/build_eval_fixture.py --out ../data/fixtures/eval-sample.jsonl
"""

from __future__ import annotations

import argparse
import json
import random
from pathlib import Path

SAFE = {
    "en": [
        "Good morning everyone, see you at the school fair later.",
        "Does anyone have a spare copy of the textbook?",
        "The library opens at seven, so go early if you need a quiet desk.",
        "Anyone up for basketball practice this afternoon?",
        "Please bring an ID and a parent contact form to the office.",
        "That new coffee place near the gate is surprisingly good.",
        "Reminder: club signups close on Friday at noon.",
        "I left a blue umbrella in room 204 if someone finds it.",
        "The exam schedule changed, check the board outside the library.",
        "Congrats to the choir for the weekend performance.",
        "Study group for Calculus is forming, add your name on the sheet.",
        "Lost a set of keys near the court, blue lanyard.",
        "What time does the canteen open on Saturdays?",
        "The internet was slow again during the last period.",
        "Sharing my notes for last week's lesson, hope they help.",
        "Anyone else bring food for the picnic?",
        "Our class cleaned the classroom and it looks great.",
        "The first batch of tickets sold out already.",
        "Has anyone seen a black folder near the computer lab?",
        "Good luck on the exams, you can do it.",
    ],
    "tl": [
        "Magandang umaga sa inyong lahat! Kita-kita tayo sa paaralan.",
        "May extra ba kayong kopya ng aklat?",
        "Bukas ang library at pitong, pumunta agad kung kailangan mo ng tahimik.",
        "Sino interested sa basketball practice mamaya?",
        "Dalhin ninyo ang ID at parent contact form papunta sa office.",
        "Salamat sa lahat sa tulong ninyo sa proyekto.",
        "Ang ganda ng performance ng choir kahapon.",
        "Nag-aapply pa ba kayo sa club? Isara na sa isang araw.",
        "May nahanap ba kayo na susi sa basketball court?",
        "Kumusta kayo? Ako ay maayos lang.",
        "Nakalimutan ko ang libro sa bahay, may extra ba kayo?",
        "Sino ang gusto mag-join sa picnic?",
        "Good luck sa exam, kayang kaya ninyo iyan.",
        "Ang initaw sa kantin ngayon, masarap tlaga.",
    ],
    "ceb": [
        "Maayo nga umaga sa inyong tanan! Kitang kita sa school fair.",
        "Adto ka bay ad nga extra nga imong libro?",
        "Nga abo gikan siyent ithirty ang library, early ka if quiet ka gusto.",
        "Kinsa ang gusto mag-basketball practice karon nga hapon?",
        "Bring mo ang ID ug parent contact form kung going to the office.",
        "Salamat sa inyong tabang sa project.",
        "Nindot man performance sa choir kagahapon.",
        "Naa pa ka nga apply sa club? Naa katanan ang last day sa Friday.",
        "Nasa di ka man makita nga mga key sa court?",
        "Naokay ka man gud, unsa man naimong problema?",
        "Naiakong nga akong library sa akong balay, may extra ka?",
        "Kinsa ang gusto mag-join sa picnic?",
        "Good luck sa exam, kaya ninyo tanan.",
        "Sayon pagkaon sa kantin karong adlawa, maayo gyud.",
    ],
}

VIOLATING = {
    "en": [
        "TOKEN_BLOCK you are worthless and nobody wants you here.",
        "TOKEN_BLOCK stop posting this garbage everywhere.",
        "TOKEN_ESCALATE_THREAT I will find you and make you regret it.",
        "TOKEN_BLOCK shut up you stupid idiot.",
        "TOKEN_ESCALATE_THREAT I am going to hurt you after school.",
        "TOKEN_BLOCK nobody cares about your useless opinion.",
        "TOKEN_BLOCK go away you pathetic loser.",
        "TOKEN_ESCALATE_THREAT wait outside the gate tonight alone.",
    ],
    "tl": [
        "TOKEN_BLOCK isang kawalang-hanggang ka, walang gusto sa iyo dito.",
        "TOKEN_BLOCK tahimik ka na, walang ka lamang saysag.",
        "TOKEN_ESCALATE_THREAT hahanapin ko ka at gagawin kong mag-regret ka.",
        "TOKEN_BLOCK ikaw ay walang kwenta at nakakahiya ka.",
        "TOKEN_ESCALATE_THREAT pagkatapos ng klase, hintayin mo ako sa labas.",
        "TOKEN_BLOCK tumigil ka na, sayang ang oras mo.",
    ],
    "ceb": [
        "TOKEN_BLOCK walay worth ka ug walay gustay mo diri.",
        "TOKEN_BLOCK tahimik ka na, sayaw ra imong nawno.",
        "TOKEN_ESCALATE_THREAT huntok ka nako ug makapay ka nga maaga.",
        "TOKEN_BLOCK ikaw gayud nga kaayo og pila, matag na ka.",
        "TOKEN_ESCALATE_THREAT humani nako ka sa giate human sukad.",
        "TOKEN_BLOCK imong gikan diri lamang gyud, kay ambal.",
    ],
}

SUBCATEGORIES = {
    "TOKEN_BLOCK": "harassment",
    "TOKEN_ESCALATE_THREAT": "threat",
}

# Rows that carry no keyword marker, so only the language model can catch them.
# These are deliberately mild everyday insults - nothing threatening, explicit
# or hateful, and no real person's words.
ML_ONLY = {
    "en": [
        "You are such a moron, nobody wants you here.",
        "What a complete idiot, honestly.",
        "You are pathetic and everybody knows it.",
        "Only an idiot would post this.",
    ],
    "tl": [
        "Ikaw ay isang bakla, walang gusto sa iyo dito.",
        "Ang kapal ng talino mo, parang bingi.",
        "Ikaw ay nakakahiya, walang ka magandang sinasabi.",
    ],
    "ceb": [
        "Ikaw man ang usa ka bweta, walay gustay mo diri.",
        "Unsay kaayo, wala ka nga utsa.",
        "Ikaw man ang akong giisay, maayo kaayo ka.",
    ],
}


def build(count_per_cell: int, seed: int) -> list[dict]:
    rng = random.Random(seed)
    rows: list[dict] = []
    n = 1
    for language, texts in SAFE.items():
        for text in texts:
            for _ in range(count_per_cell):
                rows.append(
                    {
                        "id": str(n),
                        "text": text,
                        "label": "non-violating",
                        "subcategory": "clean",
                        "language": language,
                    }
                )
                n += 1
    for language, texts in VIOLATING.items():
        for text in texts:
            for _ in range(count_per_cell):
                sub = SUBCATEGORIES.get("TOKEN_ESCALATE_THREAT" if "THREAT" in text else "TOKEN_BLOCK", "harassment")
                rows.append(
                    {
                        "id": str(n),
                        "text": text,
                        "label": "violating",
                        "subcategory": sub,
                        "language": language,
                    }
                )
                n += 1
    for language, texts in ML_ONLY.items():
        for text in texts:
            for _ in range(count_per_cell):
                rows.append(
                    {
                        "id": str(n),
                        "text": text,
                        "label": "violating",
                        "subcategory": "ml-only",
                        "language": language,
                    }
                )
                n += 1
    rng.shuffle(rows)
    for i, row in enumerate(rows, start=1):
        row["id"] = str(i)
    return rows


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default="../data/fixtures/eval-sample.jsonl")
    ap.add_argument("--repeat", type=int, default=3, help="copies of each sentence")
    ap.add_argument("--seed", type=int, default=20261021)
    args = ap.parse_args()

    rows = build(args.repeat, args.seed)
    out = Path(args.out)
    out.parent.mkdir(parents=True, exist_ok=True)
    with out.open("w", encoding="utf-8") as f:
        for row in rows:
            f.write(json.dumps(row, ensure_ascii=False) + "\n")

    violating = sum(1 for r in rows if r["label"] == "violating")
    print(f"Wrote {len(rows)} rows ({violating} violating) to {out}")
    for language in ("en", "tl", "ceb"):
        subset = [r for r in rows if r["language"] == language]
        v = sum(1 for r in subset if r["label"] == "violating")
        print(f"  {language}: {len(subset)} rows, {v} violating")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
