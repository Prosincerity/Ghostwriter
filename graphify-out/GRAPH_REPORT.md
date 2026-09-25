# Graph Report - Gh0stwrit3r  (2026-09-25)

## Corpus Check
- 286 files · ~537,765 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2050 file(s) not represented in the graph (top: (none) 1892, .xml 77, .test 17)

## Summary
- 3113 nodes · 7825 edges · 181 communities (144 shown, 37 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 1210 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `8b7704ff`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- ImportVoicePreference.java
- Generate
- ProjectMetadata
- ProjectStorageTest
- WaveformViewport
- DictionaryScreen.kt
- BeatPlayerPanel.kt
- WaveformExtractor
- android.content.Context
- .writeText
- gradlew
- SeekBarPreference
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
- InterpretPhoneme
- EditorScreen
- TextToSpeechTestCase
- compiledata.c
- IpaSearchKeys
- Ghostwriter agent instructions
- espeak_api.c
- eSpeakNGWorker
- Android
- SettingsFormatTest.kt
- SpeechSynthesis
- espeak_ng
- tests/encoding.c
- speech.c
- spect.c
- main
- fifo.c
- wavegen.c
- demo.js
- eSpeakActivity
- Language Attributes
- ttsengine.cpp
- ContextWrapper
- ucd_properties
- mbrowrap.c
- EditorScreenTest.kt
- tests/readclause.c
- espeak_Initialize
- Phoneme Features and the International Phonetic Alphabet
- android.view.View
- GetSampleText
- ucd.h
- DictionaryInstallerInstrumentedTest
- Phoneme Model
- eSpeakService.c
- GhostwriterTheme
- TranslateWord3
- ucd_lookup_category
- FormantTransition2
- FrameManagerImpl
- klatt.c
- sPlayer.c
- Diacritics
- ProjectStorageBeatTest
- MainActivity.kt
- speechPlayer.cpp
- PronunciationSource
- espeak_ng_CompileDictionary
- TtsService
- Text to Phoneme Translation
- index.md
- SSML (Speech Synthesis Markup Language)
- .excludedWords
- Phoneme Tables
- ProcessSsmlTag
- Lookup
- speechWaveGenerator.cpp
- VoiceVariant
- Translator
- uprintf
- HomeScreen.kt
- intonation.c
- .isTtsLangCode
- .excludedWords
- Phoneme Instructions
- CodePoint
- Configuration Files
- tr_languages.c
- TtsService.java
- SetSpeed
- printdata.py
- ucd.py
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- test
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- Row
- CheckVoiceData
- emoji
- BeatPlayerInstrumentedTest
- espeak-ng.c
- espeak_ng_SetOutputHooks
- Resonator
- Change Log
- CheckVoiceDataTest
- ProjectStorage
- LoadVoice
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- SelectPhonemeTable
- documentation/README.md
- Third-party software and dictionary data
- TranslateClauseWithTerminator
- WaveformCache
- printucddata_cpp.cpp
- Vowels
- composable
- DictionaryScreen
- LoadLanguageOptions
- common
- EspeakIpaInstrumentedTest
- DictionaryInstaller.kt
- espeak_callback
- utf8_in
- Unicode Character Database Tools
- EspeakIpa
- eSpeak NG user guide
- ParallelFormantGenerator
- create_dict_corpus_file.py
- Translation fuzzers
- Feature Roadmap
- Ghostwriter documentation
- GetFileLength
- SynthCallback
- android/gradlew
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- Using eSpeak NG as a library
- espeak-ng
- Build setup
- Contribution Guide
- numbers.md
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- file
- Diacritics
- Phoneme Properties
- IgnoreOrReplaceChar
- Third-party software and dictionary data
- Testing and coverage
- espeakng_glue.cpp
- GetFrameRms
- rgroup_sorter
- espeak_SetPhonemeCallback
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

## Communities (181 total, 37 thin omitted)

### Community 0 - "BeatPlayer"
Cohesion: 0.13
Nodes (3): BeatPlayer, BeatPlayerTest, MediaPlayer

### Community 1 - "ImportVoicePreference.java"
Cohesion: 0.10
Nodes (21): accessibilityevent, android.os.AsyncTask, android.widget.ProgressBar, arrays, bufferedinputstream, bytearrayoutputstream, downloadmanager, environment (+13 more)

### Community 2 - "Generate"
Cohesion: 0.14
Nodes (29): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), FMT_PARAMS, PHONEME_DATA (+21 more)

