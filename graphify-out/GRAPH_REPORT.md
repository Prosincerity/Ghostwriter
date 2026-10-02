# Graph Report - Gh0stwrit3r  (2026-10-02)

## Corpus Check
- 379 files · ~586,385 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2065 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 4709 nodes · 11503 edges · 252 communities (149 shown, 103 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 1470 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `bb2e62f3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- .configure
- Generate
- .writeText
- ProjectStorageTest
- WaveformViewTest
- edge-to-edge/SKILL.md
- SeekBarPreference
- WaveformExtractor
- event.c
- VoiceSettingsTest
- script
- WaveformView.kt
- What You Must Do When Invoked
- PcmBeatDecoderTest
- eSpeakNGWorker
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
- BeatPlaybackService
- EditorScreenTest
- dictionary.c
- compiledata.c
- .createSeekBarPreference
- PcmRingBuffer
- Recording Perfetto Traces on Android (Helper Scripts)
- libespeak-ng/readclause.c
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- Synthesize
- TestConnection
- DictionaryDownloads.kt
- Text to Phoneme Translation
- wavegen.c
- demo.js
- eSpeakActivity
- ucd_script_
- TtsEngine
- tr_languages.c
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- Android Profiler Orchestrator
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- EditorScreen
- .fromIpa
- t_espeak_command
- Phoneme Model
- eSpeakService.c
- BeatPlayerPanel
- Configuration Files
- Querying Perfetto traces
- remember
- FrameManagerImpl
- Synthesizing Perfetto Trace Configs (Mix & Match)
- SystemFontFile
- Diacritics
- PlaybackFocusState
- .cancelDownloadRestoresControlsWithoutInstalling
- speechPlayer.cpp
- TtsService
- .excludedWords
- category
- Perfetto Trace Analysis
- index.md
- SSML (Speech Synthesis Markup Language)
- Android
- ucd_category_
- withDictionaryTestContext
- speechWaveGenerator.cpp
- VoiceVariant
- klatt.c
- ucd_category_group_
- .excludedWords
- intonation.c
- Recording Orchestrator
- ucd.h
- Phoneme Instructions
- CodePoint
- AssonanceData
- GhostwriterTheme
- TtsService.java
- DictionaryArchiveSource
- printdata.py
- ucd.py
- MakePhonemeList
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- .setMarkers
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Analysis Orchestrator
- LyricTextStyleTest.kt
- status_to_espeak_error
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
- DictionaryHeadword
- ProjectInfoDialog.kt
- Third-party software and dictionary data
- LyricTextSettings
- perfetto_sql.md
- printucddata_cpp.cpp
- Vowels
- guiding_principles.md
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- android.content.Context
- Language Attributes
- ProcessSsmlTag
- DictionarySearch
- WaveformCache
- WaveformViewport
- Android CLI Specialist
- eSpeak NG user guide
- hints_cpu.md
- PcmLoopRenderer
- .plan
- hints_graphics.md
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- .parse
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- .plan
- EditorScreen.kt
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- hints_io.md
- DictionarySearchData
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- EspeakIpaInstrumentedTest
- SmoothLoopPlayback
- compiledict.c
- hints_ipc.md
- Feature roadmap
- DictionaryScreen
- ImportVoicePreference.java
- CheckVoiceDataTest
- EditorMarkerDialogsTest.kt
- PlaybackFrameLedger
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- ProjectStorage
- hints_memory.md
- LoadSpect
- ProjectSummaryTest
- sPlayer.c
- hints_power.md
- DictionaryInstaller.kt
- WaveformViewTest.kt
- GhostThemeContrastTest.kt
- Voice Attributes
- SettingsScreen.kt
- ProjectLyricsStorage
- Dictionaries
- Q: Why do waveform markers lag behind the pointer while dragging?
- ESPEAK_NG_API
- Ghostwriter agent instructions
- Q: Does this app have haptic feedback for waveform markers, beat controls, and destructive actions?
- ttsengine.cpp
- main
- Native pronunciation engine
- Testing
- Settings
- SettingsFormatTest.kt
- ContextWrapper
- getopt.c
- Third-party software and dictionary data
- Releasing
- Ghostwriter
- Translation fuzzers
- EspeakIpa
- Build setup
- ExampleInstrumentedTest.kt
- WaveformWarningTest.kt
- FramePositionEvents
- HomeScreen.kt
- Contribution Guide
- Q: can you check which part of the UI is customizable and is currently in a default state? give me suggestions fitting for this app on how the UI should be changed to be prettier.
- Q: Why does justified lyric text overflow with tight letter spacing?
- numbers.md
- Ghostwriter documentation

## God Nodes (most connected - your core abstractions)
1. `ucd_script_` - 186 edges
2. `script` - 186 edges
3. `WaveformMarker` - 80 edges
4. `BeatPlayer` - 75 edges
5. `GhostwriterTheme()` - 65 edges
6. `SpeechSynthesis` - 49 edges
7. `ProjectStorageTest` - 42 edges
8. `LyricTextSettings` - 38 edges
9. `ucd_properties()` - 38 edges
10. `ucd_category_` - 36 edges

## Surprising Connections (you probably didn't know these)
- `Projects and settings` --references--> `LyricTextSettings`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/data/LyricTextSettings.kt
- `Playback and waveforms` --references--> `BeatPlayer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/BeatPlayer.kt
- `Playback and waveforms` --references--> `PcmLoopRenderer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmLoopRenderer.kt
- `Playback and waveforms` --references--> `PcmRingBuffer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmRingBuffer.kt
- `Playback and waveforms` --references--> `SmoothLoopPlayback`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/SmoothLoopPlayback.kt

## Import Cycles
- None detected.

## Communities (252 total, 103 thin omitted)

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "Generate"
Cohesion: 0.07
Nodes (39): GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), WritePitch(), CountVowelPosition(), InterpretCondition(), InterpretPhoneme(), InterpretPhoneme2() (+31 more)

