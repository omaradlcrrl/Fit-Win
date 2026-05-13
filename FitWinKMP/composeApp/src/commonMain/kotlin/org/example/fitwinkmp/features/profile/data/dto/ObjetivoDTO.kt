package org.example.fitwinkmp.features.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ObjetivoDTO(
    val objetivoId: Int? = null,
    val tipo: String? = null,
    val caloriasObjetivo: Double? = null,
    val proteinasObjetivo: Double? = null,
    val carbohidratosObjetivo: Double? = null,
    val grasasObjetivo: Double? = null,
    val imc: Double? = null,
    val activo: Boolean? = null,
    val usuarioId: Int? = null,
    val peso: Double? = null,
    val altura: Double? = null
)
