package org.example.fitwinkmp.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nombre: String,
    val apellidos: String,
    val correoElectronico: String,
    val password: String,
    val fechaNacimiento: String? = null,
    val altura: Double? = null,
    val genero: String? = null,
    val nivelActividad: String? = null,
    val pesoActual: Double? = null,
    val objetivo: String? = null,
    val idioma: String = "es"
)
