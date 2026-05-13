package org.example.fitwinkmp.features.training.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SesionEntrenamientoDTO(
    val sesionId: Int? = null,
    val usuarioId: Int,
    val rutinaId: Int? = null,
    val nombreRutina: String? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val duracionMinutos: Int? = null,
    val nivelIntensidad: Int? = null,
    val nivelRecuperacion: Int? = null,
    val notasUsuario: String? = null
)

@Serializable
data class SerieRealizadaDTO(
    val serieId: Int? = null,
    val sesionId: Int,
    val ejercicioId: Int,
    val pesoKg: Double? = null,
    val repeticionesRealizadas: Int? = null,
    val completado: Boolean,
    val orden: Int
)

@Serializable
data class EjercicioGlobalDTO(
    val ejercicioGlobalId: Int,
    val nombre: String,
    val categoria: String,
    val musculoPrimario: String,
    val musculosSecundarios: String? = null,
    val equipamiento: String,
    val cueCoaching: String? = null
)

@Serializable
data class RutinaDTO(
    val rutinaId: Int? = null,
    val nombre: String,
    val etiqueta: String? = null,
    val diasActivos: String? = null,
    val duracionEstimadaMin: Int? = null,
    val fechaCreacion: String? = null,
    val usuarioId: Int
)

@Serializable
data class EjercicioDTO(
    val ejercicioId: Int? = null,
    val ejercicioGlobalId: Int,
    val nombreEjercicio: String? = null,
    val rutinaId: Int,
    val usuarioId: Int,
    val diaSemana: String,
    val series: Int,
    val repeticionesMin: Int,
    val repeticionesMax: Int,
    val descansoSegundos: Int,
    val pesoKg: Double? = null,
    val posicion: Int
)
