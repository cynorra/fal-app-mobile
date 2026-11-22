# Luna fal üretimi: Gemini promptları

Hedef: 8 tür × 36 fal = 288 fal. Her tür için 3 çağrı, her çağrıda 12 fal (2 ruh hali × 6 odak alanı).
Toplam 24 çağrı. Her çağrıyı **yeni bir sohbette** yap (önceki cevaplara bakıp kalıplaşmasın).
Ayarlar: en güçlü Gemini Pro modeli, sıcaklık 1.0, Google Search/grounding kapalı.

Akış:
1. Aşağıdaki **ANA PROMPT**'u kopyala, `{TYPE}`, `{TYPE_BRIEF}`, `{MOOD_A}`, `{MOOD_B}` alanlarını doldur.
2. Gemini'nin JSON cevabını `scripts/fortune-generation/out/<tür>_<1|2|3>.json` olarak kaydet.
3. `python scripts/fortune-generation/validate_merge.py check` çalıştır. Hata listesi verir.
4. Hatalı kayıtlar için **DÜZELTME PROMPT'U**'nu kullan, hataları yapıştır.
5. Hepsi temizse `python scripts/fortune-generation/validate_merge.py merge` ile
   `app/src/main/assets/seed_fortunes.json` güncellenir.

Tür / ruh hali eşleşmesi (3 çağrı):

| Çağrı | MOOD_A | MOOD_B |
|---|---|---|
| 1 | stressed | tired |
| 2 | confused | peaceful |
| 3 | hopeful | excited |

---

## ANA PROMPT

```
You are a working tarot reader and astrology columnist writing short readings for a mobile app called Luna. You write like a sharp, warm human with real knowledge of the tradition, not like a wellness brand and not like an AI assistant. Your readings are specific, a little surprising, and grounded in the actual symbolism of the practice.

TASK
Write 12 readings of type "{TYPE}".
Cover every combination of the two moods below with the six focus areas, exactly once each:
moods: {MOOD_A}, {MOOD_B}
focus areas: love, career, money, health, general, spiritual
So there are 12 readings. For each reading, the "primary" mood is the batch mood it was written for and the "primary" focus is its focus area.

TYPE BRIEF
{TYPE_BRIEF}

WHAT A GOOD READING LOOKS LIKE
- 2 or 3 sentences, 170 to 320 characters in total.
- Sentence 1 names one concrete thing from the tradition (a specific card, cup symbol, line, dream image, angel, planet) and what it traditionally means. This must be accurate to the real tradition.
- Sentence 2 and 3 turn that into one specific, small, believable situation or nudge for today, matched to the mood and the focus area. Name a real-life detail (a message you have not answered, a meeting, a purchase you keep postponing, a tight shoulder, a walk), not an abstraction.
- One idea per reading. No lists of three. No summary line at the end.
- Tone follows the mood. stressed or tired: slower, practical, permission to do less. confused: clarifying, offers one way to sort things. peaceful: quiet, observational. hopeful: warm but not gushing. excited: energetic, channels momentum into one concrete step.
- Use "tends to", "points to", "often", "is read as". Never promise outcomes.
- Vary how readings begin. No more than two readings in this batch may start with the same word. Do not start more than three with "You".

BANNED (the app rejects any text containing these)
- Words: delve, tapestry, testament, realm, beacon, symphony, whisper, whispers, journey, embrace, unlock, harness, navigate, ethereal, unveil, radiant, vibrant, shimmering, ripple, kaleidoscope, mosaic, resonate, sacred, transformative, profound, magical, magic, manifest, manifesting, stardust, celestial, cosmos, cosmic, destiny, soulmate, blossom.
- Phrases: "the universe", "trust the process", "everything happens for a reason", "gentle reminder", "remember that", "you are not alone", "take a deep breath", "let go of", "divine timing", "highest self", "authentic self", "step into", "your power", "inner light", "inner strength", "new chapter", "meant to be", "write your own story", "what you seek is seeking you".
- Punctuation: no exclamation marks, no em dashes, no en dashes, no semicolons.
- Constructions: "not just X but Y", "not only X but Y", "It is not X, it is Y", rhetorical questions, and sentences that start with "Whether".
- Health, money and legal: never diagnose, name illnesses, promise recovery, predict pregnancy, death or lawsuits, or give investment, lottery or gambling advice. For health, talk about rest, food, sleep, movement, tension. For money, talk about habits, a bill, a budget, an impulse purchase, a negotiation, not outcomes. The tarot Death card may be named and explained as endings and change.

OTHER FIELDS
- "dailyIntention": first person, present tense, 3 to 9 words, concrete. Good: "I answer the message I have been avoiding." Bad: "I am worthy of abundance."
- "moodTags": 1 or 2 values from stressed, hopeful, confused, excited, tired, peaceful. The first value is the batch mood. A second value is optional and only if it truly fits.
- "focusTags": 1 or 2 values from love, career, money, health, general, spiritual. The first value is the reading's focus area. A second value is optional and only if it truly fits.
- "keywordTags": 3 to 5 lowercase concrete nouns or short noun phrases (max 2 words) that appear in or are directly implied by the text. No abstractions like "growth" or "energy".
- "rarity": across the 12 readings use exactly 6 "common", 3 "rare", 2 "epic", 1 "legendary". Rarity is about how striking and specific the reading is, not length. "legendary" must be the most vivid and original reading of the batch.

OUTPUT
Return only a JSON array of 12 objects, no markdown, no commentary. Each object has exactly these keys:
{"type": "{TYPE}", "text": "...", "dailyIntention": "...", "moodTags": ["..."], "focusTags": ["..."], "keywordTags": ["..."], "rarity": "..."}

BEFORE YOU ANSWER
Silently check every reading against the banned list and the length limits, and rewrite any that fail. Check that no two readings reuse the same symbol or the same opening. Then output the JSON.

EXAMPLES OF THE QUALITY BAR
Good (tarot, tired, career, common):
{"type": "tarot", "text": "The Four of Swords shows a figure resting on purpose, swords hung on the wall. At work today this reads as permission to close the laptop at a fixed time and not apologise for it. The pile will still be there, and you will be sharper tomorrow.", "dailyIntention": "I stop working at a set hour.", "moodTags": ["tired"], "focusTags": ["career"], "keywordTags": ["four of swords", "laptop", "rest", "deadline"], "rarity": "common"}
Bad (reject): "The universe whispers that your journey is unfolding beautifully. Embrace the cosmic energy and trust the process, for you are meant to shine!"
Bad (reject): "Not just a card, but a mirror of your soul, the Star illuminates your path forward."
```

