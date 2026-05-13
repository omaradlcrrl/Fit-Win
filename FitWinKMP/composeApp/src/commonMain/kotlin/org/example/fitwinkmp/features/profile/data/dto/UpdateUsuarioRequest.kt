package org.example.fitwinkmp.features.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsuarioRequest(
    val nombre: String,
    val apellidos: String,
    val correoElectronico: String,
    val altura: Double? = null,
    val pesoActual: Double? = null,
    val genero: String? = null,
    val nivelActividad: String? = null,
    val objetivo: String? = null,
    val idioma: String? = null
)
