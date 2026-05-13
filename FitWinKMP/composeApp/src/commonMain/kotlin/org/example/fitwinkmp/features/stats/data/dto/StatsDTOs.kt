package org.example.fitwinkmp.features.stats.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicionCorporalDTO(
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

@Serializable
data class RecordPersonalDTO(
    val recordId: Int? = null,
    val ejercicioGlobalId: Int? = null,
    val nombreEjercicio: String? = null,
    val usuarioId: Int? = null,
    val pesoKg: Double? = null,
    val repeticiones: Int? = null,
    val fecha: String? = null
)

@Serializable
data class SesionStatsDTO(
    val sesionId: Int? = null,
    val rutinaId: Int? = null,
    val nombreRutina: String? = null,
    val usuarioId: Int? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val duracionMinutos: Int? = null,
    val nivelIntensidad: Int? = null,
    val nivelRecuperacion: Int? = null,
    val notasUsuario: String? = null
)

@Serializable
data class SerieStatsDTO(
    val serieId: Int? = null,
    val sesionId: Int? = null,
    val ejercicioId: Int? = null,
    val nombreEjercicio: String? = null,
    val pesoKg: Double? = null,
    val repeticionesRealizadas: Int? = null,
    val completado: Boolean? = null,
    val orden: Int? = null
)

@Serializable
data class ComidaStatsDTO(
    val comidaId: Int? = null,
    val usuarioId: Int? = null,
    val nombre: String? = null,
    val calorias: Double? = null,
    val proteinas: Double? = null,
    val carbohidratos: Double? = null,
    val grasasSaturadas: Double? = null,
    val tipoComida: String? = null,
    val cantidad: Double? = null,
    val unidad: String? = null,
    val fecha: String? = null
)

@Serializable
data class ObjetivoStatsDTO(
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

// Domain models calculados en cliente
data class PhysiqueData(
    val ultimaMedicion: MedicionCorporalDTO?,
    val historialMediciones: List<MedicionCorporalDTO>,
    val imc: Double?,
    val ffmi: Double?,
    val ratioCinturaHombro: Double?,
    val tendenciaPeso: Double?,       // % cambio último mes
    val tendenciaGrasa: Double?,
    val alturaUsuario: Double?
)

data class PerformanceData(
    val sesiones: List<SesionStatsDTO>,
    val records: List<RecordPersonalDTO>,
    val tonelajeMensual: Double,
    val frecuenciaUltimaSemana: Int,    // sesiones en últimos 7 días
    val frecuenciaUltimoMes: Int,       // sesiones en últimos 30 días
    val duracionMediaMin: Double,
    val intensidadMedia: Double,
    val recuperacionMedia: Double,
    val rachaActual: Int,               // días consecutivos
    val mejoresRecords: List<RecordConRm>
)

data class RecordConRm(
    val nombreEjercicio: String,
    val pesoKg: Double,
    val repeticiones: Int?,
    val rmEstimado: Double,             // Fórmula Epley
    val fecha: String?
)

data class NutritionData(
    val adherenciaCaloricaHoy: Double?, // %
    val adherenciaProteinasHoy: Double?,
    val adherenciaCarbosHoy: Double?,
    val adherenciaGrasasHoy: Double?,
    val proteinaPorKg: Double?,
    val caloriasMediasSemana: Double
)

// ─── Fotos de Progreso ────────────────────────────────────────────────────────

@Serializable
data class FotoProgresoDTO(
    val fotoId: Int? = null,
    val urlFoto: String? = null,
    val tipoFoto: String? = null,  // FRONTAL, LATERAL_DERECHA, LATERAL_IZQUIERDA, ESPALDA
    val fecha: String? = null,
    val usuarioId: Int? = null
)

// ─── Modelos de dominio para gráficas ─────────────────────────────────────────

/** Un punto en una gráfica: fecha ISO (YYYY-MM-DD) + valor */
data class ChartPoint(
    val fecha: String,
    val valor: Double
)

/** Conjunto de datos de una gráfica con estadísticas precalculadas */
data class ChartData(
    val puntos: List<ChartPoint>,
    val metrica: ChartMetrica,
    val min: Double,
    val max: Double,
    val media: Double,
    val primero: Double?,
    val ultimo: Double?,
    val cambioAbsoluto: Double?,    // ultimo - primero
    val cambioPorcentaje: Double?   // % cambio
)

/** Métricas disponibles para graficar (todas extraídas de MedicionCorporal) */
enum class ChartMetrica(
    val label: String,
    val campo: String,
    val unidad: String
) {
    PESO("Peso", "peso", "kg"),
    PORCENTAJE_GRASA("% Grasa", "porcentajeGrasa", "%"),
    MASA_MAGRA("Masa Magra", "masaMagra", "kg"),
    PECHO("Pecho", "pecho", "cm"),
    ESPALDA("Espalda", "espalda", "cm"),
    HOMBRO("Hombro", "hombro", "cm"),
    BRAZO("Brazo", "brazo", "cm"),
    MUSLO("Muslo", "muslo", "cm"),
    CINTURA("Cintura", "cintura", "cm");

    /** Extrae el valor de esta métrica de un DTO de medición */
    fun extractValue(m: MedicionCorporalDTO): Double? = when (this) {
        PESO -> m.peso
        PORCENTAJE_GRASA -> m.porcentajeGrasa
        MASA_MAGRA -> m.masaMagra
        PECHO -> m.pecho
        ESPALDA -> m.espalda
        HOMBRO -> m.hombro
        BRAZO -> m.brazo
        MUSLO -> m.muslo
        CINTURA -> m.cintura
    }
}

/** Rangos temporales para filtrar las gráficas */
enum class ChartRango(val label: String, val dias: Int?) {
    DIAS_7("7D", 7),
    DIAS_30("30D", 30),
    DIAS_90("90D", 90),
    TODO("TODO", null)
}