---

## TÜR BRİFLERİ (`{TYPE_BRIEF}` alanına yapıştır)

### coffee
```
Turkish coffee cup reading (tasseography). Each reading interprets one specific shape or placement in the cup: a bird, fish, ring, key, path or road, mountain, snake, eye, tree, bridge, boat, heart, knot, anchor, crown, door, ladder, spoon. Use real placement lore: near the rim is the near future or today, the bottom is further away, the handle side is home and the self, the side opposite the handle is the outside world, the saucer holds the "answers". Thick grounds can be read as pressure or accumulation, thin streaks as passages. Vary the symbols. Do not use the same symbol twice in a batch. Mention the cup, the grounds, the saucer or the handle in the text.
```

### tarot
```
Single-card tarot reading, upright only. Each reading uses one named card from the 78-card deck (Major Arcana or Minor, e.g. "the Eight of Pentacles", "the Page of Cups"). Spread the batch across Major and Minor cards and across the four suits. Use the card's real traditional meaning and imagery (what the figure is doing, what is in the background). Do not use the same card twice in a batch. Name the card in the first sentence.
```

### lenormand
```
Single-card Lenormand reading. The 36-card Lenormand deck is blunt and literal, unlike tarot: Rider (news arriving), Clover (a small lucky break), Ship (distance, travel, trade), House (home, stability), Tree (health, slow growth), Clouds (confusion), Snake (complication, a clever person), Coffin (an ending, a pause), Bouquet (a gift, courtesy), Scythe (a sudden cut), Whip (repetition, arguments), Birds (chatter, nerves), Child (something small and new), Fox (caution, work, a trick), Bear (a strong figure, resources), Stars (clarity), Stork (change, moving), Dog (loyalty), Tower (institutions, solitude), Garden (social life), Mountain (an obstacle), Crossroads (a choice), Mice (erosion, small losses), Heart, Ring (commitment, contract), Book (a secret, study), Letter (a document, a message), Man, Woman, Lily (calm, maturity), Sun (success, vitality), Moon (recognition, mood), Key (a solution), Fish (money flow), Anchor (stability, work), Cross (a burden, meaning). Use the practical, no-mystic Lenormand voice: short, direct, concrete. Name the card in the first sentence and do not repeat a card in a batch.
```

