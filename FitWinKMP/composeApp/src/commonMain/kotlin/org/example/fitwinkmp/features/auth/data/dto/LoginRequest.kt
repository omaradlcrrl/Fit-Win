package org.example.fitwinkmp.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val correoElectronico: String,
    val password: String
)
