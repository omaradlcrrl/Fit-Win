package org.example.fitwinkmp.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null
)
