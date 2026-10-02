# Dictionaries

Ghostwriter provides pronunciation and rhyme search for English (`en`),
German (`de`), and Turkish (`tr`). Dictionary data is produced by
[Ghostwriter Dictionary](https://github.com/Prosincerity/Ghostwriter-Dict).
Each language has separate Wiktionary-derived and eSpeak-generated indexes.

## Using dictionaries

Open **Settings → Download Dictionaries** to download a source. Sources can be
installed independently, with archive size, progress, cancellation, and retry
shown in the download screen. Network access is needed for downloads; normal
editing and installed-dictionary searches work offline.

The editor's book button opens the Dictionary screen and returns to the same
project. Select an installed language and a search mode, then enter a word.
Results show pronunciation and source labels, with Previous and Next controls.
If no dictionaries are installed, the screen links to downloads.

The bundled pronunciation engine can generate IPA (International Phonetic
Alphabet) for a missing input word. Matching words still come from installed
indexes; the engine does not supply a searchable word list.

## Release inputs and installation

[`dictionary_release.json`](../app/src/main/assets/dictionary_release.json)
defines the pinned release, explicit archive URLs, and compressed sizes.
Update this manifest when changing dictionary releases. Use finished `.db.gz`
assets rather than word lists or intermediate archives, and verify their
schema and pronunciation rules. Do not derive download URLs from an assumed
release date.

`DictionaryInstaller.kt` uses framework networking and SQLite APIs. It streams
decompressed data into a staging file in versioned app-private internal
storage, checks that the expected columns can be queried, and activates the
file only after successful installation. This check is not a full integrity
or content audit. Failed or cancelled installs clean up staging files and
preserve usable older releases. Downloaded databases are excluded from Android
backup and are not bundled in the APK.

The expected table is:

```text
dictionary(word, ipa, ipa_reversed, assonance_reversed)
```

The primary key is `(word, ipa)`, with indexes on the reversed search fields.
Installed databases are opened read-only; runtime fallback never adds rows.

## Pronunciation lookup

`DictionaryPronunciations.kt` checks sources in this order:

1. The installed Wiktionary index.
2. The installed eSpeak index.
3. The bundled [eSpeak engine](ESPEAK_NATIVE.md), if both indexes miss.

Lookup returns all IPA variants from the first source containing the word and
records their source. It does not merge variants across sources.
`DictionaryHeadword.kt` applies the producer's normalization and eligibility
rules, including Unicode NFC and punctuation cleanup. Spelling identity
remains case-sensitive.

## Search behavior

`DictionarySearchPlan.kt` defines phonetic search stages, and
`DictionarySearch.kt` reads candidates in batches and returns bounded pages.
Search excludes the queried spelling and deduplicates result words.
Different spellings with the same pronunciation remain eligible.

| Mode | Matching and ordering |
| --- | --- |
| Rhyme | Match the full vowel-through-end sequence of the stressed syllable; shorter candidate pronunciations are scanned first within each source |
| Assonance | Match the complete vowel sequence first, then progressively shorter vowel suffixes; exact matches precede fallback matches across sources |
| Word prefix | Match the written beginning of a word, preserving case |
| Word suffix | Match phoneme endings, with longer matches before shorter ones across sources |

Rhyme matching uses the last primary stress, falling back to secondary stress.
A pronunciation with one vowel can omit stress; an unmarked pronunciation
with multiple vowels has no reliable rime. Rhyme mode does not progressively
shorten that rime. It suppresses longer compounds when their written and
phonetic ending duplicates the query or an earlier match. Word-suffix mode
retains compounds.

For assonance with multiple input pronunciations, shorter complete vowel keys
are searched first. Each exact key is searched across both sources before the
next key; fallback tiers then run from longest to shortest. For assonance and
word-suffix searches, Wiktionary takes precedence within the same tier. Rhyme
and word-prefix searches traverse Wiktionary before eSpeak.

English and German assonance results group compatible spelling and phonetic
forms. English favors a returned base form. German favors a compatible
returned infinitive; otherwise, it favors the form whose pronunciation is a
prefix of the most other returned pronunciations, then the shortest spelling.
German grouping accounts
for syllabic endings and final devoicing. These are matching heuristics,
not lemma lookup: the databases have no lemma column. Completed pages keep
their selected forms so later batches do not shift pagination offsets.

`IpaSearchKeys.kt` follows the producer's phoneme-token rules when constructing
reversed keys. Reversing raw Unicode characters would break multi-character
phonemes and modifiers. Candidates are checked against token boundaries after
SQL filtering. Queries are parameterized; prefix queries use case-sensitive
`LIKE` and escape literal wildcard characters. See `IpaSearchKeys.kt` and its
JVM tests for suffix-length rules and language-specific edge cases.

## Responsiveness and verification

Installed-status checks, lookup, search, native initialization, and downloads
run off the UI thread. Input changes cancel obsolete searches and reset stale
results and errors. Download progress delivery belongs to the download job,
so UI updates finish before that job returns.

Cancellation is cooperative between database rows and scans and before
installation activation. It cannot interrupt a SQLite call or native
conversion already in progress. Result pages and database batches limit
individual reads; broad searches can still scan many batches.

See [Testing](TESTING.md) for regression coverage and
[Releasing](RELEASING.md) for full-data offline checks. A timed benchmark with
release dictionaries, particularly for phonetic suffix searches, remains
outstanding. Syllable coloring is separate planned work in the
[roadmap](FEATURES.md).

Attribution and data-license terms are maintained in
[THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md).
