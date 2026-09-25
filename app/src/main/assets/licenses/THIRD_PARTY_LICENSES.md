# Third-party software and dictionary data

Ghostwriter's own license is [GPL-3.0-or-later](LICENSE). The notices below
identify material that has its own authors and license terms. Copies of this
notice, the GPL text, the Unicode notice, and the Ghostwriter Dictionary MIT
notice are packaged in the APK's
`assets/licenses/` directory. The downloadable dictionary databases are data,
separate from the app's source code license.

## Ghostwriter Dictionary index code

The IPA tokenizer and search-key rules in `IpaSearchKeys.kt` and headword
normalization in `DictionaryHeadword.kt` are adapted from Ghostwriter Dictionary's
`scripts/generate_rhyme_db.py` and `scripts/clean_rhyme_wordlist.py`. Copyright (c) 2026
Ghostwriter Dictionary contributors. That source code is under the **MIT
License**; its complete notice is packaged in `Ghostwriter-Dict-MIT.txt`.
Ghostwriter's Kotlin adaptation is distributed as part of this GPL-3.0-or-later
app. The downloadable databases retain their separate CC BY-SA 4.0 terms.

## eSpeak NG 1.52.0

Ghostwriter includes eSpeak NG's native pronunciation engine and a reduced set
of its compiled English, German, and Turkish language data for offline IPA
generation. The source is pinned at commit
`4870adfa25b1a32b4361592f1be8a40337c58d6c` in
[`third_party/espeak-ng`](third_party/espeak-ng). Copyright belongs to the
eSpeak NG contributors, including the authors identified in the source files.
The engine is licensed under **GPL-3.0-or-later**. The complete GPL text is in
this repository's [LICENSE](LICENSE) and the upstream [COPYING](third_party/espeak-ng/COPYING).

The native build also uses eSpeak NG's `ucd-tools` code, which is
GPL-3.0-or-later. Its Unicode data carries the Unicode copyright and
permission notice in [COPYING.UCD](third_party/espeak-ng/src/ucd-tools/COPYING.UCD).
The upstream `ieee80.c` source additionally offers a BSD 2-clause license;
Ghostwriter uses its GPL-3.0-or-later option. The source retains both notices.
The Android build disables optional Sonic, MBROLA, PcAudio, Klatt, and
speechPlayer components. Build and data-generation details are in
[ESPEAK_NATIVE.md](documentation/ESPEAK_NATIVE.md).

## Downloadable rhyme dictionaries

The six databases in the
[`kaikki-v20260920` release](https://github.com/Prosincerity/Ghostwriter-Dict/releases/tag/kaikki-v20260920)
are offered under **Creative Commons Attribution-ShareAlike 4.0 International
(CC BY-SA 4.0)**. Their authoritative attribution and modification notice is
[Ghostwriter Dictionary's LICENSE-DATA.md](https://github.com/Prosincerity/Ghostwriter-Dict/blob/main/LICENSE-DATA.md).
The [CC BY-SA 4.0 legal text](https://creativecommons.org/licenses/by-sa/4.0/legalcode)
governs redistribution of these databases.

The `*_kaikki-*.db.gz` files contain pronunciations extracted from English,
German, and Turkish Wiktionary editions by
[Wiktextract](https://github.com/tatuylonen/wiktextract) and distributed through
[Kaikki.org](https://kaikki.org/dictionary/). Credit belongs to the
[Wiktionary contributors](https://en.wiktionary.org/wiki/Wiktionary:Copyrights).
The `*_espeak_kaikki-*.db.gz` files retain Wiktionary-derived headwords and
contain pronunciations generated with eSpeak NG 1.52. Both sets were filtered,
normalized, deduplicated, and converted into SQLite rhyme indexes by the
Ghostwriter Dictionary project. The source project's `LICENSE-DATA.md` records
the full modification list. The separate filenames preserve the provenance of
the pronunciations. They do not change eSpeak NG's software license.

Ghostwriter downloads these databases on request; they are **not included in
the APK**. The app's About screen provides attribution and license links even
before a database is downloaded. Any redistribution of a downloaded or adapted
database should carry the source project's attribution and modification notice
and comply with CC BY-SA 4.0. No affiliation with or endorsement by Wiktionary,
Wikimedia, Wiktextract, Kaikki.org, or their contributors is implied.
