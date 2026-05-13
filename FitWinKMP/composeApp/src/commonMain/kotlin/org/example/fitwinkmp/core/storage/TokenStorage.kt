package org.example.fitwinkmp.core.storage

import com.russhwolf.settings.Settings

class TokenStorage(private val settings: Settings = Settings()) {

    fun saveJwt(token: String) = settings.putString(KEY_JWT, token)
    fun getJwt(): String? = settings.getStringOrNull(KEY_JWT)

    fun saveRefreshToken(token: String) = settings.putString(KEY_REFRESH, token)
    fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH)

    fun saveUsuarioId(id: Int) = settings.putInt(KEY_USER_ID, id)
    fun getUsuarioId(): Int? = settings.getIntOrNull(KEY_USER_ID)

    fun saveActiveRutinaId(id: Int) = settings.putInt(KEY_ACTIVE_RUTINA_ID, id)
    fun getActiveRutinaId(): Int? = settings.getIntOrNull(KEY_ACTIVE_RUTINA_ID)
    fun removeActiveRutinaId() = settings.remove(KEY_ACTIVE_RUTINA_ID)

    fun hasSession(): Boolean = getJwt() != null && getRefreshToken() != null

    fun clear() {
        settings.remove(KEY_JWT)
        settings.remove(KEY_REFRESH)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_ACTIVE_RUTINA_ID)
    }

    companion object {
        private const val KEY_JWT = "jwt"
        private const val KEY_REFRESH = "refreshToken"
        private const val KEY_USER_ID = "usuarioId"
        private const val KEY_ACTIVE_RUTINA_ID = "activeRutinaId"
    }
}
