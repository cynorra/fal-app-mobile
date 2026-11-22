#!/usr/bin/env python3
"""Renders Luna Play Store graphics (icon 512x512, feature graphic 1024x500)
from the same geometry as the adaptive launcher icon (res/drawable/ic_launcher_*.xml).

Usage: python make_assets.py   ->  store-listing/assets/{icon-512.png, feature-graphic-1024x500.png}
"""
import math
import os
from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

OUT = os.path.join(os.path.dirname(__file__), "..", "..", "store-listing", "assets")
os.makedirs(OUT, exist_ok=True)

SS = 3  # supersampling factor


def radial(size, center, radius, stops):
    """stops: list of (t, (r,g,b)) with t in 0..1"""
    w, h = size
    img = Image.new("RGB", size)
    px = img.load()
    cx, cy = center
    for y in range(h):
        for x in range(w):
            t = min(1.0, math.hypot(x - cx, y - cy) / radius)
            for i in range(len(stops) - 1):
                t0, c0 = stops[i]
                t1, c1 = stops[i + 1]
                if t <= t1:
                    k = (t - t0) / (t1 - t0) if t1 > t0 else 0
                    px[x, y] = tuple(int(c0[j] + (c1[j] - c0[j]) * k) for j in range(3))
                    break
    return img


def linear(size, p0, p1, stops):
    w, h = size
    img = Image.new("RGB", size)
    px = img.load()
    dx, dy = p1[0] - p0[0], p1[1] - p0[1]
    L = dx * dx + dy * dy
    for y in range(h):
        for x in range(w):
            t = ((x - p0[0]) * dx + (y - p0[1]) * dy) / L
            t = max(0.0, min(1.0, t))
            for i in range(len(stops) - 1):
                t0, c0 = stops[i]
                t1, c1 = stops[i + 1]
                if t <= t1:
                    k = (t - t0) / (t1 - t0) if t1 > t0 else 0
                    px[x, y] = tuple(int(c0[j] + (c1[j] - c0[j]) * k) for j in range(3))
                    break
    return img


def sparkle_pts(cx, cy, s, n=24):
    """Same 4-point sparkle as the vector (quadratic curves through the centre)."""
    pts = []
    corners = [(cx, cy - s), (cx + s, cy), (cx, cy + s), (cx - s, cy)]
    for i in range(4):
        p0, p2 = corners[i], corners[(i + 1) % 4]
        for k in range(n):
            t = k / n
            x = (1 - t) ** 2 * p0[0] + 2 * (1 - t) * t * cx + t * t * p2[0]
            y = (1 - t) ** 2 * p0[1] + 2 * (1 - t) * t * cy + t * t * p2[1]
            pts.append((x, y))
    return pts


def render_mark(px_size, view):
    """Return RGBA 'mark' (crescent + sparkles) for the viewport (x0,y0,x1,y1) in 108-units."""
    x0, y0, x1, y1 = view
    scale = px_size / (x1 - x0)
    S = SS
    big = int(px_size * S)
    k = scale * S

    def T(x, y):
        return ((x - x0) * k, (y - y0) * k)

    # crescent = C1 minus C2
    m1 = Image.new("L", (big, big), 0)
    m2 = Image.new("L", (big, big), 0)
    d1, d2 = ImageDraw.Draw(m1), ImageDraw.Draw(m2)
    for d, (cx, cy, r) in ((d1, (50, 56, 26)), (d2, (60, 50, 22))):
        cxp, cyp = T(cx, cy)
        d.ellipse([cxp - r * k, cyp - r * k, cxp + r * k, cyp + r * k], fill=255)
    crescent = ImageChops.subtract(m1, m2)

    gold = linear((big, big), T(30, 30), T(76, 82),
                  [(0, (255, 241, 184)), (0.5, (232, 196, 90)), (1, (184, 134, 11))])
    layer = Image.new("RGBA", (big, big), (0, 0, 0, 0))
    layer.paste(gold, (0, 0), crescent)

    sp = Image.new("RGBA", (big, big), (0, 0, 0, 0))
    ds = ImageDraw.Draw(sp)
    for (cx, cy, s, col) in ((70, 46, 10, (255, 244, 204)),
                             (80, 64, 4.5, (255, 233, 168)),
                             (34, 34, 3, (255, 233, 168))):
        pts = [T(x, y) for x, y in sparkle_pts(cx, cy, s)]
        ds.polygon(pts, fill=col + (255,))
    layer = Image.alpha_composite(layer, sp)

    # soft glow
    glow = layer.filter(ImageFilter.GaussianBlur(px_size * S * 0.025))
    glow.putalpha(glow.getchannel("A").point(lambda a: int(a * 0.45)))
    out = Image.alpha_composite(glow, layer)
    return out.resize((px_size, px_size), Image.LANCZOS)


def background(w, h, center, radius):
    bg = radial((w, h), center, radius,
                [(0, (91, 53, 166)), (0.55, (42, 23, 104)), (1, (11, 8, 32))])
    dr = ImageDraw.Draw(bg)
    import random
    rnd = random.Random(7)
    for _ in range(int(w * h / 4500)):
        x, y = rnd.randrange(w), rnd.randrange(h)
        r = rnd.choice([1, 1, 1, 2])
        a = rnd.choice([70, 110, 160])
        dr.ellipse([x - r, y - r, x + r, y + r], fill=(255, 255, 255))
        # fake alpha by blending
    return bg


def icon_512():
    size = 512
    bg = background(size, size, (size / 2, size * 0.40), size * 0.78)
    mark = render_mark(size, (8, 10, 98, 100))  # crop slightly inside the 108 viewport, keeps padding
    img = bg.convert("RGBA")
    img = Image.alpha_composite(img, mark)
    img.convert("RGB").save(os.path.join(OUT, "icon-512.png"), optimize=True)


def feature_graphic():
    W, H = 1024, 500
    bg = background(W, H, (W * 0.28, H * 0.45), W * 0.75)
    img = bg.convert("RGBA")
    mark = render_mark(440, (14, 14, 94, 94))
    img.alpha_composite(mark, (50, 30))

    font_path = r"C:\Windows\Fonts\segoeuib.ttf"
    font_light = r"C:\Windows\Fonts\segoeui.ttf"
    d = ImageDraw.Draw(img)
    title = ImageFont.truetype(font_path, 118)
    sub = ImageFont.truetype(font_path, 40)
    tag = ImageFont.truetype(font_light, 32)
    x = 520
    d.text((x, 120), "Luna", font=title, fill=(255, 233, 168))
    d.text((x + 4, 262), "Tarot & Horoscope", font=sub, fill=(255, 255, 255))
    d.text((x + 4, 322), "Daily card · Birth chart", font=tag, fill=(220, 210, 245))
    d.text((x + 4, 366), "Zodiac match · Works offline", font=tag, fill=(220, 210, 245))
    img.convert("RGB").save(os.path.join(OUT, "feature-graphic-1024x500.png"), optimize=True)


if __name__ == "__main__":
    icon_512()
    feature_graphic()
    print("done:", os.path.abspath(OUT))
