# Ghostwriter agent instructions

Before modifying code, read:

- documentation/README.md
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
- In this Distrobox environment, always invoke ADB through
  `~/Android/Sdk/platform-tools/adb`, never an `adb` resolved from `PATH`.
  Run every ADB command with elevated execution permission
  (`sandbox_permissions="require_escalated"`); sandboxed ADB can crash here.
  Apply the same SDK path and execution permission to tools that invoke ADB.
- Agents may run relevant instrumented tests on an already connected emulator
  or physical device, including devices connected over wireless debugging.
  Confirm the target with `~/Android/Sdk/platform-tools/adb devices -l` using
  elevated execution permission, and select it with `ANDROID_SERIAL`.
  If no device is available, compile the tests and leave execution to the maintainer.
- Before completing a task, run the relevant non-device tests, including JVM tests.
- Run ./gradlew test and ./gradlew lint when practical.
- Do not modify main unless explicitly requested.

## Commit rules

- Make small, atomic commits: one logical change each. Never mix features,
  fixes, refactors, or formatting in one commit.
- Use Conventional Commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`,
  `chore:`. Write in the imperative mood and keep the subject at 50 characters
  or fewer. Add a body explaining *why* when the reason isn't obvious.
- Never commit secrets or generated files. Never force-push or amend commits
  that are already pushed.
- If the working tree contains unrelated changes, leave them unstaged and
  tell the maintainer.
- Commit as you go, after each working step, not one big commit at the end.
- Every commit must build and pass tests.

## Current architecture

See documentation/README.md for architecture and deliberate decisions.

If documentation conflicts with assumptions or general Android conventions,
the repository documentation wins.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

When the user types `/graphify`, use the installed graphify skill or instructions before doing anything else.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- Dirty graphify-out/ files are expected after hooks or incremental updates; dirty graph files are not a reason to skip graphify. Only skip graphify if the task is about stale or incorrect graph output, or the user explicitly says not to use it.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).
