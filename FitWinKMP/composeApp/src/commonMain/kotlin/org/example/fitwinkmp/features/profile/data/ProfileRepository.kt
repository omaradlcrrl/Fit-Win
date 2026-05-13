package org.example.fitwinkmp.features.profile.data

import org.example.fitwinkmp.features.profile.data.api.ProfileApi
import org.example.fitwinkmp.features.profile.data.dto.MedicionDTO
import org.example.fitwinkmp.features.profile.data.dto.ObjetivoDTO
import org.example.fitwinkmp.features.profile.data.dto.UpdateUsuarioRequest
import org.example.fitwinkmp.shared.model.Usuario

class ProfileRepository(private val api: ProfileApi) {

    suspend fun getProfile(id: Int): Result<Usuario> =
        runCatching { api.getUsuario(id) }

    suspend fun updateProfile(id: Int, request: UpdateUsuarioRequest): Result<Usuario> =
        runCatching { api.updateUsuario(id, request) }

    suspend fun getObjetivoActual(usuarioId: Int): Result<ObjetivoDTO> =
        runCatching { api.getObjetivoActual(usuarioId) }

    suspend fun getUltimaMedicion(usuarioId: Int): Result<MedicionDTO> =
        runCatching { api.getUltimaMedicion(usuarioId) }

    suspend fun generarObjetivo(usuarioId: Int): Result<ObjetivoDTO> =
        runCatching { api.generarObjetivo(usuarioId) }
}
