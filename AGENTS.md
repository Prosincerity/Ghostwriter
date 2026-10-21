# Ghostwriter contributor instructions

This guide defines project constraints and the workflow for repository changes.

## Required reading

Read these files before modifying code:

- [Architecture](documentation/README.md)
- [Features](documentation/FEATURES.md)
- [Project README](README.md)

Repository documentation takes precedence over assumptions about Android design.

## Project constraints

- Never add AI features to the Android app or Google Play Services.
- Keep features offline by default.
- Minimize dependencies. Obtain explicit maintainer approval before replacing
  simple implementations with larger libraries or frameworks.
- Keep `SharedPreferences`, hand-rolled navigation, and `org.json` unless
  specifically instructed otherwise.
- Prefer Android/AOSP APIs where they adequately solve the problem.
- Do not add ads, analytics, telemetry, social features, or monetization that
  compromises software freedom. Accounts belong only to the optional sync work
  listed in [Features](documentation/FEATURES.md#import-export-and-sync).

## Development workflow

- Work on `dev`. Do not modify or merge into `main` without an explicit request.
- Inspect the architecture before introducing abstractions.
- Make focused changes. Do not refactor unrelated code.
- Follow [Testing](documentation/TESTING.md) for verification scope, test design,
  coverage gates, and device access. Documentation-only changes require diff
  and command review, not Gradle or device execution.
- Wait for Gradle builds and tests to finish before inspecting results.
  Do not poll partial logs or stream intermediate output. Use completion
  notifications or long blocking continuations when required.

## Commits

- Make one small, atomic commit per working step. Separate features, fixes,
  refactors, and formatting changes.
- Use Conventional Commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, or
  `chore:`. Use the imperative mood and a subject of at most 50 characters.
- Explain the reason in the body when it is not obvious.
- Commit code only after compilation and the focused checks required by
  [Testing](documentation/TESTING.md#verification-scope) pass.
- Leave unrelated changes unstaged and report them to the maintainer.
- Never commit secrets or generated files.
- Never force-push or amend commits that have been pushed.

## Knowledge graph

Use the installed `graphify` skill before any other action when the user invokes
`/graphify`. For codebase questions, query `graphify-out/graph.json` first when
it exists:

```sh
graphify query "How does lyric recovery work?"
```

Use path and concept queries to narrow the result:

```sh
graphify path "EditorScreen" "ProjectStorage"
graphify explain "BeatPlayer"
```

- Dirty `graphify-out/` files are expected and do not justify skipping the graph.
- Skip the graph only for stale or incorrect graph-output work, or at the
  user's explicit request.
- Use `graphify-out/wiki/index.md` for broad navigation when it exists.
- Read `graphify-out/GRAPH_REPORT.md` only for broad architecture review or
  when scoped queries provide insufficient context.

After code changes, refresh the graph with AST extraction only:

```sh
graphify update .
```