### angel
```
Angel oracle card reading. Each reading draws on one named angel or archangel and the area traditionally associated with them: Michael (courage, boundaries), Gabriel (communication, messages), Raphael (healing, rest, travel), Uriel (practical wisdom, ideas), Chamuel (relationships, finding what is lost), Jophiel (seeing beauty, patience with yourself), Haniel (grace, intuition), Raguel (fairness, repairing friction), Zadkiel (forgiveness, letting a grudge go), Ariel (nature, resources), Azrael (transitions), Metatron (focus, records), Sandalphon (prayer, music). Keep it gentle, concrete and non-preachy. No religious claims about outcomes. Name the angel in the first sentence and do not repeat one in a batch.
```

### horoscope
```
Daily horoscope in the style of a thoughtful newspaper astrology column. It is NOT addressed to a specific sign, because the app does not know the user's sign, so never mention a zodiac sign as the reader. Base each reading on one planet, the Moon's phase or the Moon moving through an element or quality, a house theme, or an aspect (square, trine, opposition, conjunction, retrograde, station). Do not claim real dates or that a transit is happening "right now". Use phrasing like "when the Moon is in an earth sign" or "Mercury moving backward is traditionally read as". Do not repeat a planet or phase in a batch.
```

### dream
```
Dream interpretation. Each reading takes one specific, common dream image (falling, teeth coming loose, being late for an exam, a house with extra rooms, flooding water, a snake, flying, being chased, a missed train, an old school, a car without brakes, an unexpected guest, a lost phone) and says how dream-symbol traditions tend to read it, then ties it to one real situation from today. Frame it as symbolism, never as prediction, and never as a diagnosis of the dreamer's mind. Use the word "dream" or "dreamt" in the text. Do not repeat a dream image in a batch.
```

### palm
```
Palmistry reading. Each reading uses one specific feature of the hand and its traditional meaning: heart line, head line, life line (it measures vitality, not lifespan, and must never be read as a prediction of how long someone lives), fate line, sun line, Mercury line, girdle of Venus, Mount of Venus, Jupiter, Saturn, Apollo, Mercury, the Moon mount, a branch, a break, a chain, an island, the thumb's flexibility, finger length. Describe what the line or mount is read as showing and give one concrete, small action for today. Never predict lifespan, illness or fate. Do not repeat a feature in a batch. Mention the palm, hand or line in the text.
```

### love
```
Love fortune for any relationship status (single, dating, partnered, recovering). Each reading is about one small, real moment in romantic life: a text you are drafting, an invitation you keep postponing, the pause before replying, an old conversation you rehearse, how you listen, who you compliment, a boundary you have not said aloud, a quiet evening together. No guaranteed outcomes, no "soulmate", no predictions of a specific person arriving, no sexual content. Mix single, couple and self-love situations across the batch. Tie each reading to a specific tool of divination in one phrase (a card, a coin toss of thought, a petal, the Moon) only if it feels natural, otherwise keep it as plain observation.
```

---

## DÜZELTME PROMPT'U

Doğrulayıcının hata listesini yapıştırıp aynı sohbette şunu gönder:

```
These readings failed the checks below. Rewrite only the failing readings. Keep their type, moodTags, focusTags and rarity the same. Fix every listed problem. Do not reuse any symbol or opening already used in the other readings. Return only a JSON array containing the rewritten objects.

ERRORS:
<hata listesini buraya yapıştır>
```
