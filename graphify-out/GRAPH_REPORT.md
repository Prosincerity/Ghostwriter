# Graph Report - Gh0stwrit3r  (2026-10-01)

## Corpus Check
- 366 files · ~579,373 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2065 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 4562 nodes · 11034 edges · 237 communities (140 shown, 97 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 1438 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `93105ce3`
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
- assertequals
- WaveformExtractor
- android.content.Context
- VoiceSettingsTest
- script
- WaveformView.kt
- What You Must Do When Invoked
- PcmBeatDecoderTest
- event.c
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
- EditorScreenTest
- TranslateWord3
- compiledata.c
- SeekBarPreference
- PcmRingBuffer
- Recording Perfetto Traces on Android (Helper Scripts)
- tests/readclause.c
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- Synthesize
- TestConnection
- EditorScreen
- tr_languages.c
- wavegen.c
- demo.js
- eSpeakActivity
- ucd_script_
- TtsEngine
- LyricTextStyleTest.kt
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- Android Profiler Orchestrator
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- TranslateClauseWithTerminator
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
- SystemFontFile
- Diacritics
- spect.c
- speechPlayer.cpp
- TtsService
- .excludedWords
- category
- Perfetto Trace Analysis
- index.md
- SSML (Speech Synthesis Markup Language)
- DictionarySearch
- ucd_category_
- withDictionaryTestContext
- speechWaveGenerator.cpp
- VoiceVariant
- klatt.c
- ucd_category_group_
- BeatPlaybackServiceTest
- intonation.c
- Recording Orchestrator
- ucd.h
- Phoneme Instructions
- CodePoint
- .createSeekBarPreference
- SettingsScreenTest.kt
- TtsService.java
- DictionaryArchiveSource
- printdata.py
- ucd.py
- CheckVoiceDataTest
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- BeatPlaybackService
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Analysis Orchestrator
- ProjectInfoBpmTest.kt
- espeak_api.c
- EditorMarkerDialogsTest.kt
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
- espeak_ng_SetVoiceByName
- Third-party software and dictionary data
- BeatPlaybackService.kt
- perfetto_sql.md
- printucddata_cpp.cpp
- Vowels
- guiding_principles.md
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- dictionary.c
- ImportVoicePreference.java
- ssml.c
- .fromIpa
- WaveformCache
- DictionaryHeadword
- Android CLI Specialist
- eSpeak NG user guide
- hints_cpu.md
- PcmLoopRenderer
- .excludedWords
- hints_graphics.md
- Dictionaries
- numbers.md
- Voice Attributes
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
- BeatPlaybackServiceTest.kt
- SmoothLoopPlayback
- Third-party software and dictionary data
- hints_ipc.md
- Feature roadmap
- GhostwriterTheme
- IpaSearchKeys
- PlaybackFrameLedger
- Releasing
- Ghostwriter
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- LyricTextSettings
- hints_memory.md
- .parse
- Phoneme Tables
- sPlayer.c
- hints_power.md
- synthdata.c
- BeatPlayerPanel
- main
- Language Attributes
- MainActivity.kt
- .writeText
- AnnouncePunctuation
- PlaybackNotificationUpdater
- ExampleInstrumentedTest.kt
- DictionarySearchData
- Build setup
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- espeak-ng.c
- ContextWrapper
- EspeakIpa
- .plan
- Settings
- SettingsScreen
- LoadVoice
- AssonanceData
- Contribution Guide
- AboutScreenTest.kt
- category_group
- Translation fuzzers
- WaveformWarningTest.kt
- ProjectStorage
- .cancelDownloadRestoresControlsWithoutInstalling
- rgroup_sorter
- FramePositionEvents
- SynthCallback
- Playback and waveforms

## God Nodes (most connected - your core abstractions)
1. `ucd_script_` - 186 edges
2. `script` - 186 edges
3. `WaveformMarker` - 68 edges
4. `BeatPlayer` - 62 edges
5. `GhostwriterTheme()` - 56 edges
6. `SpeechSynthesis` - 49 edges
7. `ProjectStorageTest` - 42 edges
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

## Communities (237 total, 97 thin omitted)

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "synthesize.c"
Cohesion: 0.10
Nodes (35): GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), AdjustFormants(), AllocFrame(), CopyFrame(), DoAmplitude(), DoEmbedded() (+27 more)

