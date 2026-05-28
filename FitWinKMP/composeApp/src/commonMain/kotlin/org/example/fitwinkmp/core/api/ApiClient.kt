package org.example.fitwinkmp.core.api

import org.example.fitwinkmp.core.session.SessionEvent
import org.example.fitwinkmp.core.session.SessionEvents
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.auth.data.dto.RefreshRequest
import org.example.fitwinkmp.features.auth.data.dto.RefreshResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val BASE_URL = "http://10.0.2.2:3036/api/v1/FWBBD/"

private fun baseJson() = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private fun baseConfig(): HttpClientConfig<*>.() -> Unit = {
    install(ContentNegotiation) { json(baseJson()) }
    install(Logging) { level = LogLevel.INFO }
    install(DefaultRequest) {
        url(BASE_URL)
        contentType(ContentType.Application.Json)
    }
}

fun buildPublicClient(): HttpClient = HttpClient {
    baseConfig()()
}

fun buildAuthClient(tokenStorage: TokenStorage): HttpClient = HttpClient {
    baseConfig()()
    install(Auth) {
        bearer {
            loadTokens {
                val jwt = tokenStorage.getJwt()
                val refresh = tokenStorage.getRefreshToken()
                if (jwt != null && refresh != null) BearerTokens(jwt, refresh) else null
            }
            refreshTokens {
                val refreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null
                try {
                    val response: RefreshResponse = client.post("auth/refresh") {
                        markAsRefreshTokenRequest()
                        setBody(RefreshRequest(refreshToken))
                    }.body()
                    tokenStorage.saveJwt(response.token)
                    tokenStorage.saveRefreshToken(response.refreshToken)
                    BearerTokens(response.token, response.refreshToken)
                } catch (_: Exception) {
                    tokenStorage.clear()
                    SessionEvents.emit(SessionEvent.Expired)
                    null
                }
            }
            sendWithoutRequest { request ->
                val segments = request.url.pathSegments
                // Solo omitir el token en endpoints realmente públicos:
                // usuarios/login, usuarios/save (registro) y auth/refresh
                val esLogin    = segments.contains("login")
                val esRegistro = segments.contains("usuarios") && segments.contains("save")
                val esRefresh  = segments.contains("refresh")
                !(esLogin || esRegistro || esRefresh)
            }
        }
    }
}
