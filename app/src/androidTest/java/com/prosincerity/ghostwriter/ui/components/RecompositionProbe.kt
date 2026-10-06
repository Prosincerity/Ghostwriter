package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.Composer
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.ExperimentalComposeRuntimeApi
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.tooling.CompositionObserver
import androidx.compose.runtime.tooling.CompositionObserverHandle
import androidx.compose.runtime.tooling.ObservableComposition
import androidx.compose.runtime.tooling.setObserver

/** Captures the next component scope so tests can restart it without changing its caller. */
@OptIn(ExperimentalComposeRuntimeApi::class, InternalComposeApi::class)
internal class RecompositionProbe : CompositionObserver {
    val scopes = mutableListOf<RecomposeScope>()
    val entries = mutableMapOf<RecomposeScope, Int>()
    private var composer: Composer? = null
    private var handle: CompositionObserverHandle? = null
    private var awaitingScope = false

    fun captureNext(composer: Composer) {
        this.composer = composer
        if (handle == null) handle = checkNotNull(composer.composition.setObserver(this))
        awaitingScope = true
    }

    fun dispose() { handle?.dispose() }

    override fun onScopeEnter(scope: RecomposeScope) {
        entries[scope] = (entries[scope] ?: 0) + 1
        if (awaitingScope) {
            scopes += scope
            awaitingScope = false
        }
    }

    override fun onBeginComposition(composition: ObservableComposition) = Unit
    override fun onEndComposition(composition: ObservableComposition) = Unit
    override fun onReadInScope(scope: RecomposeScope, value: Any) = Unit
    override fun onScopeExit(scope: RecomposeScope) {
        // Components without observable reads otherwise discard their restart
        // callback. Mark them used after defaults have been calculated.
        if (scope in scopes) composer?.recordUsed(scope)
    }
    override fun onScopeInvalidated(scope: RecomposeScope, value: Any?) = Unit
    override fun onScopeDisposed(scope: RecomposeScope) = Unit
}
