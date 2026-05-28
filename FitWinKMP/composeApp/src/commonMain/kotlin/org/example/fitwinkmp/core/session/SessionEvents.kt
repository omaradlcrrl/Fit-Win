package org.example.fitwinkmp.core.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class SessionEvent {
    data object Expired : SessionEvent()
}

object SessionEvents {
    private val _pending = MutableStateFlow<SessionEvent?>(null)
    val pending: StateFlow<SessionEvent?> = _pending

    fun emit(event: SessionEvent) {
        _pending.value = event
    }

    fun consume() {
        _pending.value = null
    }
}
