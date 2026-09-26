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
