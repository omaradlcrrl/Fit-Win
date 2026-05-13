package org.example.fitwinkmp.features.stats.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.fitwinkmp.features.stats.data.dto.*

class StatsApi(private val httpClient: HttpClient) {

    // ─── Mediciones Corporales ───────────────────────────────────────────────
    suspend fun getUltimaMedicion(usuarioId: Int): MedicionCorporalDTO =
        httpClient.get("mediciones/ultima/$usuarioId").body()

    suspend fun getMedicionesByRange(
        usuarioId: Int,
        desde: String,
        hasta: String
    ): List<MedicionCorporalDTO> =
        httpClient.get("mediciones/range/$usuarioId") {
            parameter("desde", desde)
            parameter("hasta", hasta)
        }.body()

    suspend fun saveMedicion(dto: MedicionCorporalDTO): MedicionCorporalDTO =
        httpClient.post("mediciones/save") {
            setBody(dto)
        }.body()

    // ─── Records Personales ──────────────────────────────────────────────────
    suspend fun getRecords(usuarioId: Int): List<RecordPersonalDTO> =
        httpClient.get("records") {
            parameter("usuarioId", usuarioId)
        }.body()

    // ─── Sesiones de Entrenamiento ───────────────────────────────────────────
    suspend fun getSesiones(usuarioId: Int): List<SesionStatsDTO> =
        httpClient.get("sesiones") {
            parameter("usuarioId", usuarioId)
        }.body()

    // ─── Series Realizadas ───────────────────────────────────────────────────
    suspend fun getSeriesBySesion(sesionId: Int): List<SerieStatsDTO> =
        httpClient.get("series") {
            parameter("sesionId", sesionId)
        }.body()

    // ─── Comidas ─────────────────────────────────────────────────────────────
    suspend fun getComidasByRange(
        usuarioId: Int,
        desde: String,
        hasta: String
    ): List<ComidaStatsDTO> =
        httpClient.get("comidas/range/$usuarioId") {
            parameter("desde", desde)
            parameter("hasta", hasta)
        }.body()

    suspend fun getComidasHoy(usuarioId: Int): List<ComidaStatsDTO> =
        httpClient.get("comidas/hoy/$usuarioId").body()

    // ─── Objetivos ────────────────────────────────────────────────────────────
    suspend fun getObjetivoActual(usuarioId: Int): ObjetivoStatsDTO =
        httpClient.get("objetivos/actual/$usuarioId").body()

    // ─── Usuario ──────────────────────────────────────────────────────────────
    suspend fun getUsuario(usuarioId: Int): org.example.fitwinkmp.shared.model.Usuario =
        httpClient.get("usuarios/$usuarioId").body()

    // ─── Endpoints nuevos para Fase 1 y 2 ───────────────────────────────────

    /** Todas las mediciones del usuario (sin filtro de rango) — para gráficas completas */
    suspend fun getAllMediciones(usuarioId: Int): List<MedicionCorporalDTO> =
        httpClient.get("mediciones/usuario/$usuarioId").body()

    /** Actualiza una medición existente por ID */
    suspend fun updateMedicion(medicionId: Int, dto: MedicionCorporalDTO): MedicionCorporalDTO =
        httpClient.put("mediciones/actualizar/$medicionId") {
            setBody(dto)
        }.body()

    /** Borra la medición de hoy del usuario */
    suspend fun deleteMedicionHoy(usuarioId: Int): String =
        httpClient.delete("mediciones/deleteHoy/$usuarioId").body()

    // ─── Fotos de Progreso ────────────────────────────────────────────────────

    suspend fun getFotosProgreso(usuarioId: Int): List<FotoProgresoDTO> =
        httpClient.get("fotos-progreso") {
            parameter("usuarioId", usuarioId)
        }.body()

    suspend fun saveFotoProgreso(dto: FotoProgresoDTO): FotoProgresoDTO =
        httpClient.post("fotos-progreso/save") {
            setBody(dto)
        }.body()

    suspend fun deleteFotoProgreso(fotoId: Int): String =
        httpClient.delete("fotos-progreso/$fotoId").body()

    // ─── Records ─────────────────────────────────────────────────────────────
    suspend fun saveRecord(dto: RecordPersonalDTO): RecordPersonalDTO =
        httpClient.post("records/save") {
            setBody(dto)
        }.body()
}
