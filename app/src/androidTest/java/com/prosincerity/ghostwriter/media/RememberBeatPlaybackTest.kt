package com.prosincerity.ghostwriter.media

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RememberBeatPlaybackTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun rejectedBinding_doesNotUnbindAndChangingProjectRetries() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var binds = 0
        var unbinds = 0
        val rejecting = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun bindService(intent: Intent, connection: ServiceConnection, flags: Int): Boolean {
                binds++
                return false
            }
            override fun unbindService(connection: ServiceConnection) { unbinds++ }
        }
        val title = mutableStateOf("First")
        val visible = mutableStateOf(true)
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides rejecting) {
                if (visible.value) assertNull(rememberBeatPlayback(title.value))
            }
        }
        composeRule.waitUntil(5000) { binds == 1 }
        composeRule.runOnIdle { title.value = "Second" }
        composeRule.waitUntil(5000) { binds == 2 }
        composeRule.runOnIdle { visible.value = false }
        composeRule.runOnIdle { assertEquals(0, unbinds) }
    }

    @Test fun disconnectClearsServiceAndChangingProjectRebinds() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        var connection: ServiceConnection? = null
        val observing = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun bindService(intent: Intent, conn: ServiceConnection, flags: Int): Boolean {
                connection = conn
                return context.bindService(intent, conn, flags)
            }
        }
        val title = mutableStateOf("First")
        val visible = mutableStateOf(true)
        var service: BeatPlaybackService? = null
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides observing) {
                if (visible.value) service = rememberBeatPlayback(title.value)
            }
        }
        try {
            composeRule.waitUntil(5000) { service != null }
            composeRule.runOnIdle { connection!!.onServiceDisconnected(ComponentName(context, BeatPlaybackService::class.java)) }
            composeRule.waitUntil(5000) { service == null }
            composeRule.runOnIdle { title.value = "Second" }
            composeRule.waitUntil(5000) { service != null }
        } finally {
            composeRule.runOnIdle { visible.value = false }
            context.stopService(Intent(context, BeatPlaybackService::class.java))
        }
    }
}
