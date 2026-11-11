# Dictionaries

This guide describes dictionary installation, pronunciation lookup, and search behavior.

## Use dictionaries

Languages are English (`en`), German (`de`), and Turkish (`tr`). Each has separate
Wiktionary-derived and eSpeak-generated indexes from
[Ghostwriter Dictionary](https://github.com/Prosincerity/Ghostwriter-Dict).

1. Open **Settings → Download Dictionaries** and install a source.
2. Open the editor's book button, select an installed language and mode, and search.
3. Use **Previous** and **Next** to page through words, IPA, and source labels.

Downloads require network access and offer progress, cancellation, and retry.
Installed search works offline. Bundled pronunciation generation can supply IPA
for a missing input, but searchable result words come from installed indexes.

## Release manifest and installation

Opening **Download Dictionaries** automatically checks the repository's latest
published stable GitHub release. **Check for updates** repeats the check.
The app reads explicit asset URLs and compressed sizes from GitHub, and hashes
from that release's `SHA256SUMS`. Validated metadata is cached locally; a failed
check retains the saved release and installed dictionaries. Editing and search
never check GitHub. Archive downloads and updates start only when selected.

[`dictionary_release.json`](../app/src/main/assets/dictionary_release.json)
supplies a known release, URLs, sizes, and SHA-256 hashes before the first
successful check. Publishing a newer dictionary release does not require
changing this fallback or releasing a new app version.

Publish the six `.db.gz` assets and `SHA256SUMS` in a stable release marked
latest on GitHub. The supported names are `<lang>_<version>.db.gz` and
`<lang>_espeak_<version>.db.gz` for `en`, `de`, and `tr`; dates are not parsed.
Checksum entries can use archive basenames or paths such as `en/<archive>`.
Every archive must have one checksum. Missing or ambiguous assets, invalid
checksums, or conflicts with GitHub asset digests reject the release metadata.

`DictionaryInstaller.kt` streams decompressed data into versioned app-private
storage. It verifies the entire compressed archive's SHA-256 and size, then
checks that the required columns can be queried before activating the staged
database. Schema validation is not a full content audit.
Failed or cancelled installs remove staging files and retain usable older data.
The downloads screen offers an update when an older release is available
offline but the selected latest release is missing. Failed updates retain that action
and the older database until a retry succeeds.

Downloaded databases are excluded from Android backup and the APK.
Search opens them read-only and never inserts generated pronunciations.
The required `dictionary` columns are:

| Column | Use |
| --- | --- |
| `word` | Written headword |
| `ipa` | Pronunciation variant |
| `ipa_reversed` | Reversed phoneme search key |
| `assonance_reversed` | Reversed vowel search key |

## Pronunciation lookup

`DictionaryPronunciations.kt` returns all variants from the first matching source:

1. Installed Wiktionary index.
2. Installed eSpeak index.
3. Bundled [native engine](ESPEAK_NATIVE.md).

Variants from different sources are not merged. `DictionaryHeadword.kt`
normalizes Unicode NFC, apostrophes, dashes, and subscript digits, then checks
headword eligibility. Lookup spelling remains case-sensitive.

## Search modes

`DictionarySearchPlan.kt` orders search stages; `DictionarySearch.kt` scans
candidates and returns pages. Ordinary modes exclude the input spelling and
deduplicate result words. Different spellings may share a pronunciation.

| Mode | Match |
| --- | --- |
| Rhyme | Full stressed-vowel-through-end sequence, without shortening |
| Assonance | Complete vowel sequence, followed by shorter vowel suffixes |
| Word prefix | Written beginning, case-sensitive |
| Word suffix | Phoneme endings, longer matches first |
| Double rhyme | German compounds or two-word input; see below |

Rhyme uses the last primary stress, falling back to secondary stress.
An unstressed single-vowel pronunciation is eligible; an unmarked multi-vowel
pronunciation has no reliable rime. Candidates with shorter pronunciations
are scanned first within each source.

Phoneme-based modes (rhyme, word suffix, and each double-rhyme anchor) share
minimum overlap rules. A single-vowel input of up to four phonemes permits short
matches such as cat/hat. Other inputs require at least three phonemes in the
matching span. Inputs with multiple vowels require at least two vowels in that
span; stress and tone markers do not count as phonemes. A final-stress rime
below these minimums produces no rhyme matches rather than using a weaker key.
Pronunciation variants are evaluated independently.

Word suffix uses the whole input through four phonemes. Longer inputs use
suffixes of x-2, x-4, and so on, extending any tier that falls below the minimum
overlap. For example, writing can match lighting but cannot match baiting or
sing solely through their final vowel. Assonance already stops shorter tiers
at two vowels for multi-vowel inputs. Word prefix remains a spelling search.

Rhyme suppresses compounds that repeat the written and phonetic ending of the
query or an earlier result. German compares dictionary-backed final components.
Word-suffix mode retains compounds.

Assonance searches shorter complete input vowel keys first. Each exact key
visits both sources before the next key; fallback tiers run longest first.
Assonance and word-suffix searches prioritize Wiktionary within a tier.
Rhyme and word-prefix searches visit Wiktionary before eSpeak.

English and German assonance group compatible written and phonetic forms.
English favors a returned base form. German favors a compatible infinitive,
then pronunciation-prefix support and shorter spelling. These are heuristics,
not lemma lookup. Completed pages retain their selected forms.

`IpaSearchKeys.kt` reverses phoneme tokens rather than Unicode characters.
SQL queries use parameters; `LIKE` escapes literal wildcards. Token checks
validate candidates after SQL filtering.

## German decompounding

`GermanDecompounder.kt` tries the longest dictionary-backed head first and
backtracks when the modifier is unknown. Surface parts reconstruct the input.
Linking material stays on the modifier, with its lemma stored separately.
Multiple components and dropped final `e` stems are supported.

Derivational suffixes remain attached when the base is attested, unless an
independently stressed noun head licenses a split. The bound head `nahme`
requires attested `nehmen`. Generated IPA never establishes that a word exists.

`DictionaryLexicon.kt` batches indexed atom lookups and caches resolved splits
within each search. Inputs longer than 96 characters remain whole.

### Regression examples

| Input | Analysis covered by regression tests |
| --- | --- |
| `Hundehütte` | `Hunde + hütte` |
| `Wissenschaftskolleg` | `Wissenschafts + kolleg` |
| `Wissenschaft` | Whole derivation |
| `Gewehrschaft` | `Gewehr + schaft` |

Missing entries, inflection, ambiguous derivations, and incomplete stress
notation can prevent or distort a split. The lexicon supplies no part-of-speech
or etymology annotations.

## Double rhymes

Select German and **Double rhyme**. Enter a compound such as `Straßenbande`
or exactly two words such as `bleibe Straße`. Compound input uses its modifier
and final head as anchors; each surface component receives a pronunciation.

Both anchors must match complete vowel sequences. Attested compounds precede
constructed pairs. Each anchor's input and candidate rime must also meet the
shared phoneme and vowel minimums, including for loose matches. Two short
monosyllable anchors can still form a double rhyme. Within each group, lower
total distance ranks first: exact rimes score zero, voicing variants one, and
other consonant differences two.
Syllabic consonants, diphthong marks, and German `ɑ`/`a` are normalized.

Attested compounds show their parts underneath; free pairs use `+`.
Component spellings occur at most once, ignoring case, across sources and pages.
Compounds with both component bases identical to the input are rejected;
constructed pairs change both written anchors. Pairs need not form natural phrases.

`DoubleRhymeSearch.kt` rebuilds a deterministic selection before slicing pages.
It scans indexed vowel buckets, caches sound profiles, and pairs remaining words
without a Cartesian product. Broad searches can still require substantial work.

## Cancellation and distribution

Search and installation run off the UI thread. Input changes cancel obsolete
searches and clear stale results and errors. Cancellation is cooperative between
rows and scans; it cannot interrupt a SQLite call or native conversion in progress.

Follow [Releasing](RELEASING.md#runtime-checks) for full-data checks and benchmarks.
[THIRD_PARTY_LICENSES.md](../THIRD_PARTY_LICENSES.md) defines attribution and data terms.
