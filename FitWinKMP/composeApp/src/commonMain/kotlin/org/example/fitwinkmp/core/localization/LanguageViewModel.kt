package org.example.fitwinkmp.core.localization

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel : ViewModel() {

    private val store = LanguageStore()

    private val _language = MutableStateFlow(store.get())
    val language: StateFlow<String> = _language.asStateFlow()

    fun setLanguage(lang: String) {
        store.save(lang)
        _language.value = lang
    }
}
