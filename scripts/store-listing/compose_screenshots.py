"""
Builds Play Store phone screenshots and the feature graphic from REAL app screenshots.

  python compose_screenshots.py <raw_dir> <out_dir>

raw_dir holds PNGs captured from the app (810x1440 emulator captures, test-ad banner at the bottom
is cropped away). Output: out_dir/phone-XX-name.png (1080x1920) and feature-graphic-1024x500.png.
No image model is involved, so every pixel of UI is the genuine app.
"""
import math
import os
import random
import sys

from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

RAW, OUT = sys.argv[1], sys.argv[2]
ICON = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "store-listing", "assets", "icon-512.png")
os.makedirs(OUT, exist_ok=True)

FONTS = r"C:\Windows\Fonts"
SERIF_B = os.path.join(FONTS, "georgiab.ttf")
SANS = os.path.join(FONTS, "segoeui.ttf")
SANS_SB = os.path.join(FONTS, "seguisb.ttf") if os.path.exists(os.path.join(FONTS, "seguisb.ttf")) else os.path.join(FONTS, "segoeuib.ttf")

INDIGO, VIOLET, VIOLET_HI = (11, 8, 32), (42, 23, 104), (91, 53, 166)
GOLD, GOLD_HI = (212, 175, 55), (243, 215, 126)

# (file, [(text, is_gold), ...] headline words, subline)
SHOTS = [
    ("home", [("Your ", 0), ("cosmic", 1), (" guide,", 0), (" every day", 0)], "Tarot, horoscope and dreams in one calm place", "violet-right"),
    ("daily", [("Draw your ", 0), ("card", 1), (" of the day", 0)], "A fresh reading each morning", "gold-left"),
    ("tarot", [("Let the ", 0), ("cards", 1), (" speak", 0)], "Draw a spread whenever you need a nudge", "violet-left"),
    ("birth", [("Map your ", 0), ("birth chart", 1)], "Sun, Moon and Rising at a glance", "gold-right"),
    ("compat", [("Check zodiac ", 0), ("compatibility", 1)], "Love, trust and passion between signs", "violet-right"),
    ("dream", [("Decode your ", 0), ("dreams", 1)], "Search symbols and their meanings", "gold-left"),
    ("oracle", [("Yes or no? ", 0), ("Ask the orb", 1)], "A quick nudge when you need one", "violet-left"),
    ("fortune", [("Readings that ", 0), ("feel", 1), (" personal", 0)], "Pick your mood and focus", "gold-right"),
]


def font(path, size):
    return ImageFont.truetype(path, size)


