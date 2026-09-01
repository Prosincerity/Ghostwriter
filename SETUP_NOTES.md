# Setup notes

This repo currently contains:

- `README.md`, `FEATURES.md`, `LICENSE`, `.gitignore` — ready to use as-is.
- `app/src/main/java/com/ghostwriter/exe/` — a Phase 1 barebones notepad screen
  (`MainActivity.kt`) plus a small Material 3 dark theme
  (`ui/theme/Color.kt`, `Theme.kt`, `Type.kt`).

**On purpose, this repo does NOT include Gradle build files
(`build.gradle.kts`, `settings.gradle.kts`, the Gradle wrapper, or
`AndroidManifest.xml`).** Android Studio's "New Project" wizard generates
these with whatever Android Gradle Plugin, Kotlin, and Compose versions are
current *at the moment you create the project* — hand-writing them here would
almost certainly leave you with stale, mismatched versions by the time you
open Android Studio.

## How to combine the two

1. In Android Studio: **File → New → New Project → Empty Activity** (make
   sure "Empty Activity" — the Compose one, not the legacy Views one — is
   selected). Use package name `com.ghostwriter.exe`.
2. Close Android Studio once the project finishes generating.
3. Copy this repo's files into the generated project folder, allowing
   `MainActivity.kt` and the `ui/theme/` files to overwrite the wizard's
   generated versions.
4. Copy this repo's `.gitignore`, `LICENSE`, `README.md`, and `FEATURES.md`
   into the project root (the wizard also generates its own `.gitignore` —
   compare the two and merge; they're nearly identical).
5. Reopen the project in Android Studio and let Gradle sync.

Full step-by-step walkthrough is in the chat where this repo was generated.
