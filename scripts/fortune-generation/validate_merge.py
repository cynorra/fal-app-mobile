#!/usr/bin/env python3
"""Validate Gemini-generated fortunes and merge them into the app's seed file.

  python scripts/fortune-generation/validate_merge.py paste   # clipboard (Gemini reply) -> out/NN_type.json + check
  python scripts/fortune-generation/validate_merge.py fix     # clipboard (repaired items) -> replaces them in the last file
  python scripts/fortune-generation/validate_merge.py check   # check everything in out/
  python scripts/fortune-generation/validate_merge.py merge   # write app/src/main/assets/seed_fortunes.json

Input : scripts/fortune-generation/out/*.json  (each file = JSON array, see PROMPTS.md)
Output: app/src/main/assets/seed_fortunes.json (merge only, and only if check passes)
"""
import glob
import itertools
import json
import os
import re
import subprocess
import sys
import time
from collections import Counter, defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
OUT_DIR = os.path.join(HERE, "out")
SEED = os.path.join(HERE, "..", "..", "app", "src", "main", "assets", "seed_fortunes.json")

TYPES = ["coffee", "tarot", "lenormand", "angel", "horoscope", "dream", "palm", "love"]
MOODS = ["stressed", "hopeful", "confused", "excited", "tired", "peaceful"]
FOCUS = ["love", "career", "money", "health", "general", "spiritual"]
RARITY = ["common", "rare", "epic", "legendary"]
KEYS = {"type", "text", "dailyIntention", "moodTags", "focusTags", "keywordTags", "rarity"}

BANNED_WORDS = """delve tapestry testament realm beacon symphony whisper whispers journey embrace unlock harness
navigate ethereal unveil radiant vibrant shimmering ripple ripples kaleidoscope mosaic resonate resonates sacred
transformative profound magical magic manifest manifesting stardust celestial cosmos cosmic destiny soulmate
blossom blossoms illuminate illuminates tranquil serenity""".split()
BANNED_PHRASES = [
    "the universe", "trust the process", "everything happens for a reason", "gentle reminder",
    "remember that", "you are not alone", "take a deep breath", "let go of", "divine timing",
    "highest self", "authentic self", "step into", "your power", "inner light", "inner strength",
    "new chapter", "meant to be", "write your own story", "what you seek", "in the grand scheme",
    "rich tapestry", "dance of", "sea of", "ever-changing", "ever changing",
]
BANNED_PATTERNS = [
    (r"\bnot (just|only|merely|simply)\b", "'not just/only X but Y' construction"),
    (r"\b(it|this|that)(?:'s| is) not [^.]{1,60}?[,;] ?(it|this|that)(?:'s| is)\b", "'it is not X, it is Y' construction"),
    (r"\?", "question mark / rhetorical question"),
    (r"^\s*whether\b|[.!?] +whether\b", "sentence starting with 'Whether'"),
    (r"!", "exclamation mark"),
    (r"[—–]", "em/en dash"),
    (r";", "semicolon"),
]
SAFETY = [
    r"\bdiagnos\w*", r"\bcure[sd]?\b", r"\bdisease\w*", r"\billness\w*", r"\bcancer\b", r"\bpregnan\w*",
    r"\blawsuit\w*", r"\binvest\w*", r"\bstocks?\b", r"\bcrypto\w*", r"\blotter\w*", r"\bgambl\w*",
    r"\bguarantee\w*", r"\bwill definitely\b", r"\bsurely\b", r"\bdie[sd]?\b", r"\bdying\b",
    r"\blife expectancy\b", r"\bshort life\b", r"\blong life\b",
]
GROUND = {
    "coffee": r"\b(cup|grounds|saucer|handle|rim)\b",
    "tarot": r"\b(Fool|Magician|High Priestess|Empress|Emperor|Hierophant|Lovers|Chariot|Strength|Hermit|Wheel of Fortune|Justice|Hanged Man|Death|Temperance|Devil|Tower|Star|Moon|Sun|Judgement|World|Ace|Two|Three|Four|Five|Six|Seven|Eight|Nine|Ten|Page|Knight|Queen|King) (of (Wands|Cups|Swords|Pentacles)|card)\b|\b(Fool|Magician|High Priestess|Empress|Emperor|Hierophant|Lovers|Chariot|Strength|Hermit|Wheel of Fortune|Justice|Hanged Man|Temperance|Devil|Tower|Star|Judgement|World|Moon|Sun|Death)\b",
    "lenormand": r"\b(Rider|Clover|Ship|House|Tree|Clouds|Snake|Coffin|Bouquet|Scythe|Whip|Birds|Child|Fox|Bear|Stars|Stork|Dog|Tower|Garden|Mountain|Crossroads|Mice|Heart|Ring|Book|Letter|Man|Woman|Lily|Sun|Moon|Key|Fish|Anchor|Cross)\b",
    "angel": r"\b(Archangel|Michael|Gabriel|Raphael|Uriel|Chamuel|Jophiel|Haniel|Raguel|Zadkiel|Ariel|Azrael|Metatron|Sandalphon|Raziel|Jeremiel|Barachiel|Jegudiel|Selaphiel|Cassiel|Camael)\b",
    "horoscope": r"\b(Moons?|Mercury|Venus|Mars|Jupiter|Saturn|Uranus|Neptune|Pluto|Sun|eclipse|retrograde|square|trine|opposition|conjunction|house)\b",
    "dream": r"\b(dream|dreams|dreamt|dreamed|dreaming)\b",
    "palm": r"\b(palms?|hands?|lines?|mounts?|fingers?|thumbs?)\b",
}
SIGNS = r"\b(Aries|Taurus|Gemini|Cancer|Leo|Virgo|Libra|Scorpio|Sagittarius|Capricorn|Aquarius|Pisces)\b"


