# Graph Report - Gh0stwrit3r  (2026-09-24)

## Corpus Check
- 282 files · ~534,703 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2050 file(s) not represented in the graph (top: (none) 1892, .xml 77, .test 17)

## Summary
- 3066 nodes · 7706 edges · 183 communities (142 shown, 41 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1194 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `933a26a9`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- DictionaryInstallerInstrumentedTest
- composable
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- DictionaryScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- MainActivity.kt
- .writeText
- gradlew
- WaveformWarningTest.kt
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
- DictionaryInstallerInstrumentedTest.kt
- Ghostwriter agent instructions
- espeak_ng
- Build setup
- Android
- SettingsFormatTest.kt
- VoiceSettingsTest
- tr_languages.c
- tests/encoding.c
- Synthesize
- synthesize.c
- LoadVoice
- speech.c
- wavegen.c
- demo.js
- eSpeakActivity
- Language Attributes
- TtsEngine
- file
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Diacritics
- VoiceVariantPreference.java
- android.content.Context
- ucd.h
- ProcessSsmlTag
- Phoneme Model
- eSpeakService.c
- utf8_in
- TranslateWord3
- SpeechSynthesis
- ImportVoicePreference.java
- FrameManagerImpl
- klatt.c
- espeak-ng.c
- Diacritics
- ProjectStorageBeatTest
- espeak_ng_CompilePhonemeDataPath
- speechPlayer.cpp
- TranslateClauseWithTerminator
- compile_line
- TtsService
- VoiceSettingsTest.java
- index.md
- SSML (Speech Synthesis Markup Language)
- TtsService.java
- synthdata.c
- TextToSpeechTestCase
- sPlayer.c
- speechWaveGenerator.cpp
- VoiceVariant
- ProjectLyricsStorage
- DictionaryInstaller
- SelectPhonemeTable
- intonation.c
- .isTtsLangCode
- espeak_api.c
- Phoneme Instructions
- CodePoint
- Lookup
- main
- GhostwriterTheme
- uprintf
- printdata.py
- ucd.py
- Voice
- Tune Definitions
- X-SAMPA Transcription Scheme
- SpeechWaveGeneratorImpl
- tolower
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- rgroup_sorter
- ucd_get_category_group_string
- emoji
- espeak_SetUriCallback
- utf8_out
- eSpeakNGWorker
- Resonator
- Change Log
- CheckVoiceDataTest.java
- ProjectStorage
- setlengths.c
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- BeatPlayerInstrumentedTest
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- Third-party software and dictionary data
- Diacritics
- espeak_ng_PrintStatusCodeMessage
- printucddata_cpp.cpp
- Vowels
- Phoneme Properties
- DictionaryScreen
- Building
- common
- EspeakIpaInstrumentedTest
- DictionaryInstaller.kt
- spect.c
- ContextWrapper
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- Phoneme Tables
- create_dict_corpus_file.py
- Translation fuzzers
- Feature Roadmap
- Third-party software and dictionary data
- DictionaryHeadword
- Ghostwriter documentation
- android/gradlew
- Testing and coverage
- Using eSpeak NG as a library
- espeak-ng
- espeakng_glue.cpp
- Contribution Guide
- numbers.md
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- ucd_lookup_category
- WaveformExtractorInstrumentedTest
- LoadSoundFile
- AnnouncePunctuation
- ssml-fuzzer.c
- espeak_ng_CompileMbrolaVoice
- DictionaryPronunciations.kt
- LookupMnemName
- phoneme_add_feature
- .searchesRhymeAssonanceAndWordsWithSourcePrecedence
- row
- ProjectInfoBpmTest.kt

## God Nodes (most connected - your core abstractions)
1. `SpeechSynthesis` - 49 edges
2. `BeatPlayer` - 48 edges
3. `ucd_properties()` - 38 edges
4. `GhostwriterTheme()` - 35 edges
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

## Communities (183 total, 41 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.13
Nodes (3): BeatPlayer, BeatPlayerTest, MediaPlayer

### Community 1 - "DictionaryInstallerInstrumentedTest"
Cohesion: 0.31
Nodes (5): DictionaryInstallerInstrumentedTest, ByteArray, Context, DictionaryArchiveSource, IpaGenerator

### Community 2 - "composable"
Cohesion: 0.11
Nodes (15): LyricsNotepadTest, WaveformMarkerDialog(), Modifier, LyricsNotepad(), EditorMarkerDialogs(), composable, darkcolorscheme, issystemindarktheme (+7 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.13
Nodes (8): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest, jsonarray

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.07
Nodes (67): add, alertdialog, alignment, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, ReassignBeatDialog() (+59 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.12
Nodes (16): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, button, card, columnscope, delay (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (10): abs, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, audioformat, ByteBuffer, MediaCodec (+2 more)

### Community 9 - "MainActivity.kt"
Cohesion: 0.08
Nodes (30): AboutScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle (+22 more)

### Community 10 - ".writeText"
Cohesion: 0.16
Nodes (4): IntArray, StagedFileWriter, IntArray, WaveformCache

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (34): IntArray, Modifier, WaveformView(), canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures (+26 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.25
Nodes (8): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 6. Implement rhyme queries and editor behavior, 7. Licensing, attribution, and release verification, Steps

### Community 20 - "EditorScreen.kt"
Cohesion: 0.11
Nodes (20): activityresultcontracts, displayNameFor(), Context, atomicboolean, atomiclong, book, context, coroutinestart (+12 more)

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
Cohesion: 0.09
Nodes (10): android.content.DialogInterface, android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage (+2 more)

### Community 31 - "EditorScreen"
Cohesion: 0.27
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 33 - "compiledata.c"
Cohesion: 0.17
Nodes (42): PHONEME_TAB_LIST, StringToWord(), CompileContext, PHONEME_TAB, CalculateSample(), CallPhoneme(), CheckNextChar(), CompileElif() (+34 more)

### Community 34 - "DictionaryInstallerInstrumentedTest.kt"
Cohesion: 0.21
Nodes (8): ContextWrapper, Context, ContextWrapper, bytearrayinputstream, gzipoutputstream, instrumentationregistry, runblocking, sqlitedatabase

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "espeak_ng"
Cohesion: 0.16
Nodes (13): dirent, encoding, errno, espeak_ng, limits, memcheck, msan_interface, speechplayer (+5 more)

### Community 37 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Open and build, Requirements

### Community 38 - "Android"
Cohesion: 0.20
Nodes (10): Android, Building, Building eSpeak NG, Building with Gradle, Dependencies, Dependencies, Installing, Opening project in Android Studio (+2 more)

### Community 41 - "tr_languages.c"
Cohesion: 0.05
Nodes (60): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+52 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (50): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+42 more)

### Community 43 - "Synthesize"
Cohesion: 0.10
Nodes (40): espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark() (+32 more)

### Community 44 - "synthesize.c"
Cohesion: 0.12
Nodes (42): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), AdjustFormants(), AllocFrame() (+34 more)

### Community 45 - "LoadVoice"
Cohesion: 0.09
Nodes (42): GetFileLength(), strncpy0(), LookupEnvelopeName(), LookupMnem(), espeak_VOICE, SetVoiceStack(), check_data_path(), attrcopy_utf8() (+34 more)

### Community 46 - "speech.c"
Cohesion: 0.06
Nodes (50): assert, audio, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, io, new, pthread (+42 more)

### Community 47 - "wavegen.c"
Cohesion: 0.08
Nodes (43): espeak_ng_OUTPUT_HOOKS, sonic, ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand(), MarkerEvent(), MbrolaFill() (+35 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (29): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+21 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (21): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter, java.lang.ref.WeakReference (+13 more)

### Community 50 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 51 - "TtsEngine"
Cohesion: 0.09
Nodes (34): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+26 more)

### Community 52 - "file"
Cohesion: 0.17
Nodes (17): after, assertfalse, assertnotnull, assertnull, asserttrue, byteorder, cancellationexception, countdownlatch (+9 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.14
Nodes (27): activitynotfoundexception, androidjunit4, ExampleInstrumentedTest, assertequals, assertisenabled, assertisnotenabled, assertnotequals, asserttextcontains (+19 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.14
Nodes (34): phoneme, readclause, speech, synthesize, CheckPhonemeMode(), clause_type_from_codepoint(), IsRomanU(), ReadClause() (+26 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.18
Nodes (27): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), espeak_EVENT (+19 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.13
Nodes (21): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView (+13 more)

### Community 60 - "android.content.Context"
Cohesion: 0.08
Nodes (24): android.app.Application, android.content.Context, android.os.Bundle, android.preference.DialogPreference, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment (+16 more)

### Community 61 - "ucd.h"
Cohesion: 0.11
Nodes (31): category, property, script, codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl() (+23 more)

### Community 62 - "ProcessSsmlTag"
Cohesion: 0.24
Nodes (18): PARAM_STACK, SSML_STACK, AddNameData(), attr_prosody_value(), attrcmp(), attrlookup(), attrnumber(), espeak_VOICE (+10 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "utf8_in"
Cohesion: 0.14
Nodes (24): MatchRecord, is_str_totally_null(), IsDigit(), utf8_in(), utf8_in2(), Translator, WORD_TAB, DollarRule() (+16 more)

### Community 66 - "TranslateWord3"
Cohesion: 0.15
Nodes (26): IsAlpha(), IsBracket(), AppendPhonemes(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), SetSpellingStress() (+18 more)

### Community 67 - "SpeechSynthesis"
Cohesion: 0.11
Nodes (4): CheckVoiceData, Override, SpeechSynthesis, SynthReadyCallback

### Community 68 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (22): accessibilityevent, android.os.AsyncTask, android.widget.ProgressBar, arrays, bufferedinputstream, bytearrayoutputstream, downloadmanager, engine (+14 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.20
Nodes (22): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+14 more)

### Community 71 - "espeak-ng.c"
Cohesion: 0.17
Nodes (13): fcntl, getopt, gcd(), getopt(), getopt_internal(), permute_args(), espeak_EVENT, FILE (+5 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "espeak_ng_CompilePhonemeDataPath"
Cohesion: 0.30
Nodes (15): espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), espeak_ng_CompileIntonation(), espeak_ng_CompileIntonationPath(), espeak_ng_CompilePhonemeData(), espeak_ng_CompilePhonemeDataPath() (+7 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.19
Nodes (10): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+2 more)

### Community 76 - "TranslateClauseWithTerminator"
Cohesion: 0.21
Nodes (18): Translator, IsSpace(), towlower2(), PHONEME_LIST2, Translator, WORD_TAB, CalcWordLength(), CombineFlag() (+10 more)

### Community 77 - "compile_line"
Cohesion: 0.18
Nodes (21): IsDigit09(), isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+13 more)

### Community 78 - "TtsService"
Cohesion: 0.18
Nodes (4): android.speech.tts.SynthesisRequest, Override, SuppressWarnings, TtsService

### Community 79 - "VoiceSettingsTest.java"
Cohesion: 0.32
Nodes (5): android.content.SharedPreferences, jsonexception, jsonobject, org.json.JSONObject, preferencemanager

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "TtsService.java"
Cohesion: 0.12
Nodes (24): android.speech.tts.SynthesisCallback, android.speech.tts.TextToSpeechService, android.test.AndroidTestCase, anyof, assertthat, audiotrack, bundle, checksum (+16 more)

### Community 83 - "synthdata.c"
Cohesion: 0.21
Nodes (19): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+11 more)

### Community 84 - "TextToSpeechTestCase"
Cohesion: 0.12
Nodes (12): android.annotation.SuppressLint, android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception (+4 more)

### Community 85 - "sPlayer.c"
Cohesion: 0.25
Nodes (14): frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP() (+6 more)

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.17
Nodes (5): java.util.regex.Pattern, VoiceVariantTest, VariantData, Override, VoiceVariant

### Community 89 - "DictionaryInstaller"
Cohesion: 0.26
Nodes (7): DictionaryDownloadProgress, DictionaryInstaller, FilterInputStream, DictionarySource, ESPEAK, WIKTIONARY, ByteArray

### Community 90 - "SelectPhonemeTable"
Cohesion: 0.16
Nodes (18): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+10 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - ".isTtsLangCode"
Cohesion: 0.19
Nodes (6): description, org.hamcrest.Matcher, Override, TextToSpeechServiceTest, TtsServiceTest, typesafematcher

### Community 93 - "espeak_api.c"
Cohesion: 0.17
Nodes (19): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, espeak_ng_ClearErrorContext(), ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER (+11 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - "Lookup"
Cohesion: 0.31
Nodes (16): Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter(), LookupLetter() (+8 more)

### Community 97 - "main"
Cohesion: 0.12
Nodes (23): espeak_ng_OUTPUT_MODE, main(), PrintVersion(), t_espeak_callback, event_set_callback(), ESPEAK_API, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT (+15 more)

### Community 98 - "GhostwriterTheme"
Cohesion: 0.14
Nodes (11): Row, BeatComponentsTest, HomeScreenTest, LongBeatWarningDialog(), BeatPlaybackControls(), BeatPlayerPanel(), IntArray, formatPlaybackTime() (+3 more)

### Community 99 - "uprintf"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "Voice"
Cohesion: 0.15
Nodes (3): SpeechSynthesisTest, Override, Voice

### Community 103 - "Tune Definitions"
Cohesion: 0.14
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "X-SAMPA Transcription Scheme"
Cohesion: 0.14
Nodes (14): Consonants, Intonation, Length, Manner of Articulation, Other Symbols, Other Symbols, Phoneme Transcription Schemes, References (+6 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.11
Nodes (15): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGenerator, setFrameManager, SpeechWaveGeneratorImpl, cascade (+7 more)

### Community 106 - "tolower"
Cohesion: 0.16
Nodes (12): stddef, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), ucd_isupper(), isupper(), tolower() (+4 more)

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

### Community 112 - "ucd_get_category_group_string"
Cohesion: 0.22
Nodes (8): category_group, get_category_group_string(), ucd_category, ucd_category_group, ucd_script, ucd_get_category_group_string(), ucd_get_category_string(), ucd_get_script_string()

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 115 - "utf8_out"
Cohesion: 0.60
Nodes (6): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress()

### Community 116 - "eSpeakNGWorker"
Cohesion: 0.22
Nodes (7): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices

### Community 117 - "Resonator"
Cohesion: 0.10
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.15
Nodes (12): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+4 more)

### Community 119 - "CheckVoiceDataTest.java"
Cohesion: 0.28
Nodes (4): android.test.ActivityUnitTestCase, arraylist, java.lang.reflect.Field, CheckVoiceDataTest

### Community 120 - "ProjectStorage"
Cohesion: 0.23
Nodes (3): MainActivityTest, Context, ProjectStorage

### Community 121 - "setlengths.c"
Cohesion: 0.21
Nodes (12): SPEED_FACTORS, espeak_ng_STATUS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetParameter(), SetSpeed() (+4 more)

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

### Community 127 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 129 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 130 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.43
Nodes (7): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage()

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "Phoneme Properties"
Cohesion: 0.20
Nodes (7): endtype, lengthmod, Phoneme Properties, Properties, starttype, Type, voicingswitch

### Community 134 - "DictionaryScreen"
Cohesion: 0.11
Nodes (18): DictionaryScreenTest, DictionaryMatch, DictionarySearch, DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX (+10 more)

### Community 135 - "Building"
Cohesion: 0.20
Nodes (10): Building, Cross Compilation, Dependencies, eSpeak NG Feature Configuration, Extended Dictionary Configuration, Installing, Linux, Mac, BSD, LLVM Fuzzer Support (+2 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "DictionaryInstaller.kt"
Cohesion: 0.20
Nodes (11): DictionaryAsset, DictionaryLanguage, DictionaryRelease, Context, JSONObject, SQLiteDatabase, coroutinecontext, ensureactive (+3 more)

### Community 139 - "spect.c"
Cohesion: 0.14
Nodes (17): ieee80, osbyteorder, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength() (+9 more)

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "Phoneme Tables"
Cohesion: 0.22
Nodes (9): Attributes, Conditional Statements, Conditions, Customization of sound source files, Phoneme Definitions, Phoneme Files, Phoneme Tables, Sound Specifications (+1 more)

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 148 - "Third-party software and dictionary data"
Cohesion: 0.50
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 149 - "DictionaryHeadword"
Cohesion: 0.19
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

### Community 150 - "Ghostwriter documentation"
Cohesion: 0.67
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 152 - "Testing and coverage"
Cohesion: 0.67
Nodes (3): Coverage, Regular checks, Testing and coverage

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 156 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 170 - "ucd_lookup_category"
Cohesion: 0.46
Nodes (7): codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), lookup_category_group()

### Community 172 - "LoadSoundFile"
Cohesion: 0.29
Nodes (7): FILE, Read4Bytes(), espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, LoadSoundFile(), LoadSoundFile2(), LookupSoundicon()

### Community 173 - "AnnouncePunctuation"
Cohesion: 0.20
Nodes (14): DecodePhonemes(), AnnouncePunctuation(), Translator, DecodeWithPhonemeMode(), Eof(), GetC(), IgnoreOrReplaceChar(), LookupCharName() (+6 more)

### Community 174 - "ssml-fuzzer.c"
Cohesion: 0.40
Nodes (3): libgen, espeak_EVENT, SynthCallback()

### Community 175 - "espeak_ng_CompileMbrolaVoice"
Cohesion: 0.29
Nodes (7): basename(), espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, espeak_ng_CompileMbrolaVoice(), FILE, Write4Bytes()

### Community 176 - "DictionaryPronunciations.kt"
Cohesion: 0.53
Nodes (3): DictionaryPronunciations, SQLiteDatabase, PronunciationResult

### Community 177 - "LookupMnemName"
Cohesion: 0.33
Nodes (6): DecodeRule(), print_dictionary_flags(), MNEM_TAB, LookupMnemName(), MNEM_TAB, ReadNumbers()

### Community 178 - "phoneme_add_feature"
Cohesion: 0.40
Nodes (5): phoneme_feature_t, espeak_ng_STATUS, PHONEME_TAB, phoneme_add_feature(), phoneme_feature_from_string()

## Knowledge Gaps
- **451 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+446 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 873 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **41 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BeatPlayer` connect `BeatPlayer` to `GhostwriterTheme`, `Phoneme Properties`, `DictionaryScreen.kt`, `BeatPlayerPanel.kt`, `EditorScreen.kt`, `.load`, `EditorScreenTest.kt`, `BeatPlayerInstrumentedTest`, `EditorScreen`?**
  _High betweenness centrality (0.095) - this node is a cross-community bridge._
- **Why does `Phoneme Properties` connect `Phoneme Properties` to `Phoneme Tables`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Why does `Phoneme Tables` connect `Phoneme Tables` to `index.md`, `Phoneme Properties`, `Phoneme Instructions`?**
  _High betweenness centrality (0.056) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `ucd_properties()` (e.g. with `clause_type_from_codepoint()` and `ucd_isalnum()`) actually correct?**
  _`ucd_properties()` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _451 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.1339031339031339 - nodes in this community are weakly interconnected._