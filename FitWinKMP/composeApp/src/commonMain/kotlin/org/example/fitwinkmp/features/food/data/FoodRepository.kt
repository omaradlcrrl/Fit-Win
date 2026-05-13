package org.example.fitwinkmp.features.food.data

import org.example.fitwinkmp.features.food.data.api.FoodApi
import org.example.fitwinkmp.features.food.data.dto.ComidaDTO
import org.example.fitwinkmp.features.stats.data.dto.ObjetivoStatsDTO

class FoodRepository(private val api: FoodApi) {

    suspend fun getComidasHoy(usuarioId: Int): Result<List<ComidaDTO>> {
        return try {
            Result.success(api.getComidasHoy(usuarioId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComidasFecha(usuarioId: Int, fecha: String): Result<List<ComidaDTO>> {
        return try {
            Result.success(api.getComidasFecha(usuarioId, fecha))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveComida(comidaDTO: ComidaDTO): Result<Unit> {
        return try {
            api.saveComida(comidaDTO)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateComida(id: Int, comidaDTO: ComidaDTO): Result<ComidaDTO> {
        return try {
            Result.success(api.updateComida(id, comidaDTO))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComida(id: Int): Result<Unit> {
        return try {
            api.deleteComida(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getObjetivoActual(usuarioId: Int): Result<ObjetivoStatsDTO> {
        return try {
            Result.success(api.getObjetivoActual(usuarioId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