### Community 3 - "ProjectMetadata"
Cohesion: 0.13
Nodes (8): ProjectInfoDialogTest, JSONObject, ProjectMetadata, WaveformMarker, ProjectInfoDialog(), trimmedOrNull(), ProjectMetadataTest, jsonarray

### Community 6 - "DictionaryScreen.kt"
Cohesion: 0.09
Nodes (52): alertdialog, alignment, ReassignBeatDialog(), arrowback, backhandler, box, check, column (+44 more)

### Community 7 - "BeatPlayerPanel.kt"
Cohesion: 0.12
Nodes (16): CenteredPlayerContent(), Modifier, WaveformZoomButton(), arrangement, button, card, columnscope, delay (+8 more)

### Community 8 - "WaveformExtractor"
Cohesion: 0.11
Nodes (7): WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, WaveformExtractorTest, MediaCodec, MediaFormat

### Community 9 - "android.content.Context"
Cohesion: 0.08
Nodes (29): adapterview, android.app.Application, android.content.Context, android.content.DialogInterface, android.content.SharedPreferences, android.os.Bundle, android.preference.DialogPreference, android.preference.Preference (+21 more)

### Community 10 - ".writeText"
Cohesion: 0.14
Nodes (3): ProjectLyricsStorage, IntArray, StagedFileWriter

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "SeekBarPreference"
Cohesion: 0.09
Nodes (9): android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage, Punctuation (+1 more)

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
Nodes (21): activityresultcontracts, displayNameFor(), Context, atomicboolean, atomiclong, book, context, coroutinestart (+13 more)

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

### Community 31 - "EditorScreen"
Cohesion: 0.27
Nodes (5): EditorScreenTest, IntArray, EditorScreen(), PendingBeatPreparation, MutableState

### Community 32 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (9): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception, Voice (+1 more)

### Community 33 - "compiledata.c"
Cohesion: 0.08
Nodes (77): phoneme_feature_t, PHONEME_TAB_LIST, FILE, Read4Bytes(), StringToWord(), CompileContext, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS (+69 more)

### Community 34 - "IpaSearchKeys"
Cohesion: 0.20
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "Ghostwriter agent instructions"
Cohesion: 0.33
Nodes (5): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints

### Community 36 - "espeak_api.c"
Cohesion: 0.16
Nodes (20): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, espeak_VOICE (+12 more)

### Community 37 - "eSpeakNGWorker"
Cohesion: 0.10
Nodes (18): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, PrintVersion() (+10 more)

### Community 38 - "Android"
Cohesion: 0.10
Nodes (20): Android, Building, Building, Building eSpeak NG, Building with Gradle, Cross Compilation, Dependencies, Dependencies (+12 more)

### Community 39 - "SettingsFormatTest.kt"
Cohesion: 0.22
Nodes (3): ExampleInstrumentedTest, formatInterval(), SettingsFormatTest

### Community 40 - "SpeechSynthesis"
Cohesion: 0.10
Nodes (7): jsonexception, jsonobject, org.json.JSONObject, SpeechSynthesisTest, VoiceSettingsTest, SpeechSynthesis, VoiceSettings

### Community 41 - "espeak_ng"
Cohesion: 0.12
Nodes (20): common, dirent, encoding, errno, espeak_ng, libgen, limits, memcheck (+12 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (50): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+42 more)

### Community 43 - "speech.c"
Cohesion: 0.09
Nodes (47): audio, io, espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char() (+39 more)

### Community 44 - "spect.c"
Cohesion: 0.15
Nodes (16): ieee80, osbyteorder, SpectFrame, SpectSeq, ieee_extended_to_double(), espeak_ng_STATUS, FILE, GetFrameLength() (+8 more)

### Community 45 - "main"
Cohesion: 0.15
Nodes (26): FILE, DisplayVoices(), main(), strncpy0(), espeak_VOICE, SetVoiceStack(), espeak_ng_ERROR_CONTEXT, espeak_ng_Initialize() (+18 more)

### Community 46 - "fifo.c"
Cohesion: 0.10
Nodes (36): espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, pthread, add_time_in_ms(), espeak_EVENT, espeak_ng_STATUS, clock_gettime2() (+28 more)

### Community 47 - "wavegen.c"
Cohesion: 0.13
Nodes (29): sonic, MarkerEvent(), MbrolaFill(), WritePitch(), AdvanceParameters(), ApplyBreath(), frame_t, voice_t (+21 more)

