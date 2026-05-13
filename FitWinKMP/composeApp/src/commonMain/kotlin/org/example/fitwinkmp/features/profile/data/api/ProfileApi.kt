package org.example.fitwinkmp.features.profile.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.fitwinkmp.features.profile.data.dto.MedicionDTO
import org.example.fitwinkmp.features.profile.data.dto.ObjetivoDTO
import org.example.fitwinkmp.features.profile.data.dto.UpdateUsuarioRequest
import org.example.fitwinkmp.shared.model.Usuario

class ProfileApi(private val httpClient: HttpClient) {

    suspend fun getUsuario(id: Int): Usuario =
        httpClient.get("usuarios/$id").body()

    suspend fun updateUsuario(id: Int, request: UpdateUsuarioRequest): Usuario =
        httpClient.put("usuarios/actualizar/$id") {
            setBody(request)
        }.body()

    suspend fun getObjetivoActual(usuarioId: Int): ObjetivoDTO =
        httpClient.get("objetivos/actual/$usuarioId").body()

    suspend fun getUltimaMedicion(usuarioId: Int): MedicionDTO =
        httpClient.get("mediciones/ultima/$usuarioId").body()

    suspend fun generarObjetivo(usuarioId: Int): ObjetivoDTO =
        httpClient.post("objetivos/generar/$usuarioId").body()
}
