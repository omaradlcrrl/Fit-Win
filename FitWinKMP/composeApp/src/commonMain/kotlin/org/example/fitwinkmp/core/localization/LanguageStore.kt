package org.example.fitwinkmp.core.localization

import com.russhwolf.settings.Settings

class LanguageStore(private val settings: Settings = Settings()) {
    fun save(lang: String) = settings.putString(KEY_LANG, lang)
    fun get(): String = settings.getString(KEY_LANG, DEFAULT_LANG)

    companion object {
        private const val KEY_LANG = "app_language"
        private const val DEFAULT_LANG = "es"
    }
}
