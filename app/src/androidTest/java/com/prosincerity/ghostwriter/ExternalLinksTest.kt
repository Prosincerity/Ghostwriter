package com.prosincerity.ghostwriter

import android.content.ActivityNotFoundException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExternalLinksTest {
    @Test
    fun unavailableBrowser_reportsLaunchFailure() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            assertFalse(openExternalLink(instrumentation.targetContext, "https://example.invalid") {
                throw ActivityNotFoundException("No browser")
            })
        }
    }
}
