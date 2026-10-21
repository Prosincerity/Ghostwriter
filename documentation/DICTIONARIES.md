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
| Double rhyme (German) | Match two independent sound groups from a compound or two words; attested compounds precede free pairs, with each component used once across pages |
| Word prefix | Match the written beginning of a word, preserving case |
| Word suffix | Match phoneme endings, with longer matches before shorter ones across sources |

Rhyme matching uses the last primary stress, falling back to secondary stress.
A pronunciation with one vowel can omit stress; an unmarked pronunciation
with multiple vowels has no reliable rime. Rhyme mode does not progressively
shorten that rime. It suppresses longer compounds when their written and
phonetic ending duplicates the query or an earlier match. German uses a
dictionary-backed decompounder and compares the final component, including
between two compounds. Word-suffix mode retains compounds.

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

### German decompounding

German decompounding preserves the original surface spelling and attaches
linking material (`s`, `es`, `n`, `en`, `e`, `er`, `ens`, `ns`) to the left
component, while recording its dictionary base separately. It analyses the
longest head first, backtracks when the modifier is unknown, and supports
multiple components and stems with a dropped final `e`. For example,
`Wissenschaftskolleg` becomes `Wissenschafts + kolleg` and `Hundehütte` becomes
`Hunde + hütte`. There are no word-specific exclusions. Productive
derivational suffixes
(`schaft`, `heit`, `keit`, `ung`, `nis`, `tum`, `ling`) stay attached when their
base/stem is attested. Independently stressed noun heads can override this
heuristic: `Gewehrschaft` is `Gewehr + schaft`, while `Wissenschaft` stays
whole. The bound compound head `-nahme` is supported when its source verb
`nehmen` is attested; its pronunciation can be generated offline without
inventing a standalone dictionary entry. `Handschuh` is `Hand + schuh`,
following Wiktionary's morphological analysis rather than semantic opacity.

The databases supply the lexicon and IPA, but contain no parts of speech or
etymology annotations. These are dictionary-backed morphological heuristics,
not a claim to reproduce all Wiktionary analyses. Missing entries, ambiguous
derivations, inflection, and incomplete stress notation can prevent or distort
a split. The algorithm uses batched indexed word lookups; generated IPA never
counts as evidence that an atom exists. Words longer than 96 characters stay
whole. No frequency dataset or additional library is required.

### Double rhymes

Select German and **Double rhyme**, then enter a compound such as
`Straßenbande` or exactly two words such as `bleibe Straße`. A compound input
uses its modifier and final head as the two anchors. Pronunciations are looked
up per surface component, including linking sounds, with the existing offline
fallback. The display retains an attested compound's spelling and shows its
parts underneath; constructed pairs are shown with `+`.

Both anchors must match the complete vowel sequence. Within the attested and
constructed groups, exact rimes precede voicing variants, then other
consonant differences. Syllabic `n`, `l`, and `m` count as reduced schwa
syllables, diphthong nonsyllabic marks are normalized, and eSpeak
`ɑ` matches Wiktionary `a` in German. Thus
`Bande–Schande` is exact, `Bande–Kante` differs by voicing, and
`Straße–Frage` is a loose vowel match. These are phonetic suggestions;
constructed pairs need not form natural phrases.

Attested compounds reserve their two written components before free pairs
are constructed. A component spelling is used at most once, ignoring case,
including across pages and pronunciation sources. Fully identical component
bases are rejected. An attested compound may retain one input component,
allowing `bleibe Straße → Seitenstraße`; constructed pairs change both
written anchors. This two-anchor rule intentionally differs from ordinary
rhyme mode's repeated-head suppression. Results depend on the installed
vocabulary: attested `Nasenkante` wins over a constructed pair using `Nasen`,
but the app does not claim an unseen joined spelling is an existing word.

Search rebuilds the same deterministic selection before slicing each page.
It scans the relevant vowel-key buckets in batches and does not build the
Cartesian product of alternatives. Broad inputs can still require substantial
work; performance with complete release dictionaries remains unbenchmarked.

### Source-checked regression examples

These distinctions were checked against the linked dictionary analyses before
setting their test expectations. Linking material stays on the left surface.

| Example | Expected analysis | Reference |
| --- | --- | --- |
| Handschuh | Hand + schuh, a compound | [Wiktionary](https://de.wiktionary.org/wiki/Handschuh) |
| Hundehütte | Hunde + hütte; Hund + e + Hütte | [Wiktionary](https://de.wiktionary.org/wiki/Hundeh%C3%BCtte) |
| Wissenschaftskolleg | Wissenschafts + kolleg | [Algolia's worked example](https://www.algolia.com/blog/engineering/increase-decompounding-accuracy-by-generating-a-language-specific-lexicon) |
| Hinterziehung | Whole; derivation from hinterziehen with -ung | [Wiktionary](https://de.wiktionary.org/wiki/Hinterziehung) |
| Leidenschaft | Whole; Leiden with -schaft | [Wiktionary](https://de.wiktionary.org/wiki/Leidenschaft) |
| Verwandtschaft | Whole; verwandt with -schaft | [Wiktionary](https://de.wiktionary.org/wiki/Verwandtschaft) |
| Freiheit | Whole; frei with -heit | [Wiktionary](https://de.wiktionary.org/wiki/Freiheit) |
| Freundlichkeit | Whole; freundlich with -keit | [Wiktionary](https://de.wiktionary.org/wiki/Freundlichkeit) |
| Rechnung | Whole; rechnen with -ung | [Wiktionary](https://de.wiktionary.org/wiki/Rechnung) |
| Anwaltschaft | Whole; example of the derivational suffix -schaft | [Wiktionary](https://de.wiktionary.org/wiki/-schaft) |
| Gewehrschaft | Gewehr + schaft; Schaft is an independently stressed noun | [Wiktionary](https://de.wiktionary.org/wiki/Gewehrschaft) |
| Seitenstraße | Seiten + straße; Seite + n + Straße | [Wiktionary](https://de.wiktionary.org/wiki/Seitenstra%C3%9Fe) |
| Geiselnahme | Geisel + nahme; the second part is a bound lexeme | [Wiktionary](https://de.wiktionary.org/wiki/Geiselnahme) |

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
