package org.example.fitwinkmp.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val usuarioId: Int,
    val nombre: String,
    val apellidos: String,
    val correoElectronico: String,
    val fechaNacimiento: String? = null,
    val fechaRegistro: String? = null,
    val altura: Double? = null,
    val idioma: String? = null,
    val estrategia: String? = null,
    val ajusteCalorico: Int? = null,
    val genero: String? = null,
    val nivelActividad: String? = null,
    val pesoActual: Double? = null,
    val objetivo: String? = null,
    val onboardingCompleto: Boolean? = false
)