### Community 48 - "demo.js"
Cohesion: 0.06
Nodes (29): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+21 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (21): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter, java.lang.ref.WeakReference (+13 more)

### Community 50 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 51 - "ttsengine.cpp"
Cohesion: 0.10
Nodes (37): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+29 more)

### Community 52 - "ContextWrapper"
Cohesion: 0.40
Nodes (3): ContextWrapper, Context, ContextWrapper

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (27): poll, procfs, signal, stdarg, BOOL, close_mbrola(), close_pipes(), create_pipes() (+19 more)

### Community 55 - "EditorScreenTest.kt"
Cohesion: 0.17
Nodes (25): activitynotfoundexception, androidjunit4, assertisenabled, assertisnotenabled, asserttextcontains, createandroidcomposerule, createcomposerule, ghostwriter_repository_url (+17 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.12
Nodes (39): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), CheckPhonemeMode(), clause_type_from_codepoint(), Eof() (+31 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.17
Nodes (31): espeak_AUDIO_OUTPUT, espeak_ng_OUTPUT_MODE, espeak_POSITION_TYPE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_Synth(), fifo_init(), espeak_ng_InitializeOutput() (+23 more)

### Community 58 - "Phoneme Features and the International Phonetic Alphabet"
Cohesion: 0.10
Nodes (20): Backness, Consonants, Gemination, Height, Intonation, Length, Manner of Articulation, Other Symbols (+12 more)

### Community 59 - "android.view.View"
Cohesion: 0.13
Nodes (20): android.app.Activity, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter, android.widget.TextView, FileListAdapter (+12 more)

### Community 61 - "ucd.h"
Cohesion: 0.09
Nodes (33): category, property, script, stddef, 6.2.0 - 2013-10-16, codepoint_t, ucd_tolower(), ucd_totitle() (+25 more)

### Community 62 - "DictionaryInstallerInstrumentedTest"
Cohesion: 0.21
Nodes (7): DictionaryInstallerInstrumentedTest, ContextWrapper, ByteArray, Context, ContextWrapper, DictionaryArchiveSource, IpaGenerator

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "GhostwriterTheme"
Cohesion: 0.20
Nodes (7): BeatComponentsTest, LongBeatWarningDialog(), BeatPlaybackControls(), BeatPlayerPanel(), IntArray, formatPlaybackTime(), GhostwriterTheme()

### Community 66 - "TranslateWord3"
Cohesion: 0.16
Nodes (25): IsAlpha(), IsBracket(), GetVowelStress(), SetWordStress(), TranslateRules(), IsSuperscript(), SetSpellingStress(), AlphabetFromChar() (+17 more)

### Community 67 - "ucd_lookup_category"
Cohesion: 0.33
Nodes (9): category_group, codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), get_category_group_string() (+1 more)

### Community 68 - "FormantTransition2"
Cohesion: 0.38
Nodes (10): AdjustFormants(), AllocFrame(), frame_t, frameref_t, CopyFrame(), DuplicateLastFrame(), formants_reduce_hf(), FormantTransition2() (+2 more)

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.22
Nodes (20): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+12 more)

