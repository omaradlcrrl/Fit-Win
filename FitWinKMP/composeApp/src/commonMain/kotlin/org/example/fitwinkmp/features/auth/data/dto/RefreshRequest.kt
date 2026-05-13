package org.example.fitwinkmp.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(val refreshToken: String)
