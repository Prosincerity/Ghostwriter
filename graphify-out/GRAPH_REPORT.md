# Graph Report - Gh0stwrit3r  (2026-09-23)

## Corpus Check
- 272 files · ~524,189 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2049 file(s) not represented in the graph (top: (none) 1892, .xml 77, .test 17)

## Summary
- 2990 nodes · 7483 edges · 176 communities (137 shown, 39 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1187 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `899e25cf`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- DictionaryInstaller.kt
- LyricsNotepad.kt
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- HomeScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- MainActivity.kt
- .writeText
- gradlew
- file
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
- WaveformMarker
- EditorScreenTest
- documentation/README.md
- compiledata.c
- SeekBarPreference
- Ghostwriter agent instructions
- speech.c
- TESTING.md
- VoiceVariantPreference
- SettingsFormatTest
- SpeechSynthesis
- tr_languages.c
- tests/encoding.c
- espeak_command.c
- synthesize.c
- LoadVoice
- fifo.c
- wavegen.c
- demo.js
- eSpeakActivity
- Language Attributes
- ttsengine.cpp
- TtsService.java
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Phoneme Features and the International Phonetic Alphabet
- VoiceVariantPreference.java
- android.content.Context
- ucd.h
- GhostwriterTheme
- Phoneme Model
- eSpeakService.c
- utf8_in
- TranslateWord3
- CheckVoiceData
- ImportVoicePreference.java
- FrameManagerImpl
- parwave
- espeak-ng.c
- Diacritics
- ProjectStorageBeatTest
- main
- speechPlayer.cpp
- TranslateClauseWithTerminator
- compile_line
- TtsService
- WaveformExtractorTest
- index.md
- SSML (Speech Synthesis Markup Language)
- sPlayer.c
- synthdata.c
- TextToSpeechTestCase
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- speechWaveGenerator.cpp
- VoiceVariant
- Wavegen
- .createSeekBarPreference
- SelectPhonemeTable
- intonation.c
- .isTtsLangCode
- status_to_espeak_error
- Phoneme Instructions
- CodePoint
- Lookup
- ProjectStorage
- espeakng_glue.cpp
- ucd_lookup_category
- printdata.py
- ucd.py
- Voice
- Tune Definitions
- X-SAMPA Transcription Scheme
- SpeechWaveGeneratorImpl
- uprintf
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- rgroup_sorter
- ucd_get_category_group_string
- emoji
- espeak_SetUriCallback
- setlengths.c
- eSpeakNGWorker
- Resonator
- Change Log
- CheckVoiceDataTest
- MainActivityTest
- ucd_lookup_category_group
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- BeatPlayerInstrumentedTest
- Phoneme Properties
- Android
- CheckVoiceData.java
- espeak_ng_PrintStatusCodeMessage
- printucddata_cpp.cpp
- Vowels
- Phoneme Tables
- utf8_out
- ParallelFormantGenerator
- common
- EspeakIpaInstrumentedTest
- Diacritics
- math.h
- Diacritics
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- synth_fuzzer.c
- create_dict_corpus_file.py
- Translation fuzzers
- WaveformWarningTest.kt
- ProjectInfoBpmTest.kt
- WaveformCache
- android/gradlew
- GetSampleText
- Using eSpeak NG as a library
- espeak-ng
- ExampleInstrumentedTest.kt
- Contribution Guide
- numbers.md
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- KlattInit
- Feature Roadmap
- TtsMatcher.java
- espeak_ng_SetOutputHooks
- espeak_ng_SetRandSeed
- WcmdqStop

## God Nodes (most connected - your core abstractions)
1. `SpeechSynthesis` - 49 edges
2. `BeatPlayer` - 48 edges
3. `ucd_properties()` - 38 edges
4. `ProjectStorage` - 33 edges
5. `GhostwriterTheme()` - 31 edges
6. `create_text_decoder()` - 31 edges
7. `text_decoder_eof()` - 31 edges
8. `espeak_Initialize()` - 31 edges
9. `ProjectStorageTest` - 30 edges
10. `ReadClause()` - 30 edges

## Surprising Connections (you probably didn't know these)
- `API and verification` --references--> `EspeakIpaInstrumentedTest`  [INFERRED]
  documentation/ESPEAK_NATIVE.md → app/src/androidTest/java/com/prosincerity/ghostwriter/data/EspeakIpaInstrumentedTest.kt
- `Phase 2 — Songwriting environment` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `6. Implement rhyme queries and editor behavior` --references--> `LyricsNotepad()`  [INFERRED]
  documentation/RHYME_DETECTION_PLAN.md → app/src/main/java/com/prosincerity/ghostwriter/ui/components/LyricsNotepad.kt
- `Phase 2 — Songwriting environment` --references--> `EditorScreen()`  [INFERRED]
  documentation/FEATURES.md → app/src/main/java/com/prosincerity/ghostwriter/ui/screens/EditorScreen.kt
- `Installing` --references--> `eSpeakActivity`  [INFERRED]
  third_party/espeak-ng/docs/building.md → third_party/espeak-ng/android/src/com/reecedunn/espeak/eSpeakActivity.java

## Import Cycles
- None detected.

## Communities (176 total, 39 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.13
Nodes (3): BeatPlayer, BeatPlayerTest, MediaPlayer

### Community 1 - "DictionaryInstaller.kt"
Cohesion: 0.07
Nodes (27): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, DictionaryArchiveSource, DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller (+19 more)

### Community 2 - "LyricsNotepad.kt"
Cohesion: 0.18
Nodes (9): LyricsNotepadTest, Modifier, LyricsNotepad(), 6. Implement rhyme queries and editor behavior, keyboardcapitalization, keyboardoptions, text, textfield (+1 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.14
Nodes (7): ProjectInfoDialogTest, JSONObject, ProjectMetadata, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest, jsonarray

### Community 4 - "ProjectStorageTest"
Cohesion: 0.12
Nodes (3): EditorScreen(), PendingBeatPreparation, ProjectStorageTest

### Community 6 - "HomeScreen.kt"
Cohesion: 0.06
Nodes (66): add, alertdialog, alignment, ReassignBeatDialog(), DictionaryDownloads(), DictionaryDownloadsScreen(), Modifier, arrowback (+58 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.13
Nodes (15): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, card, columnscope, delay, fillmaxsize (+7 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.16
Nodes (9): abs, DecoderProgressGuard, IntArray, WaveformExtractor, audioformat, ByteBuffer, MediaCodec, mediaextractor (+1 more)

### Community 9 - "MainActivity.kt"
Cohesion: 0.07
Nodes (29): AboutScreenTest, HomeScreenTest, About, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle (+21 more)

### Community 10 - ".writeText"
Cohesion: 0.14
Nodes (3): ProjectLyricsStorage, IntArray, StagedFileWriter

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "file"
Cohesion: 0.16
Nodes (22): after, assertequals, assertfalse, assertnotnull, assertnull, asserttrue, bytearrayinputstream, byteorder (+14 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.06
Nodes (34): IntArray, Modifier, WaveformView(), canvas, ceil, cliptobounds, detectdraggestures, detecttapgestures (+26 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "Steps"
Cohesion: 0.22
Nodes (9): 1. Pin the release inputs, 2. Download, install, and read the SQLite databases, 3. Build a minimal eSpeak NG 1.52 Android library, 4. Add the IPA-only JNI wrapper and prove it on Android, 5. Join database lookup and runtime fallback, 7. Licensing, attribution, and release verification, Goal and data flow, Rhyme detection: dictionary and eSpeak NG integration plan (+1 more)

### Community 20 - "EditorScreen.kt"
Cohesion: 0.09
Nodes (26): activityresultcontracts, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, displayNameFor(), Context, atomicboolean (+18 more)

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

### Community 30 - "WaveformMarker"
Cohesion: 0.24
Nodes (8): WaveformMarker, WaveformMarkerDialog(), EditorMarkerDialogs(), composable, darkcolorscheme, issystemindarktheme, lightcolorscheme, materialtheme

### Community 31 - "EditorScreenTest"
Cohesion: 0.30
Nodes (3): EditorScreenTest, IntArray, MutableState

### Community 32 - "documentation/README.md"
Cohesion: 0.32
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 33 - "compiledata.c"
Cohesion: 0.07
Nodes (84): phoneme_feature_t, PHONEME_TAB_LIST, SpectFrame, SpectSeq, FILE, Read4Bytes(), StringToWord(), CompileContext (+76 more)

### Community 34 - "SeekBarPreference"
Cohesion: 0.12
Nodes (13): android.content.DialogInterface, android.preference.DialogPreference, android.widget.EditText, android.widget.RadioButton, android.widget.SeekBar, editable, OnSeekBarChangeListener, preferencemanager (+5 more)

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "speech.c"
Cohesion: 0.14
Nodes (19): audio, common, dirent, encoding, errno, espeak_ng, io, limits (+11 more)

### Community 37 - "TESTING.md"
Cohesion: 0.25
Nodes (6): Build setup, Open and build, Requirements, Coverage, Regular checks, Testing and coverage

### Community 38 - "VoiceVariantPreference"
Cohesion: 0.23
Nodes (5): Override, VariantData, VariantDataListAdapter, ViewHolder, VoiceVariantPreference

### Community 40 - "SpeechSynthesis"
Cohesion: 0.10
Nodes (8): android.content.SharedPreferences, jsonexception, jsonobject, org.json.JSONObject, VoiceSettingsTest, SpeechSynthesis, SynthReadyCallback, VoiceSettings

### Community 41 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.20
Nodes (49): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+41 more)

### Community 43 - "espeak_command.c"
Cohesion: 0.08
Nodes (51): espeak_Char(), espeak_Key(), espeak_Synth_Mark(), espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t (+43 more)

### Community 44 - "synthesize.c"
Cohesion: 0.11
Nodes (43): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), WritePitch(), AdjustFormants() (+35 more)

### Community 45 - "LoadVoice"
Cohesion: 0.07
Nodes (63): PARAM_STACK, SSML_STACK, GetFileLength(), strncpy0(), LookupMnem(), AddNameData(), espeak_VOICE, SetVoiceStack() (+55 more)

### Community 46 - "fifo.c"
Cohesion: 0.11
Nodes (28): espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, pthread, add_time_in_ms(), espeak_EVENT, espeak_ng_STATUS, clock_gettime2() (+20 more)

### Community 47 - "wavegen.c"
Cohesion: 0.19
Nodes (19): sonic, MarkerEvent(), MbrolaFill(), GetAmplitude(), PlaySilence(), PlayWave(), SetAmplitude(), SetEmbedded() (+11 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (28): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10, 1.49.1 - 2017-01-21 (+20 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (21): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter, java.lang.ref.WeakReference (+13 more)

### Community 50 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.09
Nodes (36): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+28 more)

### Community 52 - "TtsService.java"
Cohesion: 0.12
Nodes (23): android.speech.tts.SynthesisCallback, android.speech.tts.TextToSpeechService, android.test.AndroidTestCase, anyof, assertthat, audiotrack, bundle, checksum (+15 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.17
Nodes (24): activitynotfoundexception, androidjunit4, assertisenabled, assertisnotenabled, asserttextcontains, createandroidcomposerule, createcomposerule, ghostwriter_repository_url (+16 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.13
Nodes (38): readclause, AnnouncePunctuation(), Translator, CheckPhonemeMode(), clause_type_from_codepoint(), DecodeWithPhonemeMode(), GetC(), IgnoreOrReplaceChar() (+30 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.14
Nodes (33): espeak_AUDIO_OUTPUT, phoneme, speech, synthesize, ESPEAK_API, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize() (+25 more)

### Community 58 - "Phoneme Features and the International Phonetic Alphabet"
Cohesion: 0.10
Nodes (20): Backness, Consonants, Gemination, Height, Intonation, Length, Manner of Articulation, Other Symbols (+12 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.19
Nodes (17): adapterview, android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView (+9 more)

### Community 60 - "android.content.Context"
Cohesion: 0.14
Nodes (15): android.app.Application, android.content.Context, android.os.Bundle, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity, android.preference.PreferenceFragment, android.preference.PreferenceGroup (+7 more)

### Community 61 - "ucd.h"
Cohesion: 0.12
Nodes (28): category, property, MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune(), MNEM_TAB (+20 more)

### Community 62 - "GhostwriterTheme"
Cohesion: 0.20
Nodes (7): BeatComponentsTest, LongBeatWarningDialog(), BeatPlaybackControls(), BeatPlayerPanel(), IntArray, formatPlaybackTime(), GhostwriterTheme()

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "utf8_in"
Cohesion: 0.14
Nodes (25): MatchRecord, IsDigit(), utf8_in(), utf8_in2(), DecodeRule(), print_dictionary_flags(), Translator, WORD_TAB (+17 more)

### Community 66 - "TranslateWord3"
Cohesion: 0.17
Nodes (25): IsAlpha(), IsBracket(), AppendPhonemes(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), SetSpellingStress() (+17 more)

### Community 67 - "CheckVoiceData"
Cohesion: 0.15
Nodes (8): android.widget.ProgressBar, CheckVoiceData, Override, AsyncExtract, DownloadVoiceData, ExtractProgress, Override, FileUtils

### Community 68 - "ImportVoicePreference.java"
Cohesion: 0.11
Nodes (17): accessibilityevent, android.os.AsyncTask, android.widget.Spinner, arrays, bufferedinputstream, bytearrayoutputstream, downloadmanager, environment (+9 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "parwave"
Cohesion: 0.16
Nodes (21): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+13 more)

### Community 71 - "espeak-ng.c"
Cohesion: 0.16
Nodes (14): assert, fcntl, getopt, gcd(), getopt(), getopt_internal(), permute_args(), espeak_EVENT (+6 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "main"
Cohesion: 0.12
Nodes (23): espeak_ng_OUTPUT_MODE, main(), espeak_PARAMETER, espeak_SetParameter(), t_espeak_callback, event_set_callback(), fifo_init(), ESPEAK_NG_API (+15 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "TranslateClauseWithTerminator"
Cohesion: 0.12
Nodes (28): Translator, is_str_totally_null(), IsSpace(), towlower2(), InitGroups(), LoadDictionary(), Reverse4Bytes(), Eof() (+20 more)

### Community 77 - "compile_line"
Cohesion: 0.15
Nodes (23): IsDigit09(), isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context() (+15 more)

### Community 78 - "TtsService"
Cohesion: 0.18
Nodes (4): android.speech.tts.SynthesisRequest, Override, SuppressWarnings, TtsService

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "sPlayer.c"
Cohesion: 0.32
Nodes (11): frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame(), isKlattFrameFollowing(), MIN() (+3 more)

### Community 83 - "synthdata.c"
Cohesion: 0.21
Nodes (19): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+11 more)

### Community 84 - "TextToSpeechTestCase"
Cohesion: 0.21
Nodes (7): android.annotation.SuppressLint, android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase

### Community 85 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.15
Nodes (18): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+10 more)

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.22
Nodes (4): java.util.regex.Pattern, VoiceVariantTest, Override, VoiceVariant

### Community 88 - "Wavegen"
Cohesion: 0.19
Nodes (13): GetFrameRms(), AdvanceParameters(), ApplyBreath(), frame_t, voice_t, InitBreath(), PeaksToHarmspect(), resonator() (+5 more)

### Community 89 - ".createSeekBarPreference"
Cohesion: 0.15
Nodes (5): Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 90 - "SelectPhonemeTable"
Cohesion: 0.20
Nodes (15): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+7 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (16): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+8 more)

### Community 92 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 93 - "status_to_espeak_error"
Cohesion: 0.18
Nodes (11): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, 1.47.12 - 2013-10-12, espeak_ng_STATUS, wchar_t, espeak_SetPunctuationList() (+3 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - "Lookup"
Cohesion: 0.31
Nodes (16): Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter(), LookupLetter() (+8 more)

### Community 97 - "ProjectStorage"
Cohesion: 0.26
Nodes (3): Context, ProjectStorage, ioexception

### Community 99 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (15): ucd_lookup_category(), codepoint_t, ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph() (+7 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "Voice"
Cohesion: 0.24
Nodes (3): SpeechSynthesisTest, Override, Voice

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
Cohesion: 0.33
Nodes (12): codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), codepoint_t, FILE, fget_utf8c(), fput_utf8c() (+4 more)

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
Cohesion: 0.17
Nodes (11): category_group, script, get_category_group_string(), get_category_string(), get_script_string(), ucd_category, ucd_category_group, ucd_script (+3 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 115 - "setlengths.c"
Cohesion: 0.24
Nodes (10): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+2 more)

### Community 116 - "eSpeakNGWorker"
Cohesion: 0.22
Nodes (7): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 121 - "ucd_lookup_category_group"
Cohesion: 0.21
Nodes (10): stddef, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category_group(), lookup_category_group(), codepoint_t (+2 more)

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

### Community 127 - "Phoneme Properties"
Cohesion: 0.20
Nodes (7): endtype, lengthmod, Phoneme Properties, Properties, starttype, Type, voicingswitch

### Community 128 - "Android"
Cohesion: 0.10
Nodes (20): Android, Building, Building, Building eSpeak NG, Building with Gradle, Cross Compilation, Dependencies, Dependencies (+12 more)

### Community 129 - "CheckVoiceData.java"
Cohesion: 0.22
Nodes (7): arraylist, engine, intent, list, Exception, Voice, VoiceData

### Community 130 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.31
Nodes (10): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+2 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "Phoneme Tables"
Cohesion: 0.22
Nodes (9): Attributes, Conditional Statements, Conditions, Customization of sound source files, Phoneme Definitions, Phoneme Files, Phoneme Tables, Sound Specifications (+1 more)

### Community 134 - "utf8_out"
Cohesion: 0.60
Nodes (6): utf8_out(), PHONEME_LIST, PHONEME_TAB, GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress()

### Community 135 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "Diacritics"
Cohesion: 0.20
Nodes (10): Air Flow, Articulation, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Phonation, Rounding and Labialization (+2 more)

### Community 139 - "math.h"
Cohesion: 0.29
Nodes (3): ieee80, ieee_extended_to_double(), main()

### Community 140 - "Diacritics"
Cohesion: 0.25
Nodes (8): Articulation, Co-articulation, Consonant Release, Diacritics, Phonation, Rounding and Labialization, Syllabicity, Tongue Root

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "synth_fuzzer.c"
Cohesion: 0.20
Nodes (6): libgen, espeak_EVENT, espeak_callback(), espeak_EVENT, SynthCallback(), time

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

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

### Community 170 - "KlattInit"
Cohesion: 0.29
Nodes (7): KlattFini(), KlattInit(), KlattFiniSP(), KlattInitSP(), KlattResetSP(), WavegenFini(), WavegenInit()

### Community 171 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 172 - "TtsMatcher.java"
Cohesion: 0.50
Nodes (3): description, org.hamcrest.Matcher, typesafematcher

### Community 173 - "espeak_ng_SetOutputHooks"
Cohesion: 0.50
Nodes (4): espeak_ng_OUTPUT_HOOKS, ESPEAK_NG_API, espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks()

### Community 174 - "espeak_ng_SetRandSeed"
Cohesion: 0.50
Nodes (4): ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_rand(), espeak_srand()

## Knowledge Gaps
- **441 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+436 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 860 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **39 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BeatPlayer` connect `BeatPlayer` to `ProjectStorageTest`, `HomeScreen.kt`, `BeatPlayerPanel.kt`, `.load`, `EditorScreen.kt`, `EditorScreenTest.kt`, `BeatPlayerInstrumentedTest`, `GhostwriterTheme`, `Phoneme Properties`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `ucd_properties()` (e.g. with `clause_type_from_codepoint()` and `ucd_isalnum()`) actually correct?**
  _`ucd_properties()` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _441 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.1339031339031339 - nodes in this community are weakly interconnected._
- **Should `DictionaryInstaller.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.070578231292517 - nodes in this community are weakly interconnected._