### Community 71 - "sPlayer.c"
Cohesion: 0.17
Nodes (19): KlattFini(), KlattInit(), KlattReset(), frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA (+11 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 74 - "MainActivity.kt"
Cohesion: 0.07
Nodes (30): AboutScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Bundle (+22 more)

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "PronunciationSource"
Cohesion: 0.11
Nodes (10): SQLiteDatabase, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryHeadword, sourceLabel() (+2 more)

### Community 77 - "espeak_ng_CompileDictionary"
Cohesion: 0.19
Nodes (17): isspace2(), CompileContext, ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, clean_context(), compile_dictlist_end() (+9 more)

### Community 78 - "TtsService"
Cohesion: 0.13
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, Override, SuppressWarnings, TtsService, Override, Voice

### Community 79 - "Text to Phoneme Translation"
Cohesion: 0.10
Nodes (21): Character Substitution, Conditional Rules, Flags, Letter groups, Letter names, Multiple Words, Numbers, Numbers and Character Names (+13 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - ".excludedWords"
Cohesion: 0.23
Nodes (5): AssonanceFormFilter, Entry, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 83 - "Phoneme Tables"
Cohesion: 0.22
Nodes (9): Attributes, Conditional Statements, Conditions, Customization of sound source files, Phoneme Definitions, Phoneme Files, Phoneme Tables, Sound Specifications (+1 more)

### Community 84 - "ProcessSsmlTag"
Cohesion: 0.23
Nodes (19): PARAM_STACK, SSML_STACK, AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8(), attrlookup(), attrnumber() (+11 more)

### Community 85 - "Lookup"
Cohesion: 0.29
Nodes (17): IsDigit09(), Lookup(), Translator, WORD_TAB, CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter() (+9 more)

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.17
Nodes (5): java.util.regex.Pattern, VoiceVariantTest, VariantData, Override, VoiceVariant

### Community 88 - "Translator"
Cohesion: 0.11
Nodes (28): MatchRecord, is_str_totally_null(), IsDigit(), utf8_in2(), AppendPhonemes(), Translator, WORD_TAB, DecodePhonemes() (+20 more)

### Community 89 - "uprintf"
Cohesion: 0.20
Nodes (20): codepoint_t, ucd_isalnum(), ucd_isgraph(), ucd_isprint(), ucd_ispunct(), ucd_isspace(), ucd_isxdigit(), isalnum() (+12 more)

### Community 90 - "HomeScreen.kt"
Cohesion: 0.15
Nodes (13): add, HomeScreenTest, HomeScreen(), NewProjectDialog(), RenameProjectDialog(), clickable, delete, imeaction (+5 more)

### Community 91 - "intonation.c"
Cohesion: 0.23
Nodes (19): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+11 more)

### Community 92 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 93 - ".excludedWords"
Cohesion: 0.33
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - "Configuration Files"
Cohesion: 0.12
Nodes (16): Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation, Dictionary Files (+8 more)

### Community 97 - "tr_languages.c"
Cohesion: 0.40
Nodes (15): (Re)definition of character groups, SetLengthMods(), Translator, NewTranslator(), ResetLetterBits(), SelectTranslator(), SetArabicLetters(), SetCyrillicLetters() (+7 more)

### Community 98 - "TtsService.java"
Cohesion: 0.10
Nodes (29): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, audiotrack, bundle, checksum (+21 more)

### Community 99 - "SetSpeed"
Cohesion: 0.18
Nodes (11): SPEED_FACTORS, Translator, voice_t, CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods() (+3 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 103 - "Tune Definitions"
Cohesion: 0.15
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 106 - "test"
Cohesion: 0.13
Nodes (15): shouldWarnBeforeWaveformExtraction(), parsePositiveBpm(), ProjectInfoBpmTest, WaveformWarningTest, assertequals, assertfalse, assertnotequals, assertnotnull (+7 more)

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

### Community 111 - "Row"
Cohesion: 0.52
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 112 - "CheckVoiceData"
Cohesion: 0.23
Nodes (3): CheckVoiceData, Override, SynthReadyCallback

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 115 - "espeak-ng.c"
Cohesion: 0.16
Nodes (13): assert, fcntl, getopt, gcd(), getopt(), getopt_internal(), permute_args(), espeak_EVENT (+5 more)

### Community 116 - "espeak_ng_SetOutputHooks"
Cohesion: 0.50
Nodes (4): espeak_ng_OUTPUT_HOOKS, ESPEAK_NG_API, espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks()

### Community 117 - "Resonator"
Cohesion: 0.17
Nodes (11): Resonator, a, anti, b, bandwidth, c, frequency, p1 (+3 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 10.0.0 - 2017-06-25, 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 120 - "ProjectStorage"
Cohesion: 0.22
Nodes (3): MainActivityTest, Context, ProjectStorage

### Community 121 - "LoadVoice"
Cohesion: 0.20
Nodes (15): LookupMnem(), ParseSsmlReference(), ReplaceKeyName(), espeak_ng_STATUS, LoadMbrolaTable(), system_data_dirs(), FILE, voice_t (+7 more)

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

### Community 126 - "SelectPhonemeTable"
Cohesion: 0.15
Nodes (19): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+11 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 129 - "TranslateClauseWithTerminator"
Cohesion: 0.17
Nodes (21): Translator, IsSpace(), towlower2(), MbrolaReset(), SpeakNextClause(), PHONEME_LIST2, Translator, WORD_TAB (+13 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "composable"
Cohesion: 0.11
Nodes (15): LyricsNotepadTest, WaveformMarkerDialog(), Modifier, LyricsNotepad(), EditorMarkerDialogs(), composable, darkcolorscheme, issystemindarktheme (+7 more)

### Community 134 - "DictionaryScreen"
Cohesion: 0.15
Nodes (13): DictionaryScreenTest, DictionaryMatch, DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, DictionarySearchResult (+5 more)

### Community 135 - "LoadLanguageOptions"
Cohesion: 0.16
Nodes (14): LANGUAGE_OPTIONS, DecodeRule(), print_dictionary_flags(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune() (+6 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "EspeakIpaInstrumentedTest"
Cohesion: 0.25
Nodes (6): EspeakIpaInstrumentedTest, API and verification, Clean checkout and build, eSpeak NG offline IPA fallback, Regenerate the packaged data, Source and licensing

### Community 138 - "DictionaryInstaller.kt"
Cohesion: 0.12
Nodes (19): DictionaryAsset, DictionaryDownloadProgress, DictionaryInstaller, FilterInputStream, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK (+11 more)

### Community 140 - "utf8_in"
Cohesion: 0.30
Nodes (12): utf8_in(), utf8_out(), compile_line(), compile_rule(), PHONEME_LIST, PHONEME_TAB, EncodePhonemes(), GetTranslatedPhonemeString() (+4 more)

### Community 141 - "Unicode Character Database Tools"
Cohesion: 0.25
Nodes (7): Bugs, Build Dependencies, Building, Debian, License Information, Unicode Character Database Tools, Updating the UCD Data

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "ParallelFormantGenerator"
Cohesion: 0.22
Nodes (8): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 147 - "Feature Roadmap"
Cohesion: 0.40
Nodes (5): Feature Roadmap, Guiding rules, Non-goals, Phase 1 — Barebones notepad (MVP), Phase 2 — Songwriting environment

### Community 148 - "Ghostwriter documentation"
Cohesion: 0.67
Nodes (3): Architecture, Current limits, Ghostwriter documentation

### Community 149 - "GetFileLength"
Cohesion: 0.21
Nodes (13): GetFileLength(), ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage() (+5 more)

### Community 151 - "android/gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 152 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 153 - "Using eSpeak NG as a library"
Cohesion: 0.50
Nodes (4): Additional info, Error solutions, Simple steps, Using eSpeak NG as a library

### Community 154 - "espeak-ng"
Cohesion: 0.67
Nodes (4): data, espeak-ng, installer, libespeak-ng

### Community 155 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Open and build, Requirements

### Community 156 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 170 - "file"
Cohesion: 0.17
Nodes (16): abs, after, asserttrue, audioformat, bytearrayinputstream, ByteBuffer, byteorder, cancellationexception (+8 more)

### Community 171 - "Diacritics"
Cohesion: 0.20
Nodes (10): Air Flow, Articulation, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Phonation, Rounding and Labialization (+2 more)

### Community 172 - "Phoneme Properties"
Cohesion: 0.20
Nodes (7): endtype, lengthmod, Phoneme Properties, Properties, starttype, Type, voicingswitch

### Community 173 - "IgnoreOrReplaceChar"
Cohesion: 0.33
Nodes (4): IgnoreOrReplaceChar(), lookupwchar2(), ESPEAK_API, espeak_SetUriCallback()

### Community 174 - "Third-party software and dictionary data"
Cohesion: 0.50
Nodes (4): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Third-party software and dictionary data

### Community 175 - "Testing and coverage"
Cohesion: 0.67
Nodes (3): Coverage, Regular checks, Testing and coverage

### Community 177 - "GetFrameRms"
Cohesion: 0.67
Nodes (3): GetFrameRms(), PeaksToHarmspect(), wavegen_peaks_t

## Knowledge Gaps
- **452 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+447 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 880 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **37 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BeatPlayer` connect `BeatPlayer` to `GhostwriterTheme`, `.load`, `DictionaryScreen.kt`, `BeatPlayerPanel.kt`, `Phoneme Properties`, `BeatPlayerInstrumentedTest`, `EditorScreen.kt`, `EditorScreenTest.kt`, `EditorScreen`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Why does `eSpeakActivity` connect `eSpeakActivity` to `TextToSpeechTestCase`, `android.content.Context`, `android.view.View`, `Android`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Why does `Phoneme Tables` connect `Phoneme Tables` to `index.md`, `Phoneme Properties`, `Phoneme Instructions`?**
  _High betweenness centrality (0.050) - this node is a cross-community bridge._
- **Are the 21 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 21 INFERRED edges - model-reasoned connections that need verification._
- **Are the 8 inferred relationships involving `ucd_properties()` (e.g. with `clause_type_from_codepoint()` and `ucd_isalnum()`) actually correct?**
  _`ucd_properties()` has 8 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _452 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.1339031339031339 - nodes in this community are weakly interconnected._