# Graph Report - Gh0stwrit3r  (2026-10-01)

## Corpus Check
- 366 files · ~579,222 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2065 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 4559 nodes · 11021 edges · 230 communities (138 shown, 92 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 1434 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `91551a6e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- .configure
- synthesize.c
- ProjectStorageBeatTest
- ProjectStorageTest
- WaveformViewport
- edge-to-edge/SKILL.md
- file
- WaveformExtractor
- android.content.Context
- VoiceSettingsTest
- script
- WaveformView.kt
- What You Must Do When Invoked
- PcmBeatDecoderTest
- fifo.c
- TextToSpeechTestCase
- graphify reference: extra exports and benchmark
- Investigation Protocol
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- Android
- EditorScreen
- compiledict.c
- compiledata.c
- .fromIpa
- PcmRingBuffer
- Recording Perfetto Traces on Android (Helper Scripts)
- tests/readclause.c
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- espeak_command.c
- TestConnection
- EditorScreen.kt
- Text to Phoneme Translation
- wavegen.c
- demo.js
- eSpeakActivity
- ucd_script_
- ttsengine.cpp
- LyricTextStyleTest.kt
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- Android Profiler Orchestrator
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- WaveformViewTest.kt
- SelectPhonemeTable
- Phoneme Model
- eSpeakService.c
- LyricsNotepad.kt
- DictionaryInstaller.kt
- Querying Perfetto traces
- PlaybackFocusState
- FrameManagerImpl
- Synthesizing Perfetto Trace Configs (Mix & Match)
- SystemFontCatalogTest.kt
- Diacritics
- BeatPlaybackService.kt
- Phoneme Tables
- speechPlayer.cpp
- TtsService
- .excludedWords
- category
- Perfetto Trace Analysis
- index.md
- SSML (Speech Synthesis Markup Language)
- DictionarySearch
- ucd_category_
- DictionaryDownloadsTest.kt
- speechWaveGenerator.cpp
- VoiceVariant
- klatt.c
- ucd_category_group_
- BeatPlaybackService
- intonation.c
- Recording Orchestrator
- ucd.h
- Phoneme Instructions
- CodePoint
- .createSeekBarPreference
- SettingsScreen
- TtsService.java
- unpackDictionaryArchive
- printdata.py
- ucd.py
- CheckVoiceDataTest
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- BeatPlaybackServiceTest
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Analysis Orchestrator
- ProjectInfoDialog
- espeak_api.c
- .render
- uprintf
- Resonator
- Change Log
- System-Wide Triage
- MainActivityTest
- Getting `trace_processor` working
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- LoadVoice
- Third-party software and dictionary data
- FramePositionEvents
- perfetto_sql.md
- printucddata_cpp.cpp
- Vowels
- guiding_principles.md
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- DictionaryDownloads
- ImportVoicePreference.java
- ProcessSsmlTag
- EspeakIpa.kt
- WaveformCache
- Playback and waveforms
- Android CLI Specialist
- eSpeak NG user guide
- hints_cpu.md
- PcmLoopRenderer
- .excludedWords
- hints_graphics.md
- Dictionaries
- numbers.md
- Language Attributes
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- Ghostwriter agent instructions
- Native pronunciation engine
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- hints_io.md
- Testing
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- SmoothLoopPlayback
- Third-party software and dictionary data
- hints_ipc.md
- Feature roadmap
- GhostwriterTheme
- .plan
- PlaybackFrameLedger
- Releasing
- Ghostwriter
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- tr_languages.c
- hints_memory.md
- .parse
- espeak_ng_Initialize
- sPlayer.c
- hints_power.md
- LyricTextSettings
- BeatPlayerPanel
- Configuration Files
- SystemFontFile
- MainActivity.kt
- ProjectLyricsStorage
- DictionaryHeadword
- PlaybackNotificationUpdater
- ExampleInstrumentedTest.kt
- LyricTextAlignment
- Build setup
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- DictionarySearchData
- RememberBeatPlayback.kt
- EspeakIpaInstrumentedTest
- IpaSearchKeys
- Settings
- SettingsFormatTest.kt
- espeak-ng.c
- AssonanceData
- Contribution Guide
- Callback
- espeak_ng_PrintStatusCodeMessage
- Translation fuzzers
- ProjectStorage

## God Nodes (most connected - your core abstractions)
1. `ucd_script_` - 186 edges
2. `script` - 186 edges
3. `WaveformMarker` - 68 edges
4. `BeatPlayer` - 62 edges
5. `GhostwriterTheme()` - 56 edges
6. `SpeechSynthesis` - 49 edges
7. `ProjectStorageTest` - 39 edges
8. `ucd_properties()` - 38 edges
9. `ucd_category_` - 36 edges
10. `category` - 36 edges

## Surprising Connections (you probably didn't know these)
- `Projects and settings` --references--> `LyricTextSettings`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/data/LyricTextSettings.kt
- `Playback and waveforms` --references--> `BeatPlayer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/BeatPlayer.kt
- `Playback and waveforms` --references--> `PcmLoopRenderer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmLoopRenderer.kt
- `Playback and waveforms` --references--> `PcmRingBuffer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmRingBuffer.kt
- `Playback and waveforms` --references--> `PlaybackFrameLedger`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PlaybackFrameLedger.kt

## Import Cycles
- None detected.

## Communities (230 total, 92 thin omitted)

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "synthesize.c"
Cohesion: 0.06
Nodes (48): CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods(), SetSpeedMultiplier(), GetMbrName(), MbrolaGenerate() (+40 more)

### Community 6 - "edge-to-edge/SKILL.md"
Cohesion: 0.12
Nodes (16): Adaptive Scaffolds, Checklist, Dialogs, IME, IMEs with Scaffolds code patterns, IMEs without Scaffolds code patterns, Lists, Navigation Bar Contrast \& System Bar Icons (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (4): WaveformExtractorInstrumentedTest, DecoderProgressGuard, WaveformExtractor, WaveformExtractorTest

### Community 9 - "android.content.Context"
Cohesion: 0.06
Nodes (6): EspeakApp, ImportVoicePreference, SeekBarPreference, SpeakPunctuationPreference, PrefsEspeakFragment, TtsSettingsActivity

### Community 12 - "script"
Cohesion: 0.01
Nodes (184): script, Adlm, Afak, Aghb, Ahom, Arab, Armi, Armn (+176 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "PcmBeatDecoderTest"
Cohesion: 0.11
Nodes (3): PcmBeatDecoderTest, PcmBeatDecoder, PcmBeatDecoderTest

### Community 19 - "fifo.c"
Cohesion: 0.09
Nodes (26): add_time_in_ms(), clock_gettime2(), event_clear_all(), event_copy(), event_declare(), event_delete(), event_init(), event_notify() (+18 more)

### Community 20 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (5): TextToSpeechTest, TextToSpeechTestCase, Exception, Voice, VoiceData

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "Investigation Protocol"
Cohesion: 0.20
Nodes (9): Candidate Investigation Protocol, Context, Investigation Protocol, Step 1: Prerequisites, Step 2: Calculate the time distribution, Step 3: Domain and Hints Discovery, Step 4: Exhaustive Investigation (Do Not Give Up Early), Step 5: Contextualize the Workload (+1 more)

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

### Community 30 - "Android"
Cohesion: 0.14
Nodes (14): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Dependencies, Installing (+6 more)

### Community 31 - "EditorScreen"
Cohesion: 0.24
Nodes (3): EditorScreenTest, EditorScreen(), PendingBeatPreparation

### Community 32 - "compiledict.c"
Cohesion: 0.05
Nodes (92): IsAlpha(), IsBracket(), IsDigit(), IsDigit09(), IsSpace(), isspace2(), towlower2(), utf8_in() (+84 more)

### Community 33 - "compiledata.c"
Cohesion: 0.07
Nodes (64): Read4Bytes(), StringToWord(), CalculateSample(), CallPhoneme(), CheckNextChar(), clean_context(), CompileElif(), CompileElse() (+56 more)

### Community 35 - "PcmRingBuffer"
Cohesion: 0.06
Nodes (13): ObservedSource, SmoothLoopPlaybackTest, PcmSource, PcmBeat, Loop, Page, PcmRingBuffer, Region (+5 more)

### Community 36 - "Recording Perfetto Traces on Android (Helper Scripts)"
Cohesion: 0.20
Nodes (9): 0. Download the Helper Scripts, 1. Memory Tracing, 2. Stack Sampling / Callstack Profiling (traced_perf), 3. System Tracing (CPU, Scheduling, & ATrace), 4. Custom Configs (Mix & Match Data Sources), A. Java Heap Dump (ART), B. Native C/C++ Heap Profiling (heapprofd), C. System-wide Memory Counters (+1 more)

### Community 38 - "tests/readclause.c"
Cohesion: 0.11
Nodes (36): AnnouncePunctuation(), CheckPhonemeMode(), clause_type_from_codepoint(), DecodeWithPhonemeMode(), Eof(), GetC(), IgnoreOrReplaceChar(), IsRomanU() (+28 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.07
Nodes (10): BeatPlayerInstrumentedTest, ProjectMetadata, WaveformMarker, indexOfSelectedMarker(), MarkerLoopFrames, MarkerLoopRange, replaceLoopMarker(), EditorMarkerDialogs() (+2 more)

### Community 41 - "speech.c"
Cohesion: 0.10
Nodes (5): ieee_extended_to_double(), espeak_SetUriCallback(), espeak_callback(), main(), SynthCallback()

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (47): create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc(), string_decoder_getc_auto(), string_decoder_getc_codepage(), string_decoder_getc_iso_10646_ucs_2(), string_decoder_getc_us_ascii() (+39 more)

### Community 43 - "espeak_command.c"
Cohesion: 0.10
Nodes (34): create_espeak_char(), create_espeak_key(), create_espeak_mark(), create_espeak_parameter(), create_espeak_punctuation_list(), create_espeak_terminated_msg(), create_espeak_text(), create_espeak_voice_name() (+26 more)

### Community 44 - "TestConnection"
Cohesion: 0.15
Nodes (6): openDictionaryArchive(), DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, TestConnection

### Community 46 - "Text to Phoneme Translation"
Cohesion: 0.10
Nodes (21): Character Substitution, Conditional Rules, Flags, Letter groups, Letter names, Multiple Words, Numbers, Numbers and Character Names (+13 more)

### Community 47 - "wavegen.c"
Cohesion: 0.07
Nodes (38): espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), KlattFini(), GetFrameRms(), MarkerEvent(), MbrolaFill(), MbrolaReset() (+30 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (8): eSpeakActivity, EspeakHandler, State, DOWNLOAD_FAILED, ERROR, LOADING, SUCCESS, GetSampleText

### Community 50 - "ucd_script_"
Cohesion: 0.01
Nodes (184): ucd_script_, UCD_SCRIPT_Adlm, UCD_SCRIPT_Afak, UCD_SCRIPT_Aghb, UCD_SCRIPT_Ahom, UCD_SCRIPT_Arab, UCD_SCRIPT_Armi, UCD_SCRIPT_Armn (+176 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (17): espeak_callback(), espeak_status_to_hresult(), TtsEngine, AddRef, TtsEngine_CreateInstance(), GetObjectToken, GetOutputFormat, GetStringValue (+9 more)

### Community 53 - "ucd_properties"
Cohesion: 0.23
Nodes (29): properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo(), properties_Lo_ideographic(), properties_Lu() (+21 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (20): close_mbrola(), close_pipes(), create_pipes(), err(), flush_mbrola(), free_pending_data(), init_mbrola(), lastErrorStr_mbrola() (+12 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.06
Nodes (3): AboutScreenTest, HomeScreenTest, ProjectInfoDialogTest

### Community 56 - "Android Profiler Orchestrator"
Cohesion: 0.22
Nodes (7): Environment setup, Set `$SKILL_ROOT`, Analysis, Android Profiler Orchestrator, Intent Disambiguation, Prerequisites and Setup, Recording

### Community 57 - "espeak_Initialize"
Cohesion: 0.12
Nodes (31): main(), PrintVersion(), espeak_Initialize(), espeak_SetVoiceByName(), espeak_Synth(), event_set_callback(), espeak_ng_GetSampleRate(), espeak_ng_InitializeOutput() (+23 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.11
Nodes (10): FileListAdapter, ViewHolder, InformationListAdapter, ViewHolder, VariantData, VariantDataListAdapter, ViewHolder, VoiceVariantPreference (+2 more)

### Community 61 - "WaveformViewTest.kt"
Cohesion: 0.14
Nodes (5): WaveformViewTest, MarkerLoopRole, END, NONE, START

### Community 62 - "SelectPhonemeTable"
Cohesion: 0.18
Nodes (11): MakePhonemeList(), ReInterpretPhoneme(), SetRegressiveVoicing(), SubstitutePhonemes(), LookupPhonemeTable(), SelectPhonemeTable(), SelectPhonemeTableName(), SetUpPhonemeTable() (+3 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.16
Nodes (18): getJniEnv(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeClassInit(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeCreate(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetAvailableVoices(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetVersion(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetPunctuationCharacters() (+10 more)

### Community 66 - "DictionaryInstaller.kt"
Cohesion: 0.14
Nodes (8): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY, FilterInputStream

### Community 67 - "Querying Perfetto traces"
Cohesion: 0.22
Nodes (8): Analytical Workflow (Standard Operating Procedure), Common Analysis Patterns, Discovering what's in the trace, Querying a trace: sessions, Querying Perfetto traces, Tips for writing good PerfettoSQL, Using the standard library, Where to look for more

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (15): FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex, newFrameRequest, oldFrameRequest, sampleCounter (+7 more)

### Community 70 - "Synthesizing Perfetto Trace Configs (Mix & Match)"
Cohesion: 0.29
Nodes (6): Data sources at a glance, Exemplar configs, Pitfalls, Shape of a config, Synthesizing Perfetto Trace Configs (Mix & Match), Top-level knobs

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "Phoneme Tables"
Cohesion: 0.12
Nodes (16): Attributes, Conditional Statements, Conditions, Customization of sound source files, endtype, lengthmod, Phoneme Definitions, Phoneme Files (+8 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (11): create, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize(), speechPlayer_terminate(), SpeechWaveGenerator, create (+3 more)

### Community 76 - "TtsService"
Cohesion: 0.09
Nodes (5): SpeechSynthesisTest, TextToSpeechServiceTest, TtsServiceTest, TtsService, Voice

### Community 77 - ".excludedWords"
Cohesion: 0.29
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 78 - "category"
Cohesion: 0.06
Nodes (32): category, Cc, Cf, Cn, Co, Cs, Ii, Ll (+24 more)

### Community 79 - "Perfetto Trace Analysis"
Cohesion: 0.33
Nodes (5): Perfetto Trace Analysis, Step 1: Identify what to investigate first, Step 2: Run a system-wide triage, Step 3: Investigate each branch in parallel, Step 4: Final report and consolidation

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.11
Nodes (18): audio, break, emphasis, HTML, HTML, mark, p, prosody (+10 more)

### Community 83 - "DictionarySearch"
Cohesion: 0.42
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 84 - "ucd_category_"
Cohesion: 0.06
Nodes (32): ucd_category_, UCD_CATEGORY_Cc, UCD_CATEGORY_Cf, UCD_CATEGORY_Cn, UCD_CATEGORY_Co, UCD_CATEGORY_Cs, UCD_CATEGORY_Ii, UCD_CATEGORY_Ll (+24 more)

### Community 85 - "DictionaryDownloadsTest.kt"
Cohesion: 0.10
Nodes (9): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, dictionaryArchive(), withDictionaryTestContext(), ContextWrapper, DictionaryDownloadsTest, InputStream, DictionaryArchiveSource (+1 more)

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (10): FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue, VoiceGenerator, aspirationGen, glottisOpen (+2 more)

### Community 88 - "klatt.c"
Cohesion: 0.19
Nodes (18): antiresonator(), DBtoLIN(), flutter(), frame_init(), gen_noise(), impulsive_source(), KlattInit(), KlattReset() (+10 more)

### Community 89 - "ucd_category_group_"
Cohesion: 0.10
Nodes (22): ucd_get_category_group_for_category(), ucd_lookup_category_group(), category_group, C, I, M, N, P (+14 more)

### Community 91 - "intonation.c"
Cohesion: 0.21
Nodes (14): calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments(), count_pitch_vowels(), CountUnstressed() (+6 more)

### Community 92 - "Recording Orchestrator"
Cohesion: 0.40
Nodes (4): Handling Composite Requests, Recording Orchestrator, Workflow Discovery and Routing, Workflow Selection

### Community 93 - "ucd.h"
Cohesion: 0.14
Nodes (26): ucd_lookup_category(), ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph(), ucd_islower() (+18 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 97 - "SettingsScreen"
Cohesion: 0.17
Nodes (4): SettingsScreenTest, SettingsDropdownRow(), SettingsScreen(), SettingsSliderRow()

### Community 99 - "unpackDictionaryArchive"
Cohesion: 0.28
Nodes (5): unpackDictionaryArchive(), FilterInputStream, DictionaryDownloadProgress, DictionaryArchiveTest, TrackedInput

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 103 - "Tune Definitions"
Cohesion: 0.14
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 107 - "comentrypoints.c"
Cohesion: 0.27
Nodes (6): ClassFactory_AddRef(), ClassFactory_LockServer(), ClassFactory_QueryInterface(), ClassFactory_Release(), DllCanUnloadNow(), DllGetClassObject()

### Community 108 - "espeakng.js"
Cohesion: 0.15
Nodes (8): eSpeakNG(), Building, Credits, Demo, Download, espeakng.js, Notes, Usage

### Community 109 - "utils.h"
Cohesion: 0.23
Nodes (4): calculateValueAtFadePosition(), ISNAN(), MAX(), MIN()

### Community 110 - "uprintf"
Cohesion: 0.15
Nodes (16): 10.0.0 - 2017-06-25, ucd_tolower(), ucd_totitle(), ucd_toupper(), lookup_script(), tolower(), toupper(), ucd_lookup_script() (+8 more)

### Community 111 - "Analysis Orchestrator"
Cohesion: 0.50
Nodes (3): Analysis Orchestrator, Handling Composite Requests, Workflow Discovery and Routing

### Community 112 - "ProjectInfoDialog"
Cohesion: 0.32
Nodes (4): parsePositiveBpm(), ProjectInfoDialog(), trimmedOrNull(), ProjectInfoBpmTest

### Community 114 - "espeak_api.c"
Cohesion: 0.17
Nodes (10): espeak_Char(), espeak_CompileDictionary(), espeak_Key(), espeak_SetParameter(), espeak_SetPunctuationList(), espeak_SetVoiceByFile(), espeak_SetVoiceByProperties(), espeak_Synth_Mark() (+2 more)

### Community 116 - "uprintf"
Cohesion: 0.30
Nodes (11): get_category_string(), get_script_string(), ucd_get_category_string(), ucd_get_script_string(), fget_utf8c(), fput_utf8c(), main(), print_file() (+3 more)

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.15
Nodes (12): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+4 more)

### Community 119 - "System-Wide Triage"
Cohesion: 0.50
Nodes (3): Final Output Format, Quick reference for triaging, System-Wide Triage

### Community 122 - "lb.md"
Cohesion: 0.18
Nodes (10): Build and use the project, Integration, Introduction, lb_emoji, lb_list, lb_rules, Luxembourgish customization, Phoneme inventory (+2 more)

### Community 123 - "MBROLA Voices"
Cohesion: 0.17
Nodes (11): 1. Add MBROLA voice definition file, 2. Add MBROLA phoneme translation file, 3. Compile voice and update Makefile.am file, Adding new MBROLA voice entry to eSpeak NG, Installation of MBROLA package from source, Installation of standard packages, Linux Installation, MBROLA Voices (+3 more)

### Community 124 - "Kirshenbaum (ASCII-IPA) Transcription Scheme"
Cohesion: 0.18
Nodes (11): Consonants, Diacritics, Kirshenbaum (ASCII-IPA) Transcription Scheme, Length, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+3 more)

### Community 125 - "CascadeFormantGenerator"
Cohesion: 0.18
Nodes (10): CascadeFormantGenerator, r1, r2, r3, r4, r5, r6, rN0 (+2 more)

### Community 126 - "LoadVoice"
Cohesion: 0.07
Nodes (42): GetFileLength(), is_str_totally_null(), strncpy0(), DecodeRule(), print_dictionary_flags(), InitGroups(), LoadDictionary(), Reverse4Bytes() (+34 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.33
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (7): fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint(), uprintf_is()

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "DictionaryDownloads"
Cohesion: 0.29
Nodes (4): DictionaryDownloads(), DictionaryDownloadsScreen(), downloadSourceLabel(), dictionaryLanguageLabel()

### Community 137 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (5): CheckVoiceData, AsyncExtract, DownloadVoiceData, ExtractProgress, FileUtils

### Community 138 - "ProcessSsmlTag"
Cohesion: 0.14
Nodes (20): LookupMnem(), AddNameData(), LoadSoundFile(), LoadSoundFile2(), LookupSoundicon(), attr_prosody_value(), attrcmp(), attrcopy_utf8() (+12 more)

### Community 141 - "Playback and waveforms"
Cohesion: 0.33
Nodes (6): Architecture, Dictionaries, Ghostwriter documentation, Known limitations, Playback and waveforms, Projects and settings

### Community 142 - "Android CLI Specialist"
Cohesion: 0.07
Nodes (26): Android Interaction Rules, Annotated Screenshot, Input, Screenshot, Text Input, Tools, UI Dump, Handling failure (+18 more)

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 146 - "PcmLoopRenderer"
Cohesion: 0.30
Nodes (3): PcmLoopRenderer, PcmLoopRendererTest, UnavailableSource

### Community 147 - ".excludedWords"
Cohesion: 0.17
Nodes (5): AssonanceFormFilter, Entry, PrefixNode, Pronounced, AssonanceFormFilterTest

### Community 149 - "Dictionaries"
Cohesion: 0.33
Nodes (6): Dictionaries, Pronunciation lookup, Release inputs and installation, Responsiveness and verification, Search behavior, Using dictionaries

### Community 152 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 156 - "Ghostwriter agent instructions"
Cohesion: 0.40
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 157 - "Native pronunciation engine"
Cohesion: 0.40
Nodes (5): Native pronunciation engine, Regenerating language data, Runtime contract, Source and Android build, Verification and distribution

### Community 171 - "Testing"
Cohesion: 0.40
Nodes (5): Android tests, Checks without a device, Coverage and regression guidance, Coverage reports, Testing

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 175 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 177 - "Feature roadmap"
Cohesion: 0.18
Nodes (11): About and attribution, Beats and playback, Dictionaries, Feature roadmap, Guiding rules, Implementation guidance, Import, export, and optional sync, Lyric editor (+3 more)

### Community 178 - "GhostwriterTheme"
Cohesion: 0.10
Nodes (17): DictionaryScreenTest, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryMatch, DictionarySearchMode (+9 more)

### Community 182 - "Releasing"
Cohesion: 0.50
Nodes (4): Build and verify, Maintainer runtime checks, Package matching source, Releasing

### Community 184 - "Ghostwriter"
Cohesion: 0.50
Nodes (4): Build and test, Documentation and contributions, Ghostwriter, License

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 187 - "tr_languages.c"
Cohesion: 0.33
Nodes (15): (Re)definition of character groups, SetLengthMods(), NewTranslator(), ProcessLanguageOptions(), ResetLetterBits(), SelectTranslator(), SetArabicLetters(), SetCyrillicLetters() (+7 more)

### Community 190 - "espeak_ng_Initialize"
Cohesion: 0.12
Nodes (12): eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, InitNamedata(), InitText2() (+4 more)

### Community 192 - "sPlayer.c"
Cohesion: 0.25
Nodes (9): fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP(), KlattInitSP(), KlattResetSP(), MIN(), mixWaveFile(), needsMixWaveFile() (+1 more)

### Community 194 - "LyricTextSettings"
Cohesion: 0.25
Nodes (3): LyricTextSettings, LyricTextSettingsTest, LyricTextStyleTest

### Community 195 - "BeatPlayerPanel"
Cohesion: 0.11
Nodes (9): BeatComponentsTest, LongBeatWarningDialog(), ReassignBeatDialog(), WaveformMarkerDialog(), BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), WaveformZoomButton() (+1 more)

### Community 196 - "Configuration Files"
Cohesion: 0.12
Nodes (16): Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation, Dictionary Files (+8 more)

### Community 198 - "SystemFontFile"
Cohesion: 0.30
Nodes (5): LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), LyricFontTest

### Community 200 - "MainActivity.kt"
Cohesion: 0.13
Nodes (8): MainActivity, openExternalLink(), AboutLink(), AboutScreen(), AboutSectionTitle(), HomeScreen(), NewProjectDialog(), RenameProjectDialog()

### Community 203 - "PlaybackNotificationUpdater"
Cohesion: 0.30
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 205 - "LyricTextAlignment"
Cohesion: 0.14
Nodes (13): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+5 more)

### Community 206 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Checkout and build, Requirements

### Community 207 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (3): copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize()

### Community 208 - "DictionarySearchData"
Cohesion: 0.22
Nodes (3): DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 217 - "IpaSearchKeys"
Cohesion: 0.23
Nodes (4): DictionarySearchPlan, Prefix, IpaSearchKeys, Keys

### Community 219 - "SettingsFormatTest.kt"
Cohesion: 0.38
Nodes (3): formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 220 - "espeak-ng.c"
Cohesion: 0.17
Nodes (10): gcd(), getopt(), getopt_internal(), getopt_long(), permute_args(), CloseWavFile(), DisplayVoices(), OpenWavFile() (+2 more)

### Community 222 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 225 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.39
Nodes (4): create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage()

### Community 226 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 228 - "ProjectStorage"
Cohesion: 0.20
Nodes (9): ProjectStorage, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Screen (+1 more)

## Knowledge Gaps
- **1010 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+1005 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1533 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **92 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (3× useful, score=2.922057951)
- `DictionarySearch` (2× useful, score=1.92387683)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `script` connect `script` to `uprintf`, `ucd.h`, `uprintf`?**
  _High betweenness centrality (0.102) - this node is a cross-community bridge._
- **Why does `ucd_script_` connect `ucd_script_` to `uprintf`, `ucd.h`, `uprintf`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **Why does `WaveformMarker` connect `WaveformMarker` to `BeatPlayer`, `BeatPlayerPanel`, `ProjectStorageBeatTest`, `file`, `BeatPlaybackServiceTest`, `EditorScreen.kt`, `SettingsScreen.kt`, `WaveformView.kt`, `.render`, `EditorScreenTest.kt`, `BeatPlaybackServiceTest.kt`, `WaveformViewTest.kt`, `EditorScreen`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `WaveformMarker` (e.g. with `.frameMarkers_persistExactSamplesWithoutMillisecondRounding()` and `.frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames()`) actually correct?**
  _`WaveformMarker` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _1010 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.11406423034330011 - nodes in this community are weakly interconnected._