# Gh0stwrit3r.exe agent instructions

Before modifying code, read:

- documentation/AGENT_CONTEXT.md
- documentation/FEATURES.md
- README.md

## Non-negotiable project constraints

- Never add AI features to the Android application.
- Do not add Google Play Services.
- Features must work offline by default.
- Minimize dependencies. Do not replace deliberately simple implementations
  with larger libraries/frameworks without explicit maintainer approval.
- Keep SharedPreferences unless specifically instructed otherwise.
- Keep hand-rolled navigation unless specifically instructed otherwise.
- Keep org.json unless specifically instructed otherwise.
- Prefer Android/AOSP APIs where they adequately solve the problem.

## Development workflow

- Work against the dev branch.
- Make focused changes; do not opportunistically refactor unrelated code.
- Inspect existing architecture before introducing abstractions.
- Add or update tests for behavior changes.
- Before completing a task, run the relevant Gradle tests.
- Run ./gradlew test and ./gradlew lint when practical.
- Do not modify main unless explicitly requested.

## Current architecture

See documentation/AGENT_CONTEXT.md for authoritative details.

If documentation conflicts with assumptions or general Android conventions,
the repository documentation wins.
