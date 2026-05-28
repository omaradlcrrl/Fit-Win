package org.example.fitwinkmp.features.auth.data

import org.example.fitwinkmp.core.error.ApiException
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.auth.data.dto.*
import org.example.fitwinkmp.shared.model.Usuario
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class AuthRepository(
    private val publicClient: HttpClient,
    private val authClient: HttpClient,
    private val tokenStorage: TokenStorage
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> = runCatching {
        val response = publicClient.post("usuarios/login") {
            setBody(LoginRequest(email, password))
        }
        if (!response.status.isSuccess()) {
            val error = response.body<ApiError>()
            throw ApiException(response.status.value, error.message ?: "Error desconocido")
        }
        val body = response.body<LoginResponse>()
        tokenStorage.saveJwt(body.token)
        tokenStorage.saveRefreshToken(body.refreshToken)
        tokenStorage.saveUsuarioId(body.usuarioId)
        body
    }

    suspend fun register(request: RegisterRequest): Result<Unit> = runCatching {
        val response = publicClient.post("usuarios/save") {
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val error = response.body<ApiError>()
            throw ApiException(response.status.value, error.message ?: "Error desconocido")
        }
        // Con un 2xx basta; no leemos el cuerpo.
    }

    suspend fun logout(): Result<Unit> = runCatching {
        if (tokenStorage.getJwt() == null) return@runCatching
        authClient.post("auth/logout")
        tokenStorage.clear()
    }
}
