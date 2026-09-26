# Rhyme branch review

Scope: app code introduced between `dev` (`534a240`) and the starting
`rhyme-detection` tip (`ddc947b`). Cleanup stays on the feature branch as
requested. Existing storage, navigation, dependency choices, and dictionary
matching rules remain the baseline.

## Staged changes

1. **Source selection.** Lookup and search independently repeat source priority
   and attribution pairs; installer and downloads repeat asset selection.
   Centralize these mappings in the existing source/language types. Preserve
   Wiktionary precedence and per-source downloads. Remove the unused combined
   download-size property.
2. **Cancellation.** Dictionary cursor loops do not observe cancellation, and
   installation can activate a file after cancellation during the final read.
   Share a cancellation-aware cursor reader, add checkpoints before expensive
   work and activation, and test cancellation and staging-file cleanup.
3. **Screen state and disk access.** Both dictionary screens validate databases
   during composition. Load installed status on the IO dispatcher. The search
   screen repeats cancellation/reset code in three callbacks and leaves stale
   errors behind after input changes. Share that reset operation, centralize
   language labels, and cover input changes and superseded searches in Compose
   tests.

## Validation

- Baseline `./gradlew test lint`: passed.
- Stage 1: `./gradlew test lint` passed (134 JVM tests); full
  `./gradlew connectedDebugAndroidTest` passed (58 emulator tests).
  The Gradle comma-separated class filter ran only its first class, so full
  suite results were checked in the generated XML before accepting this stage.
- Each stage runs relevant Gradle JVM/lint and emulator tests before commit.
- Refresh `graphify-out/` after source edits.

## Review limits

The JNI bridge, native build configuration, IPA token rules, matching filters,
navigation additions, backup exclusions, and existing branch tests were also
inspected. Upstream eSpeak sources and unrelated pre-existing app code are
outside this cleanup. Synthetic fixtures exercise ranking and pagination;
the existing roadmap's realistic-database performance benchmark remains open.
