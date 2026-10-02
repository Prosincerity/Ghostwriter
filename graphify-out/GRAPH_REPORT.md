# Graph Report - Gh0stwrit3r  (2026-10-02)

## Corpus Check
- 384 files · ~590,014 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2066 file(s) not represented in the graph (top: (none) 1892, .xml 83, .test 17)

## Summary
- 4796 nodes · 11742 edges · 238 communities (143 shown, 95 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 1481 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7e1904a4`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- android.content.Context
- DictionaryScreen
- synthesize.c
- ProjectStorage
- ProjectStorageTest
- WaveformViewTest
- edge-to-edge/SKILL.md
- Synthesize
- WaveformExtractor
- ImportVoicePreference.java
- VoiceSettingsTest
- script
- WaveformView.kt
- What You Must Do When Invoked
- PcmBeatDecoderTest
- InputStream
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
- .excludedWords
- EditorScreen
- dictionary.c
- compiledata.c
- SeekBarPreference
- PcmRingBuffer
- Recording Perfetto Traces on Android (Helper Scripts)
- tests/readclause.c
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- MainActivity.kt
- TestConnection
- eSpeakNGWorker
- Text to Phoneme Translation
- wavegen.c
- demo.js
- eSpeakActivity
- ucd_script_
- ttsengine.cpp
- BeatPlaybackService
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- Android Profiler Orchestrator
- espeak_Initialize
- Phoneme Features and the International Phonetic Alphabet
- VoiceVariantPreference.java
- espeak_api.c
- BeatPlaybackServiceTest
- DictionaryInstaller
- Phoneme Model
- eSpeakService.c
- GhostwriterTheme
- tr_languages.c
- Querying Perfetto traces
- Configuration Files
- FrameManagerImpl
- Synthesizing Perfetto Trace Configs (Mix & Match)
- LyricTextSettings
- Diacritics
- PlaybackFocusState
- Diacritics
- speechPlayer.cpp
- TtsService
- .excludedWords
- category
- Perfetto Trace Analysis
- index.md
- SSML (Speech Synthesis Markup Language)
- Android
- ucd_category_
- DictionaryArchiveSource
- speechWaveGenerator.cpp
- VoiceVariant
- klatt.c
- ucd_category_group_
- BeatPlaybackService.kt
- intonation.c
- Recording Orchestrator
- ucd.h
- Phoneme Instructions
- CodePoint
- AssonanceData
- fifo.c
- TtsService.java
- unpackDictionaryArchive
- printdata.py
- ucd.py
- SelectPhonemeTable
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- .setMarkers
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Analysis Orchestrator
- SystemFontCatalogTest.kt
- ssml.c
- AboutScreenTest
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
- SettingsScreen.kt
- Third-party software and dictionary data
- HomeScreenPreviews.kt
- perfetto_sql.md
- printucddata_cpp.cpp
- Vowels
- guiding_principles.md
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- numbers.md
- BeatLoopMode
- BeatPlayer
- DictionarySearch
- WaveformCache
- WaveformViewport
- Android CLI Specialist
- eSpeak NG user guide
- hints_cpu.md
- PcmLoopRenderer
- SmoothLoopPlayback
- hints_graphics.md
- main
- .parse
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- .configure
- EditorScreen.kt
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- hints_io.md
- Language Attributes
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- EspeakIpaInstrumentedTest
- hints_ipc.md
- Feature roadmap
- CheckVoiceDataTest
- EditorLyricsSession
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- hints_memory.md
- Contribution Guide
- ProjectSummaryTest
- sPlayer.c
- hints_power.md
- compiledict.c
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- GhostThemeContrastTest.kt
- MarkerLoopRole
- ServiceConnection
- .writeText
- Dictionaries
- Q: Why do waveform markers lag behind the pointer while dragging?
- Translation fuzzers
- Ghostwriter agent instructions
- Q: Does this app have haptic feedback for waveform markers, beat controls, and destructive actions?
- espeak_ng_PrintStatusCodeMessage
- voices.c
- Native pronunciation engine
- Testing
- SettingsFormatTest.kt
- ProjectInfoDialog
- ExampleInstrumentedTest.kt
- Third-party software and dictionary data
- Releasing
- Ghostwriter
- assertequals
- Build setup
- Q: How do three beat loop modes reach the editor, notification, and lock screen?
- Q: can you check which part of the UI is customizable and is currently in a default state? give me suggestions fitting for this app on how the UI should be changed to be prettier.
- Q: Why does justified lyric text overflow with tight letter spacing?
- Ghostwriter documentation
- Q: Why does seeking beyond the beat loop end snap the playhead back?

## God Nodes (most connected - your core abstractions)
1. `ucd_script_` - 186 edges
2. `script` - 186 edges
3. `WaveformMarker` - 87 edges
4. `BeatPlayer` - 82 edges
5. `GhostwriterTheme()` - 67 edges
6. `SpeechSynthesis` - 49 edges
7. `ProjectStorageTest` - 42 edges
8. `LyricTextSettings` - 38 edges
9. `ucd_properties()` - 38 edges
10. `ucd_category_` - 36 edges

## Surprising Connections (you probably didn't know these)
- `Projects and settings` --references--> `EditorLyricsSession`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/data/EditorLyricsSession.kt
- `Projects and settings` --references--> `LyricTextSettings`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/data/LyricTextSettings.kt
- `Playback and waveforms` --references--> `BeatPlayer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/BeatPlayer.kt
- `Playback and waveforms` --references--> `PcmRingBuffer`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/PcmRingBuffer.kt
- `Playback and waveforms` --references--> `SmoothLoopPlayback`  [INFERRED]
  documentation/README.md → app/src/main/java/com/prosincerity/ghostwriter/media/SmoothLoopPlayback.kt

## Import Cycles
- None detected.

## Communities (238 total, 95 thin omitted)

### Community 0 - "android.content.Context"
Cohesion: 0.07
Nodes (5): EspeakApp, ImportVoicePreference, SpeakPunctuationPreference, PrefsEspeakFragment, TtsSettingsActivity

### Community 1 - "DictionaryScreen"
Cohesion: 0.16
Nodes (11): DictionaryScreenTest, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryMatch, DictionarySearchResult (+3 more)

### Community 2 - "synthesize.c"
Cohesion: 0.06
Nodes (50): CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods(), SetSpeedMultiplier(), GetMbrName(), MbrolaGenerate() (+42 more)

### Community 5 - "WaveformViewTest"
Cohesion: 0.15
Nodes (4): WaveformViewTest, PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 6 - "edge-to-edge/SKILL.md"
Cohesion: 0.12
Nodes (16): Adaptive Scaffolds, Checklist, Dialogs, IME, IMEs with Scaffolds code patterns, IMEs without Scaffolds code patterns, Lists, Navigation Bar Contrast \& System Bar Icons (+8 more)

### Community 7 - "Synthesize"
Cohesion: 0.11
Nodes (28): create_espeak_char(), create_espeak_key(), create_espeak_mark(), create_espeak_punctuation_list(), create_espeak_terminated_msg(), create_espeak_text(), create_espeak_voice_name(), create_espeak_voice_spec() (+20 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.08
Nodes (6): WaveformExtractorInstrumentedTest, DecoderProgressGuard, WaveformExtractor, PlaybackHandoff, WaveformExtractorTest, PlaybackHandoffTest

### Community 9 - "ImportVoicePreference.java"
Cohesion: 0.10
Nodes (4): AsyncExtract, DownloadVoiceData, ExtractProgress, FileUtils

### Community 12 - "script"
Cohesion: 0.01
Nodes (184): script, Adlm, Afak, Aghb, Ahom, Arab, Armi, Armn (+176 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.04
Nodes (4): CenteredPlayerContent(), WaveformZoomButton(), displayLabel(), WaveformView()

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "PcmBeatDecoderTest"
Cohesion: 0.11
Nodes (3): PcmBeatDecoderTest, PcmBeatDecoder, PcmBeatDecoderTest

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

### Community 30 - ".excludedWords"
Cohesion: 0.06
Nodes (17): DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, DictionarySearchPlan, Prefix, AssonanceFormFilter (+9 more)

### Community 32 - "dictionary.c"
Cohesion: 0.06
Nodes (85): is_str_totally_null(), IsAlpha(), IsBracket(), IsDigit09(), IsSpace(), towlower2(), utf8_in(), utf8_in2() (+77 more)

### Community 33 - "compiledata.c"
Cohesion: 0.07
Nodes (65): Read4Bytes(), StringToWord(), CalculateSample(), CallPhoneme(), CheckNextChar(), clean_context(), CompileElif(), CompileElse() (+57 more)

### Community 34 - "SeekBarPreference"
Cohesion: 0.09
Nodes (6): SeekBarPreference, Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 35 - "PcmRingBuffer"
Cohesion: 0.06
Nodes (13): ObservedSource, SmoothLoopPlaybackTest, PcmSource, PcmBeat, Loop, Page, PcmRingBuffer, Region (+5 more)

### Community 36 - "Recording Perfetto Traces on Android (Helper Scripts)"
Cohesion: 0.20
Nodes (9): 0. Download the Helper Scripts, 1. Memory Tracing, 2. Stack Sampling / Callstack Profiling (traced_perf), 3. System Tracing (CPU, Scheduling, & ATrace), 4. Custom Configs (Mix & Match Data Sources), A. Java Heap Dump (ART), B. Native C/C++ Heap Profiling (heapprofd), C. System-wide Memory Counters (+1 more)

### Community 38 - "tests/readclause.c"
Cohesion: 0.14
Nodes (27): CheckPhonemeMode(), clause_type_from_codepoint(), IsRomanU(), ReadClause(), RemoveChar(), main(), set_text(), test_arabic() (+19 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.06
Nodes (13): BeatPlayerInstrumentedTest, ProjectMetadata, WaveformMarker, indexOfSelectedMarker(), MarkerLoopFrames, MarkerLoopRange, replaceLoopMarker(), crossesWaveformMarker() (+5 more)

### Community 40 - "SpeechSynthesis"
Cohesion: 0.06
Nodes (5): SpeechSynthesisTest, CheckVoiceData, GetSampleText, SpeechSynthesis, SynthReadyCallback

### Community 41 - "speech.c"
Cohesion: 0.09
Nodes (4): ieee_extended_to_double(), espeak_callback(), main(), SynthCallback()

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (47): create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc(), string_decoder_getc_auto(), string_decoder_getc_codepage(), string_decoder_getc_iso_10646_ucs_2(), string_decoder_getc_us_ascii() (+39 more)

### Community 43 - "MainActivity.kt"
Cohesion: 0.08
Nodes (18): HomeScreenTest, ProjectSummary, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home (+10 more)

### Community 44 - "TestConnection"
Cohesion: 0.12
Nodes (7): openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, TestConnection

### Community 45 - "eSpeakNGWorker"
Cohesion: 0.10
Nodes (12): eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, PrintVersion(), event_set_callback() (+4 more)

### Community 46 - "Text to Phoneme Translation"
Cohesion: 0.10
Nodes (21): Character Substitution, Conditional Rules, Flags, Letter groups, Letter names, Multiple Words, Numbers, Numbers and Character Names (+13 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (37): espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), KlattFini(), MarkerEvent(), MbrolaFill(), MbrolaReset(), WritePitch() (+29 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (7): eSpeakActivity, EspeakHandler, State, DOWNLOAD_FAILED, ERROR, LOADING, SUCCESS

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

### Community 56 - "Android Profiler Orchestrator"
Cohesion: 0.22
Nodes (7): Environment setup, Set `$SKILL_ROOT`, Analysis, Android Profiler Orchestrator, Intent Disambiguation, Prerequisites and Setup, Recording

### Community 57 - "espeak_Initialize"
Cohesion: 0.19
Nodes (23): espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), main(), test_espeak_initialize(), _test_espeak_ng_phoneme_events_cb(), test_espeak_ng_synthesize() (+15 more)

### Community 58 - "Phoneme Features and the International Phonetic Alphabet"
Cohesion: 0.10
Nodes (20): Backness, Consonants, Gemination, Height, Intonation, Length, Manner of Articulation, Other Symbols (+12 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.11
Nodes (10): FileListAdapter, ViewHolder, InformationListAdapter, ViewHolder, VariantData, VariantDataListAdapter, ViewHolder, VoiceVariantPreference (+2 more)

### Community 60 - "espeak_api.c"
Cohesion: 0.19
Nodes (9): espeak_Char(), espeak_CompileDictionary(), espeak_Key(), espeak_SetParameter(), espeak_SetPunctuationList(), espeak_SetVoiceByFile(), espeak_Synth_Mark(), status_to_espeak_error() (+1 more)

### Community 62 - "DictionaryInstaller"
Cohesion: 0.15
Nodes (7): DictionaryInstaller, DictionarySource, ESPEAK, WIKTIONARY, DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (16): getJniEnv(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeClassInit(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeCreate(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetAvailableVoices(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetVersion(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetPunctuationCharacters() (+8 more)

### Community 65 - "GhostwriterTheme"
Cohesion: 0.22
Nodes (4): BeatComponentsTest, BeatPlaybackControls(), BeatPlayerPanel(), GhostwriterTheme()

### Community 66 - "tr_languages.c"
Cohesion: 0.33
Nodes (15): (Re)definition of character groups, SetLengthMods(), NewTranslator(), ProcessLanguageOptions(), ResetLetterBits(), SelectTranslator(), SetArabicLetters(), SetCyrillicLetters() (+7 more)

### Community 67 - "Querying Perfetto traces"
Cohesion: 0.22
Nodes (8): Analytical Workflow (Standard Operating Procedure), Common Analysis Patterns, Discovering what's in the trace, Querying a trace: sessions, Querying Perfetto traces, Tips for writing good PerfettoSQL, Using the standard library, Where to look for more

### Community 68 - "Configuration Files"
Cohesion: 0.12
Nodes (16): Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation, Dictionary Files (+8 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (15): FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex, newFrameRequest, oldFrameRequest, sampleCounter (+7 more)

### Community 70 - "Synthesizing Perfetto Trace Configs (Mix & Match)"
Cohesion: 0.29
Nodes (6): Data sources at a glance, Exemplar configs, Pitfalls, Shape of a config, Synthesizing Perfetto Trace Configs (Mix & Match), Top-level knobs

### Community 71 - "LyricTextSettings"
Cohesion: 0.05
Nodes (14): SystemFontCatalogTest, LyricsNotepadTest, SettingsScreenTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), LyricTextSettings (+6 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "Diacritics"
Cohesion: 0.20
Nodes (10): Air Flow, Articulation, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Phonation, Rounding and Labialization (+2 more)

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

### Community 85 - "DictionaryArchiveSource"
Cohesion: 0.13
Nodes (8): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, dictionaryArchive(), withDictionaryTestContext(), ContextWrapper, DictionaryDownloadsTest, DictionaryArchiveSource, IpaGenerator

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (10): FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue, VoiceGenerator, aspirationGen, glottisOpen (+2 more)

### Community 88 - "klatt.c"
Cohesion: 0.20
Nodes (17): antiresonator(), DBtoLIN(), flutter(), frame_init(), gen_noise(), impulsive_source(), KlattInit(), KlattReset() (+9 more)

### Community 89 - "ucd_category_group_"
Cohesion: 0.10
Nodes (22): ucd_get_category_group_for_category(), ucd_lookup_category_group(), category_group, C, I, M, N, P (+14 more)

### Community 91 - "intonation.c"
Cohesion: 0.23
Nodes (13): calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments(), count_pitch_vowels(), CountUnstressed() (+5 more)

### Community 92 - "Recording Orchestrator"
Cohesion: 0.40
Nodes (4): Handling Composite Requests, Recording Orchestrator, Workflow Discovery and Routing, Workflow Selection

### Community 93 - "ucd.h"
Cohesion: 0.14
Nodes (30): ucd_lookup_category(), ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph(), ucd_islower() (+22 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 97 - "fifo.c"
Cohesion: 0.08
Nodes (34): gcd(), getopt(), getopt_internal(), getopt_long(), permute_args(), add_time_in_ms(), clock_gettime2(), event_clear_all() (+26 more)

### Community 99 - "unpackDictionaryArchive"
Cohesion: 0.24
Nodes (5): unpackDictionaryArchive(), FilterInputStream, DictionaryDownloadProgress, DictionaryArchiveTest, TrackedInput

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 102 - "SelectPhonemeTable"
Cohesion: 0.09
Nodes (21): MakePhonemeList(), ReInterpretPhoneme(), SetRegressiveVoicing(), SubstitutePhonemes(), AnnouncePunctuation(), DecodeWithPhonemeMode(), Eof(), GetC() (+13 more)

### Community 103 - "Tune Definitions"
Cohesion: 0.14
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

### Community 112 - "SystemFontCatalogTest.kt"
Cohesion: 0.09
Nodes (15): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+7 more)

### Community 114 - "ssml.c"
Cohesion: 0.19
Nodes (19): LookupEnvelopeName(), LookupMnem(), AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber() (+11 more)

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
Cohesion: 0.17
Nodes (11): 1. Add MBROLA voice definition file, 2. Add MBROLA phoneme translation file, 3. Compile voice and update Makefile.am file, Adding new MBROLA voice entry to eSpeak NG, Installation of MBROLA package from source, Installation of standard packages, Linux Installation, MBROLA Voices (+3 more)

### Community 124 - "Kirshenbaum (ASCII-IPA) Transcription Scheme"
Cohesion: 0.18
Nodes (11): Consonants, Diacritics, Kirshenbaum (ASCII-IPA) Transcription Scheme, Length, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+3 more)

### Community 125 - "CascadeFormantGenerator"
Cohesion: 0.18
Nodes (10): CascadeFormantGenerator, r1, r2, r3, r4, r5, r6, rN0 (+2 more)

### Community 127 - "SettingsScreen.kt"
Cohesion: 0.05
Nodes (9): SlimSlider(), AboutLink(), AboutScreen(), AboutSectionTitle(), SettingsDropdownRow(), SettingsNavigationRow(), SettingsScreen(), SettingsSection() (+1 more)

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

### Community 137 - "BeatLoopMode"
Cohesion: 0.28
Nodes (4): BeatLoopMode, MARKERS, OFF, WHOLE_BEAT

### Community 139 - "DictionarySearch"
Cohesion: 0.32
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 142 - "Android CLI Specialist"
Cohesion: 0.07
Nodes (26): Android Interaction Rules, Annotated Screenshot, Input, Screenshot, Text Input, Tools, UI Dump, Handling failure (+18 more)

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 146 - "PcmLoopRenderer"
Cohesion: 0.09
Nodes (11): FramePositionEvents, PcmLoopBounds, PcmLoopRenderer, PlaybackFrameLedger, PcmLoopRendererTest, UnavailableSource, PlaybackFrameLedgerTest, Architecture (+3 more)

### Community 149 - "main"
Cohesion: 0.17
Nodes (11): main(), create_espeak_parameter(), SetParameter(), espeak_ng_GetSampleRate(), espeak_ng_Initialize(), espeak_ng_InitializeOutput(), espeak_ng_InitializePath(), espeak_ng_SetParameter() (+3 more)

### Community 150 - ".parse"
Cohesion: 0.22
Nodes (4): DictionaryAsset, DictionaryLanguage, DictionaryRelease, DictionaryReleaseTest

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 156 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 157 - "EditorScreen.kt"
Cohesion: 0.05
Nodes (8): rememberBeatPlayback(), ServiceConnection, LongBeatWarningDialog(), ReassignBeatDialog(), displayNameFor(), PendingBeatPreparation, shouldWarnBeforeWaveformExtraction(), WaveformWarningTest

### Community 171 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 177 - "Feature roadmap"
Cohesion: 0.18
Nodes (11): About and attribution, Beats and playback, Dictionaries, Feature roadmap, Guiding rules, Implementation guidance, Import, export, and optional sync, Lyric editor (+3 more)

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 189 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 192 - "sPlayer.c"
Cohesion: 0.25
Nodes (9): fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP(), KlattInitSP(), KlattResetSP(), MIN(), mixWaveFile(), needsMixWaveFile() (+1 more)

### Community 194 - "compiledict.c"
Cohesion: 0.13
Nodes (18): IsDigit(), isspace2(), clean_context(), compile_dictlist_end(), compile_dictlist_file(), compile_dictlist_start(), compile_dictrules(), compile_lettergroup() (+10 more)

### Community 195 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (3): copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize()

### Community 198 - "MarkerLoopRole"
Cohesion: 0.22
Nodes (5): EditorMarkerDialogsTest, MarkerLoopRole, END, NONE, START

### Community 200 - "ServiceConnection"
Cohesion: 0.15
Nodes (4): ContextWrapper, ContextWrapper, ContextWrapper, ServiceConnection

### Community 203 - "Dictionaries"
Cohesion: 0.33
Nodes (6): Dictionaries, Pronunciation lookup, Release inputs and installation, Responsiveness and verification, Search behavior, Using dictionaries

### Community 204 - "Q: Why do waveform markers lag behind the pointer while dragging?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Why do waveform markers lag behind the pointer while dragging?, Source Nodes

### Community 205 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 206 - "Ghostwriter agent instructions"
Cohesion: 0.40
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 207 - "Q: Does this app have haptic feedback for waveform markers, beat controls, and destructive actions?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does this app have haptic feedback for waveform markers, beat controls, and destructive actions?, Source Nodes

### Community 208 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.39
Nodes (4): create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage()

### Community 209 - "voices.c"
Cohesion: 0.07
Nodes (41): CloseWavFile(), DisplayVoices(), OpenWavFile(), SynthCallback(), Write4Bytes(), GetFileLength(), strncpy0(), CheckTranslator() (+33 more)

### Community 216 - "Native pronunciation engine"
Cohesion: 0.40
Nodes (5): Native pronunciation engine, Regenerating language data, Runtime contract, Source and Android build, Verification and distribution

### Community 217 - "Testing"
Cohesion: 0.40
Nodes (5): Android tests, Checks without a device, Coverage and regression guidance, Coverage reports, Testing

### Community 219 - "SettingsFormatTest.kt"
Cohesion: 0.30
Nodes (4): formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 220 - "ProjectInfoDialog"
Cohesion: 0.19
Nodes (5): ProjectInfoDialogTest, parsePositiveBpm(), ProjectInfoDialog(), trimmedOrNull(), ProjectInfoBpmTest

### Community 222 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 223 - "Releasing"
Cohesion: 0.50
Nodes (4): Build and verify, Maintainer runtime checks, Package matching source, Releasing

### Community 225 - "Ghostwriter"
Cohesion: 0.50
Nodes (4): Build and test, Documentation and contributions, Ghostwriter, License

### Community 229 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Checkout and build, Requirements

### Community 234 - "Q: How do three beat loop modes reach the editor, notification, and lock screen?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How do three beat loop modes reach the editor, notification, and lock screen?, Source Nodes

### Community 237 - "Q: can you check which part of the UI is customizable and is currently in a default state? give me suggestions fitting for this app on how the UI should be changed to be prettier."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: can you check which part of the UI is customizable and is currently in a default state? give me suggestions fitting for this app on how the UI should be changed to be prettier., Source Nodes

### Community 240 - "Q: Why does justified lyric text overflow with tight letter spacing?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Why does justified lyric text overflow with tight letter spacing?, Source Nodes

### Community 254 - "Q: Why does seeking beyond the beat loop end snap the playhead back?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Why does seeking beyond the beat loop end snap the playhead back?, Source Nodes

## Knowledge Gaps
- **1030 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+1025 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1584 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **95 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (4× useful, score=3.858770915)
- `BeatPlayer` (3× useful, score=2.952332556)
- `BeatPlaybackControls.kt` (2× useful, score=1.962841508)
- `WaveformView.kt` (2× useful, score=1.962841508)
- `DictionarySearch` (2× useful, score=1.883710141)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ucd_script_` connect `ucd_script_` to `uprintf`, `ucd.h`, `uprintf`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `GhostwriterTheme`, `PcmRingBuffer`, `WaveformMarker`, `WaveformExtractor`, `BeatLoopMode`, `.setMarkers`, `WaveformView.kt`, `PcmLoopRenderer`, `SmoothLoopPlayback`, `BeatPlaybackService`, `EditorScreenTest.kt`, `EditorScreen.kt`?**
  _High betweenness centrality (0.065) - this node is a cross-community bridge._
- **Why does `script` connect `script` to `ucd.h`, `uprintf`?**
  _High betweenness centrality (0.054) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `WaveformMarker` (e.g. with `.frameMarkers_persistExactSamplesWithoutMillisecondRounding()` and `.frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames()`) actually correct?**
  _`WaveformMarker` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsToFullVolume()`) actually correct?**
  _`BeatPlayer` has 25 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _1030 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `android.content.Context` be split into smaller, more focused modules?**
  _Cohesion score 0.07138047138047138 - nodes in this community are weakly interconnected._