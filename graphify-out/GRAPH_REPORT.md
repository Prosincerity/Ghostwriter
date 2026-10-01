# Graph Report - Gh0stwrit3r  (2026-10-01)

## Corpus Check
- 339 files · ~561,497 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 2059 file(s) not represented in the graph (top: (none) 1892, .xml 82, .test 17)

## Summary
- 4365 nodes · 10704 edges · 224 communities (136 shown, 88 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 1417 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3b0f478c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BeatPlayer
- .configure
- synthesize.c
- ProjectStorageBeatTest
- ProjectStorageTest
- WaveformViewport
- HomeScreen.kt
- WaveformExtractorTest
- TextToSpeechTestCase
- VoiceSettingsTest
- script
- WaveformView.kt
- What You Must Do When Invoked
- android.content.Context
- fifo.c
- BeatPlaybackService.kt
- graphify reference: extra exports and benchmark
- synthdata.c
- graphify reference: query, path, explain
- graphify reference: add a URL and watch a folder
- graphify reference: commit hook and native CLAUDE.md integration
- graphify reference: incremental update and cluster-only
- graphify reference: GitHub clone and cross-repo merge
- graphify reference: transcribe video and audio
- extraction-spec.md
- Android
- EditorScreenTest
- compile_line
- compiledata.c
- .fromIpa
- PcmRingBuffer
- TranslateClauseWithTerminator
- DictionaryInstaller.kt
- tests/readclause.c
- WaveformMarker
- SpeechSynthesis
- tests/encoding.c
- Synthesize
- TestConnection
- MainActivity.kt
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
- WaveformViewTest
- espeak_Initialize
- Phoneme Features and the International Phonetic Alphabet
- VoiceVariantPreference.java
- MarkerLoopFrames
- utf8_in
- EspeakIpaInstrumentedTest
- Phoneme Model
- eSpeakService.c
- EditorScreen.kt
- Language Attributes
- WaveformExtractor
- PlaybackFocusState
- FrameManagerImpl
- EditorScreen
- SystemFontCatalogTest.kt
- Diacritics
- SettingsFormatTest.kt
- DictionarySearchData
- speechPlayer.cpp
- TtsService
- .excludedWords
- category
- DictionaryArchive.kt
- index.md
- SSML (Speech Synthesis Markup Language)
- DictionarySearch
- ucd_category_
- .parse
- speechWaveGenerator.cpp
- VoiceVariant
- LoadLanguageOptions
- ucd_category_group_
- void
- intonation.c
- PlaybackNotificationUpdater
- main
- Phoneme Instructions
- CodePoint
- SeekBarPreference
- SettingsScreenTest.kt
- TtsService.java
- DictionaryArchiveSource
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
- klatt.c
- uprintf
- Resonator
- Change Log
- Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize
- MainActivityTest
- dictionary.c
- lb.md
- MBROLA Voices
- Kirshenbaum (ASCII-IPA) Transcription Scheme
- CascadeFormantGenerator
- ProjectInfoBpmTest.kt
- BeatPlayerPanel.kt
- Third-party software and dictionary data
- MarkerLoopRole
- ProjectStorage
- printucddata_cpp.cpp
- Vowels
- unpackDictionaryArchive
- Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability.
- GhostwriterTheme
- DownloadVoiceData.java
- ssml.c
- AssonanceData
- WaveformCache
- LyricTextSettings
- waveformMarkerHitBounds
- eSpeak NG user guide
- ContextWrapper
- PcmLoopRenderer
- .excludedWords
- L
- Dictionaries
- eSpeakNGWorker
- EspeakIpa
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
- .cancelDownloadRestoresControlsWithoutInstalling
- Testing
- Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?
- Translation fuzzers
- SmoothLoopPlayback
- Third-party software and dictionary data
- BeatPlaybackServiceTest
- Feature roadmap
- DictionaryScreen
- DictionarySearchMode
- PlaybackFrameLedger
- Releasing
- Ghostwriter
- Q: What should be checked before merging rhyme-detection into dev for a tester version, with LyricPad changes moved to their own branch?
- Dictionary data license and attribution
- espeak_ng_PrintStatusCodeMessage
- PcmBeat
- Settings
- SelectPhonemeTable
- sPlayer.c
- ReadClause
- DictionaryDownloads.kt
- Diacritics
- espeak_ng_Initialize
- BeatPlaybackServiceTest.kt
- spect.c
- PcmBeatDecoderTest
- ContextWrapper
- ProjectLyricsStorage
- DictionaryHeadword
- utf8_out
- ExampleInstrumentedTest.kt
- WaveformWarningTest.kt
- Build setup
- SmoothLoopPlaybackTest
- SynthCallback
- rgroup_sorter
- SystemFontFile
- LyricTextAlignment
- LyricsNotepad
- setlengths.c
- getopt.c
- Contribution Guide
- numbers.md

## God Nodes (most connected - your core abstractions)
1. `ucd_script_` - 186 edges
2. `script` - 186 edges
3. `BeatPlayer` - 61 edges
4. `WaveformMarker` - 57 edges
5. `GhostwriterTheme()` - 54 edges
6. `SpeechSynthesis` - 49 edges
7. `ucd_properties()` - 38 edges
8. `ucd_category_` - 36 edges
9. `category` - 36 edges
10. `LyricTextSettings` - 35 edges

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

## Communities (224 total, 88 thin omitted)

### Community 1 - ".configure"
Cohesion: 0.16
Nodes (13): Building, Cross Compilation, eSpeak NG Feature Configuration, Extended Dictionary Configuration, LLVM Fuzzer Support, Sanitizer Flag Configuration, Bugs, Build Dependencies (+5 more)

### Community 2 - "synthesize.c"
Cohesion: 0.12
Nodes (32): GetMbrName(), MbrolaGenerate(), MbrolaTranslate(), AdjustFormants(), AllocFrame(), CopyFrame(), DoAmplitude(), DoEmbedded() (+24 more)

### Community 6 - "HomeScreen.kt"
Cohesion: 0.15
Nodes (3): HomeScreen(), NewProjectDialog(), RenameProjectDialog()

### Community 9 - "TextToSpeechTestCase"
Cohesion: 0.17
Nodes (5): TextToSpeechTest, TextToSpeechTestCase, Exception, Voice, VoiceData

### Community 12 - "script"
Cohesion: 0.01
Nodes (184): script, Adlm, Afak, Aghb, Ahom, Arab, Armi, Armn (+176 more)

### Community 17 - "What You Must Do When Invoked"
Cohesion: 0.08
Nodes (24): For /graphify add and --watch, For /graphify query, For the commit hook and native CLAUDE.md integration, For --update and --cluster-only, /graphify, Honesty Rules, Interpreter guard for subcommands, Part A - Structural extraction for code files (+16 more)

### Community 18 - "android.content.Context"
Cohesion: 0.06
Nodes (5): EspeakApp, ImportVoicePreference, SpeakPunctuationPreference, PrefsEspeakFragment, TtsSettingsActivity

### Community 19 - "fifo.c"
Cohesion: 0.09
Nodes (24): add_time_in_ms(), clock_gettime2(), event_clear_all(), event_copy(), event_declare(), event_delete(), event_init(), event_notify() (+16 more)

### Community 21 - "graphify reference: extra exports and benchmark"
Cohesion: 0.22
Nodes (8): graphify reference: extra exports and benchmark, Step 6b - Wiki (only if --wiki flag), Step 7 - Neo4j export (only if --neo4j or --neo4j-push flag), Step 7a - FalkorDB export (only if --falkordb or --falkordb-push flag), Step 7b - SVG export (only if --svg flag), Step 7c - GraphML export (only if --graphml flag), Step 7d - MCP server (only if --mcp flag), Step 8 - Token reduction benchmark (only if total_words > 5000)

### Community 22 - "synthdata.c"
Cohesion: 0.19
Nodes (12): CountVowelPosition(), InterpretCondition(), InterpretPhoneme(), InterpretPhoneme2(), InvalidInstn(), LookupPhonemeString(), LookupSpect(), NumInstnWords() (+4 more)

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

### Community 32 - "compile_line"
Cohesion: 0.19
Nodes (13): isspace2(), clean_context(), compile_dictlist_end(), compile_dictlist_file(), compile_dictlist_start(), compile_dictrules(), compile_lettergroup(), compile_line() (+5 more)

### Community 33 - "compiledata.c"
Cohesion: 0.09
Nodes (56): Read4Bytes(), StringToWord(), CalculateSample(), CallPhoneme(), CheckNextChar(), clean_context(), CompileElif(), CompileElse() (+48 more)

### Community 34 - ".fromIpa"
Cohesion: 0.17
Nodes (3): IpaSearchKeys, Keys, IpaSearchKeysTest

### Community 35 - "PcmRingBuffer"
Cohesion: 0.14
Nodes (5): Loop, Page, PcmRingBuffer, Region, PcmRingBufferTest

### Community 36 - "TranslateClauseWithTerminator"
Cohesion: 0.16
Nodes (18): IsSpace(), towlower2(), CalcWordLength(), CombineFlag(), CountSyllables(), DeleteTranslator(), FindReplacementChars(), SetAlternateTranslator() (+10 more)

### Community 37 - "DictionaryInstaller.kt"
Cohesion: 0.17
Nodes (7): DictionaryAsset, DictionaryInstaller, DictionaryLanguage, DictionaryRelease, DictionarySource, ESPEAK, WIKTIONARY

### Community 38 - "tests/readclause.c"
Cohesion: 0.15
Nodes (23): clause_type_from_codepoint(), main(), set_text(), test_arabic(), test_armenian(), test_devanagari(), test_ethiopic(), test_fullwidth() (+15 more)

### Community 39 - "WaveformMarker"
Cohesion: 0.16
Nodes (3): ProjectMetadata, WaveformMarker, ProjectMetadataTest

### Community 40 - "SpeechSynthesis"
Cohesion: 0.07
Nodes (4): SpeechSynthesisTest, SpeechSynthesis, SynthReadyCallback, Voice

### Community 42 - "tests/encoding.c"
Cohesion: 0.20
Nodes (45): create_text_decoder(), destroy_text_decoder(), espeak_ng_EncodingFromName(), null_decoder_getc(), string_decoder_getc_auto(), string_decoder_getc_codepage(), string_decoder_getc_iso_10646_ucs_2(), string_decoder_getc_us_ascii() (+37 more)

### Community 43 - "Synthesize"
Cohesion: 0.08
Nodes (38): espeak_Char(), espeak_Key(), espeak_SetPunctuationList(), espeak_Synth_Mark(), create_espeak_char(), create_espeak_key(), create_espeak_mark(), create_espeak_punctuation_list() (+30 more)

### Community 44 - "TestConnection"
Cohesion: 0.12
Nodes (7): openDictionaryArchive(), FilterInputStream, DictionaryArchiveSourceTest, ByteArrayInputStream, InputStream, ByteArrayInputStream, TestConnection

### Community 45 - "MainActivity.kt"
Cohesion: 0.11
Nodes (13): About, Dictionary, DictionaryDownloads, Editor, GhostwriterApp(), Home, MainActivity, openExternalLink() (+5 more)

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

### Community 53 - "ucd_properties"
Cohesion: 0.23
Nodes (29): properties_Cc(), properties_Cf(), properties_Cn(), properties_Ll(), properties_Lm(), properties_Lo(), properties_Lo_ideographic(), properties_Lu() (+21 more)

### Community 54 - "mbrowrap.c"
Cohesion: 0.11
Nodes (20): close_mbrola(), close_pipes(), create_pipes(), err(), flush_mbrola(), free_pending_data(), init_mbrola(), lastErrorStr_mbrola() (+12 more)

### Community 57 - "espeak_Initialize"
Cohesion: 0.14
Nodes (26): espeak_Initialize(), espeak_SetParameter(), espeak_SetVoiceByFile(), espeak_SetVoiceByName(), espeak_SetVoiceByProperties(), espeak_Synth(), status_to_espeak_error(), main() (+18 more)

### Community 58 - "Phoneme Features and the International Phonetic Alphabet"
Cohesion: 0.10
Nodes (20): Backness, Consonants, Gemination, Height, Intonation, Length, Manner of Articulation, Other Symbols (+12 more)

### Community 59 - "VoiceVariantPreference.java"
Cohesion: 0.11
Nodes (10): FileListAdapter, ViewHolder, InformationListAdapter, ViewHolder, VariantData, VariantDataListAdapter, ViewHolder, VoiceVariantPreference (+2 more)

### Community 60 - "MarkerLoopFrames"
Cohesion: 0.27
Nodes (3): MarkerLoopFrames, MarkerLoopRange, MarkerLoopTest

### Community 61 - "utf8_in"
Cohesion: 0.13
Nodes (27): IsAlpha(), IsBracket(), IsDigit(), utf8_in(), utf8_in2(), compile_rule(), EncodePhonemes(), MatchRule() (+19 more)

### Community 63 - "Phoneme Model"
Cohesion: 0.07
Nodes (28): Active Articulators, Air Flow, Co-articulation, Co-articulation, Consonant Release, Fortis and Lenis, Initiator, Intonation (+20 more)

### Community 64 - "eSpeakService.c"
Cohesion: 0.20
Nodes (16): getJniEnv(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeClassInit(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeCreate(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetAvailableVoices(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeGetVersion(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetParameter(), Java_com_reecedunn_espeak_SpeechSynthesis_nativeSetPunctuationCharacters() (+8 more)

### Community 65 - "EditorScreen.kt"
Cohesion: 0.08
Nodes (3): rememberBeatPlayback(), ServiceConnection, displayNameFor()

### Community 66 - "Language Attributes"
Cohesion: 0.05
Nodes (38): brackets, bracketsAnnounced, breath, breathw, consonants, dictionary, dictmin, dictrules (+30 more)

### Community 67 - "WaveformExtractor"
Cohesion: 0.11
Nodes (3): DecoderProgressGuard, WaveformExtractor, PcmSources

### Community 69 - "FrameManagerImpl"
Cohesion: 0.11
Nodes (15): FrameManagerImpl, curFrame, curFrameIsNULL, frameRequestQueue, lastUserIndex, newFrameRequest, oldFrameRequest, sampleCounter (+7 more)

### Community 72 - "Diacritics"
Cohesion: 0.09
Nodes (22): Articulation, Co-articulation, Conlang X-SAMPA Transcription Scheme, Consonant Release, Consonants, Diacritics, Intonation, Length (+14 more)

### Community 73 - "SettingsFormatTest.kt"
Cohesion: 0.22
Nodes (6): LongBeatWarningDialog(), ReassignBeatDialog(), formatPlaybackTime(), formatInterval(), formatTypographyNumber(), SettingsFormatTest

### Community 74 - "DictionarySearchData"
Cohesion: 0.22
Nodes (3): DictionarySearchData, DictionarySearchRows, InstalledDictionarySearchData

### Community 75 - "speechPlayer.cpp"
Cohesion: 0.14
Nodes (11): create, speechPlayer_getLastIndex(), speechPlayer_initialize(), speechPlayer_queueFrame(), speechPlayer_synthesize(), speechPlayer_terminate(), SpeechWaveGenerator, create (+3 more)

### Community 76 - "TtsService"
Cohesion: 0.13
Nodes (3): TextToSpeechServiceTest, TtsServiceTest, TtsService

### Community 77 - ".excludedWords"
Cohesion: 0.29
Nodes (4): CompoundRhymeFilter, Entry, Pronounced, CompoundRhymeFilterTest

### Community 78 - "category"
Cohesion: 0.06
Nodes (32): category, Cc, Cf, Cn, Co, Cs, Ii, Ll (+24 more)

### Community 81 - "SSML (Speech Synthesis Markup Language)"
Cohesion: 0.11
Nodes (18): audio, break, emphasis, HTML, HTML, mark, p, prosody (+10 more)

### Community 83 - "DictionarySearch"
Cohesion: 0.42
Nodes (4): DictionarySearchInstrumentedTest, Row, DictionaryPronunciations, DictionarySearch

### Community 84 - "ucd_category_"
Cohesion: 0.06
Nodes (32): ucd_category_, UCD_CATEGORY_Cc, UCD_CATEGORY_Cf, UCD_CATEGORY_Cn, UCD_CATEGORY_Co, UCD_CATEGORY_Cs, UCD_CATEGORY_Ii, UCD_CATEGORY_Ll (+24 more)

### Community 86 - "speechWaveGenerator.cpp"
Cohesion: 0.12
Nodes (10): FrequencyGenerator, lastCyclePos, sampleRate, NoiseGenerator, lastValue, VoiceGenerator, aspirationGen, glottisOpen (+2 more)

### Community 88 - "LoadLanguageOptions"
Cohesion: 0.22
Nodes (8): DecodeRule(), print_dictionary_flags(), CheckTranslator(), LoadLanguageOptions(), LookupTune(), LookupMnemName(), Read8Numbers(), ReadNumbers()

### Community 89 - "ucd_category_group_"
Cohesion: 0.10
Nodes (22): ucd_get_category_group_for_category(), ucd_lookup_category_group(), category_group, C, I, M, N, P (+14 more)

### Community 91 - "intonation.c"
Cohesion: 0.27
Nodes (11): calc_pitch_segment(), calc_pitches(), calc_pitches2(), CalcPitches(), CalcPitches_Tone(), count_increments(), count_pitch_vowels(), CountUnstressed() (+3 more)

### Community 92 - "PlaybackNotificationUpdater"
Cohesion: 0.30
Nodes (3): PlaybackNotificationUpdater, PlaybackNotificationUpdaterTest, TestQueue

### Community 93 - "main"
Cohesion: 0.09
Nodes (34): CloseWavFile(), DisplayVoices(), main(), OpenWavFile(), SynthCallback(), Write4Bytes(), GetFileLength(), strncpy0() (+26 more)

### Community 94 - "Phoneme Instructions"
Cohesion: 0.06
Nodes (33): Attributes, CALL, ChangeIfDiminished, ChangeIfNotStressed, ChangeIfStressed, ChangeIfUnstressed, ChangePhoneme, Conditional Statements (+25 more)

### Community 96 - "SeekBarPreference"
Cohesion: 0.09
Nodes (6): SeekBarPreference, Parameter, UnitType, Percentage, Punctuation, WordsPerMinute

### Community 99 - "DictionaryArchiveSource"
Cohesion: 0.28
Nodes (5): DictionaryInstallerInstrumentedTest, dictionaryArchive(), withDictionaryTestContext(), DictionaryArchiveSource, IpaGenerator

### Community 100 - "printdata.py"
Cohesion: 0.15
Nodes (3): isalnum(), isgraph(), ispunct()

### Community 102 - "ucd.h"
Cohesion: 0.14
Nodes (30): ucd_lookup_category(), ucd_isalnum(), ucd_isalpha(), ucd_isblank(), ucd_iscntrl(), ucd_isdigit(), ucd_isgraph(), ucd_islower() (+22 more)

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
Cohesion: 0.38
Nodes (4): calculateValueAtFadePosition(), ISNAN(), MAX(), MIN()

### Community 110 - "uprintf"
Cohesion: 0.23
Nodes (13): 10.0.0 - 2017-06-25, get_category_string(), get_script_string(), ucd_get_category_string(), ucd_get_script_string(), fget_utf8c(), fput_utf8c(), iswblank() (+5 more)

### Community 114 - "klatt.c"
Cohesion: 0.20
Nodes (17): antiresonator(), DBtoLIN(), flutter(), frame_init(), gen_noise(), impulsive_source(), KlattInit(), KlattReset() (+9 more)

### Community 116 - "uprintf"
Cohesion: 0.23
Nodes (10): ucd_tolower(), ucd_totitle(), ucd_toupper(), ucd_lookup_script(), fget_utf8c(), fput_utf8c(), main(), print_file() (+2 more)

### Community 117 - "Resonator"
Cohesion: 0.09
Nodes (19): ParallelFormantGenerator, r1, r2, r3, r4, r5, r6, sampleRate (+11 more)

### Community 118 - "Change Log"
Cohesion: 0.17
Nodes (11): 11.0.0.1 - 2021-05-04, 11.0.0 - 2018-07-08, 6.2.0 - 2013-10-16, 6.3.0 - 2013-10-16, 7.0.0.1 - 2014-07-14, 7.0.0 - 2014-06-28, 8.0.0.1 - 2016-05-31, 8.0.0 - 2015-06-06 (+3 more)

### Community 119 - "Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize"
Cohesion: 0.33
Nodes (3): copy_bytes(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(), Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize()

### Community 121 - "dictionary.c"
Cohesion: 0.19
Nodes (20): is_str_totally_null(), AppendPhonemes(), DecodePhonemes(), DollarRule(), GetVowelStress(), HashDictionary(), InitGroups(), IsLetter() (+12 more)

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

### Community 128 - "Third-party software and dictionary data"
Cohesion: 0.33
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 129 - "MarkerLoopRole"
Cohesion: 0.23
Nodes (7): MarkerLoopRole, END, NONE, START, replaceLoopMarker(), WaveformMarkerDialog(), EditorMarkerDialogs()

### Community 131 - "printucddata_cpp.cpp"
Cohesion: 0.56
Nodes (7): fget_utf8c(), fput_utf8c(), main(), print_file(), uprintf(), uprintf_codepoint(), uprintf_is()

### Community 132 - "Vowels"
Cohesion: 0.22
Nodes (8): Diphthongs, English, Long Vowels, Reduced Vowels, References, Rhotic Vowels, Short Vowels, Vowels

### Community 133 - "unpackDictionaryArchive"
Cohesion: 0.33
Nodes (4): unpackDictionaryArchive(), DictionaryDownloadProgress, DictionaryArchiveTest, TrackedInput

### Community 134 - "Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Review code added on rhyme-detection for simplification, cleanup, refactoring, and readability., Source Nodes

### Community 135 - "GhostwriterTheme"
Cohesion: 0.11
Nodes (5): BeatComponentsTest, AboutScreenTest, BeatPlaybackControls(), BeatPlayerPanel(), GhostwriterTheme()

### Community 137 - "DownloadVoiceData.java"
Cohesion: 0.10
Nodes (5): CheckVoiceData, AsyncExtract, DownloadVoiceData, ExtractProgress, FileUtils

### Community 138 - "ssml.c"
Cohesion: 0.14
Nodes (22): LookupEnvelopeName(), LookupMnem(), AddNameData(), LoadSoundFile(), LoadSoundFile2(), LookupSoundicon(), attr_prosody_value(), attrcmp() (+14 more)

### Community 141 - "LyricTextSettings"
Cohesion: 0.25
Nodes (3): LyricTextSettings, LyricTextSettingsTest, LyricTextStyleTest

### Community 143 - "eSpeak NG user guide"
Cohesion: 0.29
Nodes (7): Error solutions, eSpeak NG user guide, Installation, Linux, Problems with pcaudiolib, Problems with pulseaudio, Windows

### Community 146 - "PcmLoopRenderer"
Cohesion: 0.16
Nodes (5): FramePositionEvents, PcmLoopBounds, PcmLoopRenderer, PcmLoopRendererTest, UnavailableSource

### Community 147 - ".excludedWords"
Cohesion: 0.17
Nodes (5): AssonanceFormFilter, Entry, PrefixNode, Pronounced, AssonanceFormFilterTest

### Community 148 - "L"
Cohesion: 0.29
Nodes (16): IsDigit09(), Lookup(), CheckDotOrdinal(), CheckThousandsGroup(), hu_number_e(), LookupAccentedLetter(), LookupLetter(), LookupLetter2() (+8 more)

### Community 149 - "Dictionaries"
Cohesion: 0.33
Nodes (6): Dictionaries, Pronunciation lookup, Release inputs and installation, Responsiveness and verification, Search behavior, Using dictionaries

### Community 150 - "eSpeakNGWorker"
Cohesion: 0.10
Nodes (12): eSpeakNGWorker, current_voice, pitch, rate, samplerate, voices, PrintVersion(), event_set_callback() (+4 more)

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

### Community 170 - ".cancelDownloadRestoresControlsWithoutInstalling"
Cohesion: 0.19
Nodes (3): ByteArrayInputStream, DictionaryDownloadsTest, InputStream

### Community 171 - "Testing"
Cohesion: 0.40
Nodes (5): Android tests, Checks without a device, Coverage and regression guidance, Coverage reports, Testing

### Community 172 - "Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: How hard would it be to include loop markers in WaveformView and BeatPlayer that jump to the previous marker or beat start?, Source Nodes

### Community 173 - "Translation fuzzers"
Cohesion: 0.40
Nodes (4): Configure the project for fuzzing, Look at fuzzer coverage, Run the fuzzers, Translation fuzzers

### Community 175 - "Third-party software and dictionary data"
Cohesion: 0.40
Nodes (5): Downloadable rhyme dictionaries, eSpeak NG 1.52.0, Ghostwriter Dictionary index code, Roboto UI fonts, Third-party software and dictionary data

### Community 177 - "Feature roadmap"
Cohesion: 0.18
Nodes (11): About and attribution, Beats and playback, Dictionaries, Feature roadmap, Guiding rules, Implementation guidance, Import, export, and optional sync, Lyric editor (+3 more)

### Community 178 - "DictionaryScreen"
Cohesion: 0.15
Nodes (11): DictionaryScreenTest, PronunciationResult, PronunciationSource, ESPEAK_DATABASE, ESPEAK_GENERATED, WIKTIONARY, DictionaryMatch, DictionarySearchResult (+3 more)

### Community 179 - "DictionarySearchMode"
Cohesion: 0.16
Nodes (9): DictionarySearchMode, ASSONANCE, RHYME, WORD_PREFIX, WORD_SUFFIX, DictionarySearchPlan, Prefix, modeLabel() (+1 more)

### Community 181 - "PlaybackFrameLedger"
Cohesion: 0.16
Nodes (8): PlaybackFrameLedger, PlaybackFrameLedgerTest, Architecture, Dictionaries, Ghostwriter documentation, Known limitations, Playback and waveforms, Projects and settings

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

### Community 187 - "espeak_ng_PrintStatusCodeMessage"
Cohesion: 0.31
Nodes (5): create_version_mismatch_error_context(), espeak_ng_ClearErrorContext(), espeak_ng_GetStatusCodeMessage(), espeak_ng_PrintStatusCodeMessage(), espeak_CompileDictionary()

### Community 188 - "PcmBeat"
Cohesion: 0.18
Nodes (4): PcmBeat, MemoryPcmSource, PcmSource, PcmSourceTest

### Community 190 - "SelectPhonemeTable"
Cohesion: 0.20
Nodes (9): MakePhonemeList(), ReInterpretPhoneme(), SetRegressiveVoicing(), SubstitutePhonemes(), LookupPhonemeTable(), SelectPhonemeTable(), SelectPhonemeTableName(), SetUpPhonemeTable() (+1 more)

### Community 192 - "sPlayer.c"
Cohesion: 0.21
Nodes (11): KlattFini(), fillSpeechPlayerFrame(), isKlattFrameFollowing(), KlattFiniSP(), KlattInitSP(), KlattResetSP(), MIN(), mixWaveFile() (+3 more)

### Community 193 - "ReadClause"
Cohesion: 0.29
Nodes (13): AnnouncePunctuation(), CheckPhonemeMode(), DecodeWithPhonemeMode(), Eof(), GetC(), IgnoreOrReplaceChar(), IsRomanU(), LookupCharName() (+5 more)

### Community 194 - "DictionaryDownloads.kt"
Cohesion: 0.14
Nodes (6): AboutLink(), AboutScreen(), AboutSectionTitle(), DictionaryDownloads(), DictionaryDownloadsScreen(), downloadSourceLabel()

### Community 195 - "Diacritics"
Cohesion: 0.20
Nodes (10): Air Flow, Articulation, Co-articulation, Consonant Release, Diacritics, Fortis and Lenis, Phonation, Rounding and Labialization (+2 more)

### Community 196 - "espeak_ng_Initialize"
Cohesion: 0.13
Nodes (14): create_espeak_parameter(), fifo_init(), LoadConfig(), SetParameter(), check_data_path(), espeak_ng_GetSampleRate(), espeak_ng_Initialize(), espeak_ng_InitializeOutput() (+6 more)

### Community 198 - "spect.c"
Cohesion: 0.14
Nodes (11): ieee_extended_to_double(), GetFrameLength(), GetFrameRms(), LoadFrame(), LoadSpectSeq(), read_double(), SpectFrameCreate(), SpectFrameDestroy() (+3 more)

### Community 203 - "utf8_out"
Cohesion: 0.36
Nodes (7): utf8_out(), GetTranslatedPhonemeString(), WritePhMnemonic(), WritePhMnemonicWithStress(), espeak_TextToPhonemes(), SpeakNextClause(), TranslateClause()

### Community 206 - "Build setup"
Cohesion: 0.67
Nodes (3): Build setup, Checkout and build, Requirements

### Community 207 - "SmoothLoopPlaybackTest"
Cohesion: 0.29
Nodes (3): ObservedSource, SmoothLoopPlaybackTest, PcmSource

### Community 216 - "SystemFontFile"
Cohesion: 0.34
Nodes (5): LyricFont, lyricFontFromPreference(), SystemFontFile, systemFontOptions(), LyricFontTest

### Community 217 - "LyricTextAlignment"
Cohesion: 0.14
Nodes (13): LyricFontFamily, CURSIVE, MONOSPACE, SANS_SERIF, SERIF, SYSTEM_DEFAULT, LyricTextAlignment, CENTER (+5 more)

### Community 219 - "setlengths.c"
Cohesion: 0.22
Nodes (7): CalcLengths(), DoEmbedded2(), SetSpeed(), SetSpeedFactors(), SetSpeedMods(), SetSpeedMultiplier(), GetEnvelope()

### Community 220 - "getopt.c"
Cohesion: 0.57
Nodes (5): gcd(), getopt(), getopt_internal(), getopt_long(), permute_args()

### Community 222 - "Contribution Guide"
Cohesion: 0.67
Nodes (3): Contribution Guide, Simple steps for your feedback, Steps for your contribution

## Knowledge Gaps
- **927 isolated node(s):** `Home`, `WIKTIONARY`, `ESPEAK`, `WIKTIONARY`, `ESPEAK_DATABASE` (+922 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1423 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **88 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `BeatPlaybackService` (3× useful, score=2.983110178)
- `DictionarySearch` (2× useful, score=1.964073488)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `script` connect `script` to `uprintf`, `ucd.h`?**
  _High betweenness centrality (0.096) - this node is a cross-community bridge._
- **Why does `ucd_script_` connect `ucd_script_` to `uprintf`, `uprintf`, `ucd.h`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Why does `Text to Phoneme Translation` connect `tr_languages.c` to `index.md`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `BeatPlayer` (e.g. with `.freshPlayer_currentPositionIsZero()` and `.freshPlayer_defaultsLoopingToTrue()`) actually correct?**
  _`BeatPlayer` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 13 inferred relationships involving `WaveformMarker` (e.g. with `.frameMarkers_persistExactSamplesWithoutMillisecondRounding()` and `.frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames()`) actually correct?**
  _`WaveformMarker` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Home`, `WIKTIONARY`, `ESPEAK` to the rest of the system?**
  _927 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BeatPlayer` be split into smaller, more focused modules?**
  _Cohesion score 0.11614401858304298 - nodes in this community are weakly interconnected._