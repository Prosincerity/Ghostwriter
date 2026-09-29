# Graph Report - Gh0stwrit3r  (2026-09-29)

## Corpus Check
- 297 files · ~543,170 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2055 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3278 nodes · 8246 edges · 180 communities (137 shown, 43 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1229 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `92c7e67c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- Android
- Generate
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- DictionaryScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- android.content.Context
- ProjectStorageBeatTest
- gradlew
- Language Attributes
- WaveformView.kt
- What You Must Do When Invoked
- Steps
- Settings
- Lookup
- graphify reference: extra exports and benchmark
- Ghostwriter
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- InterpretPhoneme
- EditorScreenTest
- SpeechSynthesisTest
- compiledata.c
- IpaSearchKeys
- Ghostwriter agent instructions
- ImportVoicePreference.java
- eSpeakNGWorker
- utf8_in
- SettingsFormatTest.kt
- VoiceSettingsTest
- espeak_ng
- tests/encoding.c
- speech.c
- MainActivity.kt
- espeak_ng_Initialize
- event.c
- wavegen.c
- demo.js
- eSpeakActivity
- Translator
- ttsengine.cpp
- DictionarySearch.kt
- ucd_properties
- mbrowrap.c
- DictionaryDownloadsTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- android.view.View
- spect.c
- ucd.h
- main
- Phoneme Model
- eSpeakService.c
- EditorScreenTest.kt
- status_to_espeak_error
- EditorScreen
- SpeechSynthesis
- FrameManagerImpl
- klatt.c
- SelectPhonemeTable
- Diacritics
- .collectRows
- uprintf
- speechPlayer.cpp
- TranslateWord3
- espeak_ng_CompileDictionary
- TtsService
- tr_languages.c
- index.md
- SSML (Speech Synthesis Markup Language)
- BeatPlaybackService.kt
- EditorScreen.kt
- numbers.md
- .excludedWords
- speechWaveGenerator.cpp
- VoiceVariant
- DoSpect2
- AnnouncePunctuation
- GhostwriterTheme
- intonation.c
- Building
- Diacritics
- Phoneme Instructions
- CodePoint
- synth_fuzzer.c
- WaveformWarningTest.kt
- TtsService.java
- ProjectLyricsStorage
- printdata.py
- ucd.py
- Rhyme branch review
- Tune Definitions
- X-SAMPA Transcription Scheme
- SpeechWaveGeneratorImpl
- ProjectInfoBpmTest.kt
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Row
- utf8_out
- emoji
- ucd_lookup_category_group
- getopt.c
- SeekBarPreference
- Resonator
- Change Log
- ssml.c
- ProjectStorage
- DictionaryHeadword
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- documentation/README.md
- Third-party software and dictionary data
- espeakng_glue.cpp
- SynthCallback
- printucddata_cpp.cpp
- Vowels
- SpeakNextClause
- inputstream
- rgroup_sorter
- common
- EspeakIpaInstrumentedTest
- DictionaryInstaller.kt
- BeatPlayerInstrumentedTest
- .excludedWords
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- Feature Roadmap
- create_dict_corpus_file.py
- Translation fuzzers
- Synthesize
- espeak_ng_PrintStatusCodeMessage
- ucd_lookup_category
- WaveformCache
- android/gradlew
- bytearrayinputstream
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- Contribution Guide
- ParallelFormantGenerator
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- espeak_ng_SetOutputHooks
- ExampleInstrumentedTest.kt
- CloseWavFile
- ContextWrapper
- Third-party software and dictionary data
- CheckVoiceDataTest
- BeatPlaybackServiceTest
- LoadLanguageOptions
- espeak_SetPhonemeCallback
- row

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 51 edges
2. `SpeechSynthesis` - 49 edges
3. `GhostwriterTheme()` - 43 edges
4. `ucd_properties()` - 38 edges
5. `ProjectStorage` - 33 edges
6. `create_text_decoder()` - 31 edges
7. `text_decoder_eof()` - 31 edges
8. `espeak_Initialize()` - 31 edges
9. `ProjectStorageTest` - 30 edges
10. `ReadClause()` - 30 edges

## Surprising Connections (you probably didn't know these)
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `API and verification` --references--> `EspeakIpaInstrumentedTest`  [INFERRED]
  documentation/ESPEAK_NATIVE.md → app/src/androidTest/java/com/prosincerity/ghostwriter/data/EspeakIpaInstrumentedTest.kt
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `Installing` --references--> `eSpeakActivity`  [INFERRED]
  third_party/espeak-ng/docs/building.md → third_party/espeak-ng/android/src/com/reecedunn/espeak/eSpeakActivity.java

## Import Cycles
- None detected.

## Communities (180 total, 43 thin omitted)

### Community 1 - "Android"
Cohesion: 0.20
Nodes (10): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Installing, Opening project in Android Studio (+2 more)

### Community 2 - "Generate"
Cohesion: 0.17
Nodes (24): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), WritePitch(), PHONEME_DATA (+16 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.11
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), ProjectInfoDialog(), trimmedOrNull() (+2 more)

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.06
Nodes (72): add, alertdialog, alignment, LongBeatWarningDialog(), ReassignBeatDialog(), arrangement, arrowback, backhandler (+64 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.14
Nodes (14): CenteredPlayerContent(), Modifier, WaveformZoomButton(), button, card, columnscope, delay, fillmaxsize (+6 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.09
Nodes (10): abs, WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, MediaCodec (+2 more)

### Community 9 - "android.content.Context"
Cohesion: 0.07
Nodes (32): adapterview, android.app.Application, android.content.Context, android.content.DialogInterface, android.content.SharedPreferences, android.preference.DialogPreference, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener (+24 more)

### Community 10 - "ProjectStorageBeatTest"
Cohesion: 0.22
Nodes (3): IntArray, StagedFileWriter, ProjectStorageBeatTest

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (34): IntArray, Modifier, WaveformView(), ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures (+26 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.20
Nodes (10): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 6. Implement rhyme queries and editor behavior, 7. Licensing, attribution, and release verification, Goal and data flow (+2 more)

### Community 20 - "Lookup"
Cohesion: 0.25
Nodes (19): IsDigit09(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter() (+11 more)

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

### Community 30 - "InterpretPhoneme"
Cohesion: 0.20
Nodes (17): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+9 more)

### Community 31 - "EditorScreenTest"
Cohesion: 0.32
Nodes (3): EditorScreenTest, IntArray, MutableState

### Community 32 - "SpeechSynthesisTest"
Cohesion: 0.08
Nodes (13): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, SpeechSynthesisTest, Override, TextToSpeechServiceTest, TtsServiceTest, TextToSpeechTest, Override (+5 more)

### Community 33 - "compiledata.c"
Cohesion: 0.08
Nodes (77): phoneme_feature_t, PHONEME_TAB_LIST, FILE, Read4Bytes(), StringToWord(), CompileContext, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS (+69 more)

### Community 34 - "IpaSearchKeys"
Cohesion: 0.17
Nodes (4): SearchPrefix, IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (21): accessibilityevent, android.os.AsyncTask, android.widget.ProgressBar, arrays, bufferedinputstream, downloadmanager, environment, filefilter (+13 more)

### Community 37 - "eSpeakNGWorker"
Cohesion: 0.12
Nodes (14): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, PrintVersion() (+6 more)

### Community 38 - "utf8_in"
Cohesion: 0.15
Nodes (28): Translator, IsDigit(), IsSpace(), towlower2(), utf8_in(), utf8_in2(), compile_line(), compile_rule() (+20 more)

### Community 39 - "SettingsFormatTest.kt"
Cohesion: 0.31
Nodes (3): formatPlaybackTime(), formatInterval(), SettingsFormatTest

### Community 41 - "espeak_ng"
Cohesion: 0.15
Nodes (12): dirent, encoding, errno, espeak_ng, limits, memcheck, msan_interface, speechplayer (+4 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.15
Nodes (59): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), espeak_ng_ENCODING (+51 more)

### Community 43 - "speech.c"
Cohesion: 0.07
Nodes (45): audio, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, fcntl, io, pthread, espeak_Key() (+37 more)

### Community 44 - "MainActivity.kt"
Cohesion: 0.06
Nodes (33): AboutScreenTest, HomeScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home (+25 more)

### Community 45 - "espeak_ng_Initialize"
Cohesion: 0.12
Nodes (17): espeak_ng_OUTPUT_MODE, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_srand(), fifo_init(), LoadConfig(), espeak_ng_STATUS, SetParameter() (+9 more)

### Community 46 - "event.c"
Cohesion: 0.20
Nodes (20): add_time_in_ms(), espeak_EVENT, espeak_ng_STATUS, clock_gettime2(), event_clear_all(), event_copy(), event_declare(), event_delete() (+12 more)

### Community 47 - "wavegen.c"
Cohesion: 0.11
Nodes (33): sonic, espeak_rand(), GetFrameRms(), MarkerEvent(), MbrolaFill(), AdvanceParameters(), ApplyBreath(), frame_t (+25 more)

### Community 48 - "demo.js"
Cohesion: 0.07
Nodes (26): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10, 1.49.2 - 2017-09-24 (+18 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (24): android.content.BroadcastReceiver, android.content.Intent, android.os.Bundle, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter (+16 more)

### Community 50 - "Translator"
Cohesion: 0.15
Nodes (21): MatchRecord, 1.49.1 - 2017-01-21, is_str_totally_null(), AppendPhonemes(), Translator, WORD_TAB, DecodePhonemes(), DollarRule() (+13 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "DictionarySearch.kt"
Cohesion: 0.10
Nodes (20): PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX (+12 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "DictionaryDownloadsTest.kt"
Cohesion: 0.13
Nodes (30): activitynotfoundexception, androidjunit4, assertisenabled, assertisnotenabled, asserttextcontains, atomicinteger, continuation, countdownlatch (+22 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.14
Nodes (34): phoneme, readclause, speech, synthesize, CheckPhonemeMode(), clause_type_from_codepoint(), IsRomanU(), ReadClause() (+26 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.15
Nodes (33): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), t_espeak_callback (+25 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "android.view.View"
Cohesion: 0.13
Nodes (20): android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView, FileListAdapter (+12 more)

### Community 60 - "spect.c"
Cohesion: 0.15
Nodes (16): ieee80, osbyteorder, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength() (+8 more)

### Community 61 - "ucd.h"
Cohesion: 0.15
Nodes (22): category, property, script, get_category_string(), get_script_string(), codepoint_t, isalnum(), isalpha() (+14 more)

### Community 62 - "main"
Cohesion: 0.12
Nodes (37): DisplayVoices(), main(), GetFileLength(), strncpy0(), espeak_VOICE, SetVoiceStack(), check_data_path(), espeak_ng_InitializePath() (+29 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.19
Nodes (27): JavaVM, jboolean, jclass, jni, jstring, espeak_EVENT, jint, JNICALL (+19 more)

### Community 65 - "EditorScreenTest.kt"
Cohesion: 0.16
Nodes (14): ByteBuffer, composetimeoutexception, lifecycle, longclick, MediaController, mediametadata, MediaSession, notification (+6 more)

### Community 66 - "status_to_espeak_error"
Cohesion: 0.15
Nodes (17): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, 1.47.12 - 2013-10-12, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER (+9 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.10
Nodes (39): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+31 more)

### Community 71 - "SelectPhonemeTable"
Cohesion: 0.16
Nodes (18): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+10 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "uprintf"
Cohesion: 0.17
Nodes (19): stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), totitle(), toupper() (+11 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "TranslateWord3"
Cohesion: 0.17
Nodes (24): IsAlpha(), IsBracket(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), SetSpellingStress(), WordToString2() (+16 more)

### Community 77 - "espeak_ng_CompileDictionary"
Cohesion: 0.19
Nodes (17): isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), compile_dictlist_end() (+9 more)

### Community 78 - "TtsService"
Cohesion: 0.14
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, Override, SuppressWarnings, TtsService, Override, Voice

### Community 79 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "BeatPlaybackService.kt"
Cohesion: 0.06
Nodes (24): BeatPlaybackService, Callback, Bundle, Context, IBinder, Intent, Notification, LocalBinder (+16 more)

### Community 83 - "EditorScreen.kt"
Cohesion: 0.08
Nodes (29): activityresultcontracts, ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, displayNameFor(), Context (+21 more)

### Community 85 - ".excludedWords"
Cohesion: 0.18
Nodes (6): AssonanceFormFilter, Entry, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.22
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 88 - "DoSpect2"
Cohesion: 0.23
Nodes (15): AdjustFormants(), AllocFrame(), FMT_PARAMS, frame_t, frameref_t, PHONEME_LIST, PHONEME_TAB, CopyFrame() (+7 more)

### Community 89 - "AnnouncePunctuation"
Cohesion: 0.27
Nodes (11): AnnouncePunctuation(), Translator, DecodeWithPhonemeMode(), Eof(), GetC(), IgnoreOrReplaceChar(), LookupCharName(), LookupSpecial() (+3 more)

### Community 90 - "GhostwriterTheme"
Cohesion: 0.13
Nodes (12): BeatComponentsTest, LyricsNotepadTest, DictionaryScreenTest, DictionaryMatch, DictionarySearchResult, BeatPlaybackControls(), BeatPlayerPanel(), IntArray (+4 more)

### Community 91 - "intonation.c"
Cohesion: 0.21
Nodes (19): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+11 more)

### Community 92 - "Building"
Cohesion: 0.20
Nodes (10): Building, Cross Compilation, Dependencies, eSpeak NG Feature Configuration, Extended Dictionary Configuration, Installing, Linux, Mac, BSD, LLVM Fuzzer Support (+2 more)

### Community 93 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 96 - "synth_fuzzer.c"
Cohesion: 0.33
Nodes (4): libgen, espeak_EVENT, espeak_callback(), time

### Community 98 - "TtsService.java"
Cohesion: 0.10
Nodes (28): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, audiotrack, bundle, configuration (+20 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "Rhyme branch review"
Cohesion: 0.40
Nodes (4): Completed stages, Review limits, Rhyme branch review, Validation

### Community 103 - "Tune Definitions"
Cohesion: 0.14
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "X-SAMPA Transcription Scheme"
Cohesion: 0.14
Nodes (14): Consonants, Intonation, Length, Manner of Articulation, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+6 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 107 - "comentrypoints.c"
Cohesion: 0.27
Nodes (12): REFCLSID, BOOL, HRESULT, IClassFactory, REFIID, ULONG, ClassFactory_AddRef(), ClassFactory_LockServer() (+4 more)

### Community 108 - "espeakng.js"
Cohesion: 0.15
Nodes (8): eSpeakNG(), Building, Credits, Demo, Download, espeakng.js, Notes, Usage

### Community 109 - "utils.h"
Cohesion: 0.23
Nodes (6): sample, speechPlayer_frame_t, calculateValueAtFadePosition(), ISNAN(), MAX(), MIN()

### Community 110 - "uprintf"
Cohesion: 0.37
Nodes (12): 10.0.0 - 2017-06-25, codepoint_t, FILE, fget_utf8c(), fput_utf8c(), iswblank(), main(), print_file() (+4 more)

### Community 111 - "Row"
Cohesion: 0.45
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 112 - "utf8_out"
Cohesion: 0.60
Nodes (6): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress()

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "ucd_lookup_category_group"
Cohesion: 0.18
Nodes (12): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), get_category_group_string(), lookup_category_group() (+4 more)

### Community 115 - "getopt.c"
Cohesion: 0.46
Nodes (7): assert, getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args()

### Community 116 - "SeekBarPreference"
Cohesion: 0.09
Nodes (9): android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage, Punctuation (+1 more)

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.18
Nodes (10): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06, 9.0.0 - 2016-12-28 (+2 more)

### Community 119 - "ssml.c"
Cohesion: 0.19
Nodes (23): PARAM_STACK, SSML_STACK, LookupMnem(), AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup() (+15 more)

### Community 120 - "ProjectStorage"
Cohesion: 0.18
Nodes (4): MainActivityTest, Context, ProjectStorage, ioexception

### Community 121 - "DictionaryHeadword"
Cohesion: 0.14
Nodes (5): SQLiteDatabase, PronunciationResult, DictionaryHeadword, DictionaryHeadwordTest, normalizer

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

### Community 126 - "test"
Cohesion: 0.13
Nodes (25): after, assertequals, assertfalse, assertnotequals, assertnotnull, assertnull, asserttrue, async (+17 more)

### Community 127 - "documentation/README.md"
Cohesion: 0.16
Nodes (9): Architecture, Current limits, Ghostwriter documentation, Build setup, Open and build, Requirements, Coverage, Regular checks (+1 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "SpeakNextClause"
Cohesion: 0.12
Nodes (17): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+9 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "DictionaryInstaller.kt"
Cohesion: 0.06
Nodes (32): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, Context, dictionaryArchive(), ByteArray, Context, ContextWrapper, T (+24 more)

### Community 140 - ".excludedWords"
Cohesion: 0.33
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "Synthesize"
Cohesion: 0.26
Nodes (14): process_espeak_command(), InitNamedata(), InitText2(), espeak_ng_STATUS, espeak_POSITION_TYPE, wchar_t, espeak_ng_SetPunctuationList(), sync_espeak_Char() (+6 more)

### Community 148 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.27
Nodes (11): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+3 more)

### Community 149 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?, Source Nodes

### Community 156 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 157 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 170 - "espeak_ng_SetOutputHooks"
Cohesion: 0.50
Nodes (4): espeak_ng_OUTPUT_HOOKS, ESPEAK_NG_API, espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks()

### Community 172 - "CloseWavFile"
Cohesion: 0.40
Nodes (6): espeak_EVENT, FILE, CloseWavFile(), OpenWavFile(), SynthCallback(), Write4Bytes()

### Community 173 - "ContextWrapper"
Cohesion: 0.40
Nodes (3): ContextWrapper, Context, ContextWrapper

### Community 174 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 175 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.26
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 177 - "LoadLanguageOptions"
Cohesion: 0.20
Nodes (12): DecodeRule(), print_dictionary_flags(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune(), MNEM_TAB (+4 more)

## Knowledge Gaps
- **459 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+454 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 914 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **43 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Why does `eSpeakActivity` connect `eSpeakActivity` to `SpeechSynthesisTest`, `android.content.Context`, `android.view.View`, `Android`?**
  _High betweenness centrality (0.060) - this node is a cross-community bridge._
- **Are the 23 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 23 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _459 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.10420168067226891 - nodes in this community are weakly interconnected._
- **Should `ProjectMetadata` be split into smaller, more focused modules?**
  _Cohesion score 0.10887096774193548 - nodes in this community are weakly interconnected._
- **Should `ProjectStorageTest` be split into smaller, more focused modules?**
  _Cohesion score 0.12096774193548387 - nodes in this community are weakly interconnected._