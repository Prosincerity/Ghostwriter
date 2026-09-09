# Ghostwriter agent instructions

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

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

When the user types `/graphify`, use the installed graphify skill or instructions before doing anything else.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- Dirty graphify-out/ files are expected after hooks or incremental updates; dirty graph files are not a reason to skip graphify. Only skip graphify if the task is about stale or incorrect graph output, or the user explicitly says not to use it.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).
