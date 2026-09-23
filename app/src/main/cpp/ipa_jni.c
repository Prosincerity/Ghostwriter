#include <jni.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>
#include <espeak-ng/speak_lib.h>

static pthread_mutex_t engine_mutex = PTHREAD_MUTEX_INITIALIZER;
static int initialized = 0;

static char *copy_bytes(JNIEnv *env, jbyteArray bytes, jsize maximum) {
    if (bytes == NULL) return NULL;
    jsize length = (*env)->GetArrayLength(env, bytes);
    if (length <= 0 || length > maximum) return NULL;
    char *copy = malloc((size_t)length + 1);
    if (copy == NULL) return NULL;
    (*env)->GetByteArrayRegion(env, bytes, 0, length, (jbyte *)copy);
    if ((*env)->ExceptionCheck(env) || memchr(copy, 0, (size_t)length) != NULL) {
        free(copy);
        return NULL;
    }
    copy[length] = 0;
    return copy;
}

JNIEXPORT jboolean JNICALL
Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativeInitialize(
    JNIEnv *env, jobject self, jbyteArray path_bytes) {
    (void)self;
    char *path = copy_bytes(env, path_bytes, 4096);
    if (path == NULL) return JNI_FALSE;
    pthread_mutex_lock(&engine_mutex);
    if (!initialized) {
        initialized = espeak_Initialize(AUDIO_OUTPUT_SYNCHRONOUS, 0, path,
                                         espeakINITIALIZE_DONT_EXIT) >= 0;
    }
    pthread_mutex_unlock(&engine_mutex);
    free(path);
    return initialized ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jbyteArray JNICALL
Java_com_prosincerity_ghostwriter_data_EspeakIpa_nativePhonemize(
    JNIEnv *env, jobject self, jbyteArray word_bytes, jint language) {
    (void)self;
    const char *voice = language == 0 ? "en" : language == 1 ? "de" :
                        language == 2 ? "tr" : NULL;
    if (voice == NULL) return NULL;
    char *word = copy_bytes(env, word_bytes, 512);
    if (word == NULL) return NULL;
    jbyteArray output = NULL;
    pthread_mutex_lock(&engine_mutex);
    if (initialized && espeak_SetVoiceByName(voice) == EE_OK) {
        const void *cursor = word;
        const char *ipa = espeak_TextToPhonemes(&cursor, espeakCHARS_UTF8,
                                                espeakPHONEMES_IPA);
        if (ipa != NULL) {
            size_t length = strlen(ipa);
            if (length > 0 && length <= 4096) {
                output = (*env)->NewByteArray(env, (jsize)length);
                if (output != NULL) {
                    (*env)->SetByteArrayRegion(env, output, 0, (jsize)length,
                                               (const jbyte *)ipa);
                }
            }
        }
    }
    pthread_mutex_unlock(&engine_mutex);
    free(word);
    return output;
}
