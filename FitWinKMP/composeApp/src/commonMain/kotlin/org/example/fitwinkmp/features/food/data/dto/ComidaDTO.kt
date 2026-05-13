package org.example.fitwinkmp.features.food.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ComidaDTO(
    val comidaId: Int? = null,
    val usuarioId: Int,
    val nombre: String,
    val calorias: Double,
    val proteinas: Double,
    val carbohidratos: Double,
    val grasasSaturadas: Double,
    val tipoComida: String, // DESAYUNO, ALMUERZO, CENA, SNACK
    val cantidad: Double,
    val unidad: String, // GRAMOS, ML, UNIDAD
    val fecha: String // YYYY-MM-DD
)
