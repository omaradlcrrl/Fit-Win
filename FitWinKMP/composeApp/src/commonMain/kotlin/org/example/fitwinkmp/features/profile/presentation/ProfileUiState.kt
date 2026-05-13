package org.example.fitwinkmp.features.profile.presentation

import org.example.fitwinkmp.features.profile.data.dto.MedicionDTO
import org.example.fitwinkmp.features.profile.data.dto.ObjetivoDTO
import org.example.fitwinkmp.shared.model.Usuario

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(
        val usuario: Usuario,
        val objetivo: ObjetivoDTO?,
        val ultimaMedicion: MedicionDTO?
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
