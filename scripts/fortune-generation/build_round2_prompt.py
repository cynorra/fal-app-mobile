#!/usr/bin/env python3
"""Builds PROMPT_ROUND2.txt: the Gemini prompt for a second round of fortunes.

It reads the current seed (round 1), lists which symbols are already used per type, which cards/
angels are still unused, which words are overused, and attaches four approved examples.
Run:  python scripts/fortune-generation/build_round2_prompt.py
"""
import collections
import json
import os
import re

HERE = os.path.dirname(os.path.abspath(__file__))
SEED = os.path.join(HERE, "..", "..", "app", "src", "main", "assets", "seed_fortunes.json")
BASE = os.path.join(HERE, "PROMPT_GEMINI.txt")
OUT = os.path.join(HERE, "PROMPT_ROUND2.txt")

MAJOR = ["the fool", "the magician", "the high priestess", "the empress", "the emperor", "the hierophant",
         "the lovers", "the chariot", "strength", "the hermit", "wheel of fortune", "justice", "the hanged man",
         "death", "temperance", "the devil", "the tower", "the star", "the moon", "the sun", "judgement",
         "the world"]
RANKS = ["ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "page", "knight",
         "queen", "king"]
SUITS = ["wands", "cups", "swords", "pentacles"]
TAROT = MAJOR + [f"{r} of {s}" for s in SUITS for r in RANKS]


def sym(x):
    k = [t.lower() for t in x["keywordTags"]]
    return k[1] if x["type"] == "dream" and len(k) > 1 else k[0]


def main():
    d = json.load(open(SEED, encoding="utf-8"))["fortunes"]
    used = collections.defaultdict(set)
    for x in d:
        used[x["type"]].add(sym(x))

    lines = ["ROUND 1 ALREADY EXISTS. Do not use any of these as the main symbol of a reading in this round "
             "(except where a type below says reuse is allowed):"]
    for t in ["coffee", "tarot", "lenormand", "angel", "horoscope", "dream", "palm"]:
        lines.append(f"{t}: " + ", ".join(sorted(used[t])))
    def norm(c):
        return re.sub(r"^the ", "", c)

    used_norm = {norm(u) for u in used["tarot"]}
    unused_tarot = [c for c in TAROT if norm(c) not in used_norm]
    lines.append("")
    lines.append("Tarot: round 1 used 36 cards. Use these still-unused cards first, each at most once in this "
                 "round, spread across suits and Major cards: " + ", ".join(unused_tarot) + ".")
    lines.append("Lenormand: round 1 used all 36 cards once. In this round you may use each card at most once "
                 "more. Choose the card that fits the mood and focus best, and use a situation that differs "
                 "from the round 1 reading of the same card.")
    lines.append("Angel: you may use each angel at most 3 times in this round, always with a different "
                 "traditional aspect than before.")
    lines.append("Love: there is no symbol, so variety comes from the situation. Round 1 touched texts, "
                 "dinners, invitations, boundaries and cafes. Find other ordinary romantic moments.")

    kw = collections.Counter(k.lower() for x in d for k in x["keywordTags"])
    motifs = [w for w, n in kw.most_common(60) if n >= 5 and w not in {s for v in used.values() for s in v}
              and w not in ("dream", "dreamt", "palm", "hand", "cup", "rim", "grounds", "handle", "saucer")]
    lines.append("")
    lines.append("OVERUSED EVERYDAY DETAILS IN ROUND 1 (avoid them unless the tradition truly needs one): "
                 + ", ".join(motifs[:24]) + ".")
    lines.append("OVERUSED WORDS: quiet, small, plain, warm, steady, simple, fresh, real, actually, unhurried, "
                 "gentle, soft. Use none of them more than once in any batch. Also vary the time words: no more "
                 "than 4 readings of 12 may contain \"tonight\" and no more than 4 may contain \"today\".")
    lines.append("Pick everyday details from other parts of life: a commute, a neighbor, a pet, a birthday, "
                 "a pharmacy errand, a bus stop, a shared ride, a receipt, a library book, a recipe, a "
                 "voicemail, a plant, a weekend market, a repair, a laundry day, a school pickup.")
    used_block = "\n".join(lines) + "\n\n"

    best = {}
    for x in d:
        if x["rarity"] == "legendary" and x["type"] in ("coffee", "tarot", "lenormand", "angel") \
                and x["type"] not in best:
            best[x["type"]] = x
    examples = "\n".join(json.dumps(best[t], ensure_ascii=False) for t in best)
    ex_keys = ("type", "text", "dailyIntention", "moodTags", "focusTags", "keywordTags", "rarity")
    examples = "\n".join(json.dumps({k: best[t][k] for k in ex_keys}, ensure_ascii=False) for t in best)

    s = open(BASE, encoding="utf-8").read()

    def rep(a, b):
        nonlocal s
        assert a in s, a[:70]
        s = s.replace(a, b, 1)

    rep("Batches 1 to 6 (all coffee and tarot) are already done and approved, so start with batch 7 right now.",
        "A first round of 288 readings already exists and is listed below. This is round 2: you produce 288 "
        "NEW readings with the same plan, so start with batch 1 right now.")
    a = s.index("Coffee and tarot are complete.")
    b = s.index("TYPE BRIEFS")
    s = s[:a] + used_block + s[b:]
    a = s.index("QUALITY BAR")
    b = s.index("Rejected:")
    s = (s[:a] + "QUALITY BAR\nThese four approved readings from round 1 show the level of specificity, tone and "
         "structure expected. Copy the quality, not the subject, the symbols or the phrasing. Every type has "
         "its own voice (see the briefs).\n" + examples + "\n" + s[b:])
    rep("Now send batch 7 as a raw JSON array.", "Now send batch 1 as a raw JSON array.")
    open(OUT, "w", encoding="utf-8", newline="\n").write(s)
    print(f"wrote {OUT} ({len(s)} chars); unused tarot cards: {len(unused_tarot)}")


if __name__ == "__main__":
    main()
