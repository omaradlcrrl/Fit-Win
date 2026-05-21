package org.example.fitwinkmp.features.food.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.fitwinkmp.features.food.data.dto.ComidaDTO
import org.example.fitwinkmp.features.stats.data.dto.ObjetivoStatsDTO

class FoodApi(private val httpClient: HttpClient) {

    suspend fun saveComida(comidaDTO: ComidaDTO) {
        httpClient.post("comidas/save") {
            setBody(comidaDTO)
        }
    }

    suspend fun getComidasHoy(usuarioId: Int): List<ComidaDTO> {
        return httpClient.get("comidas/hoy/$usuarioId").body()
    }

    suspend fun getComidasFecha(usuarioId: Int, fecha: String): List<ComidaDTO> {
        return httpClient.get("comidas/fecha/$usuarioId") {
            parameter("fecha", fecha)
        }.body()
    }

    suspend fun deleteComida(id: Int) {
        httpClient.delete("comidas/$id")
    }

    suspend fun updateComida(id: Int, comidaDTO: ComidaDTO): ComidaDTO =
        httpClient.put("comidas/actualizar/$id") {
            setBody(comidaDTO)
        }.body()

    suspend fun getObjetivoActual(usuarioId: Int): ObjetivoStatsDTO =
        httpClient.get("objetivos/actual/$usuarioId").body()
}
