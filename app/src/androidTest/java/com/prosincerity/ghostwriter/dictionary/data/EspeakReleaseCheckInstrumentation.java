package com.prosincerity.ghostwriter.dictionary.data;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/** Platform-only runner: AndroidX test libraries can reference classes removed from an optimized app. */
public class EspeakReleaseCheckInstrumentation extends Instrumentation {
    @Override public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        start();
    }

    @Override public void onStart() {
        String[][] cases = {
            {"englishPronunciation", "ghostwriter", "en", "ɡˈəʊstɹaɪtə"},
            {"germanPronunciation", "Übermut", "de", "ˌyːbɜmˈuːt"},
            {"turkishPronunciation", "ışık", "tr", "ɯʃˈɯk"},
            {"decomposedGermanPronunciation", "U\u0308bermut", "de", "ˌyːbɜmˈuːt"},
        };
        int failures = 0;
        for (int index = 0; index < cases.length; index++) {
            String[] test = cases[index];
            Bundle status = new Bundle();
            status.putString("id", getClass().getName());
            status.putString("class", getClass().getName());
            status.putString("test", test[0]);
            status.putInt("numtests", cases.length);
            status.putInt("current", index + 1);
            sendStatus(1, status);
            try {
                assertPronunciation(test[1], test[2], test[3]);
                status.putString("stream", ".");
                sendStatus(0, status);
            } catch (Throwable failure) {
                failures++;
                status.putString("stack", Log.getStackTraceString(failure));
                status.putString("stream", Log.getStackTraceString(failure));
                sendStatus(-2, status);
            }
        }
        Bundle result = new Bundle();
        result.putString("stream", "\nJNI tests: " + cases.length + ", failures: " + failures + "\n");
        finish(Activity.RESULT_OK, result);
    }

    private void assertPronunciation(String word, String language, String expected) throws Exception {
        AtomicReference<Object> resumed = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);
        Object immediate = new EspeakIpa(getTargetContext()).ipa(word, language, new Continuation<String>() {
            @Override public CoroutineContext getContext() { return EmptyCoroutineContext.INSTANCE; }
            @Override public void resumeWith(Object result) {
                resumed.set(result);
                completed.countDown();
            }
        });
        Object actual = immediate;
        if (!(immediate instanceof String)) {
            if (immediate == null || !completed.await(10, TimeUnit.SECONDS))
                throw new AssertionError("Native pronunciation did not complete");
            actual = resumed.get();
        }
        if (!expected.equals(actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
}
