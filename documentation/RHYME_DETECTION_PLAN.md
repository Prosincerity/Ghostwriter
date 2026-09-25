# Rhyme detection: dictionary and eSpeak NG integration plan

Status: in progress on the `rhyme-detection` branch created from `dev`.
Complete and verify each step before starting the next one.

## Goal and data flow

Provide offline IPA for English (`en`), German (`de`), and Turkish (`tr`) so
later rhyme detection can use the prepared Ghostwriter Dictionary indexes.
For each word, query the matching language's Wiktionary database first, then
its eSpeak database, then generate IPA through the bundled eSpeak NG 1.52 JNI
wrapper if neither database contains the word. Preserve every database IPA
variant and record which source supplied the result. A runtime result is a
fallback, not a new row written into the shipped databases.

The dictionary producer is
[Prosincerity/Ghostwriter-Dict](https://github.com/Prosincerity/Ghostwriter-Dict).
Its release naming scheme is `<lang>_kaikki-vYYYYMMDD.db.gz` and
`<lang>_espeak_kaikki-vYYYYMMDD.db.gz` for each of `en`, `de`, and `tr`.
The app downloads either archive independently for a selected language from a pinned
GitHub release. Each installed source works offline. The app ships the
eSpeak fallback and its language data, so missing or not-yet-downloaded
databases do not prevent IPA generation.

The corrected release is
[`kaikki-v20260920`](https://github.com/Prosincerity/Ghostwriter-Dict/releases/tag/kaikki-v20260920).
The app's `app/src/main/assets/dictionary_release.json` pins its six exact
asset URLs and published compressed sizes. Keep this manifest explicit when
updating releases; do not construct asset URLs from an assumed date.

The producer's SQLite schema is `dictionary(word, ipa, ipa_reversed,
assonance_reversed)` with primary key `(word, ipa)` and indexes on the two
reversed fields. The data contains separate Wiktionary and eSpeak indexes.
Its `scripts/generate_espeak_ipa.py` uses `espeak-ng -q --ipa -v <lang>`; a
sample comparison must check that the JNI output matches this command and
the producer's normalization policy.

## Steps

### 1. Pin the release inputs

- The corrected release URL, six asset names, and release identifier are
  recorded in `app/src/main/assets/dictionary_release.json`. Use the finished
  `.db.gz` assets, not intermediate wordlists or raw archives.
  Use the schema documented in the producer's README; a full content audit,
  row count, and SQLite integrity scan are outside this integration step.
- The producer used eSpeak NG `1.52`. The app pins source and generated language
  data to release `1.52.0` at commit `4870adfa25b1a32b4361592f1be8a40337c58d6c`.
- Confirm English voice/accent selection and IPA formatting against the
  producer's command, especially stress marks and tie/separator behavior.
- Build `arm64-v8a` for devices and `x86_64` for the Android Studio emulator.
  The six current archives total about 127 MB compressed and 467 MB unpacked;
  let users download individual sources so they choose the storage cost.

Exit check: the checked-in release manifest identifies the corrected public
release and every asset without relying on a local path. Done for
`kaikki-v20260920`; the maintainer confirmed eSpeak version 1.52 and the two ABIs.

### 2. Download, install, and read the SQLite databases

- Add a user-initiated Download action for each language and source, showing the
  archive size, progress, completion, and retry. Use an explicit `INTERNET`
  permission and Android/AOSP networking APIs without adding a networking
  library. Avoid network access during normal editing or lookup. Keep the
  previously installed release usable if a download fails or is cancelled.
- Fetch the selected language's Wiktionary or eSpeak `.db.gz` asset from the
  pinned HTTPS release URL into a temporary file, then stream-decompress it
  into versioned app-private internal storage. Activate each source only after
  its download completes; clean up partial files. SQLite needs the
  decompressed files. Report insufficient storage and network errors clearly.
- For local development, use the same `.gz` archives from the dictionary
  checkout as input to the installer through a debug or instrumented-test
  fixture path. Keep local archive paths, usernames, and generated files out
  of tracked documentation and app releases.
- Use Android's built-in `SQLiteDatabase` APIs; do not add Room or a new
  database framework. Open installed databases read-only.
- Implement a small repository with parameterized exact-word queries, a
  language allowlist, and explicit Wiktionary-then-eSpeak precedence. Return
  all IPA variants for the first source that contains the word. Keep spelling
  normalization consistent with the producer: NFC, case-sensitive identity,
  and its documented punctuation policy. Do not silently change the shipped
  SQLite schema or merge the two sources.
- Handle absent databases and database-version upgrades without blocking the
  editor or losing a previously installed pair. Keep dictionary storage
  separate from project/beat storage.

Exit check: JVM tests cover lookup/precedence/variants and instrumented tests
exercise local-archive installation, lookup, failed downloads, and offline
reuse on an AOSP emulator or device.

Implementation note: Settings now links to a dedicated download list with separate
per-language source controls, and the app has a versioned installer and read-only lookup path. Synthetic instrumented
tests are present and compile. The maintainer confirmed instrumented tests,
downloading, and cancelling on an Android Studio virtual emulator.

### 3. Build a minimal eSpeak NG 1.52 Android library

- Pin the upstream `1.52.0` source as a Git submodule or an explicitly
  documented source snapshot under `third_party/`; do not import its Android
  TTS app as a Gradle module. Use its CMake core library target and link a
  Ghostwriter JNI target from `app/src/main/cpp/`. Pin the NDK/CMake versions
  used by the working build and document a clean checkout command.
- Generate matching eSpeak data on the build host. Initially package the
  complete generated `espeak-ng-data/` tree so the Android proof is reliable;
  then reduce it to English, German, Turkish, their compiled dictionaries,
  and required shared phoneme/configuration data, validating all three after
  each reduction. Do not compile data on the phone. Keep the data version
  coupled to the library version.
- Build for each selected ABI, check packaged `.so` files and native runtime
  dependencies, and check 16 KB page compatibility on a suitable device or
  emulator before shipping.

Exit check: `assembleDebug` succeeds from a clean checkout and the data files
and native library are present in the APK for every selected ABI.

Implementation note: the pinned 1.52.0 submodule and reduced data bundle build
for both ABIs. The host generation script, exact asset list, build settings,
and verification are documented in [ESPEAK_NATIVE.md](ESPEAK_NATIVE.md).

### 4. Add the IPA-only JNI wrapper and prove it on Android

- Copy bundled data once to an app-private directory and pass its parent to
  `espeak_Initialize(AUDIO_OUTPUT_SYNCHRONOUS, ...)`. Report initialization
  errors instead of crashing when data is missing.
- Expose a narrow Kotlin API such as `ipa(word, language): String?`. Select
  one of `en`, `de`, or `tr`; pass UTF-8 input to
  `espeak_TextToPhonemes(..., espeakCHARS_UTF8, espeakPHONEMES_IPA)`; copy the
  returned UTF-8 result into an owned Java/Kotlin string immediately.
  eSpeak has global voice/output state, so serialize native calls and run
  initialization/conversion away from the editor's main thread. No audio
  playback, TTS service, microphone, or network permission is needed.
- Compare representative and out-of-vocabulary words, including German
  umlauts and Turkish dotted/dotless I, against the producer's 1.52 CLI
  `--ipa -v` output. Preserve or document any formatting difference before
  connecting the wrapper to lookup.

Exit check: instrumented tests on Android return nonempty IPA for all three
languages, match agreed CLI fixtures, and handle bad language/input/data
errors without crashing. This is the runtime proof for the native approach.

Implementation note: the narrow JNI bridge, offline data extraction, and
three-language comparison tests are present. The host API produces the same
IPA as the pinned CLI fixtures. The maintainer reports the JNI instrumented
tests passed in Android Studio with airplane mode enabled.

### 5. Join database lookup and runtime fallback

- Build a single pronunciation service used by future dictionary/rhyme UI:
  Wiktionary database, eSpeak database, then JNI for a true miss. Make failure
  and source explicit in its result type. Avoid JNI calls for database hits.
- Apply the producer's word eligibility/normalization rules before the
  fallback. Keep a small bounded in-memory cache for repeated OOV words if
  measurements show editor typing causes repeated native work.
- Test known hits, multiple variants, source precedence, OOV words, and
  unsupported languages. Run the service on a background dispatcher.

Exit check: installed databases and the bundled JNI fallback work with
airplane mode enabled and no Google services on the device.

Implementation note: the read-only lookup now falls through from Wiktionary
to eSpeak database to local generation on a background dispatcher. Device
tests passed in airplane mode. The existing dictionary lookup tests inject a
fake IPA generator and the JNI test exercises real eSpeak separately. A new
instrumented test now covers the complete service with real JNI after a
database miss; it still needs to be run in Android Studio.

### 6. Implement rhyme queries and editor behavior

- Add a Dictionary Screen opened by a book button in the Editor top bar.
  Its input accepts a word and displays that word's IPA from the selected
  language database, followed by matching words. Keep the hand-rolled
  navigation and return to the same editor project.
- For the default rhyme mode, look up the word's IPA first. Find the vowel
  of its primary-stressed syllable and require every phoneme from that vowel
  through the end of the word to match. Query `ipa_reversed` with that one
  complete reversed rime, then verify each candidate's stressed rime. A
  single-vowel pronunciation can omit a stress marker; an unmarked
  multi-vowel pronunciation has no reliable rime. Bound result counts so
  common endings do not flood the screen or stall typing. Rank short
  pronunciations first before applying the scan limit, so exact monosyllabic
  rhymes are not lost behind many longer compounds with the same ending.
- Provide a dropdown with rhyme, word-prefix, word-suffix, and assonance
  search modes. Word-prefix searches the `word` column. Word-suffix searches
  `ipa_reversed` using the input pronunciation's last x-2, x-4, x-6, ...
  phonemes, stopping before a tier of two or fewer phonemes. Input
  pronunciations with one to four phonemes instead match their entire
  pronunciation.
  Each candidate's suffix is checked exactly, and longer suffix matches rank
  first across both sources.
  Assonance uses `assonance_reversed`. Parameterize every query and escape
  literal `%`, `_`, and the chosen `LIKE` escape character. Benchmark suffix
  searches on realistic data. Keep searches off the main thread and bound
  result counts.
- Initially search installed databases. A later pass uses bundled eSpeak NG
  to generate IPA only when the input word is OOV in both sources, then uses
  that IPA for rhyme/assonance searches. Show when the IPA was generated.
- Use the existing `ipa_reversed` and `assonance_reversed` indexes for rhyme
  candidate searches. Port the dictionary producer's audited phoneme-token
  rules where a runtime IPA must produce compatible search keys; do not
  reverse raw Unicode characters. Add the documented SQLite
  `PRAGMA case_sensitive_like = ON` for prefix queries and escape user input.
- Define and test rhyme matching, multiple pronunciations, identity-rhyme
  treatment, language selection, and Dictionary Screen behavior before
  coloring syllables in `LyricsNotepad`. Keep the editor responsive and its
  autosave behavior intact. Keep syllable/bar gutter work scoped to the
  existing roadmap.

Exit check: pure JVM tests cover rhyme-key and matching logic; Compose/device
tests cover the chosen editor behavior and performance with realistic lines.

Implementation note: a first Dictionary Screen is reachable from the Editor's
book button and returns to the same project. It offers rhyme, word-prefix,
word-suffix, and assonance searches over installed databases, with bounded
pages of 60 results, source labels, and generated IPA for missing input words.
Previous and Next controls sit below the results. Search reads indexed database
rows in batches as pages are requested, preserving the match order across pages.
The IPA tokenizer and headword cleanup are ported from the dictionary producer with
JVM fixture tests. The language menu lists only installed languages, combining
the two English sources under English. With no dictionaries installed, the
screen links to dictionary downloads in Settings. Each menu opens beside its
own label.

Rhyme and assonance matching excludes the queried spelling. Different
spellings with the same pronunciation remain eligible. Search queries run on
an IO dispatcher. Rhyme mode requires an exact stressed rime instead of
trying shorter reversed prefixes. Synthetic database and Compose tests have
been added and compile; the earlier Android Studio test run passed. The new
pagination, exact-rime, phoneme-suffix, and compound-ending device tests still
need to run in Android Studio. Real-database performance, especially
word-suffix scans, and syllable coloring remain.

Rhyme results omit a longer word when its written ending is
another returned word (or the query word), the shorter word's vowel-through-end
IPA matches, and the longer word has an earlier vowel. This suppresses
compound endings such as `checkpoint`/`point` and German `-schaft`/`Schaft`
without removing one-syllable rhymes such as `bat`/`at`. Candidates are checked
against the query and earlier matches so later pages cannot remove a result
from an earlier page. Each page displays at most 60 matches. Word-suffix mode
keeps compounds because it searches the exact phoneme suffix at each length.

### 7. Licensing, attribution, and release verification

- Ghostwriter's project grant is `GPL-3.0-or-later`, matching eSpeak NG's
  `GPL-3.0-or-later` grant. Keep the repository and in-app notices consistent.
  This does not change the dictionary databases' CC BY-SA 4.0 attribution and
  sharing terms.
- Include eSpeak NG's GPL-3.0-or-later copyright/license notices, exact source
  revision, any changes, and reproducible native build instructions/source
  availability with distributed builds. Review licenses of the bundled eSpeak
  language data and any native dependencies before release.
- Keep the six dictionary databases identified as CC BY-SA 4.0 data. Carry
  Ghostwriter Dictionary's `LICENSE-DATA.md` attribution and modification
  notice, including Wiktionary contributors, Wiktextract/Kaikki, release
  provenance, and the separate eSpeak-generated source labels. Include a link
  to the CC BY-SA 4.0 legal text in the app and distribution materials. Do not
  relabel the dictionary data as GPL app code.
- Update the About screen and project documentation with accurate source and
  version details. Run `./gradlew test lint`, build instrumented tests, verify
  real-device/emulator behavior and download/offline behavior, inspect APK
  contents/size, and update the project graph with `graphify update .` after
  code changes.

Exit check: a clean release build can be reproduced, all attribution is
visible, and the offline device verification passes.
