# Graph Report - Gh0stwrit3r  (2026-09-24)

## Corpus Check
- 284 files · ~536,104 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2050 file(s) not represented in the graph (top: (none) 1892, .xml 77, .test 17)

## Summary
- 3088 nodes · 7755 edges · 168 communities (132 shown, 36 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1198 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b0de889c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- file
- Generate
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- DictionaryScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- MainActivity.kt
- ProjectStorageBeatTest
- gradlew
- .createSeekBarPreference
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
- SeekBarPreference
- EditorScreen
- documentation/README.md
- compiledata.c
- .search
- Ghostwriter agent instructions
- espeak_callback
- InterpretPhoneme
- Android
- SettingsFormatTest.kt
- VoiceSettingsTest
- espeak_ng
- tests/encoding.c
- Synthesize
- sPlayer.c
- main
- speech.c
- wavegen.c
- demo.js
- eSpeakActivity
- Language Attributes
- ttsengine.cpp
- test
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- DoSpect2
- ucd.h
- voices.c
- Phoneme Model
- eSpeakService.c
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- TranslateWord3
- SpeechSynthesis
- android.content.Context
- FrameManagerImpl
- klatt.c
- SetSpeed
- Diacritics
- ProjectStorage
- Building
- speechPlayer.cpp
- SelectPhonemeTable
- espeak_ng_CompileDictionary
- TtsService
- tr_languages.c
- index.md
- SSML (Speech Synthesis Markup Language)
- TtsService.java
- ssml.c
- TextToSpeechTestCase
- ParallelFormantGenerator
- speechWaveGenerator.cpp
- VoiceVariant
- Diacritics
- DictionaryInstallerInstrumentedTest.kt
- TranslateClauseWithTerminator
- intonation.c
- .isTtsLangCode
- espeak_api.c
- Phoneme Instructions
- CodePoint
- Lookup
- eSpeakNGWorker
- ProjectLyricsStorage
- ProjectInfoBpmTest.kt
- printdata.py
- ucd.py
- LyricsNotepad
- Tune Definitions
- X-SAMPA Transcription Scheme
- SpeechWaveGeneratorImpl
- uprintf
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Theme.kt
- espeak_ng_SetOutputHooks
- emoji
- espeak_ng_SetRandSeed
- GetFrameRms
- utf8_in
- Resonator
- Change Log
- CheckVoiceDataTest
- MainActivityTest
- espeak_SetPhonemeCallback
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- WaveformCache
- .excludedWords
- Third-party software and dictionary data
- ReadClause
- espeak_ng_PrintStatusCodeMessage
- printucddata_cpp.cpp
- Vowels
- SynthCallback
- DictionaryScreen
- espeak-ng.c
- common
- EspeakIpaInstrumentedTest
- DictionaryInstaller.kt
- spect.c
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- create_dict_corpus_file.py
- Translation fuzzers
- ucd_lookup_category
- DictionaryHeadword
- android/gradlew
- Using eSpeak NG as a library
- espeak-ng
- Contribution Guide
- numbers.md
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- espeakng_glue.cpp
- rgroup_sorter
- TESTING.md
- row

## God Nodes (most connected - your core abstractions)
1. `SpeechSynthesis` - 49 edges
2. `BeatPlayer` - 48 edges
3. `ucd_properties()` - 38 edges
4. `GhostwriterTheme()` - 36 edges
5. `ProjectStorage` - 33 edges
6. `create_text_decoder()` - 31 edges
7. `text_decoder_eof()` - 31 edges
8. `espeak_Initialize()` - 31 edges
9. `ProjectStorageTest` - 30 edges
10. `ReadClause()` - 30 edges

## Surprising Connections (you probably didn't know these)
- `API and verification` --references--> `EspeakIpaInstrumentedTest`  [INFERRED]
  documentation/ESPEAK_NATIVE.md → app/src/androidTest/java/com/prosincerity/ghostwriter/data/EspeakIpaInstrumentedTest.kt
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Installing` --references--> `eSpeakActivity`  [INFERRED]
  third_party/espeak-ng/docs/building.md → third_party/espeak-ng/android/src/com/reecedunn/espeak/eSpeakActivity.java

## Import Cycles
- None detected.

## Communities (168 total, 36 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.05
Nodes (17): BeatPlayerInstrumentedTest, BeatComponentsTest, AboutScreenTest, HomeScreenTest, BeatPlayer, BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent() (+9 more)

### Community 1 - "file"
Cohesion: 0.11
Nodes (20): abs, accessibilityevent, after, audioformat, bufferedinputstream, bytearrayoutputstream, byteorder, cancellationexception (+12 more)

### Community 2 - "Generate"
Cohesion: 0.17
Nodes (24): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), PHONEME_DATA, DoAmplitude() (+16 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.12
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), ProjectInfoDialog(), trimmedOrNull() (+2 more)

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.08
Nodes (67): add, alertdialog, alignment, arrowback, backhandler, box, check, clickable (+59 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.13
Nodes (15): WaveformZoomButton(), arrangement, button, card, columnscope, delay, fillmaxsize, ImageVector (+7 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (6): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, ByteBuffer

### Community 9 - "MainActivity.kt"
Cohesion: 0.09
Nodes (30): About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle, Context (+22 more)

### Community 10 - "ProjectStorageBeatTest"
Cohesion: 0.17
Nodes (3): IntArray, StagedFileWriter, ProjectStorageBeatTest

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (32): canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures, drawtext, fillmaxheight (+24 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.22
Nodes (9): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 7. Licensing, attribution, and release verification, Goal and data flow, Rhyme detection: dictionary and eSpeak NG integration plan (+1 more)

### Community 20 - "EditorScreen.kt"
Cohesion: 0.09
Nodes (23): activityresultcontracts, LongBeatWarningDialog(), ReassignBeatDialog(), displayNameFor(), Context, atomicboolean, atomiclong, book (+15 more)

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

### Community 30 - "SeekBarPreference"
Cohesion: 0.10
Nodes (15): android.content.DialogInterface, android.content.SharedPreferences, android.preference.DialogPreference, android.widget.EditText, android.widget.RadioButton, android.widget.SeekBar, editable, jsonexception (+7 more)

### Community 31 - "EditorScreen"
Cohesion: 0.27
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 32 - "documentation/README.md"
Cohesion: 0.19
Nodes (7): Architecture, Current limits, Ghostwriter documentation, Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 33 - "compiledata.c"
Cohesion: 0.09
Nodes (72): phoneme_feature_t, PHONEME_TAB_LIST, FILE, Read4Bytes(), StringToWord(), CompileContext, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS (+64 more)

### Community 34 - ".search"
Cohesion: 0.22
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 37 - "InterpretPhoneme"
Cohesion: 0.20
Nodes (17): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+9 more)

### Community 38 - "Android"
Cohesion: 0.20
Nodes (10): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Installing, Opening project in Android Studio (+2 more)

### Community 39 - "SettingsFormatTest.kt"
Cohesion: 0.31
Nodes (3): formatPlaybackTime(), formatInterval(), SettingsFormatTest

### Community 40 - "VoiceSettingsTest"
Cohesion: 0.26
Nodes (3): android.preference.Preference, VoiceSettingsTest, VoiceSettings

### Community 41 - "espeak_ng"
Cohesion: 0.18
Nodes (11): common, encoding, errno, espeak_ng, libgen, limits, memcheck, msan_interface (+3 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (51): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+43 more)

### Community 43 - "Synthesize"
Cohesion: 0.08
Nodes (48): espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark() (+40 more)

### Community 44 - "sPlayer.c"
Cohesion: 0.18
Nodes (18): KlattInit(), KlattReset(), frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame() (+10 more)

### Community 45 - "main"
Cohesion: 0.12
Nodes (24): espeak_ng_OUTPUT_MODE, main(), PrintVersion(), t_espeak_callback, event_set_callback(), ESPEAK_API, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT (+16 more)

### Community 46 - "speech.c"
Cohesion: 0.09
Nodes (37): assert, audio, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, io, pthread, add_time_in_ms() (+29 more)

### Community 47 - "wavegen.c"
Cohesion: 0.12
Nodes (31): sonic, MarkerEvent(), MbrolaFill(), MbrolaReset(), WritePitch(), AdvanceParameters(), ApplyBreath(), frame_t (+23 more)

### Community 48 - "demo.js"
Cohesion: 0.07
Nodes (27): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+19 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.08
Nodes (23): android.content.BroadcastReceiver, android.content.Intent, android.os.Bundle, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, java.lang.ref.WeakReference (+15 more)

### Community 50 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.09
Nodes (36): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+28 more)

### Community 52 - "test"
Cohesion: 0.17
Nodes (12): shouldWarnBeforeWaveformExtraction(), WaveformWarningTest, assertfalse, assertnotnull, asserttrue, countdownlatch, executors, jsonobject (+4 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.16
Nodes (24): activitynotfoundexception, assertequals, assertisenabled, assertisnotenabled, asserttextcontains, createandroidcomposerule, createcomposerule, ghostwriter_repository_url (+16 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.15
Nodes (30): phoneme, readclause, speech, synthesize, clause_type_from_codepoint(), espeak_ng_STATUS, main(), set_text() (+22 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.18
Nodes (27): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), espeak_EVENT (+19 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.13
Nodes (21): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView (+13 more)

### Community 60 - "DoSpect2"
Cohesion: 0.23
Nodes (15): AdjustFormants(), AllocFrame(), FMT_PARAMS, frame_t, frameref_t, PHONEME_LIST, PHONEME_TAB, CopyFrame() (+7 more)

### Community 61 - "ucd.h"
Cohesion: 0.09
Nodes (33): category, property, script, stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle() (+25 more)

### Community 62 - "voices.c"
Cohesion: 0.08
Nodes (51): dirent, GetFileLength(), strncpy0(), MNEM_TAB, Translator, CheckTranslator(), LoadConfig(), LoadLanguageOptions() (+43 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 66 - "TranslateWord3"
Cohesion: 0.15
Nodes (28): IsAlpha(), IsBracket(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), LookupLetter(), SetSpellingStress() (+20 more)

### Community 67 - "SpeechSynthesis"
Cohesion: 0.08
Nodes (3): SpeechSynthesisTest, SpeechSynthesis, SynthReadyCallback

### Community 68 - "android.content.Context"
Cohesion: 0.06
Nodes (30): android.app.Application, android.content.Context, android.os.AsyncTask, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment, android.preference.PreferenceGroup, android.util.AttributeSet (+22 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.16
Nodes (23): klatt_frame_ptr, resonator_ptr, speechplayer, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN() (+15 more)

### Community 71 - "SetSpeed"
Cohesion: 0.18
Nodes (11): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+3 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "Building"
Cohesion: 0.20
Nodes (10): Building, Cross Compilation, Dependencies, eSpeak NG Feature Configuration, Extended Dictionary Configuration, Installing, Linux, Mac, BSD, LLVM Fuzzer Support (+2 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.15
Nodes (13): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+5 more)

### Community 76 - "SelectPhonemeTable"
Cohesion: 0.20
Nodes (15): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+7 more)

### Community 77 - "espeak_ng_CompileDictionary"
Cohesion: 0.16
Nodes (20): IsDigit(), isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+12 more)

### Community 78 - "TtsService"
Cohesion: 0.15
Nodes (6): android.speech.tts.SynthesisRequest, Override, SuppressWarnings, TtsService, Override, Voice

### Community 79 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "TtsService.java"
Cohesion: 0.09
Nodes (31): android.speech.tts.SynthesisCallback, android.speech.tts.TextToSpeechService, android.test.AndroidTestCase, anyof, arraylist, assertthat, audiotrack, bundle (+23 more)

### Community 83 - "ssml.c"
Cohesion: 0.12
Nodes (33): PARAM_STACK, SSML_STACK, DecodeRule(), print_dictionary_flags(), MNEM_TAB, LookupMnem(), LookupMnemName(), AddNameData() (+25 more)

### Community 84 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (10): android.annotation.SuppressLint, android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception (+2 more)

### Community 85 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.18
Nodes (5): java.util.regex.Pattern, VoiceVariantTest, VariantData, Override, VoiceVariant

### Community 88 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 89 - "DictionaryInstallerInstrumentedTest.kt"
Cohesion: 0.15
Nodes (13): androidjunit4, ContextWrapper, Context, ContextWrapper, ExampleInstrumentedTest, assertnotequals, assertnull, bytearrayinputstream (+5 more)

### Community 90 - "TranslateClauseWithTerminator"
Cohesion: 0.14
Nodes (25): 1.49.1 - 2017-01-21, Translator, is_str_totally_null(), IsSpace(), towlower2(), InitGroups(), LoadDictionary(), Reverse4Bytes() (+17 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 93 - "espeak_api.c"
Cohesion: 0.19
Nodes (18): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, FILE (+10 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (32): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+24 more)

### Community 96 - "Lookup"
Cohesion: 0.31
Nodes (16): IsDigit09(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter() (+8 more)

### Community 97 - "eSpeakNGWorker"
Cohesion: 0.22
Nodes (7): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "LyricsNotepad"
Cohesion: 0.20
Nodes (9): LyricsNotepadTest, Modifier, LyricsNotepad(), Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment (+1 more)

### Community 103 - "Tune Definitions"
Cohesion: 0.14
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "X-SAMPA Transcription Scheme"
Cohesion: 0.14
Nodes (14): Consonants, Intonation, Length, Manner of Articulation, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+6 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 106 - "uprintf"
Cohesion: 0.20
Nodes (20): codepoint_t, ucd_isalnum(), ucd_isgraph(), ucd_isprint(), ucd_ispunct(), ucd_isspace(), ucd_isxdigit(), isalnum() (+12 more)

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
Cohesion: 0.20
Nodes (18): get_category_string(), ucd_category, ucd_category_group, ucd_script, ucd_get_category_group_string(), ucd_get_category_string(), ucd_get_script_string(), codepoint_t (+10 more)

### Community 111 - "Theme.kt"
Cohesion: 0.50
Nodes (3): darkcolorscheme, issystemindarktheme, lightcolorscheme

### Community 112 - "espeak_ng_SetOutputHooks"
Cohesion: 0.50
Nodes (4): espeak_ng_OUTPUT_HOOKS, ESPEAK_NG_API, espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks()

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "espeak_ng_SetRandSeed"
Cohesion: 0.50
Nodes (4): ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand()

### Community 115 - "GetFrameRms"
Cohesion: 0.67
Nodes (3): GetFrameRms(), PeaksToHarmspect(), wavegen_peaks_t

### Community 116 - "utf8_in"
Cohesion: 0.16
Nodes (23): MatchRecord, utf8_in(), utf8_in2(), compile_line(), AppendPhonemes(), Translator, WORD_TAB, DecodePhonemes() (+15 more)

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 10.0.0 - 2017-06-25, 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

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

### Community 127 - ".excludedWords"
Cohesion: 0.33
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 129 - "ReadClause"
Cohesion: 0.19
Nodes (20): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress(), AnnouncePunctuation(), Translator (+12 more)

### Community 130 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.39
Nodes (8): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage()

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 134 - "DictionaryScreen"
Cohesion: 0.09
Nodes (25): DictionarySearchInstrumentedTest, Row, DictionaryScreenTest, DictionaryPronunciations, SQLiteDatabase, PronunciationResult, PronunciationSource, ESPEAK_DATABASE (+17 more)

### Community 135 - "espeak-ng.c"
Cohesion: 0.17
Nodes (14): fcntl, getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args(), espeak_EVENT (+6 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "DictionaryInstaller.kt"
Cohesion: 0.08
Nodes (25): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, ContextWrapper, DictionaryArchiveSource, DictionaryAsset, DictionaryDownloadProgress (+17 more)

### Community 139 - "spect.c"
Cohesion: 0.15
Nodes (16): ieee80, osbyteorder, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength() (+8 more)

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 148 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (9): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), get_category_group_string() (+1 more)

### Community 149 - "DictionaryHeadword"
Cohesion: 0.19
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 156 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 174 - "TESTING.md"
Cohesion: 0.25
Nodes (6): Build setup, Open and build, Requirements, Coverage, Regular checks, Testing and coverage

## Knowledge Gaps
- **452 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+447 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 877 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **36 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BeatPlayer` connect `BeatPlayer` to `DictionaryScreen.kt`, `BeatPlayerPanel.kt`, `EditorScreen.kt`, `EditorScreenTest.kt`, `EditorScreen`?**
  _High betweenness centrality (0.089) - this node is a cross-community bridge._
- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.071) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `ucd_properties()` (e.g. with `clause_type_from_codepoint()` and `ucd_isalnum()`) actually correct?**
  _`ucd_properties()` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _452 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.05213089802130898 - nodes in this community are weakly interconnected._
- **Should `file` be split into smaller, more focused modules?**
  _Cohesion score 0.10541310541310542 - nodes in this community are weakly interconnected._