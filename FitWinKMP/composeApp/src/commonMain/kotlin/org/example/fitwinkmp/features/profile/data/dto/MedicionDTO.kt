package org.example.fitwinkmp.features.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicionDTO(
    val medicionId: Int? = null,
    val usuarioId: Int? = null,
    val fecha: String? = null,
    val peso: Double? = null,
    val porcentajeGrasa: Double? = null,
    val masaMagra: Double? = null,
    val pecho: Double? = null,
    val espalda: Double? = null,
    val brazo: Double? = null,
    val muslo: Double? = null,
    val hombro: Double? = null,
    val cintura: Double? = null
)