### Community 5 - "WaveformViewTest"
Cohesion: 0.17
Nodes (4): WaveformViewTest, PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 6 - "edge-to-edge/SKILL.md"
Cohesion: 0.12
Nodes (16): Adaptive Scaffolds, Checklist, Dialogs, IME, IMEs with Scaffolds code patterns, IMEs without Scaffolds code patterns, Lists, Navigation Bar Contrast \& System Bar Icons (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (4): WaveformExtractorInstrumentedTest, DecoderProgressGuard, WaveformExtractor, WaveformExtractorTest

### Community 9 - "event.c"
Cohesion: 0.10
Nodes (26): add_time_in_ms(), clock_gettime2(), event_clear_all(), event_copy(), event_declare(), event_delete(), event_init(), event_notify() (+18 more)

### Community 12 - "script"
Cohesion: 0.01
Nodes (184): script, Adlm, Afak, Aghb, Ahom, Arab, Armi, Armn (+176 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "PcmBeatDecoderTest"
Cohesion: 0.11
Nodes (3): PcmBeatDecoderTest, PcmBeatDecoder, PcmBeatDecoderTest

### Community 19 - "eSpeakNGWorker"
Cohesion: 0.13
Nodes (8): eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, event_set_callback(), espeak_SetSynthCallback()

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

### Community 30 - "BeatPlaybackService"
Cohesion: 0.05
Nodes (5): BeatPlaybackServiceTest, ServiceConnection, BeatPlaybackService, Callback, LocalBinder

### Community 32 - "dictionary.c"
Cohesion: 0.06
Nodes (82): 1.49.1 - 2017-01-21, is_str_totally_null(), IsAlpha(), IsBracket(), IsDigit(), IsSpace(), towlower2(), utf8_in() (+74 more)

### Community 33 - "compiledata.c"
Cohesion: 0.10
Nodes (52): Read4Bytes(), StringToWord(), CalculateSample(), CallPhoneme(), CheckNextChar(), clean_context(), CompileElif(), CompileElse() (+44 more)

### Community 34 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 35 - "PcmRingBuffer"
Cohesion: 0.06
Nodes (13): ObservedSource, SmoothLoopPlaybackTest, PcmSource, PcmBeat, Loop, Page, PcmRingBuffer, Region (+5 more)

### Community 36 - "Recording Perfetto Traces on Android (Helper Scripts)"
Cohesion: 0.20
Nodes (9): 0. Download the Helper Scripts, 1. Memory Tracing, 2. Stack Sampling / Callstack Profiling (traced_perf), 3. System Tracing (CPU, Scheduling, & ATrace), 4. Custom Configs (Mix & Match Data Sources), A. Java Heap Dump (ART), B. Native C/C++ Heap Profiling (heapprofd), C. System-wide Memory Counters (+1 more)

### Community 38 - "libespeak-ng/readclause.c"
Cohesion: 0.11
Nodes (38): AnnouncePunctuation(), CheckPhonemeMode(), clause_type_from_codepoint(), DecodeWithPhonemeMode(), Eof(), GetC(), IgnoreOrReplaceChar(), IsRomanU() (+30 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.06
Nodes (16): BeatPlayerInstrumentedTest, MarkerLoopRole, END, NONE, START, ProjectMetadata, WaveformMarker, indexOfSelectedMarker() (+8 more)

### Community 40 - "SpeechSynthesis"
Cohesion: 0.08
Nodes (3): SpeechSynthesisTest, SpeechSynthesis, SynthReadyCallback

### Community 41 - "speech.c"
Cohesion: 0.08
Nodes (4): espeak_SetUriCallback(), espeak_SetPhonemeCallback(), espeak_callback(), SynthCallback()

### Community 42 - "tests/encoding.c"
Cohesion: 0.17
Nodes (48): create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc(), string_decoder_getc_auto(), string_decoder_getc_codepage(), string_decoder_getc_iso_10646_ucs_2(), string_decoder_getc_us_ascii() (+40 more)

### Community 43 - "Synthesize"
Cohesion: 0.26
Nodes (11): process_espeak_command(), InitNamedata(), InitText2(), espeak_ng_SetPunctuationList(), sync_espeak_Char(), sync_espeak_Key(), sync_espeak_SetPunctuationList(), sync_espeak_Synth() (+3 more)

### Community 44 - "TestConnection"
Cohesion: 0.12
Nodes (7): openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, TestConnection

### Community 45 - "DictionaryDownloads.kt"
Cohesion: 0.13
Nodes (6): AboutLink(), AboutScreen(), AboutSectionTitle(), DictionaryDownloads(), DictionaryDownloadsScreen(), downloadSourceLabel()

### Community 46 - "Text to Phoneme Translation"
Cohesion: 0.10
Nodes (21): Character Substitution, Conditional Rules, Flags, Letter groups, Letter names, Multiple Words, Numbers, Numbers and Character Names (+13 more)

### Community 47 - "wavegen.c"
Cohesion: 0.10
Nodes (31): MarkerEvent(), MbrolaFill(), MbrolaReset(), AdvanceParameters(), ApplyBreath(), espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks(), GetAmplitude() (+23 more)

### Community 48 - "demo.js"
Cohesion: 0.07
Nodes (27): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+19 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (8): eSpeakActivity, EspeakHandler, State, DOWNLOAD_FAILED, ERROR, LOADING, SUCCESS, GetSampleText

### Community 50 - "ucd_script_"
Cohesion: 0.01
Nodes (184): ucd_script_, UCD_SCRIPT_Adlm, UCD_SCRIPT_Afak, UCD_SCRIPT_Aghb, UCD_SCRIPT_Ahom, UCD_SCRIPT_Arab, UCD_SCRIPT_Armi, UCD_SCRIPT_Armn (+176 more)

### Community 51 - "TtsEngine"
Cohesion: 0.11
Nodes (15): espeak_status_to_hresult(), TtsEngine, AddRef, TtsEngine_CreateInstance(), GetObjectToken, GetOutputFormat, GetStringValue, objectToken (+7 more)

### Community 52 - "tr_languages.c"
Cohesion: 0.15
Nodes (23): (Re)definition of character groups, CalcLengths(), DoEmbedded2(), SetLengthMods(), SetSpeed(), SetSpeedFactors(), SetSpeedMods(), SetSpeedMultiplier() (+15 more)

### Community 53 - "ucd_properties"
Cohesion: 0.23
Nodes (29): properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo(), properties_Lo_ideographic(), properties_Lu() (+21 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (20): close_mbrola(), close_pipes(), create_pipes(), err(), flush_mbrola(), free_pending_data(), init_mbrola(), lastErrorStr_mbrola() (+12 more)

### Community 56 - "Android Profiler Orchestrator"
Cohesion: 0.22
Nodes (7): Environment setup, Set `$SKILL_ROOT`, Analysis, Android Profiler Orchestrator, Intent Disambiguation, Prerequisites and Setup, Recording

### Community 57 - "espeak_Initialize"
Cohesion: 0.18
Nodes (25): espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), espeak_ng_Synthesize(), main(), test_espeak_initialize(), test_espeak_ng_phoneme_events() (+17 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.13
Nodes (9): FileListAdapter, ViewHolder, InformationListAdapter, ViewHolder, VariantDataListAdapter, ViewHolder, VoiceVariantPreference, ResourceIdListAdapter (+1 more)

### Community 60 - "EditorScreen"
Cohesion: 0.19
Nodes (10): About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Screen, Settings (+2 more)

### Community 61 - ".fromIpa"
Cohesion: 0.16
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 62 - "t_espeak_command"
Cohesion: 0.11
Nodes (20): create_espeak_char(), create_espeak_mark(), create_espeak_parameter(), create_espeak_punctuation_list(), create_espeak_terminated_msg(), create_espeak_text(), create_espeak_voice_name(), create_espeak_voice_spec() (+12 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.17
Nodes (18): getJniEnv(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeClassInit(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeCreate(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetAvailableVoices(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetVersion(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetPunctuationCharacters() (+10 more)

### Community 65 - "BeatPlayerPanel"
Cohesion: 0.13
Nodes (6): BeatComponentsTest, WaveformMarkerDialog(), BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), WaveformZoomButton()

### Community 66 - "Configuration Files"
Cohesion: 0.12
Nodes (16): Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation, Dictionary Files (+8 more)

### Community 67 - "Querying Perfetto traces"
Cohesion: 0.22
Nodes (8): Analytical Workflow (Standard Operating Procedure), Common Analysis Patterns, Discovering what's in the trace, Querying a trace: sessions, Querying Perfetto traces, Tips for writing good PerfettoSQL, Using the standard library, Where to look for more

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (15): FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex, newFrameRequest, oldFrameRequest, sampleCounter (+7 more)

### Community 70 - "Synthesizing Perfetto Trace Configs (Mix & Match)"
Cohesion: 0.29
Nodes (6): Data sources at a glance, Exemplar configs, Pitfalls, Shape of a config, Synthesizing Perfetto Trace Configs (Mix & Match), Top-level knobs

### Community 71 - "SystemFontFile"
Cohesion: 0.14
Nodes (7): SystemFontCatalogTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), SystemFontCatalog, LyricFontTest

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - ".cancelDownloadRestoresControlsWithoutInstalling"
Cohesion: 0.19
Nodes (3): ByteArrayInputStream, DictionaryDownloadsTest, InputStream

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.19
Nodes (7): create, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize(), speechPlayer_terminate(), create

### Community 76 - "TtsService"
Cohesion: 0.11
Nodes (4): TextToSpeechServiceTest, TtsServiceTest, TtsService, Voice

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

### Community 83 - "Android"
Cohesion: 0.14
Nodes (14): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Dependencies, Installing (+6 more)

### Community 84 - "ucd_category_"
Cohesion: 0.06
Nodes (32): ucd_category_, UCD_CATEGORY_Cc, UCD_CATEGORY_Cf, UCD_CATEGORY_Cn, UCD_CATEGORY_Co, UCD_CATEGORY_Cs, UCD_CATEGORY_Ii, UCD_CATEGORY_Ll (+24 more)

### Community 85 - "withDictionaryTestContext"
Cohesion: 0.19
Nodes (5): DictionaryInstallerInstrumentedTest, dictionaryArchive(), withDictionaryTestContext(), ContextWrapper, IpaGenerator

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (10): FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue, VoiceGenerator, aspirationGen, glottisOpen (+2 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.18
Nodes (3): VoiceVariantTest, VariantData, VoiceVariant

### Community 88 - "klatt.c"
Cohesion: 0.20
Nodes (17): antiresonator(), DBtoLIN(), flutter(), frame_init(), gen_noise(), impulsive_source(), KlattInit(), KlattReset() (+9 more)

### Community 89 - "ucd_category_group_"
Cohesion: 0.10
Nodes (22): ucd_get_category_group_for_category(), ucd_lookup_category_group(), category_group, C, I, M, N, P (+14 more)

### Community 90 - ".excludedWords"
Cohesion: 0.16
Nodes (5): AssonanceFormFilter, Entry, PrefixNode, Pronounced, AssonanceFormFilterTest

### Community 91 - "intonation.c"
Cohesion: 0.21
Nodes (15): calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments(), count_pitch_vowels(), CountUnstressed() (+7 more)

### Community 92 - "Recording Orchestrator"
Cohesion: 0.40
Nodes (4): Handling Composite Requests, Recording Orchestrator, Workflow Discovery and Routing, Workflow Selection

### Community 93 - "ucd.h"
Cohesion: 0.14
Nodes (31): DecodePhonemes(), ucd_lookup_category(), ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+23 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 97 - "GhostwriterTheme"
Cohesion: 0.09
Nodes (4): AboutScreenTest, HomeScreenTest, SettingsScreenTest, GhostwriterTheme()

### Community 99 - "DictionaryArchiveSource"
Cohesion: 0.23
Nodes (6): unpackDictionaryArchive(), FilterInputStream, DictionaryArchiveSource, DictionaryDownloadProgress, DictionaryArchiveTest, TrackedInput

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 102 - "MakePhonemeList"
Cohesion: 0.29
Nodes (4): MakePhonemeList(), ReInterpretPhoneme(), SetRegressiveVoicing(), SubstitutePhonemes()

### Community 103 - "Tune Definitions"
Cohesion: 0.15
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.11
Nodes (15): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGenerator, setFrameManager, SpeechWaveGeneratorImpl, cascade (+7 more)

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
Cohesion: 0.23
Nodes (13): 10.0.0 - 2017-06-25, get_category_string(), get_script_string(), ucd_get_category_string(), ucd_get_script_string(), fget_utf8c(), fput_utf8c(), iswblank() (+5 more)

### Community 111 - "Analysis Orchestrator"
Cohesion: 0.50
Nodes (3): Analysis Orchestrator, Handling Composite Requests, Workflow Discovery and Routing

### Community 112 - "LyricTextStyleTest.kt"
Cohesion: 0.10
Nodes (15): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+7 more)

### Community 115 - "status_to_espeak_error"
Cohesion: 0.14
Nodes (11): espeak_Char(), espeak_Key(), espeak_SetParameter(), espeak_SetPunctuationList(), espeak_SetVoiceByFile(), espeak_Synth_Mark(), status_to_espeak_error(), void() (+3 more)

### Community 116 - "uprintf"
Cohesion: 0.23
Nodes (10): ucd_tolower(), ucd_totitle(), ucd_toupper(), ucd_lookup_script(), fget_utf8c(), fput_utf8c(), main(), print_file() (+2 more)

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "System-Wide Triage"
Cohesion: 0.50
Nodes (3): Final Output Format, Quick reference for triaging, System-Wide Triage

### Community 122 - "lb.md"
Cohesion: 0.18
Nodes (10): Build and use the project, Integration, Introduction, lb_emoji, lb_list, lb_rules, Luxembourgish customization, Phoneme inventory (+2 more)

### Community 123 - "MBROLA Voices"
Cohesion: 0.18
Nodes (11): 1. Add MBROLA voice definition file, 2. Add MBROLA phoneme translation file, 3. Compile voice and update Makefile.am file, Adding new MBROLA voice entry to eSpeak NG, Installation of MBROLA package from source, Installation of standard packages, Linux Installation, MBROLA Voices (+3 more)

### Community 124 - "Kirshenbaum (ASCII-IPA) Transcription Scheme"
Cohesion: 0.18
Nodes (11): Consonants, Diacritics, Kirshenbaum (ASCII-IPA) Transcription Scheme, Length, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+3 more)

### Community 125 - "CascadeFormantGenerator"
Cohesion: 0.18
Nodes (10): CascadeFormantGenerator, r1, r2, r3, r4, r5, r6, rN0 (+2 more)

### Community 127 - "ProjectInfoDialog.kt"
Cohesion: 0.10
Nodes (6): LongBeatWarningDialog(), ReassignBeatDialog(), parsePositiveBpm(), ProjectInfoDialog(), trimmedOrNull(), ProjectInfoBpmTest

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.33
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 129 - "LyricTextSettings"
Cohesion: 0.14
Nodes (5): LyricsNotepadTest, LyricTextSettings, LyricsNotepad(), LyricTextSettingsTest, LyricTextStyleTest

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (7): fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint(), uprintf_is()

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "android.content.Context"
Cohesion: 0.11
Nodes (4): EspeakApp, ImportVoicePreference, PrefsEspeakFragment, TtsSettingsActivity

### Community 137 - "Language Attributes"
Cohesion: 0.12
Nodes (16): brackets, bracketsAnnounced, dictionary, dictmin, dictrules, intonation, Language Attributes, lowercaseSentence (+8 more)

### Community 138 - "ProcessSsmlTag"
Cohesion: 0.13
Nodes (21): LookupEnvelopeName(), LookupMnem(), AddNameData(), LoadSoundFile(), LoadSoundFile2(), LookupSoundicon(), attr_prosody_value(), attrcmp() (+13 more)

### Community 139 - "DictionarySearch"
Cohesion: 0.42
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 142 - "Android CLI Specialist"
Cohesion: 0.07
Nodes (26): Android Interaction Rules, Annotated Screenshot, Input, Screenshot, Text Input, Tools, UI Dump, Handling failure (+18 more)

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 146 - "PcmLoopRenderer"
Cohesion: 0.25
Nodes (3): PcmLoopRenderer, PcmLoopRendererTest, UnavailableSource

### Community 147 - ".plan"
Cohesion: 0.29
Nodes (3): DictionarySearchPlan, Prefix, DictionarySearchPlanTest

### Community 149 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.39
Nodes (3): copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize()

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 157 - "EditorScreen.kt"
Cohesion: 0.06
Nodes (5): MainActivity, openExternalLink(), rememberBeatPlayback(), ServiceConnection, displayNameFor()

### Community 171 - "DictionarySearchData"
Cohesion: 0.22
Nodes (3): DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 175 - "compiledict.c"
Cohesion: 0.09
Nodes (27): IsDigit09(), isspace2(), clean_context(), compile_dictlist_end(), compile_dictlist_file(), compile_dictlist_start(), compile_dictrules(), compile_lettergroup() (+19 more)

### Community 177 - "Feature roadmap"
Cohesion: 0.18
Nodes (11): About and attribution, Beats and playback, Dictionaries, Feature roadmap, Guiding rules, Implementation guidance, Import, export, and optional sync, Lyric editor (+3 more)

### Community 178 - "DictionaryScreen"
Cohesion: 0.11
Nodes (17): DictionaryScreenTest, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryMatch, DictionarySearchMode (+9 more)

### Community 179 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (5): CheckVoiceData, AsyncExtract, DownloadVoiceData, ExtractProgress, FileUtils

### Community 184 - "PlaybackFrameLedger"
Cohesion: 0.18
Nodes (6): PlaybackFrameLedger, PlaybackFrameLedgerTest, Architecture, Dictionaries, Playback and waveforms, Projects and settings

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 189 - "LoadSpect"
Cohesion: 0.14
Nodes (13): LoadSpect(), ieee_extended_to_double(), GetFrameLength(), GetFrameRms(), LoadFrame(), LoadSpectSeq(), read_double(), SpectFrameCreate() (+5 more)

### Community 192 - "sPlayer.c"
Cohesion: 0.25
Nodes (9): fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP(), KlattInitSP(), KlattResetSP(), MIN(), mixWaveFile(), needsMixWaveFile() (+1 more)

### Community 194 - "DictionaryInstaller.kt"
Cohesion: 0.16
Nodes (7): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY

### Community 198 - "Voice Attributes"
Cohesion: 0.09
Nodes (22): breath, breathw, consonants, echo, flutter, formant, freq\_add, gender (+14 more)

### Community 200 - "SettingsScreen.kt"
Cohesion: 0.33
Nodes (5): SettingsDropdownRow(), SettingsNavigationRow(), SettingsScreen(), SettingsSection(), SettingsSliderRow()

### Community 203 - "Dictionaries"
Cohesion: 0.33
Nodes (6): Dictionaries, Pronunciation lookup, Release inputs and installation, Responsiveness and verification, Search behavior, Using dictionaries

### Community 204 - "Q: Why do waveform markers lag behind the pointer while dragging?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Why do waveform markers lag behind the pointer while dragging?, Source Nodes

### Community 205 - "ESPEAK_NG_API"
Cohesion: 0.29
Nodes (5): fifo_init(), espeak_ng_GetSampleRate(), espeak_ng_InitializeOutput(), espeak_ng_InitializePath(), espeak_ng_SetPhonemeEvents()

### Community 206 - "Ghostwriter agent instructions"
Cohesion: 0.40
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 207 - "Q: Does this app have haptic feedback for waveform markers, beat controls, and destructive actions?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does this app have haptic feedback for waveform markers, beat controls, and destructive actions?, Source Nodes

### Community 209 - "main"
Cohesion: 0.05
Nodes (53): CloseWavFile(), DisplayVoices(), main(), OpenWavFile(), SynthCallback(), Write4Bytes(), espeak_ng_SetRandSeed(), espeak_rand() (+45 more)

### Community 216 - "Native pronunciation engine"
Cohesion: 0.40
Nodes (5): Native pronunciation engine, Regenerating language data, Runtime contract, Source and Android build, Verification and distribution

### Community 217 - "Testing"
Cohesion: 0.40
Nodes (5): Android tests, Checks without a device, Coverage and regression guidance, Coverage reports, Testing

### Community 219 - "SettingsFormatTest.kt"
Cohesion: 0.30
Nodes (4): formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 221 - "getopt.c"
Cohesion: 0.57
Nodes (5): gcd(), getopt(), getopt_internal(), getopt_long(), permute_args()

### Community 222 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 223 - "Releasing"
Cohesion: 0.50
Nodes (4): Build and verify, Maintainer runtime checks, Package matching source, Releasing

### Community 225 - "Ghostwriter"
Cohesion: 0.50
Nodes (4): Build and test, Documentation and contributions, Ghostwriter, License

### Community 226 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 229 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Checkout and build, Requirements

### Community 233 - "HomeScreen.kt"
Cohesion: 0.13
Nodes (5): ProjectSummary, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), EmptyProjectsPreview()

### Community 235 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 237 - "Q: can you check which part of the UI is customizable and is currently in a default state? give me suggestions fitting for this app on how the UI should be changed to be prettier."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: can you check which part of the UI is customizable and is currently in a default state? give me suggestions fitting for this app on how the UI should be changed to be prettier., Source Nodes

### Community 240 - "Q: Why does justified lyric text overflow with tight letter spacing?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Why does justified lyric text overflow with tight letter spacing?, Source Nodes

## Knowledge Gaps
- **1021 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+1016 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1561 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **103 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (3× useful, score=2.869226289)
- `BeatPlaybackControls.kt` (2× useful, score=1.968450048)
- `WaveformView.kt` (2× useful, score=1.968450048)
- `DictionarySearch` (2× useful, score=1.889092575)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ucd_script_` connect `ucd_script_` to `uprintf`, `ucd.h`, `uprintf`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `script` connect `script` to `ucd.h`, `uprintf`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `BeatPlayerPanel`, `WaveformViewTest.kt`, `PcmRingBuffer`, `WaveformMarker`, `.setMarkers`, `SmoothLoopPlayback`, `BeatPlayerPanel.kt`, `EditorScreenTest.kt`, `PlaybackFrameLedger`, `.plan`, `EditorScreen.kt`, `BeatPlaybackService`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `WaveformMarker` (e.g. with `.frameMarkers_persistExactSamplesWithoutMillisecondRounding()` and `.frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames()`) actually correct?**
  _`WaveformMarker` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _1021 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Generate` be split into smaller, more focused modules?**
  _Cohesion score 0.07138047138047138 - nodes in this community are weakly interconnected._