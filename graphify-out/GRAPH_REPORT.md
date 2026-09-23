# Graph Report - Gh0stwrit3r  (2026-09-23)

## Corpus Check
- 70 files · ~54,568 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 20 file(s) not represented in the graph (top: .xml 9, (none) 4, .properties 3)

## Summary
- 736 nodes · 1637 edges · 35 communities (25 shown, 10 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 102 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `55400c75`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- DictionaryInstaller.kt
- BeatPlayerInstrumentedTest
- ProjectStorage
- ProjectStorageTest
- WaveformViewport
- SettingsScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- MainActivity.kt
- ProjectLyricsStorage
- gradlew
- EditorScreenTest.kt
- WaveformView.kt
- What You Must Do When Invoked
- Ghostwriter
- Settings
- EditorScreen.kt
- graphify reference: extra exports and benchmark
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
- formatPlaybackTime

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 48 edges
2. `ProjectStorage` - 33 edges
3. `GhostwriterTheme()` - 31 edges
4. `ProjectStorageTest` - 30 edges
5. `WaveformViewport` - 26 edges
6. `EditorScreen()` - 26 edges
7. `ProjectStorageBeatTest` - 25 edges
8. `ProjectMetadata` - 23 edges
9. `BeatPlayerTest` - 22 edges
10. `BeatPlayerPanel()` - 19 edges

## Surprising Connections (you probably didn't know these)
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `WaveformMarkerDialog()` --calls--> `formatPlaybackTime()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatDialogs.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/PlaybackTime.kt
- `BeatPlayerPanel()` --calls--> `formatPlaybackTime()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatPlayerPanel.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/PlaybackTime.kt

## Import Cycles
- None detected.

## Communities (35 total, 10 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.07
Nodes (12): BeatComponentsTest, BeatPlayer, BeatPlaybackControls(), BeatPlayerPanel(), IntArray, GhostwriterTheme(), BeatPlayerTest, darkcolorscheme (+4 more)

### Community 1 - "DictionaryInstaller.kt"
Cohesion: 0.06
Nodes (30): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, DictionaryArchiveSource, DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller (+22 more)

### Community 3 - "ProjectStorage"
Cohesion: 0.05
Nodes (18): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, Context, IntArray, ProjectStorage, StagedFileWriter (+10 more)

### Community 4 - "ProjectStorageTest"
Cohesion: 0.08
Nodes (5): HomeScreenTest, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), ProjectStorageTest

### Community 6 - "SettingsScreen.kt"
Cohesion: 0.17
Nodes (16): arrowback, backhandler, dropdownmenuitem, experimentalmaterial3api, horizontaldivider, icon, iconbutton, localcontext (+8 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.12
Nodes (16): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, button, card, columnscope, delay (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.09
Nodes (10): abs, WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, MediaCodec (+2 more)

### Community 9 - "MainActivity.kt"
Cohesion: 0.08
Nodes (26): MainActivityTest, AboutScreenTest, About, Editor, GhostwriterApp(), Home, Context, MainActivity (+18 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "EditorScreenTest.kt"
Cohesion: 0.07
Nodes (54): activitynotfoundexception, after, androidjunit4, ExampleInstrumentedTest, shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest (+46 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (35): IntArray, Modifier, WaveformView(), canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures (+27 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Ghostwriter"
Cohesion: 0.05
Nodes (33): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints, Architecture, Current limits, Ghostwriter documentation (+25 more)

### Community 20 - "EditorScreen.kt"
Cohesion: 0.12
Nodes (17): activityresultcontracts, displayNameFor(), Context, PendingBeatPreparation, atomicboolean, atomiclong, coroutinestart, disposableeffect (+9 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

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
Cohesion: 0.16
Nodes (20): alertdialog, ReassignBeatDialog(), column, dp, fillmaxwidth, getvalue, height, heightin (+12 more)

### Community 31 - "EditorScreen"
Cohesion: 0.14
Nodes (13): LyricsNotepadTest, EditorScreenTest, IntArray, Modifier, LyricsNotepad(), EditorScreen(), Feature Roadmap, Guiding rules (+5 more)

### Community 32 - "composable"
Cohesion: 0.22
Nodes (7): composable, keyboardcapitalization, keyboardoptions, materialtheme, text, textfield, textfielddefaults

### Community 33 - "BeatPlaybackControls.kt"
Cohesion: 0.14
Nodes (13): alignment, box, icons, loop, mutablefloatstateof, pause, playarrow, size (+5 more)

### Community 34 - "HomeScreen.kt"
Cohesion: 0.15
Nodes (12): add, clickable, delete, dropdownmenu, imeaction, items, keyboardactions, lazycolumn (+4 more)

### Community 39 - "formatPlaybackTime"
Cohesion: 0.25
Nodes (4): LongBeatWarningDialog(), formatPlaybackTime(), formatInterval(), SettingsFormatTest

## Knowledge Gaps
- **73 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK_DATABASE`, `Usage`, `What graphify is for` (+68 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 214 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **10 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `EditorScreen()` connect `EditorScreen` to `BeatPlayer`, `ProjectStorage`, `ProjectStorageTest`, `formatPlaybackTime`, `WaveformExtractor`, `MainActivity.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `ProjectInfoDialog.kt`?**
  _High betweenness centrality (0.145) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `BeatPlaybackControls.kt`, `BeatPlayerInstrumentedTest`, `BeatPlayerPanel.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `EditorScreen`?**
  _High betweenness centrality (0.111) - this node is a cross-community bridge._
- **Why does `ProjectStorage` connect `ProjectStorage` to `HomeScreen.kt`, `ProjectStorageTest`, `MainActivity.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `EditorScreen`?**
  _High betweenness centrality (0.100) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 13 inferred relationships involving `WaveformViewport` (e.g. with `.clamped_shrinkingViewportKeepsScrollWithinTheNewEnd()` and `.panBy_clampsAtTheStartAndEndOfTheTimeline()`) actually correct?**
  _`WaveformViewport` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK_DATABASE` to the rest of the system?**
  _73 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.07127882599580712 - nodes in this community are weakly interconnected._