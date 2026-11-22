// Generates the Play Console multi-language listing CSV for Luna.
// Run: node scripts/store-listing/generate.mjs
import { writeFileSync, mkdirSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { A } from "./content-a.mjs";
import { B } from "./content-b.mjs";
import { C } from "./content-c.mjs";

const here = dirname(fileURLToPath(import.meta.url));
const OUT = join(here, "..", "..", "store-listing", "play_listing_all_locales.csv");

const LOCALES = `af am ar az-AZ be bg bn-BD ca cs-CZ da-DK de-DE el-GR en-AU en-CA en-GB en-IN en-SG en-US en-ZA
es-419 es-ES es-US et eu-ES fa fa-AE fa-AF fa-IR fi-FI fil fr-CA fr-FR gl-ES gu hi-IN hr hu-HU hy-AM id is-IS it-IT
iw-IL ja-JP ka-GE kk km-KH kn-IN ko-KR ky-KG lo-LA lt lv mk-MK ml-IN mn-MN mr-IN ms ms-MY my-MM ne-NP nl-NL no-NO
pa pl-PL pt-BR pt-PT rm ro ru-RU si-LK sk sl sq sr sv-SE sw ta-IN te-IN th tr-TR uk ur vi zh-CN zh-HK zh-TW zu`
  .split(/\s+/);

const CONTENT = { ...A, ...B, ...C };

// Locale -> content key. Anything not listed falls back to English.
const MAP = {
  "en-US": "en", "tr-TR": "tr", "es-ES": "es", ar: "ar", "de-DE": "de", "fr-FR": "fr",
  "pt-BR": "pt-BR", "ja-JP": "ja-JP", "ru-RU": "ru-RU", "it-IT": "it-IT", "ko-KR": "ko-KR",
  id: "id", "hi-IN": "hi-IN", "pl-PL": "pl-PL", "nl-NL": "nl-NL", vi: "vi", th: "th",
  "zh-CN": "zh-CN", "zh-TW": "zh-TW", fa: "fa", uk: "uk", ro: "ro", "sv-SE": "sv-SE",
  "cs-CZ": "cs-CZ", "el-GR": "el-GR", "hu-HU": "hu-HU", "iw-IL": "iw-IL", ms: "ms", fil: "fil",
  "da-DK": "da-DK", "fi-FI": "fi-FI", "no-NO": "no-NO", bg: "bg", hr: "hr", sk: "sk",
  // nearest-language reuse
  "en-AU": "en", "en-CA": "en", "en-GB": "en", "en-IN": "en", "en-SG": "en", "en-ZA": "en",
  "es-419": "es", "es-US": "es", ca: "es", "gl-ES": "es", "eu-ES": "es",
  "fr-CA": "fr", "pt-PT": "pt-BR", "zh-HK": "zh-TW",
  "fa-AE": "fa", "fa-AF": "fa", "fa-IR": "fa", "ms-MY": "ms",
  be: "ru-RU", kk: "ru-RU", "ky-KG": "ru-RU", sr: "hr"
};
MAP["de-DE"] = "de"; // content key is "de"

const cp = (s) => [...s].length;
const full = (c) => `${c.intro}\n\n${c.head}\n${c.bullets.map((b) => "• " + b).join("\n")}\n\n${c.outro}`;
const q = (s) => (/[",\r\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s);

const errors = [];
const rows = ["Language,Title,Short description,Full description"];
const report = { translated: 0, fallback: [] };

for (const loc of LOCALES) {
  const key = MAP[loc] ?? "en";
  const c = CONTENT[key];
  if (!c) { errors.push(`${loc}: missing content key ${key}`); continue; }
  if (!MAP[loc]) report.fallback.push(loc);
  const f = full(c);
  if (cp(c.title) > 30) errors.push(`${loc}/${key}: title ${cp(c.title)} > 30`);
  if (cp(c.short) > 80) errors.push(`${loc}/${key}: short ${cp(c.short)} > 80`);
  if (cp(f) > 4000) errors.push(`${loc}/${key}: full ${cp(f)} > 4000`);
  if (c.bullets.length !== 11) errors.push(`${loc}/${key}: bullets ${c.bullets.length}`);
  rows.push([loc, c.title, c.short, f].map(q).join(","));
}
if (!LOCALES.includes("en-US")) errors.push("en-US missing");

if (errors.length) {
  console.error("VALIDATION FAILED:\n" + errors.join("\n"));
  process.exit(1);
}
mkdirSync(dirname(OUT), { recursive: true });
writeFileSync(OUT, rows.join("\r\n") + "\r\n", { encoding: "utf8" }); // no BOM
console.log(`OK: ${LOCALES.length} locales -> ${OUT}`);
console.log(`own translations: ${Object.keys(CONTENT).length} languages`);
console.log(`English fallback (${report.fallback.length}): ${report.fallback.join(" ")}`);
