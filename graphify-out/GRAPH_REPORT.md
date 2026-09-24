# Graph Report - Gh0stwrit3r  (2026-09-24)

## Corpus Check
- 284 files · ~536,019 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2050 file(s) not represented in the graph (top: (none) 1892, .xml 77, .test 17)

## Summary
- 3087 nodes · 7757 edges · 177 communities (143 shown, 34 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1201 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `c14de23b`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- IpaGenerator
- synthesize.c
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- DictionaryScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- MainActivity.kt
- .writeText
- gradlew
- espeak_ng
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
- libespeak-ng/readclause.c
- InterpretPhoneme
- Android
- SettingsFormatTest.kt
- VoiceSettingsTest
- Voice Attributes
- tests/encoding.c
- espeak_command.c
- DoSpect2
- espeak_ng_Initialize
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
- ImportVoicePreference.java
- android.content.Context
- ucd.h
- LoadVoice
- Phoneme Model
- eSpeakService.c
- DictionaryPronunciations
- TranslateWord3
- SpeechSynthesis
- DownloadVoiceData.java
- FrameManagerImpl
- klatt.c
- ParallelFormantGenerator
- Diacritics
- ProjectStorageBeatTest
- eSpeakActivity.java
- speechPlayer.cpp
- SelectPhonemeTable
- compile_line
- TtsService
- tr_languages.c
- index.md
- SSML (Speech Synthesis Markup Language)
- TtsService.java
- LoadLanguageOptions
- TextToSpeechTestCase
- espeak_ng_SetOutputHooks
- speechWaveGenerator.cpp
- VoiceVariant
- ProjectLyricsStorage
- DictionaryInstallerInstrumentedTest.kt
- utf8_in
- intonation.c
- .isTtsLangCode
- status_to_espeak_error
- Phoneme Instructions
- CodePoint
- Lookup
- eSpeakNGWorker
- GhostwriterTheme
- ucd_lookup_category
- printdata.py
- ucd.py
- LyricsNotepad.kt
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- uprintf
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- utf8_out
- ucd_lookup_category_group
- emoji
- ProcessSsmlTag
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- dictionary.c
- Resonator
- Change Log
- CheckVoiceDataTest
- ProjectStorage
- SpeakNextClause
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- BeatPlayerInstrumentedTest
- .excludedWords
- Third-party software and dictionary data
- config.h
- espeak_ng_PrintStatusCodeMessage
- printucddata_cpp.cpp
- Vowels
- Building
- DictionaryScreen
- Voice and Language Files
- common
- EspeakIpaInstrumentedTest
- DictionaryInstaller.kt
- spect.c
- getopt.c
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- speech.h
- create_dict_corpus_file.py
- Translation fuzzers
- Voice
- Third-party software and dictionary data
- DictionaryHeadword
- Ghostwriter documentation
- android/gradlew
- State
- Using eSpeak NG as a library
- espeak-ng
- WaveformWarningTest.kt
- Contribution Guide
- numbers.md
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- ProjectInfoBpmTest.kt
- espeak-ng.c
- ExampleInstrumentedTest.kt
- Feature Roadmap
- Build setup
- Testing and coverage
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

## Communities (177 total, 34 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.10
Nodes (4): BeatPlayer, BeatPlayerTest, MediaPlayer, Type

### Community 1 - "IpaGenerator"
Cohesion: 0.20
Nodes (7): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, ContextWrapper, DictionaryArchiveSource, IpaGenerator

### Community 2 - "synthesize.c"
Cohesion: 0.16
Nodes (28): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), WritePitch(), ESPEAK_API (+20 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.11
Nodes (10): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), ProjectInfoDialog(), trimmedOrNull() (+2 more)

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.09
Nodes (64): add, alertdialog, alignment, arrowback, backhandler, box, button, check (+56 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.12
Nodes (16): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, card, columnscope, delay, fillmaxsize (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.09
Nodes (10): abs, WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, MediaCodec (+2 more)

### Community 9 - "MainActivity.kt"
Cohesion: 0.10
Nodes (25): HomeScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle (+17 more)

### Community 10 - ".writeText"
Cohesion: 0.16
Nodes (4): IntArray, StagedFileWriter, IntArray, WaveformCache

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "espeak_ng"
Cohesion: 0.19
Nodes (8): dirent, espeak_ng, speechplayer, stdbool, LookupPhonemeString(), PhonemeCode(), PhonemeReplacement(), windows

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (35): IntArray, Modifier, WaveformView(), canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures (+27 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.25
Nodes (8): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 6. Implement rhyme queries and editor behavior, 7. Licensing, attribution, and release verification, Steps

### Community 20 - "EditorScreen.kt"
Cohesion: 0.09
Nodes (22): activityresultcontracts, LongBeatWarningDialog(), ReassignBeatDialog(), displayNameFor(), Context, atomicboolean, atomiclong, book (+14 more)

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
Nodes (10): android.content.DialogInterface, android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage (+2 more)

### Community 31 - "EditorScreen"
Cohesion: 0.27
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 33 - "compiledata.c"
Cohesion: 0.08
Nodes (77): phoneme_feature_t, PHONEME_TAB_LIST, FILE, Read4Bytes(), StringToWord(), CompileContext, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS (+69 more)

### Community 34 - ".search"
Cohesion: 0.19
Nodes (5): DictionarySearch, SearchPrefix, IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "libespeak-ng/readclause.c"
Cohesion: 0.21
Nodes (7): emscripten, encoding, glue, limits, ESPEAK_API, espeak_SetUriCallback(), ucd

### Community 37 - "InterpretPhoneme"
Cohesion: 0.20
Nodes (17): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+9 more)

### Community 38 - "Android"
Cohesion: 0.20
Nodes (10): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Installing, Opening project in Android Studio (+2 more)

### Community 39 - "SettingsFormatTest.kt"
Cohesion: 0.31
Nodes (3): formatPlaybackTime(), formatInterval(), SettingsFormatTest

### Community 41 - "Voice Attributes"
Cohesion: 0.14
Nodes (14): breath, breathw, consonants, echo, flutter, formant, freq\_add, pitch (+6 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (51): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+43 more)

### Community 43 - "espeak_command.c"
Cohesion: 0.09
Nodes (48): espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark() (+40 more)

### Community 44 - "DoSpect2"
Cohesion: 0.23
Nodes (15): AdjustFormants(), AllocFrame(), FMT_PARAMS, frame_t, frameref_t, PHONEME_LIST, PHONEME_TAB, CopyFrame() (+7 more)

### Community 45 - "espeak_ng_Initialize"
Cohesion: 0.25
Nodes (8): ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_srand(), InitNamedata(), InitText2(), espeak_ng_ERROR_CONTEXT, espeak_ng_Initialize(), InitText()

### Community 46 - "speech.c"
Cohesion: 0.10
Nodes (35): assert, audio, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, io, add_time_in_ms(), espeak_EVENT (+27 more)

### Community 47 - "wavegen.c"
Cohesion: 0.11
Nodes (33): sonic, espeak_rand(), GetFrameRms(), MarkerEvent(), MbrolaFill(), AdvanceParameters(), ApplyBreath(), frame_t (+25 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.16
Nodes (7): android.content.Intent, android.view.Menu, android.view.MenuItem, eSpeakActivity, OnInitListener, Override, SuppressWarnings

### Community 50 - "Language Attributes"
Cohesion: 0.12
Nodes (16): brackets, bracketsAnnounced, dictionary, dictmin, dictrules, intonation, Language Attributes, lowercaseSentence (+8 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "test"
Cohesion: 0.16
Nodes (20): after, assertequals, assertfalse, assertnotequals, assertnotnull, assertnull, asserttrue, ByteBuffer (+12 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.18
Nodes (24): activitynotfoundexception, androidjunit4, assertisenabled, assertisnotenabled, asserttextcontains, createandroidcomposerule, createcomposerule, ghostwriter_repository_url (+16 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.10
Nodes (45): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), Translator, CheckPhonemeMode(), clause_type_from_codepoint() (+37 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.10
Nodes (43): espeak_AUDIO_OUTPUT, espeak_ng_OUTPUT_MODE, main(), PrintVersion(), espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName() (+35 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (31): adapterview, android.app.Activity, android.preference.DialogPreference, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter (+23 more)

### Community 60 - "android.content.Context"
Cohesion: 0.08
Nodes (25): android.app.Application, android.content.Context, android.content.SharedPreferences, android.os.Bundle, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment (+17 more)

### Community 61 - "ucd.h"
Cohesion: 0.15
Nodes (22): category, property, script, get_category_string(), get_script_string(), codepoint_t, isalnum(), isalpha() (+14 more)

### Community 62 - "LoadVoice"
Cohesion: 0.12
Nodes (35): GetFileLength(), strncpy0(), espeak_VOICE, SetVoiceStack(), check_data_path(), espeak_ng_STATUS, LoadMbrolaTable(), system_data_dirs() (+27 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "DictionaryPronunciations"
Cohesion: 0.28
Nodes (4): DictionarySearchInstrumentedTest, DictionaryPronunciations, SQLiteDatabase, PronunciationResult

### Community 66 - "TranslateWord3"
Cohesion: 0.19
Nodes (19): IsSuperscript(), SetSpellingStress(), WordToString2(), AlphabetFromChar(), ALPHABET, EmbeddedCommand(), strchr_w(), addPluralSuffixes() (+11 more)

### Community 67 - "SpeechSynthesis"
Cohesion: 0.07
Nodes (5): SpeechSynthesisTest, GetSampleText, Override, SpeechSynthesis, SynthReadyCallback

### Community 68 - "DownloadVoiceData.java"
Cohesion: 0.11
Nodes (13): accessibilityevent, android.os.AsyncTask, android.widget.ProgressBar, bufferedinputstream, CheckVoiceData, Override, AsyncExtract, DownloadVoiceData (+5 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.10
Nodes (39): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+31 more)

### Community 71 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "eSpeakActivity.java"
Cohesion: 0.20
Nodes (9): android.content.BroadcastReceiver, android.os.Handler, android.os.Message, intentfilter, java.lang.ref.WeakReference, listview, menuinflater, EspeakHandler (+1 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "SelectPhonemeTable"
Cohesion: 0.15
Nodes (19): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+11 more)

### Community 77 - "compile_line"
Cohesion: 0.18
Nodes (21): IsDigit09(), isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+13 more)

### Community 78 - "TtsService"
Cohesion: 0.14
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, Override, SuppressWarnings, TtsService, Override, Voice

### Community 79 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "TtsService.java"
Cohesion: 0.10
Nodes (29): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, audiotrack, bundle, checksum (+21 more)

### Community 83 - "LoadLanguageOptions"
Cohesion: 0.20
Nodes (12): DecodeRule(), print_dictionary_flags(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune(), MNEM_TAB (+4 more)

### Community 84 - "TextToSpeechTestCase"
Cohesion: 0.22
Nodes (6): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase

### Community 85 - "espeak_ng_SetOutputHooks"
Cohesion: 0.50
Nodes (4): espeak_ng_OUTPUT_HOOKS, ESPEAK_NG_API, espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks()

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.22
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 89 - "DictionaryInstallerInstrumentedTest.kt"
Cohesion: 0.13
Nodes (12): ContextWrapper, Context, ContextWrapper, bytearrayinputstream, bytearrayoutputstream, fileinputstream, fileoutputstream, gzipoutputstream (+4 more)

### Community 90 - "utf8_in"
Cohesion: 0.16
Nodes (27): Translator, is_str_totally_null(), IsAlpha(), IsBracket(), IsDigit(), IsSpace(), towlower2(), utf8_in() (+19 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 93 - "status_to_espeak_error"
Cohesion: 0.17
Nodes (16): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, wchar_t (+8 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (32): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+24 more)

### Community 96 - "Lookup"
Cohesion: 0.31
Nodes (16): Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter(), LookupLetter() (+8 more)

### Community 97 - "eSpeakNGWorker"
Cohesion: 0.22
Nodes (7): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices

### Community 98 - "GhostwriterTheme"
Cohesion: 0.11
Nodes (15): Row, BeatComponentsTest, AboutScreenTest, BeatPlaybackControls(), BeatPlayerPanel(), IntArray, AboutLink(), AboutScreen() (+7 more)

### Community 99 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "LyricsNotepad.kt"
Cohesion: 0.22
Nodes (7): LyricsNotepadTest, Modifier, LyricsNotepad(), keyboardcapitalization, keyboardoptions, textfield, textfielddefaults

### Community 103 - "Tune Definitions"
Cohesion: 0.15
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 106 - "uprintf"
Cohesion: 0.17
Nodes (19): stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), totitle(), toupper() (+11 more)

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

### Community 111 - "utf8_out"
Cohesion: 0.60
Nodes (6): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress()

### Community 112 - "ucd_lookup_category_group"
Cohesion: 0.18
Nodes (12): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), get_category_group_string(), lookup_category_group() (+4 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "ProcessSsmlTag"
Cohesion: 0.19
Nodes (22): PARAM_STACK, SSML_STACK, LookupMnem(), AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup() (+14 more)

### Community 115 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.29
Nodes (11): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+3 more)

### Community 116 - "dictionary.c"
Cohesion: 0.20
Nodes (23): MatchRecord, AppendPhonemes(), Translator, WORD_TAB, DecodePhonemes(), DollarRule(), GetVowelStress(), HashDictionary() (+15 more)

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.18
Nodes (10): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06, 9.0.0 - 2016-12-28 (+2 more)

### Community 119 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 120 - "ProjectStorage"
Cohesion: 0.19
Nodes (3): MainActivityTest, Context, ProjectStorage

### Community 121 - "SpeakNextClause"
Cohesion: 0.12
Nodes (16): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+8 more)

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

### Community 129 - "config.h"
Cohesion: 0.13
Nodes (12): common, errno, libgen, RGROUP, stdlib, rgroup_sorter(), LoadConfig(), ReadTonePoints() (+4 more)

### Community 130 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.31
Nodes (10): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+2 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "Building"
Cohesion: 0.20
Nodes (10): Building, Cross Compilation, Dependencies, eSpeak NG Feature Configuration, Extended Dictionary Configuration, Installing, Linux, Mac, BSD, LLVM Fuzzer Support (+2 more)

### Community 134 - "DictionaryScreen"
Cohesion: 0.12
Nodes (19): DictionaryScreenTest, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryMatch, DictionarySearchMode, ASSONANCE (+11 more)

### Community 135 - "Voice and Language Files"
Cohesion: 0.25
Nodes (8): gender, Identification Attributes, language, maintainer, Maintenance Attributes, name, status, Voice and Language Files

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "DictionaryInstaller.kt"
Cohesion: 0.13
Nodes (18): DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller, FilterInputStream, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK (+10 more)

### Community 139 - "spect.c"
Cohesion: 0.17
Nodes (15): ieee80, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength(), LoadFrame() (+7 more)

### Community 140 - "getopt.c"
Cohesion: 0.57
Nodes (6): getopt, gcd(), getopt(), getopt_internal(), getopt_long(), permute_args()

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "speech.h"
Cohesion: 0.40
Nodes (3): memcheck, msan_interface, osbyteorder

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "Voice"
Cohesion: 0.50
Nodes (3): Exception, Voice, VoiceData

### Community 148 - "Third-party software and dictionary data"
Cohesion: 0.50
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 149 - "DictionaryHeadword"
Cohesion: 0.20
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

### Community 150 - "Ghostwriter documentation"
Cohesion: 0.67
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 152 - "State"
Cohesion: 0.40
Nodes (5): State, DOWNLOAD_FAILED, ERROR, LOADING, SUCCESS

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 156 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 171 - "espeak-ng.c"
Cohesion: 0.24
Nodes (8): fcntl, espeak_EVENT, FILE, CloseWavFile(), DisplayVoices(), OpenWavFile(), SynthCallback(), Write4Bytes()

### Community 173 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 174 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Open and build, Requirements

### Community 175 - "Testing and coverage"
Cohesion: 0.67
Nodes (3): Coverage, Regular checks, Testing and coverage

## Knowledge Gaps
- **452 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+447 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 876 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BeatPlayer` connect `BeatPlayer` to `GhostwriterTheme`, `DictionaryScreen.kt`, `BeatPlayerPanel.kt`, `EditorScreen.kt`, `EditorScreenTest.kt`, `BeatPlayerInstrumentedTest`, `EditorScreen`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Why does `Phoneme Tables` connect `Phoneme Instructions` to `index.md`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `ucd_properties()` (e.g. with `clause_type_from_codepoint()` and `ucd_isalnum()`) actually correct?**
  _`ucd_properties()` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _452 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.10084033613445378 - nodes in this community are weakly interconnected._