TIC_WORDS = ["quiet", "plain", "small", "warm", "steady", "simple", "fresh", "actually", "real",
             "unhurried", "gentle", "soft"]
GENERALIZERS = (r"\b(rarely|usually|often|always|never|beats|settles|clears|keeps|makes|feels|"
                r"works better|asks nothing|is enough|only need|shrinks|announce)\b")
TIME_WORDS = r"\b(today|tonight|tomorrow|this|now|before|after|until|week|morning|evening|afternoon|hour|minutes)\b"


IMPERATIVE_OK = set("""send pick move drink sign pause suggest open put take grab tell cancel look tidy check write skip cook
call close enjoy treat lace delay do ignore choose set update leave say ask book plan invite sit read walk stop start
try keep let make name list note circle drop mute share reply answer text email finish begin hand pour cut sleep eat
go turn pack clear find notice give stretch breathe light spend add wear play bring order buy cancel decide rest
water fold lay hang spread wash clean sort count thank forgive meet visit hold stand wait use offer limit trade
swap store save pay review print review lower raise bake brew boil step ring knock listen speak talk""".split())
LEAD_OK = {"if", "when", "before", "after", "instead", "then", "once", "tonight", "today", "this", "and", "so",
           "while", "until", "unless", "try", "you", "your", "tell"}


def closing_aphorism(sentences):
    """True if the last sentence of a 3+ sentence reading is a generic moral instead of a concrete action."""
    if len(sentences) < 3:
        return False
    last = sentences[-1].strip()
    words = last.split()
    if len(words) > 18:
        return False
    first = re.sub(r"[^a-z]", "", words[0].lower())
    if first in IMPERATIVE_OK or first in LEAD_OK:
        # imperative or situational: only reject if it is still a pure generalization
        return bool(re.search(GENERALIZERS, last, re.I)) and not re.search(TIME_WORDS, last, re.I) and first in ("you", "your")
    return True


def load_items():
    items = []
    for path in sorted(glob.glob(os.path.join(OUT_DIR, "*.json"))):
        raw = open(path, encoding="utf-8").read().strip()
        raw = re.sub(r"^```(?:json)?\s*|\s*```$", "", raw)
        try:
            data = json.loads(raw)
        except json.JSONDecodeError as e:
            print(f"FATAL {os.path.basename(path)}: invalid JSON ({e})")
            sys.exit(2)
        if not isinstance(data, list):
            print(f"FATAL {os.path.basename(path)}: top level must be an array")
            sys.exit(2)
        for i, it in enumerate(data):
            items.append((os.path.basename(path), i + 1, it))
    return items


def shingles(text, n=4):
    w = re.findall(r"[a-z']+", text.lower())
    return {" ".join(w[i:i + n]) for i in range(max(0, len(w) - n + 1))}