def lerp(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def background(w, h, mood, seed):
    """Indigo night, soft aurora blobs, star dust, faint dot grid."""
    img = Image.new("RGB", (w, h))
    px = ImageDraw.Draw(img)
    for y in range(h):
        t = y / (h - 1)
        c = lerp(INDIGO, VIOLET, min(1, t * 1.6)) if t < 0.62 else lerp(VIOLET, INDIGO, (t - 0.62) / 0.38)
        px.line([(0, y), (w, y)], fill=c)

    def blob(cx, cy, rx, ry, color, alpha):
        layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        d = ImageDraw.Draw(layer)
        d.ellipse([cx - rx, cy - ry, cx + rx, cy + ry], fill=color + (alpha,))
        layer = layer.filter(ImageFilter.GaussianBlur(min(rx, ry) * 0.55))
        return layer

    side = -1 if mood.endswith("left") else 1
    main = VIOLET_HI if mood.startswith("violet") else GOLD
    base = img.convert("RGBA")
    base = Image.alpha_composite(base, blob(w / 2 + side * w * 0.45, h * 0.18, w * 0.5, h * 0.16, main, 120))
    base = Image.alpha_composite(base, blob(w / 2 - side * w * 0.5, h * 0.62, w * 0.45, h * 0.2, VIOLET_HI, 90))
    base = Image.alpha_composite(base, blob(w / 2 + side * w * 0.55, h * 0.9, w * 0.5, h * 0.12, GOLD, 55))
    img = base.convert("RGB")

    rnd = random.Random(seed)
    d = ImageDraw.Draw(img, "RGBA")
    for _ in range(int(w * h / 9000)):
        x, y = rnd.random() * w, rnd.random() * h
        r = rnd.choice([1, 1, 1, 2, 2, 3])
        d.ellipse([x - r, y - r, x + r, y + r], fill=(255, 255, 255, rnd.randint(40, 170)))
    step = 54
    for gx in range(step // 2, w, step):
        for gy in range(step // 2, h, step):
            d.ellipse([gx - 1, gy - 1, gx + 1, gy + 1], fill=(255, 255, 255, 14))
    return img


def gold_gradient_text(text, fnt):
    bbox = fnt.getbbox(text)
    tw, th = bbox[2] - bbox[0] + 8, bbox[3] - bbox[1] + 16
    mask = Image.new("L", (tw, th), 0)
    ImageDraw.Draw(mask).text((4 - bbox[0], 4 - bbox[1]), text, font=fnt, fill=255)
    grad = Image.new("RGB", (tw, th))
    gd = ImageDraw.Draw(grad)
    for y in range(th):
        gd.line([(0, y), (tw, y)], fill=lerp(GOLD_HI, (184, 134, 11), y / th))
    out = Image.new("RGBA", (tw, th), (0, 0, 0, 0))
    out.paste(grad, (0, 0), mask)
    return out, bbox


def draw_headline(canvas, words, y, max_w, size=88):
    """Centered, auto-shrinks until it fits on <= 2 lines; gold words use a gradient."""
    one_line = None
    for sz in range(size, 71, -4):
        f1 = font(SERIF_B, sz)
        if f1.getlength("".join(w[0] for w in words)) <= max_w:
            one_line = sz
            break
    if one_line:
        size = one_line
    while True:
        f = font(SERIF_B, size)
        lines, cur, cur_w = [], [], 0
        for text, gold in words:
            for token in text.replace(" ", "\u00a0 ").split(" "):
                if not token:
                    continue
                tw = f.getlength(token.replace("\u00a0", " "))
                if cur and cur_w + tw > max_w:
                    lines.append(cur)
                    cur, cur_w = [], 0
                cur.append((token.replace("\u00a0", " "), gold, tw))
                cur_w += tw
        lines.append(cur)
        if len(lines) <= 2 or size < 56:
            break
        size -= 4
    d = ImageDraw.Draw(canvas)
    line_h = int(size * 1.18)
    for li, line in enumerate(lines):
        total = sum(t[2] for t in line)
        x = (canvas.width - total) / 2
        for token, gold, tw in line:
            yy = y + li * line_h
            if gold:
                gt, bb = gold_gradient_text(token, f)
                canvas.paste(gt, (int(x) - 4, yy + bb[1] - 4), gt)
            else:
                d.text((x, yy), token, font=f, fill=(255, 255, 255))
            x += tw
    return y + len(lines) * line_h


def rounded_mask(size, radius):
    m = Image.new("L", size, 0)
    ImageDraw.Draw(m).rounded_rectangle([0, 0, size[0] - 1, size[1] - 1], radius=radius, fill=255)
    return m


def phone(shot_path, width, crop_top=70, crop_bottom=1308):
    """Real screenshot inside a slim bezel; the test-ad banner below the dock is cropped away."""
    src = Image.open(shot_path).convert("RGB")
    src = src.crop((0, crop_top, src.width, min(crop_bottom, src.height)))
    bez = 14 if width > 400 else 7
    inner_w = width - 2 * bez
    scale = inner_w / src.width
    screen = src.resize((inner_w, int(src.height * scale)), Image.LANCZOS)
    r_out = max(18, int(width * 0.087))
    r_in = r_out - 12 if width > 400 else r_out - 5
    h = screen.height + 2 * bez
    body = Image.new("RGBA", (width, h), (0, 0, 0, 0))
    bd = ImageDraw.Draw(body)
    bd.rounded_rectangle([0, 0, width - 1, h - 1], radius=r_out, fill=(14, 11, 30, 255))
    # hairline gold rim
    rim = Image.new("RGBA", (width, h), (0, 0, 0, 0))
    rd = ImageDraw.Draw(rim)
    rd.rounded_rectangle([0, 0, width - 1, h - 1], radius=r_out, outline=GOLD + (170,), width=3 if width > 400 else 2)
    body = Image.alpha_composite(body, rim)
    scr = screen.convert("RGBA")
    scr.putalpha(rounded_mask(screen.size, r_in))
    body.alpha_composite(scr, (bez, bez))
    # punch-hole camera
    if width > 400:
        ImageDraw.Draw(body).ellipse([width // 2 - 13, bez + 20, width // 2 + 13, bez + 46], fill=(8, 6, 18, 255))
    return body


def with_shadow(canvas, ph, pos, glow=(91, 53, 166), blur=60, alpha=150):
    sh = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    a = ph.split()[3].point(lambda v: alpha if v > 0 else 0)
    tile = Image.new("RGBA", ph.size, glow + (0,))
    tile.putalpha(a)
    sh.alpha_composite(tile, (pos[0], pos[1] + 30))
    sh = sh.filter(ImageFilter.GaussianBlur(blur))
    canvas.alpha_composite(sh)
    canvas.alpha_composite(ph, pos)


def build_phone_shots():
    for i, (name, words, sub, mood) in enumerate(SHOTS, 1):
        path = os.path.join(RAW, f"{name}.png")
        if not os.path.exists(path):
            print("missing", path)
            continue
        W, H = 1080, 1920
        canvas = background(W, H, mood, seed=i).convert("RGBA")
        y_end = draw_headline(canvas, words, 150, W - 160)
        d = ImageDraw.Draw(canvas)
        sf = font(SANS, 42)
        sw = sf.getlength(sub)
        d.text(((W - sw) / 2, y_end + 26), sub, font=sf, fill=(255, 255, 255, 190))
        ph = phone(path, 900)
        pos = ((W - ph.width) // 2, 560)
        with_shadow(canvas, ph, pos)
        out = os.path.join(OUT, f"phone-{i:02d}-{name}.png")
        canvas.convert("RGB").save(out, optimize=True)
        print("wrote", out)


def build_feature_graphic():
    W, H = 1024, 500
    canvas = background(W, H, "violet-right", seed=99).convert("RGBA")
    d = ImageDraw.Draw(canvas)

    icon = Image.open(ICON).convert("RGBA").resize((150, 150), Image.LANCZOS)
    icon.putalpha(ImageChops.multiply(icon.split()[3], rounded_mask(icon.size, 36)))
    canvas.alpha_composite(icon, (70, 120))
    lf = font(SERIF_B, 104)
    gt, bb = gold_gradient_text("Luna", lf)
    canvas.paste(gt, (240, 104 - bb[1] + 22), gt)
    tf = font(SANS, 33)
    d.text((72, 300), "Tarot, horoscope & dreams", font=tf, fill=(255, 255, 255, 235))
    d.text((72, 348), "Your daily cosmic guide", font=font(SANS, 26), fill=GOLD_HI + (230,))

    for k, (name, ang, x, y, wd) in enumerate([("daily", 8, 690, 40, 210), ("home", -7, 820, 90, 210)]):
        p = os.path.join(RAW, f"{name}.png")
        if not os.path.exists(p):
            continue
        ph = phone(p, wd, crop_top=70, crop_bottom=1100).rotate(ang, expand=True, resample=Image.BICUBIC)
        with_shadow(canvas, ph, (x, y), blur=28, alpha=120)
    out = os.path.join(OUT, "feature-graphic-1024x500.png")
    canvas.convert("RGB").save(out, optimize=True)
    print("wrote", out)


build_phone_shots()
build_feature_graphic()
