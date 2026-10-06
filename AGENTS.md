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
- Follow the testing policy below and in documentation/TESTING.md.
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
- During Gradle tests and builds, wait for the process to finish before
  inspecting results. Do not repeatedly poll status, read partial logs, or
  stream intermediate output. Prefer completion notifications or blocking
  waits; if a tool requires continuation, use long blocking waits and inspect
  the exit status and captured output only after completion.
- Do not modify main unless explicitly requested.

## Testing policy

- For bug fixes, write a failing regression test first. Run it and confirm it
  fails for the intended reason before fixing the code, then confirm it passes.
- For new logic, write tests alongside the code; use test-first development
  where the design is clear. Keep one behavior per test.
- Test behavior and observable outcomes, not private methods or internal call
  order unless that order is part of the contract. Every test must contain
  meaningful assertions. Tests solely to increase coverage are forbidden;
  a no-throw check is valid only when not throwing is the specified behavior.
- Cover happy paths, boundaries (empty, null, maximum, off-by-one), errors,
  invalid input, and both outcomes of each decision where applicable.
- Keep Activities, Fragments, Composables, and DI glue thin. Move logic into
  ViewModels or use cases so it can be unit-tested. Do not force UI or wiring
  into unit-test coverage; test UI behavior with UI tests.
- Follow the test pyramid: many fast unit tests, fewer integration tests,
  and very few end-to-end tests.
- Tests must be deterministic and independent: no shared mutable state or
  test-order dependency, and no reliance on real time, network, randomness,
  or existing filesystem state. Use controlled clocks, dispatchers, fakes,
  and isolated temporary storage when testing file behavior.
- Never fix flaky tests with retries or sleeps. Report flakiness and remove
  its source of nondeterminism. No @Ignore or disabled tests without a written
  reason and a linked tracked issue.
- Tests must run headless and offline. CI should run unit tests on every PR
  and instrumented tests on an emulator at least on merge. Keep the unit suite
  fast and report any test taking more than a few seconds.
- Keep minimal fixtures next to their tests; use committed sample files for
  parser tests. Never include secrets, real user data, or production endpoints.
- Coverage is a safety net, not a goal. Use JaCoCo instruction and branch
  coverage, reporting unit and instrumented results separately and retaining
  the aggregated coverage dashboard. Enforce module gates on combined coverage
  so Android UI does not have to satisfy unit-test coverage.
- Gate coverage per module, not per file: 80% instruction / 70% branch as the
  default baseline, and 90% / 85% for logic-heavy domain, data, or parsing
  modules. New or changed executable lines require at least 80% diff coverage;
  overall instruction and branch coverage must not decrease (ratchet).
- Exclude non-logic generated code (Hilt/Dagger, Room, Data Binding, BuildConfig,
  R), Compose previews, plain data classes, and trivial DI modules from reports.
  Do not exclude handwritten logic simply because it shares those names.
- If coverage drops or a gate fails, add meaningful tests. Do not lower
  thresholds or add exclusions without explicitly stating the justification.

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
