# Graph Report - Gh0stwrit3r  (2026-09-30)

## Corpus Check
- 333 files · ~557,306 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2058 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 3686 nodes · 9368 edges · 191 communities (148 shown, 43 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1358 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2c5b1e92`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- .configure
- Generate
- ProjectStorage
- ProjectStorageTest
- WaveformViewport
- SettingsFormatTest.kt
- ProjectStorageBeatTest
- WaveformExtractor
- SystemFontFile
- VoiceSettingsTest
- gradlew
- ucd_lookup_category
- WaveformView.kt
- What You Must Do When Invoked
- android.content.Context
- fifo.c
- BeatPlayer.kt
- graphify reference: extra exports and benchmark
- synthesize.c
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- Android
- EditorScreen
- AsyncExtract
- compiledata.c
- .fromIpa
- PcmRingBuffer
- InterpretPhoneme
- DictionaryInstaller.kt
- TextToSpeechTestCase
- WaveformMarker
- SpeechSynthesis
- speech.c
- tests/encoding.c
- t_espeak_command
- TestConnection
- GhostwriterApp
- CloseWavFile
- wavegen.c
- demo.js
- eSpeakActivity
- .excludedWords
- TtsEngine
- LyricTextStyleTest.kt
- ucd_properties
- mbrowrap.c
- GhostwriterTheme
- tests/readclause.c
- espeak_Initialize
- Diacritics
- ImportVoicePreference.java
- tr_languages.c
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- Synthesize
- Phoneme Model
- eSpeakService.c
- EditorScreen.kt
- Language Attributes
- CheckVoiceDataTest
- PlaybackFocusState
- FrameManagerImpl
- klatt.c
- LyricTextSettings
- Diacritics
- Phoneme Tables
- PronunciationResult
- speechPlayer.cpp
- TtsService
- DownloadVoiceData.java
- .isTtsLangCode
- DictionarySearch.kt
- index.md
- SSML (Speech Synthesis Markup Language)
- ContextWrapper
- DictionarySearch
- numbers.md
- .parse
- speechWaveGenerator.cpp
- VoiceVariant
- main
- sPlayer.c
- espeak_api.c
- intonation.c
- PlaybackNotificationUpdater
- dictionary.c
- Phoneme Instructions
- CodePoint
- SeekBarPreference
- LoadLanguageOptions
- TtsService.java
- withDictionaryTestContext
- printdata.py
- ucd.py
- ucd.h
- Tune Definitions
- Diacritics
- SpeechWaveGeneratorImpl
- BeatPlaybackService
- comentrypoints.c
- espeakng.js
- utils.h
- uprintf
- BeatPlayerInstrumentedTest
- SettingsScreen.kt
- emoji
- BeatPlayerPanel.kt
- MarkerLoopRole
- uprintf
- Resonator
- Change Log
- PcmSource
- MainActivityTest
- DictionaryHeadword
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- test
- SpeakNextClause
- Third-party software and dictionary data
- WaveformCache
- SelectPhonemeTable
- printucddata_cpp.cpp
- Vowels
- unpackDictionaryArchive
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- BeatPlayerPanel
- common
- ttsengine.cpp
- ssml.c
- AssonanceData
- .excludedWords
- SettingsScreen
- .setMarkers
- eSpeak NG user guide
- ContextWrapper
- create_dict_corpus_file.py
- PcmLoopRenderer
- MainActivity.kt
- ESPEAK_API
- translate.c
- eSpeakNGWorker
- android/gradlew
- EspeakIpa
- Using eSpeak NG as a library
- espeak-ng
- Q: Does dictionary assonance search put exact matches first and how do shorter vowel tiers paginate?
- DictionaryScreen
- .writeText
- minimize-corpus.sh
- generate_espeak_data.sh
- remove_string.sh
- espeak-ng/autogen.sh
- ucd-tools/autogen.sh
- InputStream
- ProjectInfoDialog
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- Translation fuzzers
- SmoothLoopPlayback
- Contribution Guide
- BeatPlaybackServiceTest
- WaveformWarningTest.kt
- DictionarySearchMode
- ProjectInfoBpmTest.kt
- row
- PlaybackFrameLedger
- espeak_ng_SetOutputHooks
- httpurlconnection
- espeak_callback
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- espeak_ng_PrintStatusCodeMessage
- documentation/README.md
- SynthCallback

## God Nodes (most connected - your core abstractions)
1. `BeatPlayer` - 60 edges
2. `GhostwriterTheme()` - 51 edges
3. `SpeechSynthesis` - 49 edges
4. `WaveformMarker` - 40 edges
5. `ucd_properties()` - 38 edges
6. `ProjectStorage` - 33 edges
7. `BeatPlaybackService` - 33 edges
8. `LyricTextSettings` - 32 edges
9. `create_text_decoder()` - 31 edges
10. `text_decoder_eof()` - 31 edges

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

## Communities (191 total, 43 thin omitted)

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "Generate"
Cohesion: 0.17
Nodes (24): FILE, PHONEME_LIST, PHONEME_TAB, GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), WritePitch(), PHONEME_DATA (+16 more)

### Community 6 - "SettingsFormatTest.kt"
Cohesion: 0.25
Nodes (4): formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 8 - "WaveformExtractor"
Cohesion: 0.07
Nodes (17): abs, WaveformExtractorInstrumentedTest, DecoderProgressGuard, IntArray, WaveformExtractor, PcmSources, WaveformExtractorTest, atomicboolean (+9 more)

### Community 9 - "SystemFontFile"
Cohesion: 0.13
Nodes (14): SystemFontCatalogTest, LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), SystemFontCatalog, LyricFontTest, assertnotequals (+6 more)

### Community 11 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 12 - "ucd_lookup_category"
Cohesion: 0.22
Nodes (20): codepoint_t, ucd_category, ucd_category_group, ucd_get_category_group_for_category(), ucd_lookup_category(), ucd_lookup_category_group(), codepoint_t, ucd_isalnum() (+12 more)

### Community 14 - "WaveformView.kt"
Cohesion: 0.08
Nodes (25): ceil, cliptobounds, detectdraggestures, detecttapgestures, detecttransformgestures, drawtext, fillmaxheight, floor (+17 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "android.content.Context"
Cohesion: 0.08
Nodes (26): android.app.Application, android.content.Context, android.content.SharedPreferences, android.os.Bundle, android.preference.DialogPreference, android.preference.Preference, android.preference.Preference.OnPreferenceChangeListener, android.preference.PreferenceActivity (+18 more)

### Community 19 - "fifo.c"
Cohesion: 0.08
Nodes (44): assert, espeak_ng_Cancel, espeak_ng_Synchronize, espeak_ng_Terminate, getopt, pthread, gcd(), getopt() (+36 more)

### Community 20 - "BeatPlayer.kt"
Cohesion: 0.20
Nodes (10): audioattributes, audiotrack, Handler, looper, MediaPlayer, mutableintstateof, PowerManager, process (+2 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "synthesize.c"
Cohesion: 0.20
Nodes (19): AdjustFormants(), AllocFrame(), ESPEAK_API, FMT_PARAMS, frame_t, frameref_t, PHONEME_LIST, PHONEME_TAB (+11 more)

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
Cohesion: 0.33
Nodes (4): EditorScreenTest, IntArray, EditorScreen(), MutableState

### Community 32 - "AsyncExtract"
Cohesion: 0.21
Nodes (6): android.widget.ProgressBar, AsyncExtract, DownloadVoiceData, ExtractProgress, Override, FileUtils

### Community 33 - "compiledata.c"
Cohesion: 0.06
Nodes (89): phoneme_feature_t, PHONEME_TAB_LIST, SpectFrame, SpectSeq, FILE, Read4Bytes(), StringToWord(), CompileContext (+81 more)

### Community 34 - ".fromIpa"
Cohesion: 0.14
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "PcmRingBuffer"
Cohesion: 0.17
Nodes (8): PcmBeat, ShortArray, Loop, Page, PcmRingBuffer, Region, ShortArray, PcmRingBufferTest

### Community 36 - "InterpretPhoneme"
Cohesion: 0.20
Nodes (17): FMT_PARAMS, frameref_t, PHONEME_DATA, PHONEME_LIST, PHONEME_TAB, Translator, WORD_PH_DATA, CountVowelPosition() (+9 more)

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.16
Nodes (12): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY, Context (+4 more)

### Community 38 - "TextToSpeechTestCase"
Cohesion: 0.16
Nodes (9): android.speech.tts.TextToSpeech, android.speech.tts.TextToSpeech.OnInitListener, TextToSpeechTest, Override, SuppressWarnings, TextToSpeechTestCase, Exception, Voice (+1 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.21
Nodes (6): WaveformMarker, MarkerLoopRange, replaceLoopMarker(), WaveformMarkerDialog(), EditorMarkerDialogs(), MarkerLoopTest

### Community 40 - "SpeechSynthesis"
Cohesion: 0.07
Nodes (7): SpeechSynthesisTest, CheckVoiceData, Override, GetSampleText, Override, SpeechSynthesis, SynthReadyCallback

### Community 41 - "speech.c"
Cohesion: 0.09
Nodes (22): audio, dirent, emscripten, encoding, errno, espeak_ng, fcntl, glue (+14 more)

### Community 42 - "tests/encoding.c"
Cohesion: 0.19
Nodes (50): espeak_ng_ENCODING, espeak_ng_TEXT_DECODER, espeak_ng_STATUS, wchar_t, create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc() (+42 more)

### Community 43 - "t_espeak_command"
Cohesion: 0.10
Nodes (32): espeak_PARAMETER, espeak_POSITION_TYPE, espeak_VOICE, t_espeak_command, wchar_t, create_espeak_char(), create_espeak_key(), create_espeak_mark() (+24 more)

### Community 44 - "TestConnection"
Cohesion: 0.11
Nodes (12): FilterInputStream, HttpURLConnection, openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream (+4 more)

### Community 45 - "GhostwriterApp"
Cohesion: 0.17
Nodes (12): HomeScreenTest, About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, Screen (+4 more)

### Community 46 - "CloseWavFile"
Cohesion: 0.40
Nodes (6): espeak_EVENT, FILE, CloseWavFile(), OpenWavFile(), SynthCallback(), Write4Bytes()

### Community 47 - "wavegen.c"
Cohesion: 0.11
Nodes (34): sonic, espeak_rand(), GetFrameRms(), MarkerEvent(), MbrolaFill(), AdvanceParameters(), ApplyBreath(), frame_t (+26 more)

### Community 48 - "demo.js"
Cohesion: 0.07
Nodes (27): 1.43.46 - 2010-06-28, 1.46.11 - 2011-12-31, 1.46.23 - 2012-09-11, 1.47.12 - 2013-10-12, 1.47.13 - 2013-10-22, 1.47.14 - 2013-12-03, 1.48.11 - 2014-08-31, 1.49.0 - 2016-09-10 (+19 more)

### Community 49 - "eSpeakActivity"
Cohesion: 0.09
Nodes (21): android.content.BroadcastReceiver, android.content.Intent, android.os.Handler, android.os.Message, android.view.Menu, android.view.MenuItem, intentfilter, java.lang.ref.WeakReference (+13 more)

### Community 50 - ".excludedWords"
Cohesion: 0.15
Nodes (7): AssonanceFormFilter, Entry, IntArray, PrefixNode, Pronounced, AssonanceFormFilterTest, Pronounced

### Community 51 - "TtsEngine"
Cohesion: 0.10
Nodes (30): DWORD, GUID, ISpObjectToken, ISpObjectWithToken, ISpTTSEngine, ISpTTSEngineSite, IUnknown, LPCWSTR (+22 more)

### Community 52 - "LyricTextStyleTest.kt"
Cohesion: 0.10
Nodes (25): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+17 more)

### Community 53 - "ucd_properties"
Cohesion: 0.22
Nodes (32): codepoint_t, ucd_category, properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo() (+24 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.12
Nodes (25): poll, procfs, signal, stdarg, close_mbrola(), close_pipes(), create_pipes(), err() (+17 more)

### Community 55 - "GhostwriterTheme"
Cohesion: 0.07
Nodes (57): activitynotfoundexception, androidjunit4, ExampleInstrumentedTest, AboutScreenTest, GhostwriterTheme(), assertisenabled, assertisnotenabled, asserttextcontains (+49 more)

### Community 56 - "tests/readclause.c"
Cohesion: 0.11
Nodes (44): phoneme, readclause, speech, synthesize, AnnouncePunctuation(), Translator, CheckPhonemeMode(), clause_type_from_codepoint() (+36 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.14
Nodes (33): espeak_AUDIO_OUTPUT, espeak_POSITION_TYPE, espeak_VOICE, espeak_Initialize(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), t_espeak_callback (+25 more)

### Community 58 - "Diacritics"
Cohesion: 0.07
Nodes (30): Air Flow, Articulation, Backness, Co-articulation, Consonant Release, Consonants, Diacritics, Fortis and Lenis (+22 more)

### Community 59 - "ImportVoicePreference.java"
Cohesion: 0.09
Nodes (30): adapterview, android.app.Activity, android.os.AsyncTask, android.util.Pair, android.view.LayoutInflater, android.view.View, android.view.ViewGroup, android.widget.ArrayAdapter (+22 more)

### Community 60 - "tr_languages.c"
Cohesion: 0.06
Nodes (54): LANGUAGE_OPTIONS, Accent (optional), Adding or Improving a Language, Adding tests, Building Phonemes, Compiling Rules File for Debugging, Configuration Files, Considerations Before Preparation (+46 more)

### Community 61 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (10): jint, JNICALL, JNIEnv, jobject, copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(), jbyteArray (+2 more)

### Community 62 - "Synthesize"
Cohesion: 0.16
Nodes (19): SPEED_FACTORS, process_espeak_command(), InitNamedata(), InitText2(), espeak_ng_STATUS, voice_t, SetParameter(), SetSpeed() (+11 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (26): JavaVM, jboolean, jclass, jstring, espeak_EVENT, jint, JNICALL, JNIEnv (+18 more)

### Community 65 - "EditorScreen.kt"
Cohesion: 0.09
Nodes (23): activityresultcontracts, ComponentName, IBinder, ServiceConnection, rememberBeatPlayback(), ServiceConnection, LongBeatWarningDialog(), ReassignBeatDialog() (+15 more)

### Community 66 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 67 - "CheckVoiceDataTest"
Cohesion: 0.33
Nodes (3): android.test.ActivityUnitTestCase, java.lang.reflect.Field, CheckVoiceDataTest

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (18): cstring, queue, speechPlayer_frame_t, FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex (+10 more)

### Community 70 - "klatt.c"
Cohesion: 0.20
Nodes (22): klatt_frame_ptr, resonator_ptr, antiresonator(), frame_t, voice_t, WGEN_DATA, DBtoLIN(), flutter() (+14 more)

### Community 71 - "LyricTextSettings"
Cohesion: 0.12
Nodes (9): LyricsNotepadTest, LyricTextSettings, Context, Settings, Modifier, LyricsNotepad(), LyricTextSettingsTest, LyricTextStyleTest (+1 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - "Phoneme Tables"
Cohesion: 0.12
Nodes (16): Attributes, Conditional Statements, Conditions, Customization of sound source files, endtype, lengthmod, Phoneme Definitions, Phoneme Files (+8 more)

### Community 74 - "PronunciationResult"
Cohesion: 0.14
Nodes (5): SQLiteDatabase, PronunciationResult, DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (14): speechPlayer_handle_t, create, sample, speechPlayer_frame_t, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize() (+6 more)

### Community 76 - "TtsService"
Cohesion: 0.14
Nodes (8): android.speech.tts.SynthesisCallback, android.speech.tts.SynthesisRequest, android.speech.tts.TextToSpeechService, Override, SuppressWarnings, TtsService, Override, Voice

### Community 77 - "DownloadVoiceData.java"
Cohesion: 0.20
Nodes (9): accessibilityevent, bufferedinputstream, bytearrayoutputstream, fileinputstream, fileoutputstream, inputstream, intent, zipentry (+1 more)

### Community 78 - ".isTtsLangCode"
Cohesion: 0.27
Nodes (3): Override, TextToSpeechServiceTest, TtsServiceTest

### Community 79 - "DictionarySearch.kt"
Cohesion: 0.14
Nodes (14): PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, sourceLabel(), coroutinecontext, cursor, dispatchers (+6 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.10
Nodes (19): audio, break, emphasis, HTML, HTML, mark, p, prosody (+11 more)

### Community 82 - "ContextWrapper"
Cohesion: 0.33
Nodes (4): ContextWrapper, Context, ContextWrapper, AssetManager

### Community 83 - "DictionarySearch"
Cohesion: 0.44
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (13): cassert, cmath, cstdlib, FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue (+5 more)

### Community 87 - "VoiceVariant"
Cohesion: 0.18
Nodes (5): java.util.regex.Pattern, VoiceVariantTest, VariantData, Override, VoiceVariant

### Community 88 - "main"
Cohesion: 0.09
Nodes (49): espeak_ng_OUTPUT_MODE, DisplayVoices(), main(), ESPEAK_NG_API, espeak_ng_SetRandSeed(), espeak_srand(), GetFileLength(), strncpy0() (+41 more)

### Community 89 - "sPlayer.c"
Cohesion: 0.21
Nodes (16): KlattFini(), frame_t, sample, speechPlayer_frame_t, voice_t, WGEN_DATA, fillSpeechPlayerFrame(), isKlattFrameFollowing() (+8 more)

### Community 90 - "espeak_api.c"
Cohesion: 0.19
Nodes (18): espeak_Cancel, espeak_ERROR, espeak_Synchronize, espeak_Terminate, ESPEAK_API, espeak_ng_STATUS, espeak_PARAMETER, FILE (+10 more)

### Community 91 - "intonation.c"
Cohesion: 0.21
Nodes (19): SYLLABLE, Translator, calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments() (+11 more)

### Community 92 - "PlaybackNotificationUpdater"
Cohesion: 0.33
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 93 - "dictionary.c"
Cohesion: 0.08
Nodes (68): limits, MatchRecord, 1.49.1 - 2017-01-21, IsAlpha(), IsBracket(), utf8_out(), AppendPhonemes(), PHONEME_LIST (+60 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.12
Nodes (17): CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, FMT, IfNextVowelAppend (+9 more)

### Community 96 - "SeekBarPreference"
Cohesion: 0.09
Nodes (10): android.content.DialogInterface, android.widget.SeekBar, OnSeekBarChangeListener, Override, SeekBarPreference, Parameter, UnitType, Percentage (+2 more)

### Community 97 - "LoadLanguageOptions"
Cohesion: 0.22
Nodes (11): DecodeRule(), print_dictionary_flags(), MNEM_TAB, Translator, CheckTranslator(), LoadLanguageOptions(), LookupTune(), LookupMnemName() (+3 more)

### Community 98 - "TtsService.java"
Cohesion: 0.11
Nodes (27): android.annotation.SuppressLint, android.test.AndroidTestCase, anyof, arraylist, assertthat, bundle, checksum, configuration (+19 more)

### Community 99 - "withDictionaryTestContext"
Cohesion: 0.20
Nodes (10): DictionaryInstallerInstrumentedTest, ByteArrayInputStream, ByteArrayInputStream, dictionaryArchive(), ByteArray, T, withDictionaryTestContext(), DictionaryDownloadsTest (+2 more)

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 101 - "ucd.py"
Cohesion: 0.23
Nodes (4): os, sys, parse_property_mapping(), parse_ucd_data()

### Community 102 - "ucd.h"
Cohesion: 0.14
Nodes (25): category, category_group, property, script, get_category_group_string(), get_category_string(), get_script_string(), codepoint_t (+17 more)

### Community 103 - "Tune Definitions"
Cohesion: 0.15
Nodes (13): Clauses, endtune, head, headenv, headextend, headlast, Intonation, nucleus (+5 more)

### Community 104 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Consonant Release, Consonants, Diacritics, Intonation, Length, Manner of Articulation (+14 more)

### Community 105 - "SpeechWaveGeneratorImpl"
Cohesion: 0.15
Nodes (11): FrameManager, getCurrentFrame, getLastIndex, queueFrame, SpeechWaveGeneratorImpl, cascade, frameManager, fricGenerator (+3 more)

### Community 106 - "BeatPlaybackService"
Cohesion: 0.07
Nodes (21): BeatPlaybackService, Callback, Bundle, Context, IBinder, Intent, Notification, LocalBinder (+13 more)

### Community 107 - "comentrypoints.c"
Cohesion: 0.27
Nodes (12): REFCLSID, BOOL, HRESULT, IClassFactory, REFIID, ULONG, ClassFactory_AddRef(), ClassFactory_LockServer() (+4 more)

### Community 108 - "espeakng.js"
Cohesion: 0.15
Nodes (8): eSpeakNG(), Building, Credits, Demo, Download, espeakng.js, Notes, Usage

### Community 109 - "utils.h"
Cohesion: 0.38
Nodes (5): sample, calculateValueAtFadePosition(), ISNAN(), MAX(), MIN()

### Community 110 - "uprintf"
Cohesion: 0.16
Nodes (21): stddef, attrnumber(), 10.0.0 - 2017-06-25, codepoint_t, ucd_tolower(), ucd_totitle(), ucd_toupper(), codepoint_t (+13 more)

### Community 112 - "SettingsScreen.kt"
Cohesion: 0.06
Nodes (66): add, alertdialog, arrowback, backhandler, box, check, clickable, column (+58 more)

### Community 113 - "emoji"
Cohesion: 0.18
Nodes (5): codecs, re, Emoji, read_emoji(), xml_etree_elementtree

### Community 114 - "BeatPlayerPanel.kt"
Cohesion: 0.09
Nodes (25): alignment, WaveformZoomButton(), arrangement, card, columnscope, delay, fillmaxsize, ghostbuttonshape (+17 more)

### Community 115 - "MarkerLoopRole"
Cohesion: 0.12
Nodes (7): JSONObject, MarkerLoopRole, END, NONE, START, ProjectMetadataTest, jsonarray

### Community 116 - "uprintf"
Cohesion: 0.25
Nodes (14): ucd_category, ucd_category_group, ucd_script, ucd_get_category_group_string(), ucd_get_category_string(), ucd_get_script_string(), codepoint_t, FILE (+6 more)

### Community 117 - "Resonator"
Cohesion: 0.09
Nodes (20): speechPlayer_frame_t, ParallelFormantGenerator, r1, r2, r3, r4, r5, r6 (+12 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "PcmSource"
Cohesion: 0.25
Nodes (3): ShortArray, MemoryPcmSource, PcmSource

### Community 121 - "DictionaryHeadword"
Cohesion: 0.20
Nodes (3): DictionaryHeadword, DictionaryHeadwordTest, normalizer

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
Cohesion: 0.11
Nodes (28): after, assertarrayequals, assertequals, assertfalse, assertnotnull, assertnull, assertsame, assertthrows (+20 more)

### Community 127 - "SpeakNextClause"
Cohesion: 0.20
Nodes (10): Translator, CalcLengths(), DoEmbedded2(), MbrolaReset(), GetEnvelope(), espeak_ng_STATUS, voice_t, DoVoiceChange() (+2 more)

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.33
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 130 - "SelectPhonemeTable"
Cohesion: 0.20
Nodes (15): PHONEME_DATA, PHONEME_LIST, PHONEME_LIST2, PHONEME_TAB, Translator, WORD_PH_DATA, MakePhonemeList(), ReInterpretPhoneme() (+7 more)

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (9): codepoint_t, FILE, fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint() (+1 more)

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "unpackDictionaryArchive"
Cohesion: 0.25
Nodes (9): ByteArray, FilterInputStream, unpackDictionaryArchive(), FilterInputStream, DictionaryDownloadProgress, DictionaryArchiveTest, ByteArray, ByteArrayInputStream (+1 more)

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "BeatPlayerPanel"
Cohesion: 0.17
Nodes (9): BeatComponentsTest, BeatPlaybackControls(), BeatPlayerPanel(), CenteredPlayerContent(), IntArray, Modifier, IntArray, Modifier (+1 more)

### Community 136 - "common"
Cohesion: 0.28
Nodes (5): common script, check_hash(), is_hash(), test_phwav(), test_wav()

### Community 137 - "ttsengine.cpp"
Cohesion: 0.38
Nodes (6): new, sapiddk, sperror, espeak_EVENT, espeak_callback(), OnEvent

### Community 138 - "ssml.c"
Cohesion: 0.19
Nodes (24): PARAM_STACK, SSML_STACK, MNEM_TAB, LookupMnem(), AddNameData(), attr_prosody_value(), attrcmp(), attrcopy_utf8() (+16 more)

### Community 140 - ".excludedWords"
Cohesion: 0.28
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 141 - "SettingsScreen"
Cohesion: 0.20
Nodes (6): SettingsScreenTest, T, SettingsDropdownRow(), SettingsScreen(), SettingsSliderRow(), ClosedFloatingPointRange

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 144 - "ContextWrapper"
Cohesion: 0.33
Nodes (3): Context, ContextWrapper, ContextWrapper

### Community 145 - "create_dict_corpus_file.py"
Cohesion: 0.40
Nodes (3): argparse, mmap, shutil

### Community 146 - "PcmLoopRenderer"
Cohesion: 0.19
Nodes (8): FramePositionEvents, ShortArray, PcmLoopBounds, PcmLoopRenderer, PcmLoopRendererTest, assumetrue, atomicreference, managementfactory

### Community 147 - "MainActivity.kt"
Cohesion: 0.11
Nodes (18): Bundle, Context, MainActivity, openExternalLink(), AboutLink(), AboutScreen(), AboutSectionTitle(), DictionaryDownloads() (+10 more)

### Community 148 - "ESPEAK_API"
Cohesion: 0.33
Nodes (6): PrintVersion(), ESPEAK_API, FILE, espeak_GetParameter(), espeak_Info(), espeak_SetPhonemeTrace()

### Community 149 - "translate.c"
Cohesion: 0.09
Nodes (55): RGROUP, Translator, is_str_totally_null(), IsDigit(), IsDigit09(), IsSpace(), isspace2(), towlower2() (+47 more)

### Community 150 - "eSpeakNGWorker"
Cohesion: 0.22
Nodes (7): espeak_VOICE, eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices

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

### Community 156 - "DictionaryScreen"
Cohesion: 0.30
Nodes (5): DictionaryScreenTest, DictionaryMatch, DictionarySearchResult, dictionaryLanguageLabel(), DictionaryScreen()

### Community 157 - ".writeText"
Cohesion: 0.15
Nodes (3): EspeakIpaInstrumentedTest, ProjectLyricsStorage, StagedFileWriter

### Community 170 - "InputStream"
Cohesion: 0.50
Nodes (3): InputStream, ByteArray, InputStream

### Community 171 - "ProjectInfoDialog"
Cohesion: 0.50
Nodes (3): ProjectInfoDialogTest, ProjectInfoDialog(), trimmedOrNull()

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 173 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 175 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

### Community 176 - "BeatPlaybackServiceTest"
Cohesion: 0.24
Nodes (7): BeatPlaybackServiceTest, ServiceConnection, ComponentName, IBinder, Notification, ServiceConnection, T

### Community 178 - "DictionarySearchMode"
Cohesion: 0.16
Nodes (9): DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, DictionarySearchPlan, Prefix, modeLabel() (+1 more)

### Community 181 - "PlaybackFrameLedger"
Cohesion: 0.15
Nodes (8): PlaybackFrameLedger, PlaybackFrameLedgerTest, Architecture, Dictionaries, Ghostwriter documentation, Known limitations, Playback and waveforms, Projects and settings

### Community 182 - "espeak_ng_SetOutputHooks"
Cohesion: 0.50
Nodes (4): espeak_ng_OUTPUT_HOOKS, ESPEAK_NG_API, espeak_ng_SetConstF0(), espeak_ng_SetOutputHooks()

### Community 185 - "Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?, Source Nodes

### Community 186 - "Dictionary data license and attribution"
Cohesion: 0.33
Nodes (5): Conditions when redistributing, Dictionary data license and attribution, Modifications, Source attribution, Warranty

### Community 187 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.33
Nodes (9): ESPEAK_NG_API, espeak_ng_ERROR_CONTEXT, espeak_ng_STATUS, FILE, create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage() (+1 more)

### Community 191 - "documentation/README.md"
Cohesion: 0.05
Nodes (41): Current architecture, Development workflow, Ghostwriter agent instructions, graphify, Non-negotiable project constraints, Dictionaries, Pronunciation lookup, Release inputs and installation (+33 more)

## Knowledge Gaps
- **479 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+474 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 971 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **43 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (2× useful, score=1.988470167)
- `DictionarySearch` (2× useful, score=1.967613008)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Building eSpeak NG` connect `Android` to `index.md`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **Why does `BeatPlayer` connect `BeatPlayer` to `PcmRingBuffer`, `BeatPlayerPanel`, `WaveformMarker`, `BeatPlaybackService`, `.setMarkers`, `BeatPlayerInstrumentedTest`, `SmoothLoopPlayback`, `BeatPlayerPanel.kt`, `BeatPlayer.kt`, `PlaybackFrameLedger`, `GhostwriterTheme`?**
  _High betweenness centrality (0.043) - this node is a cross-community bridge._
- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.038) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _479 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.13054187192118227 - nodes in this community are weakly interconnected._
- **Should `ProjectStorageTest` be split into smaller, more focused modules?**
  _Cohesion score 0.10252100840336134 - nodes in this community are weakly interconnected._