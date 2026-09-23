# Graph Report - Gh0stwrit3r  (2026-09-23)

## Corpus Check
- 70 files · ~54,849 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 20 file(s) not represented in the graph (top: .xml 9, (none) 4, .properties 3)

## Summary
- 744 nodes · 1664 edges · 40 communities (31 shown, 9 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 101 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `77a89c53`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- DictionaryInstaller.kt
- LyricsNotepad.kt
- ProjectStorage
- ProjectStorageTest
- WaveformViewport
- DictionaryDownloads.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- MainActivity.kt
- ProjectLyricsStorage
- gradlew
- EditorScreenTest.kt
- WaveformView.kt
- What You Must Do When Invoked
- Steps
- Settings
- EditorScreen.kt
- graphify reference: extra exports and benchmark
- Ghostwriter
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- ProjectInfoDialog.kt
- EditorScreen
- documentation/README.md
- BeatPlaybackControls.kt
- HomeScreen.kt
- Ghostwriter agent instructions
- Feature Roadmap
- Build setup
- Testing and coverage
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
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `WaveformMarkerDialog()` --calls--> `formatPlaybackTime()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatDialogs.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/PlaybackTime.kt
- `BeatPlayerPanel()` --calls--> `formatPlaybackTime()`  [INFERRED]
  app/src/main/java/com/prosincerity/ghostwriter/ui/components/BeatPlayerPanel.kt → app/src/main/java/com/prosincerity/ghostwriter/ui/components/PlaybackTime.kt

## Import Cycles
- None detected.

## Communities (40 total, 9 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.07
Nodes (12): BeatComponentsTest, BeatPlayer, BeatPlaybackControls(), BeatPlayerPanel(), IntArray, GhostwriterTheme(), BeatPlayerTest, darkcolorscheme (+4 more)

### Community 1 - "DictionaryInstaller.kt"
Cohesion: 0.06
Nodes (33): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, DictionaryArchiveSource, DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller (+25 more)

### Community 2 - "LyricsNotepad.kt"
Cohesion: 0.22
Nodes (7): LyricsNotepadTest, Modifier, LyricsNotepad(), 6. Implement rhyme queries and editor behavior, keyboardcapitalization, textfield, textfielddefaults

### Community 3 - "ProjectStorage"
Cohesion: 0.05
Nodes (17): BeatPlayerInstrumentedTest, ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, IntArray, ProjectStorage, StagedFileWriter (+9 more)

### Community 4 - "ProjectStorageTest"
Cohesion: 0.08
Nodes (5): HomeScreenTest, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), ProjectStorageTest

### Community 6 - "DictionaryDownloads.kt"
Cohesion: 0.14
Nodes (26): alignment, DictionaryDownloads(), DictionaryDownloadsScreen(), Modifier, arrowback, backhandler, box, check (+18 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.13
Nodes (15): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, card, columnscope, fillmaxsize, ImageVector (+7 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.09
Nodes (10): abs, WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, MediaCodec (+2 more)

### Community 9 - "MainActivity.kt"
Cohesion: 0.11
Nodes (18): AboutScreenTest, Context, MainActivity, openExternalLink(), AboutLink(), AboutScreen(), AboutSectionTitle(), SettingsDropdownRow() (+10 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "EditorScreenTest.kt"
Cohesion: 0.07
Nodes (55): activitynotfoundexception, after, androidjunit4, ExampleInstrumentedTest, shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest (+47 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (35): IntArray, Modifier, WaveformView(), canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures (+27 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.20
Nodes (9): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 7. Licensing, attribution, and release verification, Goal and data flow, Rhyme detection: dictionary and eSpeak NG integration plan (+1 more)

### Community 20 - "EditorScreen.kt"
Cohesion: 0.12
Nodes (18): activityresultcontracts, displayNameFor(), Context, PendingBeatPreparation, atomicboolean, atomiclong, coroutinestart, delay (+10 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "Ghostwriter"
Cohesion: 0.20
Nodes (10): Building, Contributing, Documentation, Ghostwriter, License, Philosophy, Roadmap, Status (+2 more)

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
Nodes (16): alertdialog, ReassignBeatDialog(), column, composable, getvalue, heightin, imepadding, keyboardtype (+8 more)

### Community 31 - "EditorScreen"
Cohesion: 0.12
Nodes (13): MainActivityTest, EditorScreenTest, IntArray, Context, About, DictionaryDownloads, Editor, GhostwriterApp() (+5 more)

### Community 32 - "documentation/README.md"
Cohesion: 0.36
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 33 - "BeatPlaybackControls.kt"
Cohesion: 0.14
Nodes (13): height, loop, modifier, mutablefloatstateof, paddingvalues, pause, playarrow, size (+5 more)

### Community 34 - "HomeScreen.kt"
Cohesion: 0.12
Nodes (15): add, button, clickable, delete, dropdownmenuitem, imeaction, items, keyboardactions (+7 more)

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 37 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Open and build, Requirements

### Community 38 - "Testing and coverage"
Cohesion: 0.67
Nodes (3): Coverage, Regular checks, Testing and coverage

### Community 39 - "formatPlaybackTime"
Cohesion: 0.25
Nodes (4): LongBeatWarningDialog(), formatPlaybackTime(), formatInterval(), SettingsFormatTest

## Knowledge Gaps
- **75 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+70 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 219 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `EditorScreen()` connect `EditorScreen` to `BeatPlayer`, `LyricsNotepad.kt`, `ProjectStorage`, `ProjectStorageTest`, `Feature Roadmap`, `formatPlaybackTime`, `WaveformExtractor`, `MainActivity.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `ProjectInfoDialog.kt`?**
  _High betweenness centrality (0.144) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `BeatPlaybackControls.kt`, `ProjectStorage`, `BeatPlayerPanel.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `EditorScreen`?**
  _High betweenness centrality (0.109) - this node is a cross-community bridge._
- **Why does `ProjectStorage` connect `ProjectStorage` to `HomeScreen.kt`, `ProjectStorageTest`, `MainActivity.kt`, `EditorScreenTest.kt`, `EditorScreen.kt`, `EditorScreen`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 13 inferred relationships involving `WaveformViewport` (e.g. with `.clamped_shrinkingViewportKeepsScrollWithinTheNewEnd()` and `.panBy_clampsAtTheStartAndEndOfTheTimeline()`) actually correct?**
  _`WaveformViewport` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _75 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.07127882599580712 - nodes in this community are weakly interconnected._