### Community 6 - "edge-to-edge/SKILL.md"
Cohesion: 0.12
Nodes (16): Adaptive Scaffolds, Checklist, Dialogs, IME, IMEs with Scaffolds code patterns, IMEs without Scaffolds code patterns, Lists, Navigation Bar Contrast \& System Bar Icons (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (4): WaveformExtractorInstrumentedTest, DecoderProgressGuard, WaveformExtractor, WaveformExtractorTest

### Community 9 - "android.content.Context"
Cohesion: 0.11
Nodes (4): EspeakApp, ImportVoicePreference, PrefsEspeakFragment, TtsSettingsActivity

### Community 12 - "script"
Cohesion: 0.01
Nodes (184): script, Adlm, Afak, Aghb, Ahom, Arab, Armi, Armn (+176 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "PcmBeatDecoderTest"
Cohesion: 0.11
Nodes (3): PcmBeatDecoderTest, PcmBeatDecoder, PcmBeatDecoderTest

### Community 19 - "event.c"
Cohesion: 0.12
Nodes (22): add_time_in_ms(), clock_gettime2(), event_clear_all(), event_copy(), event_declare(), event_delete(), event_init(), event_notify() (+14 more)

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

### Community 32 - "TranslateWord3"
Cohesion: 0.11
Nodes (40): IsAlpha(), IsBracket(), IsDigit09(), AppendPhonemes(), GetVowelStress(), Lookup(), SetWordStress(), TranslateRules() (+32 more)

### Community 33 - "compiledata.c"
Cohesion: 0.06
Nodes (75): isspace2(), Read4Bytes(), StringToWord(), strncpy0(), CalculateSample(), CallPhoneme(), CheckNextChar(), clean_context() (+67 more)

### Community 35 - "PcmRingBuffer"
Cohesion: 0.06
Nodes (13): ObservedSource, SmoothLoopPlaybackTest, PcmSource, PcmBeat, Loop, Page, PcmRingBuffer, Region (+5 more)

### Community 36 - "Recording Perfetto Traces on Android (Helper Scripts)"
Cohesion: 0.20
Nodes (9): 0. Download the Helper Scripts, 1. Memory Tracing, 2. Stack Sampling / Callstack Profiling (traced_perf), 3. System Tracing (CPU, Scheduling, & ATrace), 4. Custom Configs (Mix & Match Data Sources), A. Java Heap Dump (ART), B. Native C/C++ Heap Profiling (heapprofd), C. System-wide Memory Counters (+1 more)

### Community 38 - "tests/readclause.c"
Cohesion: 0.15
Nodes (26): CheckPhonemeMode(), clause_type_from_codepoint(), IsRomanU(), ReadClause(), main(), set_text(), test_arabic(), test_armenian() (+18 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.07
Nodes (10): BeatPlayerInstrumentedTest, ProjectMetadata, WaveformMarker, indexOfSelectedMarker(), MarkerLoopFrames, MarkerLoopRange, replaceLoopMarker(), EditorMarkerDialogs() (+2 more)

### Community 40 - "SpeechSynthesis"
Cohesion: 0.08
Nodes (3): SpeechSynthesisTest, SpeechSynthesis, SynthReadyCallback

### Community 42 - "tests/encoding.c"
Cohesion: 0.20
Nodes (45): create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc(), string_decoder_getc_auto(), string_decoder_getc_codepage(), string_decoder_getc_iso_10646_ucs_2(), string_decoder_getc_us_ascii() (+37 more)

### Community 43 - "Synthesize"
Cohesion: 0.08
Nodes (35): create_espeak_char(), create_espeak_key(), create_espeak_mark(), create_espeak_parameter(), create_espeak_punctuation_list(), create_espeak_terminated_msg(), create_espeak_text(), create_espeak_voice_name() (+27 more)

### Community 44 - "TestConnection"
Cohesion: 0.12
Nodes (7): openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, TestConnection

### Community 45 - "EditorScreen"
Cohesion: 0.13
Nodes (7): rememberBeatPlayback(), ServiceConnection, displayNameFor(), EditorScreen(), PendingBeatPreparation, ProjectInfoDialog(), trimmedOrNull()

### Community 46 - "tr_languages.c"
Cohesion: 0.06
Nodes (52): Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation, Dictionary Files (+44 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (35): espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), MarkerEvent(), MbrolaFill(), MbrolaReset(), WritePitch(), AdvanceParameters() (+27 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (8): eSpeakActivity, EspeakHandler, State, DOWNLOAD_FAILED, ERROR, LOADING, SUCCESS, GetSampleText

### Community 50 - "ucd_script_"
Cohesion: 0.01
Nodes (184): ucd_script_, UCD_SCRIPT_Adlm, UCD_SCRIPT_Afak, UCD_SCRIPT_Aghb, UCD_SCRIPT_Ahom, UCD_SCRIPT_Arab, UCD_SCRIPT_Armi, UCD_SCRIPT_Armn (+176 more)

### Community 51 - "TtsEngine"
Cohesion: 0.09
Nodes (17): espeak_callback(), espeak_status_to_hresult(), TtsEngine, AddRef, TtsEngine_CreateInstance(), GetObjectToken, GetOutputFormat, GetStringValue (+9 more)

### Community 52 - "LyricTextStyleTest.kt"
Cohesion: 0.10
Nodes (15): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+7 more)

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
Cohesion: 0.22
Nodes (22): espeak_Initialize(), espeak_SetVoiceByName(), espeak_Synth(), main(), test_espeak_initialize(), _test_espeak_ng_phoneme_events_cb(), test_espeak_ng_synthesize(), test_espeak_ng_synthesize_no_voices() (+14 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.13
Nodes (9): FileListAdapter, ViewHolder, InformationListAdapter, ViewHolder, VariantDataListAdapter, ViewHolder, VoiceVariantPreference, ResourceIdListAdapter (+1 more)

### Community 60 - "TranslateClauseWithTerminator"
Cohesion: 0.17
Nodes (16): is_str_totally_null(), IsSpace(), towlower2(), CalcWordLength(), CombineFlag(), CountSyllables(), DeleteTranslator(), FindReplacementChars() (+8 more)

### Community 61 - "WaveformViewTest.kt"
Cohesion: 0.14
Nodes (5): WaveformViewTest, MarkerLoopRole, END, NONE, START

### Community 62 - "SelectPhonemeTable"
Cohesion: 0.20
Nodes (9): MakePhonemeList(), ReInterpretPhoneme(), SetRegressiveVoicing(), SubstitutePhonemes(), LookupPhonemeTable(), SelectPhonemeTable(), SelectPhonemeTableName(), SetUpPhonemeTable() (+1 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (16): getJniEnv(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeClassInit(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeCreate(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetAvailableVoices(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetVersion(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetPunctuationCharacters() (+8 more)

### Community 66 - "DictionaryInstaller.kt"
Cohesion: 0.16
Nodes (7): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY

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
Cohesion: 0.13
Nodes (7): SystemFontCatalogTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), SystemFontCatalog, LyricFontTest

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "spect.c"
Cohesion: 0.14
Nodes (11): ieee_extended_to_double(), GetFrameLength(), GetFrameRms(), LoadFrame(), LoadSpectSeq(), read_double(), SpectFrameCreate(), SpectFrameDestroy() (+3 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.15
Nodes (10): create, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize(), SpeechWaveGenerator, create, setFrameManager (+2 more)

### Community 76 - "TtsService"
Cohesion: 0.11
Nodes (4): TextToSpeechServiceTest, TtsServiceTest, TtsService, Voice

### Community 77 - ".excludedWords"
Cohesion: 0.29
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 78 - "category"
Cohesion: 0.06
Nodes (33): category, Cc, Cf, Cn, Co, Cs, Ii, Ll (+25 more)

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
Nodes (34): get_category_string(), ucd_category_, UCD_CATEGORY_Cc, UCD_CATEGORY_Cf, UCD_CATEGORY_Cn, UCD_CATEGORY_Co, UCD_CATEGORY_Cs, UCD_CATEGORY_Ii (+26 more)

### Community 85 - "withDictionaryTestContext"
Cohesion: 0.16
Nodes (5): DictionaryInstallerInstrumentedTest, dictionaryArchive(), withDictionaryTestContext(), EspeakIpaInstrumentedTest, IpaGenerator

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (10): FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue, VoiceGenerator, aspirationGen, glottisOpen (+2 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.18
Nodes (3): VoiceVariantTest, VariantData, VoiceVariant

### Community 88 - "klatt.c"
Cohesion: 0.19
Nodes (18): antiresonator(), DBtoLIN(), flutter(), frame_init(), gen_noise(), impulsive_source(), KlattInit(), KlattReset() (+10 more)

### Community 89 - "ucd_category_group_"
Cohesion: 0.22
Nodes (9): ucd_category_group_, UCD_CATEGORY_GROUP_C, UCD_CATEGORY_GROUP_I, UCD_CATEGORY_GROUP_L, UCD_CATEGORY_GROUP_M, UCD_CATEGORY_GROUP_N, UCD_CATEGORY_GROUP_P, UCD_CATEGORY_GROUP_S (+1 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (11): calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments(), count_pitch_vowels(), CountUnstressed() (+3 more)

### Community 92 - "Recording Orchestrator"
Cohesion: 0.40
Nodes (4): Handling Composite Requests, Recording Orchestrator, Workflow Discovery and Routing, Workflow Selection

### Community 93 - "ucd.h"
Cohesion: 0.10
Nodes (34): ReadNumbers(), ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl() (+26 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 99 - "DictionaryArchiveSource"
Cohesion: 0.20
Nodes (6): unpackDictionaryArchive(), FilterInputStream, DictionaryArchiveSource, DictionaryDownloadProgress, DictionaryArchiveTest, TrackedInput

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 103 - "Tune Definitions"
Cohesion: 0.15
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
Cohesion: 0.37
Nodes (9): 10.0.0 - 2017-06-25, fget_utf8c(), fput_utf8c(), iswblank(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 111 - "Analysis Orchestrator"
Cohesion: 0.50
Nodes (3): Analysis Orchestrator, Handling Composite Requests, Workflow Discovery and Routing

### Community 114 - "espeak_api.c"
Cohesion: 0.18
Nodes (9): espeak_Char(), espeak_Key(), espeak_SetParameter(), espeak_SetPunctuationList(), espeak_SetVoiceByFile(), espeak_SetVoiceByProperties(), espeak_Synth_Mark(), status_to_espeak_error() (+1 more)

### Community 116 - "uprintf"
Cohesion: 0.56
Nodes (7): fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint(), uprintf_is()

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.09
Nodes (18): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+10 more)

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

### Community 126 - "espeak_ng_SetVoiceByName"
Cohesion: 0.13
Nodes (18): eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, SetVoiceStack(), espeak_GetCurrentVoice() (+10 more)

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

### Community 135 - "dictionary.c"
Cohesion: 0.14
Nodes (26): IsDigit(), utf8_in(), utf8_in2(), utf8_out(), compile_rule(), DollarRule(), EncodePhonemes(), GetTranslatedPhonemeString() (+18 more)

### Community 137 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (5): CheckVoiceData, AsyncExtract, DownloadVoiceData, ExtractProgress, FileUtils

### Community 138 - "ssml.c"
Cohesion: 0.20
Nodes (18): LookupMnem(), AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber(), espeak_SetUriCallback() (+10 more)

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

### Community 152 - "Voice Attributes"
Cohesion: 0.09
Nodes (22): breath, breathw, consonants, echo, flutter, formant, freq\_add, gender (+14 more)

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
Cohesion: 0.08
Nodes (17): DictionaryScreenTest, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryMatch, DictionarySearchMode (+9 more)

### Community 179 - "IpaSearchKeys"
Cohesion: 0.23
Nodes (4): DictionarySearchPlan, Prefix, IpaSearchKeys, Keys

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

### Community 187 - "LyricTextSettings"
Cohesion: 0.25
Nodes (3): LyricTextSettings, LyricTextSettingsTest, LyricTextStyleTest

### Community 190 - "Phoneme Tables"
Cohesion: 0.12
Nodes (16): Attributes, Conditional Statements, Conditions, Customization of sound source files, endtype, lengthmod, Phoneme Definitions, Phoneme Files (+8 more)

### Community 192 - "sPlayer.c"
Cohesion: 0.19
Nodes (12): KlattFini(), fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP(), KlattInitSP(), KlattResetSP(), MIN(), mixWaveFile() (+4 more)

### Community 194 - "synthdata.c"
Cohesion: 0.15
Nodes (15): CalcLengths(), DoEmbedded2(), CountVowelPosition(), GetEnvelope(), InterpretCondition(), InterpretPhoneme(), InterpretPhoneme2(), InvalidInstn() (+7 more)

### Community 195 - "BeatPlayerPanel"
Cohesion: 0.12
Nodes (8): BeatComponentsTest, LongBeatWarningDialog(), ReassignBeatDialog(), WaveformMarkerDialog(), BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), WaveformZoomButton()

### Community 196 - "main"
Cohesion: 0.10
Nodes (19): DisplayVoices(), main(), PrintVersion(), event_set_callback(), fifo_init(), fifo_is_busy(), espeak_Info(), espeak_IsPlaying() (+11 more)

### Community 198 - "Language Attributes"
Cohesion: 0.12
Nodes (16): brackets, bracketsAnnounced, dictionary, dictmin, dictrules, intonation, Language Attributes, lowercaseSentence (+8 more)

### Community 200 - "MainActivity.kt"
Cohesion: 0.08
Nodes (20): About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, MainActivity, openExternalLink() (+12 more)

### Community 202 - "AnnouncePunctuation"
Cohesion: 0.20
Nodes (13): DecodePhonemes(), AnnouncePunctuation(), DecodeWithPhonemeMode(), Eof(), GetC(), IgnoreOrReplaceChar(), LookupCharName(), LookupSpecial() (+5 more)

### Community 203 - "PlaybackNotificationUpdater"
Cohesion: 0.30
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 205 - "DictionarySearchData"
Cohesion: 0.22
Nodes (3): DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 206 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Checkout and build, Requirements

### Community 207 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (3): copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize()

### Community 208 - "espeak-ng.c"
Cohesion: 0.19
Nodes (9): gcd(), getopt(), getopt_internal(), getopt_long(), permute_args(), CloseWavFile(), OpenWavFile(), SynthCallback() (+1 more)

### Community 219 - "SettingsScreen"
Cohesion: 0.18
Nodes (7): formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsDropdownRow(), SettingsScreen(), SettingsSliderRow(), SettingsFormatTest

### Community 220 - "LoadVoice"
Cohesion: 0.06
Nodes (31): GetFileLength(), DecodeRule(), print_dictionary_flags(), CheckTranslator(), LoadConfig(), LoadLanguageOptions(), LookupTune(), LookupMnemName() (+23 more)

### Community 222 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 225 - "category_group"
Cohesion: 0.25
Nodes (8): category_group, C, I, M, N, P, S, Z

### Community 226 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 229 - ".cancelDownloadRestoresControlsWithoutInstalling"
Cohesion: 0.19
Nodes (3): ByteArrayInputStream, DictionaryDownloadsTest, InputStream

### Community 233 - "Playback and waveforms"
Cohesion: 0.33
Nodes (6): Architecture, Dictionaries, Ghostwriter documentation, Known limitations, Playback and waveforms, Projects and settings

## Knowledge Gaps
- **1010 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+1005 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1533 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **97 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (3× useful, score=2.922057951)
- `DictionarySearch` (2× useful, score=1.92387683)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `script` connect `script` to `ucd.h`, `Change Log`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **Why does `ucd_script_` connect `ucd_script_` to `ucd.h`, `Change Log`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **Why does `WaveformMarker` connect `WaveformMarker` to `BeatPlayer`, `BeatPlayerPanel`, `ProjectStorageBeatTest`, `file`, `BeatPlaybackServiceTest.kt`, `WaveformView.kt`, `EditorMarkerDialogsTest.kt`, `EditorScreenTest.kt`, `BeatPlaybackServiceTest`, `EditorScreen.kt`, `WaveformViewTest.kt`, `EditorScreenTest`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `WaveformMarker` (e.g. with `.frameMarkers_persistExactSamplesWithoutMillisecondRounding()` and `.frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames()`) actually correct?**
  _`WaveformMarker` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _1010 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.09523809523809523 - nodes in this community are weakly interconnected._