package org.example.fitwinkmp.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val usuarioId: Int,
    val nombre: String,
    val correoElectronico: String,
    val onboardingCompleto: Boolean
)
