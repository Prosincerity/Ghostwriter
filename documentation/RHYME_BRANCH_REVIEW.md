# Rhyme branch review

Scope: app code introduced between `dev` (`534a240`) and the starting
`rhyme-detection` tip (`ddc947b`). Cleanup stays on the feature branch as
requested. Existing storage, navigation, dependency choices, and dictionary
matching rules remain the baseline.

## Completed stages

1. **Source selection.** Lookup and search independently repeat source priority
   and attribution pairs; installer and downloads repeat asset selection.
   Centralize these mappings in the existing source/language types. Preserve
   Wiktionary precedence and per-source downloads. Remove the unused combined
   download-size property.
2. **Cancellation.** Dictionary cursor loops do not observe cancellation, and
   installation can activate a file after cancellation during the final read.
   Share a cancellation-aware cursor reader, add checkpoints before expensive
   work and activation, and test cancellation and staging-file cleanup.
   Load the eSpeak shared library inside its existing IO initialization lock,
   so constructing a search repository does not load JNI during composition.
3. **Screen state and disk access.** Both dictionary screens validate databases
   during composition. Load installed status on the IO dispatcher. The search
   screen repeats cancellation/reset code in three callbacks and leaves stale
   errors behind after input changes. Share that reset operation, centralize
   language labels, and cover input changes and superseded searches in Compose
   tests.
4. **Download progress lifetime.** Progress callbacks launch sibling coroutines
   that can update state after download cleanup. Make the callback suspend and
   deliver UI updates with `withContext(Main.immediate)`, so delivery belongs to
   the download job and completes before it returns. Test suspended delivery
   without timing sleeps.

## Validation

- Baseline `./gradlew test lint`: passed.
- Stage 1: `./gradlew test lint` passed (134 JVM tests); full
  `./gradlew connectedDebugAndroidTest` passed (58 emulator tests).
  The Gradle comma-separated class filter ran only its first class, so full
  suite results were checked in the generated XML before accepting this stage.
- Stage 2: `./gradlew test lint connectedDebugAndroidTest
  -Pandroid.testInstrumentationRunnerArguments.package=com.prosincerity.ghostwriter.data`
  passed (134 JVM tests and 17 emulator tests, including both new cancellation
  regressions and the real eSpeak checks).
- Stage 3: full `./gradlew test lint connectedDebugAndroidTest` passed
  (134 JVM tests and 62 emulator tests). The new Compose tests exercise stale
  errors and late responses from a canceled search while a new search runs.
- Stage 4: `./gradlew test lint connectedDebugAndroidTest
  -Pandroid.testInstrumentationRunnerArguments.class=com.prosincerity.ghostwriter.data.DictionaryInstallerInstrumentedTest`
  passed (134 JVM tests and all 7 installer emulator tests, including suspended
  progress delivery). The full emulator run above preceded this final change;
  this stage reran the affected installer suite.
- Every stage was tested before committing, and `graphify update .` refreshed
  the code graph after each stage's source edits.

## Review limits

The JNI bridge, native build configuration, IPA token rules, matching filters,
navigation additions, backup exclusions, and existing branch tests were also
inspected. Upstream eSpeak sources and unrelated pre-existing app code are
outside this cleanup. Synthetic fixtures exercise ranking and pagination;
the maintainer reported fast, responsive searches on a physical Android device
with real release dictionaries. A timed
realistic-database performance benchmark remains open.
Cancellation is checked between cursor rows and database scans; it does not
interrupt a single SQLite call or native phonemization already in progress.

## Readability pass before merge

Reviewed the branch's application additions against `dev`, including dictionary
installation and search, IPA tokenization and filtering, download/search screens,
playback integration, the JNI adapter, and native build setup. Applied focused
cleanup on `rhyme-detection`:

- Share dictionary search's source traversal, cancellation check, and database
  closing while retaining each mode's source and tier ordering.
- Separate assonance family selection from grouping, reuse the pronunciation
  extension check, and simplify suffix stages and headword punctuation checks.
- Cache the tokenizer's fixed single-character inventory once per language.
- Name download startup, audio-focus requests, notification action construction,
  and the editor's shared beat-processing guard.
- Reuse dictionary test contexts and German row fixtures, remove forwarding
  wrappers, and simplify file paths and repeated property reads.

`./gradlew test lint assembleDebugAndroidTest` passed: 182 JVM tests, no failures
or skips, and lint with no errors (14 warnings). Added JVM regressions for
infinitive selection ties, invalid pronunciations/unsupported languages, and
odd/even suffix lengths. Instrumented tests were compiled, and the maintainer
subsequently reported that the emulator tests passed.

## Follow-up review on 2026-09-30

Scope: files changed from `dev` (`534a240`) to `rhyme-detection`
(`c4feae9`), plus their direct dependencies. Reviewed dictionary installation,
lookup and search, phonetic filters, Compose screen changes, playback lifecycle,
the JNI bridge, native build configuration, and branch tests. Upstream eSpeak
implementation changes remain outside the cleanup scope.

- Validate a trailing headword plus run once instead of rescanning it at each
  plus character. Add a JVM regression for long runs and invalid prefixes/tails.
- Extract `DictionarySearchPlan` so phonetic stage ordering and deduplication
  can be tested independently of SQLite and pagination. Seven new JVM tests
  preserve rhyme/suffix ordering, short exact assonance priority, fallback tiers,
  stable ties, and handling of missing or invalid IPA.
- Extract archive copying from dictionary activation. Six new JVM tests cover
  decompressed output, compressed-byte progress and clamping, invalid gzip,
  output-open failures, progress-callback failures, and cancellation during
  suspended progress delivery. Activation and staging cleanup stay in the
  installer, with its existing instrumented regressions retained.

Behavior, source precedence, public APIs, and dependencies are unchanged.
Baseline and final `./gradlew test lint assembleDebug assembleDebugAndroidTest`
passed. The final run executed 196 JVM tests with no failures, errors, or skips;
lint reported no errors and the same 14 warnings. Device tests were compiled
but not run, in accordance with `AGENTS.md`; maintainer device validation and
the existing realistic-database benchmark remain open.

`graphify update .` refreshed the code graph without API calls. Graphify's
parser reported partial extraction of 42 upstream files; this is a graph
coverage limitation, separate from the passing native build.

## Bug hunt on 2026-09-30

Reviewed application additions from `dev` (`534a240`) through `rhyme-detection`
(`ad72706`). Fixed two confirmed assonance search bugs:

- Exact keys were nested inside source traversal, so a large longer-key
  Wiktionary tier could hide shorter-key eSpeak matches. Traverse each exact
  tier across both sources before moving to the next key. Wiktionary still
  wins within the same tier.
- A later infinitive could replace a form from an earlier page, skipping
  another word when subsequent pages were sliced. Preserve selected forms
  after full pages acquire a lookahead match, at deterministic batch
  checkpoints. Later family members remain filtered; incomplete pages still
  prefer infinitives normally. Scans retain their existing bounds.

Added a small SQLite adapter so JVM tests exercise the production search
loop with offline rows. No new dependencies. Six tests cover both bugs,
cross-source and same-source batch boundaries, direct/repeated page requests,
infinitive selection before page completion, and same-tier source precedence.
With the original ordering and pagination restored, three regressions failed
and three controls passed. With the fixes, `./gradlew test lint
assembleDebugAndroidTest` passed: 202 JVM tests, no failures/errors/skips,
and lint with no errors and the same 14 warnings. Instrumented tests were
compiled but not run; device validation remains with the maintainer.
