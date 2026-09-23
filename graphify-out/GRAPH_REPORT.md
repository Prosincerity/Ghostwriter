# Graph Report - Gh0stwrit3r  (2026-09-23)

## Corpus Check
- 71 files · ~57,740 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 20 file(s) not represented in the graph (top: .xml 9, (none) 4, .properties 3)

## Summary
- 751 nodes · 1666 edges · 42 communities (31 shown, 11 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 114 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a65152fb`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- DictionaryInstaller.kt
- BeatPlayerInstrumentedTest
- ProjectStorageBeatTest
- ProjectStorage
- WaveformViewport
- SettingsScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- GhostwriterApp
- MainActivity.kt
- gradlew
- EditorScreenTest.kt
- WaveformView.kt
- What You Must Do When Invoked
- Ghostwriter
- Settings
- EditorScreen.kt
- graphify reference: extra exports and benchmark
- Ghostwriter agent instructions
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- ProjectInfoDialog.kt
- EditorScreen
- composable
- BeatPlaybackControls.kt
- HomeScreen.kt
- Steps
- Testing and coverage
- documentation/README.md
- LyricsNotepad
- SettingsFormatTest
- Feature Roadmap
- Build setup

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 48 edges
2. `ProjectStorage` - 34 edges
3. `GhostwriterTheme()` - 31 edges
4. `ProjectStorageTest` - 30 edges
5. `WaveformViewport` - 26 edges
6. `EditorScreen()` - 26 edges
7. `ProjectStorageBeatTest` - 25 edges
8. `ProjectMetadata` - 23 edges
9. `BeatPlayerTest` - 22 edges
10. `BeatPlayerPanel()` - 19 edges

## Surprising Connections (you probably didn't know these)
- `7. Known technical debt (not yet addressed, tracked, but not urgent)` --references--> `Screen`  [INFERRED]
  documentation/AGENT_CONTEXT.md → app/src/main/java/com/prosincerity/ghostwriter/MainActivity.kt
- `5. What's actually built right now` --references--> `Home`  [INFERRED]
  documentation/AGENT_CONTEXT.md → app/src/main/java/com/prosincerity/ghostwriter/MainActivity.kt
- `5. What's actually built right now` --references--> `ProjectStorage`  [INFERRED]
  documentation/AGENT_CONTEXT.md → app/src/main/java/com/prosincerity/ghostwriter/data/ProjectStorage.kt
- `5. What's actually built right now` --references--> `StagedFileWriter`  [INFERRED]
  documentation/AGENT_CONTEXT.md → app/src/main/java/com/prosincerity/ghostwriter/data/StagedFileWriter.kt
- `5. What's actually built right now` --references--> `WaveformCache`  [INFERRED]
  documentation/AGENT_CONTEXT.md → app/src/main/java/com/prosincerity/ghostwriter/data/WaveformCache.kt

## Import Cycles
- None detected.

## Communities (42 total, 11 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.07
Nodes (11): BeatComponentsTest, BeatPlayer, LongBeatWarningDialog(), BeatPlaybackControls(), BeatPlayerPanel(), IntArray, formatPlaybackTime(), GhostwriterTheme() (+3 more)

### Community 1 - "DictionaryInstaller.kt"
Cohesion: 0.06
Nodes (30): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, DictionaryArchiveSource, DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller (+22 more)

### Community 3 - "ProjectStorageBeatTest"
Cohesion: 0.07
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, StagedFileWriter, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest (+2 more)

### Community 4 - "ProjectStorage"
Cohesion: 0.08
Nodes (5): MainActivityTest, Context, ProjectStorage, NewProjectDialog(), ProjectStorageTest

### Community 6 - "SettingsScreen.kt"
Cohesion: 0.13
Nodes (18): alignment, AboutScreenTest, AboutLink(), AboutScreen(), AboutSectionTitle(), SettingsDropdownRow(), SettingsScreen(), arrowback (+10 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.13
Nodes (15): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, card, columnscope, delay, fillmaxsize (+7 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.09
Nodes (10): abs, WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, MediaCodec (+2 more)

### Community 9 - "GhostwriterApp"
Cohesion: 0.08
Nodes (23): HomeScreenTest, ProjectLyricsStorage, About, Editor, GhostwriterApp(), Home, Context, MainActivity (+15 more)

### Community 10 - "MainActivity.kt"
Cohesion: 0.13
Nodes (17): DictionaryDownloads(), dp, enableedgetoedge, fillmaxwidth, intent, job, launch, localcontext (+9 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "EditorScreenTest.kt"
Cohesion: 0.07
Nodes (56): activitynotfoundexception, after, androidjunit4, ExampleInstrumentedTest, shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest (+48 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (35): IntArray, Modifier, WaveformView(), canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures (+27 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Ghostwriter"
Cohesion: 0.20
Nodes (10): Building, Contributing, Documentation, Ghostwriter, License, Philosophy, Roadmap, Status (+2 more)

### Community 20 - "EditorScreen.kt"
Cohesion: 0.12
Nodes (17): activityresultcontracts, displayNameFor(), Context, PendingBeatPreparation, atomicboolean, atomiclong, coroutinestart, disposableeffect (+9 more)

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

### Community 30 - "ProjectInfoDialog.kt"
Cohesion: 0.17
Nodes (14): alertdialog, ReassignBeatDialog(), column, getvalue, height, heightin, imepadding, keyboardtype (+6 more)

### Community 31 - "EditorScreen"
Cohesion: 0.13
Nodes (7): EditorScreenTest, IntArray, IntArray, IntArray, WaveformCache, EditorScreen(), MutableState

### Community 32 - "composable"
Cohesion: 0.14
Nodes (12): WaveformMarkerDialog(), EditorMarkerDialogs(), composable, darkcolorscheme, issystemindarktheme, keyboardcapitalization, keyboardoptions, lightcolorscheme (+4 more)

### Community 33 - "BeatPlaybackControls.kt"
Cohesion: 0.13
Nodes (14): box, icons, loop, mutablefloatstateof, paddingvalues, pause, playarrow, row (+6 more)

### Community 34 - "HomeScreen.kt"
Cohesion: 0.15
Nodes (12): add, button, clickable, delete, imeaction, items, keyboardactions, lazycolumn (+4 more)

### Community 35 - "Steps"
Cohesion: 0.18
Nodes (10): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 7. Licensing, attribution, and release verification, Build setup already present, Goal and data flow (+2 more)

### Community 36 - "Testing and coverage"
Cohesion: 0.22
Nodes (9): ADB and container troubleshooting, Coverage reports, Instrumented-test-only report, Local-test-only report, Regular verification, Running the unified report from Android Studio, Test stack, Testing and coverage (+1 more)

### Community 38 - "LyricsNotepad"
Cohesion: 0.40
Nodes (4): LyricsNotepadTest, Modifier, LyricsNotepad(), 6. Implement rhyme queries and editor behavior

### Community 40 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 41 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Open and build, Requirements

## Knowledge Gaps
- **82 isolated node(s):** `WIKTIONARY`, `ESPEAK_DATABASE`, `Usage`, `What graphify is for`, `Step 0 - GitHub repos and multi-path merge (only if a URL or several paths)` (+77 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 224 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ProjectStorage` connect `ProjectStorage` to `HomeScreen.kt`, `ProjectStorageBeatTest`, `GhostwriterApp`, `MainActivity.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `EditorScreen`?**
  _High betweenness centrality (0.130) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `BeatPlaybackControls.kt`, `BeatPlayerInstrumentedTest`, `BeatPlayerPanel.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `EditorScreen`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Why does `EditorScreen()` connect `EditorScreen` to `BeatPlayer`, `composable`, `ProjectStorageBeatTest`, `ProjectStorage`, `LyricsNotepad`, `WaveformExtractor`, `GhostwriterApp`, `MainActivity.kt`, `Feature Roadmap`, `EditorScreenTest.kt`, `EditorScreen.kt`, `ProjectInfoDialog.kt`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 13 inferred relationships involving `WaveformViewport` (e.g. with `.clamped_shrinkingViewportKeepsScrollWithinTheNewEnd()` and `.panBy_clampsAtTheStartAndEndOfTheTimeline()`) actually correct?**
  _`WaveformViewport` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `WIKTIONARY`, `ESPEAK_DATABASE`, `Usage` to the rest of the system?**
  _82 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.07197763801537387 - nodes in this community are weakly interconnected._