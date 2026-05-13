package org.example.fitwinkmp.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponse(
    val token: String,
    val refreshToken: String
)
