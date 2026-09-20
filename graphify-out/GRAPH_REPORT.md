# Graph Report - Gh0stwrit3r  (2026-09-20)

## Corpus Check
- 64 files · ~54,151 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 485 nodes · 809 edges · 33 communities (19 shown, 10 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 88 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `0ee4782a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- ProjectStorage
- BeatPlayerInstrumentedTest
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- ProjectStorageBeatTest
- GhostwriterTheme
- WaveformExtractor
- MainActivity.kt
- WaveformExtractorTest
- gradlew
- ExampleInstrumentedTest
- What You Must Do When Invoked
- Ghostwriter
- EditorScreen.kt
- MainActivityTest
- graphify reference: extra exports and benchmark
- Ghostwriter agent instructions
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- EditorScreenTest
- .writeText
- WaveformCache

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 44 edges
2. `ProjectStorage` - 33 edges
3. `GhostwriterTheme()` - 30 edges
4. `ProjectStorageTest` - 30 edges
5. `WaveformViewport` - 26 edges
6. `ProjectStorageBeatTest` - 25 edges
7. `ProjectMetadata` - 23 edges
8. `BeatPlayerTest` - 20 edges
9. `BeatPlayerPanel()` - 19 edges
10. `EditorScreen()` - 16 edges

## Surprising Connections (you probably didn't know these)
- `BeatPlayerPanel()` --calls--> `BeatPlaybackControls()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatPlayerPanel.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatPlaybackControls.kt
- `BeatPlayerPanel()` --calls--> `formatPlaybackTime()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatPlayerPanel.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/PlaybackTime.kt
- `BeatPlayerPanel()` --calls--> `WaveformView()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatPlayerPanel.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/WaveformView.kt
- `EditorScreen()` --calls--> `ProjectInfoDialog()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/ProjectInfoDialog.kt
- `BeatPlayerInstrumentedTest` --references--> `BeatPlayer`  [EXTRACTED]
  app/src/androidTest/java/com/prosincerity/ghostwriter/media/BeatPlayerInstrumentedTest.kt → app/src/main/java/com/prosincerity/ghostwriter/media/BeatPlayer.kt

## Import Cycles
- None detected.

## Communities (33 total, 10 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.10
Nodes (4): BeatPlayer, BeatPlaybackControls(), BeatPlayerTest, MediaPlayer

### Community 1 - "ProjectStorage"
Cohesion: 0.15
Nodes (3): Context, IntArray, ProjectStorage

### Community 2 - "BeatPlayerInstrumentedTest"
Cohesion: 0.17
Nodes (3): WaveformExtractorInstrumentedTest, BeatPlayerInstrumentedTest, ByteBuffer

### Community 3 - "ProjectMetadata"
Cohesion: 0.10
Nodes (9): ProjectInfoDialogTest, ProjectMetadata, WaveformMarker, parsePositiveBpm(), ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest, ProjectInfoBpmTest (+1 more)

### Community 5 - "WaveformViewport"
Cohesion: 0.13
Nodes (5): WaveformViewport, IntArray, Modifier, WaveformView(), WaveformViewportTest

### Community 7 - "GhostwriterTheme"
Cohesion: 0.13
Nodes (10): BeatComponentsTest, LyricsNotepadTest, HomeScreenTest, BeatPlayerPanel(), CenteredPlayerContent(), IntArray, Modifier, WaveformZoomButton() (+2 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.25
Nodes (5): DecoderProgressGuard, IntArray, WaveformExtractor, MediaCodec, MediaFormat

### Community 9 - "MainActivity.kt"
Cohesion: 0.11
Nodes (20): AboutScreenTest, About, Editor, GhostwriterApp(), Home, Context, MainActivity, openExternalLink() (+12 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Ghostwriter"
Cohesion: 0.05
Nodes (37): 1. What this project is, 2. Non-negotiable philosophy — read this before suggesting anything, 3. Tech stack (as of last verified state), 4. Repository structure (current), 5. What's actually built right now, 6. Deliberate architectural decisions — please don't silently reverse these, 7. Known technical debt (not yet addressed, tracked, but not urgent), Project handoff: Ghostwriter (+29 more)

### Community 19 - "EditorScreen.kt"
Cohesion: 0.10
Nodes (18): Context, Settings, LongBeatWarningDialog(), ReassignBeatDialog(), WaveformMarkerDialog(), Modifier, LyricsNotepad(), formatPlaybackTime() (+10 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 23 - "graphify reference: query, path, explain"
Cohesion: 0.33
Nodes (5): For /graphify explain, For /graphify path, graphify reference: query, path, explain, Step 0 — Constrained query expansion (REQUIRED before traversal), Step 1 — Traversal

### Community 24 - "graphify reference: add a URL and watch a folder"
Cohesion: 0.50
Nodes (3): For /graphify add, For --watch, graphify reference: add a URL and watch a folder

### Community 25 - "graphify reference: commit hook and native CLAUDE.md integration"
Cohesion: 0.50
Nodes (3): For git commit hook, For native CLAUDE.md integration, graphify reference: commit hook and native CLAUDE.md integration

### Community 26 - "graphify reference: incremental update and cluster-only"
Cohesion: 0.50
Nodes (3): For --cluster-only, For --update (incremental re-extraction), graphify reference: incremental update and cluster-only

### Community 31 - "EditorScreenTest"
Cohesion: 0.29
Nodes (3): EditorScreenTest, IntArray, MutableState

## Knowledge Gaps
- **76 isolated node(s):** `Home`, `Usage`, `What graphify is for`, `Step 0 - GitHub repos and multi-path merge (only if a URL or several paths)`, `Step 1 - Ensure graphify is installed` (+71 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 163 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **10 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BeatPlayer` connect `BeatPlayer` to `BeatPlayerInstrumentedTest`, `EditorScreen.kt`, `GhostwriterTheme`?**
  _High betweenness centrality (0.147) - this node is a cross-community bridge._
- **Why does `ProjectMetadata` connect `ProjectMetadata` to `ProjectStorage`, `EditorScreen.kt`?**
  _High betweenness centrality (0.141) - this node is a cross-community bridge._
- **Why does `ProjectStorage` connect `ProjectStorage` to `MainActivity.kt`, `EditorScreen.kt`, `MainActivityTest`, `EditorScreenTest`?**
  _High betweenness centrality (0.111) - this node is a cross-community bridge._
- **Are the 19 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 19 INFERRED edges - model-reasoned connections that need verification._
- **Are the 13 inferred relationships involving `WaveformViewport` (e.g. with `.clamped_shrinkingViewportKeepsScrollWithinTheNewEnd()` and `.panBy_clampsAtTheStartAndEndOfTheTimeline()`) actually correct?**
  _`WaveformViewport` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `Usage`, `What graphify is for` to the rest of the system?**
  _76 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.10338680926916222 - nodes in this community are weakly interconnected._