def check(items):
    errors, warns = [], []
    err = lambda f, i, m: errors.append(f"{f}#{i}: {m}")
    wrn = lambda f, i, m: warns.append(f"{f}#{i}: {m}")
    bytype = defaultdict(list)

    for f, i, it in items:
        if not isinstance(it, dict):
            err(f, i, "not an object")
            continue
        extra, missing = set(it) - KEYS, KEYS - set(it)
        if missing:
            err(f, i, f"missing keys {sorted(missing)}")
            continue
        if extra:
            err(f, i, f"unexpected keys {sorted(extra)}")
        t = it["type"]
        if t not in TYPES:
            err(f, i, f"unknown type {t!r}")
            continue
        text = it["text"].strip()
        low = text.lower()
        bytype[t].append((f, i, it))

        n = len(text)
        if not 150 <= n <= 340:
            err(f, i, f"text length {n} (want 170-320)")
        elif not 170 <= n <= 320:
            wrn(f, i, f"text length {n} (want 170-320)")
        sents = [s for s in re.split(r"(?<=[.!?])\s+", text) if s]
        if not 2 <= len(sents) <= 4:
            err(f, i, f"{len(sents)} sentences (want 2-3)")
        if closing_aphorism(sents):
            err(f, i, f"closing aphorism instead of a concrete action: '{sents[-1]}'")

        for w in BANNED_WORDS:
            if re.search(rf"\b{w}\b", low):
                err(f, i, f"banned word '{w}'")
        for p in BANNED_PHRASES:
            if p in low:
                err(f, i, f"banned phrase '{p}'")
        for pat, why in BANNED_PATTERNS:
            if re.search(pat, text, re.I):
                err(f, i, why)
        scan = text.replace("Death card", "")
        for pat in SAFETY:
            m = re.search(pat, scan, re.I)
            if m:
                err(f, i, f"unsafe health/money/legal wording '{m.group(0)}'")
        if t in GROUND and not re.search(GROUND[t], text, re.I if t in ("coffee", "dream", "palm", "horoscope") else 0):
            err(f, i, f"text is not grounded in the {t} tradition (no expected symbol/term)")
        if t == "horoscope" and re.search(SIGNS, text):
            err(f, i, "horoscope must not address a zodiac sign")
        if len(re.findall(r"\benerg(?:y|ies)\b", low)) > 1:
            wrn(f, i, "uses 'energy' more than once")

        di = it["dailyIntention"].strip()
        words = di.split()
        if not 3 <= len(words) <= 10:
            err(f, i, f"dailyIntention has {len(words)} words (want 3-9)")
        if not re.match(r"^(I|My|Today I|Today, I|Let me)\b", di):
            err(f, i, "dailyIntention must be first person")
        for pat, why in BANNED_PATTERNS:
            if re.search(pat, di, re.I):
                err(f, i, f"dailyIntention: {why}")
        for w in BANNED_WORDS:
            if re.search(rf"\b{w}\b", di.lower()):
                err(f, i, f"dailyIntention: banned word '{w}'")
        if re.search(r"\b(worthy|abundance|enough)\b", di.lower()):
            wrn(f, i, "dailyIntention sounds like a generic affirmation")

        for key, allowed, lo in (("moodTags", MOODS, 1), ("focusTags", FOCUS, 1)):
            v = it[key]
            if not isinstance(v, list) or not lo <= len(v) <= 2 or any(x not in allowed for x in v) or len(set(v)) != len(v):
                err(f, i, f"{key} must be 1-2 unique values from {allowed}, got {v}")
        kw = it["keywordTags"]
        if not isinstance(kw, list) or not 3 <= len(kw) <= 5:
            err(f, i, "keywordTags must have 3-5 items")
        else:
            for k in kw:
                if k != k.lower() or len(k.split()) > 3:
                    err(f, i, f"keywordTag {k!r} must be lowercase, max ~2 words")
                stem = re.sub(r"(ing|ed|es|s)$", "", k.lower().split()[-1])
                if stem and stem not in low:
                    wrn(f, i, f"keywordTag {k!r} not found in text")
        if it["rarity"] not in RARITY:
            err(f, i, f"rarity {it['rarity']!r} invalid")

    # cross-item checks, per type
    for t, lst in bytype.items():
        starts = Counter(re.sub(r"[^a-z ]", "", x["text"].lower()).split()[0] for _, _, x in lst)
        for w, c in starts.items():
            if c > max(3, len(lst) // 6):
                errors.append(f"[{t}] {c} readings start with '{w}' (max {max(3, len(lst) // 6)})")
        you = sum(x["text"].lstrip().lower().startswith("you") for _, _, x in lst)
        if you > max(3, len(lst) * 0.3):
            errors.append(f"[{t}] {you} readings start with 'You'")
        sh = [(f, i, shingles(x["text"])) for f, i, x in lst]
        for (f1, i1, s1), (f2, i2, s2) in itertools.combinations(sh, 2):
            if s1 and s2 and len(s1 & s2) / len(s1 | s2) > 0.25:
                errors.append(f"[{t}] near-duplicate text: {f1}#{i1} and {f2}#{i2}")
        trips = sum(bool(re.search(r"\b\w+, \w+,? and \w+\b", x["text"])) for _, _, x in lst)
        if trips > len(lst) * 0.25:
            warns.append(f"[{t}] {trips}/{len(lst)} readings contain an 'A, B and C' triple")
        dis = Counter(x["dailyIntention"].lower().strip(" .") for _, _, x in lst)
        for d, c in dis.items():
            if c > 1:
                errors.append(f"[{t}] duplicate dailyIntention: '{d}'")
        for w in TIC_WORDS:
            n_w = sum(bool(re.search(rf"\b{w}\b", x["text"].lower())) for _, _, x in lst)
            if n_w > max(4, len(lst) * 0.25):
                warns.append(f"[{t}] overused word '{w}' in {n_w}/{len(lst)} readings")
        r = Counter(x["rarity"] for _, _, x in lst)
        if r["legendary"] > max(1, len(lst) // 8) or r["epic"] > len(lst) // 4:
            warns.append(f"[{t}] rarity skew {dict(r)}")

    all_items = [it for _, _, it in items if isinstance(it, dict) and "text" in it]
    for w in TIC_WORDS + ["tonight", "today"]:
        n_w = sum(bool(re.search(rf"\b{w}\b", x["text"].lower())) for x in all_items)
        limit = 0.45 if w in ("tonight", "today") else 0.12
        if all_items and n_w > len(all_items) * limit:
            warns.append(f"[all] '{w}' appears in {n_w}/{len(all_items)} readings ({n_w * 100 // len(all_items)}%)")
    return errors, warns, bytype


def coverage(bytype):
    lines = []
    for t in TYPES:
        lst = bytype.get(t, [])
        grid = {(x["moodTags"][0], x["focusTags"][0]) for _, _, x in lst if x.get("moodTags") and x.get("focusTags")}
        miss = [f"{m}/{f}" for m in MOODS for f in FOCUS if (m, f) not in grid]
        lines.append(f"{t:10s} {len(lst):3d} readings, missing mood/focus combos: {len(miss)}" + (f" ({', '.join(miss[:6])}{'...' if len(miss) > 6 else ''})" if miss else ""))
    return lines


def clip_get():
    r = subprocess.run(
        ["powershell", "-NoProfile", "-Command",
         "[Console]::OutputEncoding=[Text.Encoding]::UTF8; Get-Clipboard -Raw"],
        capture_output=True)
    return r.stdout.decode("utf-8", "replace")


def clip_set(text):
    tmp = os.path.join(OUT_DIR, "_clip.txt")
    with open(tmp, "w", encoding="utf-8", newline="") as fh:
        fh.write(text)
    subprocess.run(["powershell", "-NoProfile", "-Command",
                    f"Get-Content -Raw -Encoding UTF8 '{tmp}' | Set-Clipboard"], check=False)
    os.remove(tmp)


def parse_array(raw):
    raw = raw.strip()
    raw = re.sub(r"^```(?:json)?\s*|\s*```$", "", raw)
    data = json.loads(raw)
    if not isinstance(data, list):
        raise ValueError("top level must be a JSON array")
    return data


def parse_arrays(raw):
    """Parse one or several back-to-back JSON arrays (Gemini sometimes sends 2-3 batches in one reply)."""
    raw = re.sub(r"```(?:json)?", "", raw).strip()
    dec, i, out = json.JSONDecoder(), 0, []
    while i < len(raw):
        while i < len(raw) and raw[i] in " \r\n\t,":
            i += 1
        if i >= len(raw):
            break
        obj, i = dec.raw_decode(raw, i)
        if not isinstance(obj, list):
            raise ValueError("top level must be JSON arrays")
        out.append(obj)
    if not out:
        raise ValueError("no JSON array found")
    return out


def key_of(x):
    return (x.get("type"), (x.get("moodTags") or [None])[0], (x.get("focusTags") or [None])[0])


def report(latest=None):
    items = load_items()
    errors, warns, bytype = check(items)
    total = len(items)
    print(f"{total} readings")
    if latest:
        errors = [e for e in errors if latest in e or e.startswith("[")]
        warns = [w for w in warns if latest in w or w.startswith("[")]
    if warns:
        print(f"{len(warns)} warnings:")
        print("\n".join("  " + w for w in warns[:30]))
    return items, errors, bytype


REPAIR = ("These readings failed the checks below. Rewrite only the failing readings. Keep their type, "
          "moodTags, focusTags and rarity the same. Fix every listed problem. Do not reuse any symbol or "
          "opening already used elsewhere in this conversation. Return only a JSON array containing the "
          "rewritten objects.\n\nERRORS:\n")


def finish(latest, errors):
    if errors:
        print(f"\n{len(errors)} ERRORS:")
        print("\n".join("  " + e for e in errors))
        clip_set(REPAIR + "\n".join(errors))
        print("\nDuzeltme mesaji panoya kopyalandi: Gemini'ye yapistir, cevabini kopyala, sonra 'fix' calistir.")
        sys.exit(1)
    print("\nOK. Gemini'ye 'next' yaz.")


def cmd_paste():
    try:
        arrays = parse_arrays(clip_get())
    except Exception as e:
        print(f"Panodaki metin gecerli JSON dizisi degil: {e}")
        sys.exit(2)
    os.makedirs(OUT_DIR, exist_ok=True)
    known = {x.get("text", "").strip() for _, _, x in load_items() if isinstance(x, dict)}
    saved = []
    for arr in arrays:
        texts = [x.get("text", "").strip() for x in arr if isinstance(x, dict)]
        if texts and all(t in known for t in texts):
            print(f"ATLANDI: {len(arr)} falin hepsi zaten kayitli (Gemini onceki parti tekrar gondermis).")
            continue
        n = len(glob.glob(os.path.join(OUT_DIR, "[0-9][0-9]_*.json"))) + 1
        types = "-".join(sorted({x.get("type", "x") for x in arr if isinstance(x, dict)}))
        name = f"{n:02d}_{types}.json"
        with open(os.path.join(OUT_DIR, name), "w", encoding="utf-8") as fh:
            json.dump(arr, fh, ensure_ascii=False, indent=1)
        known.update(texts)
        saved.append(name)
        print(f"Kaydedildi: {name} ({len(arr)} fal)")
    if not saved:
        print("Yeni parti yok. Gemini'ye 'Send the next batch, not the previous one' yaz.")
        sys.exit(3)
    items, errors, _ = report()
    errors = [e for e in errors if any(s in e for s in saved) or e.startswith("[")]
    finish(saved[-1], errors)


def cmd_fix():
    files = sorted(glob.glob(os.path.join(OUT_DIR, "[0-9][0-9]_*.json")))
    if not files:
        print("Duzeltilecek dosya yok")
        sys.exit(2)
    path = files[-1]
    name = os.path.basename(path)
    try:
        fixes = parse_array(clip_get())
    except Exception as e:
        print(f"Panodaki metin gecerli JSON dizisi degil: {e}")
        sys.exit(2)
    cur = json.load(open(path, encoding="utf-8"))
    idx = {key_of(x): k for k, x in enumerate(cur)}
    for fx in fixes:
        k = idx.get(key_of(fx))
        if k is None:
            print(f"Eslesmeyen duzeltme atlandi: {key_of(fx)}")
            continue
        cur[k] = fx
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(cur, fh, ensure_ascii=False, indent=1)
    print(f"Guncellendi: {name} ({len(fixes)} fal degisti)")
    _, errors, _ = report(name)
    finish(name, errors)


def main():
    cmd = sys.argv[1] if len(sys.argv) > 1 else "check"
    if cmd == "paste":
        return cmd_paste()
    if cmd == "fix":
        return cmd_fix()
    items = load_items()
    if not items:
        print(f"No input found in {OUT_DIR}")
        sys.exit(2)
    errors, warns, bytype = check(items)
    print(f"{len(items)} readings in {len(set(f for f, _, _ in items))} files")
    print("\n".join(coverage(bytype)))
    if warns:
        print(f"\n{len(warns)} warnings:")
        print("\n".join("  " + w for w in warns[:60]))
    if errors:
        print(f"\n{len(errors)} ERRORS:")
        print("\n".join("  " + e for e in errors))
        sys.exit(1)
    print("\nOK: no errors")
    if cmd != "merge":
        return
    now = int(time.time() * 1000)
    counters, out = Counter(), []
    for t in TYPES:
        for _, _, x in bytype.get(t, []):
            counters[t] += 1
            out.append({
                "id": f"{t}_{counters[t]:03d}", "type": t, "text": x["text"].strip(),
                "dailyIntention": x["dailyIntention"].strip(), "moodTags": x["moodTags"],
                "focusTags": x["focusTags"], "keywordTags": x["keywordTags"],
                "rarity": x["rarity"], "createdAt": now,
            })
    with open(SEED, "w", encoding="utf-8", newline="\n") as fh:
        json.dump({"fortunes": out}, fh, ensure_ascii=False, indent=2)
        fh.write("\n")
    print(f"Merged {len(out)} fortunes -> {os.path.abspath(SEED)}")


if __name__ == "__main__":
    main()
