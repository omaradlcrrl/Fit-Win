package org.example.fitwinkmp.features.training.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.fitwinkmp.features.training.data.dto.EjercicioGlobalDTO
import org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO
import org.example.fitwinkmp.features.training.data.dto.SesionEntrenamientoDTO
import org.example.fitwinkmp.features.training.data.dto.RutinaDTO
import org.example.fitwinkmp.features.training.data.dto.EjercicioDTO

class TrainingApi(private val httpClient: HttpClient) {

    suspend fun iniciarSesion(sesionDTO: SesionEntrenamientoDTO): SesionEntrenamientoDTO {
        return httpClient.post("sesiones/iniciar") {
            setBody(sesionDTO)
        }.body()
    }

    suspend fun finalizarSesion(id: Int, sesionDTO: SesionEntrenamientoDTO): SesionEntrenamientoDTO {
        return httpClient.put("sesiones/finalizar/$id") {
            setBody(sesionDTO)
        }.body()
    }

    suspend fun registrarSerie(serieDTO: SerieRealizadaDTO): SerieRealizadaDTO {
        return httpClient.post("series/save") {
            setBody(serieDTO)
        }.body()
    }

    suspend fun getEjerciciosGlobales(): List<EjercicioGlobalDTO> {
        return httpClient.get("ejercicios-globales").body()
    }
    
    suspend fun getRutinas(usuarioId: Int): List<RutinaDTO> {
        return httpClient.get("rutinas") {
            parameter("usuarioId", usuarioId)
        }.body()
    }
    
    suspend fun saveRutina(rutina: RutinaDTO): RutinaDTO {
        return httpClient.post("rutinas/save") {
            setBody(rutina)
        }.body()
    }
    
    suspend fun deleteRutina(id: Int) {
        httpClient.delete("rutinas/$id")
    }
    
    suspend fun saveEjercicio(ejercicio: EjercicioDTO): EjercicioDTO {
        return httpClient.post("ejercicios/save") {
            setBody(ejercicio)
        }.body()
    }
    
    suspend fun getEjerciciosHoy(usuarioId: Int, diaSemana: String): List<EjercicioDTO> {
        return httpClient.get("ejercicios") {
            parameter("usuarioId", usuarioId)
            parameter("diaSemana", diaSemana)
        }.body()
    }

    suspend fun getEjerciciosPorRutina(rutinaId: Int): List<EjercicioDTO> {
        return httpClient.get("ejercicios") {
            parameter("rutinaId", rutinaId)
        }.body()
    }

    suspend fun updateRutina(id: Int, rutina: RutinaDTO): RutinaDTO =
        httpClient.put("rutinas/actualizar/$id") {
            setBody(rutina)
        }.body()

    suspend fun deleteEjercicio(id: Int) {
        httpClient.delete("ejercicios/$id")
    }

    suspend fun updateSerie(id: Int, serieDTO: SerieRealizadaDTO): SerieRealizadaDTO =
        httpClient.put("series/actualizar/$id") {
            setBody(serieDTO)
        }.body()

    suspend fun deleteSerie(id: Int) {
        httpClient.delete("series/$id")
    }

    suspend fun deleteSesion(id: Int) {
        httpClient.delete("sesiones/$id")
    }
}
