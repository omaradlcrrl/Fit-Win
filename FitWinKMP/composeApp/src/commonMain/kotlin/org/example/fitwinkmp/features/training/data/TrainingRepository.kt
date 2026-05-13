package org.example.fitwinkmp.features.training.data

import org.example.fitwinkmp.features.training.data.api.TrainingApi
import org.example.fitwinkmp.features.training.data.dto.EjercicioGlobalDTO
import org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO
import org.example.fitwinkmp.features.training.data.dto.SesionEntrenamientoDTO

class TrainingRepository(private val api: TrainingApi) {

    suspend fun iniciarSesion(usuarioId: Int, rutinaId: Int? = null): Result<SesionEntrenamientoDTO> {
        return try {
            val dto = SesionEntrenamientoDTO(usuarioId = usuarioId, rutinaId = rutinaId)
            Result.success(api.iniciarSesion(dto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finalizarSesion(
        sesionId: Int,
        intensidad: Int,
        recuperacion: Int,
        notas: String?
    ): Result<SesionEntrenamientoDTO> {
        return try {
            val dto = SesionEntrenamientoDTO(
                usuarioId = 0, // Ignorado en finalizar en el backend
                nivelIntensidad = intensidad,
                nivelRecuperacion = recuperacion,
                notasUsuario = notas
            )
            Result.success(api.finalizarSesion(sesionId, dto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarSerie(serie: SerieRealizadaDTO): Result<SerieRealizadaDTO> {
        return try {
            Result.success(api.registrarSerie(serie))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEjerciciosGlobales(): Result<List<EjercicioGlobalDTO>> {
        return try {
            Result.success(api.getEjerciciosGlobales())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveRutina(rutina: org.example.fitwinkmp.features.training.data.dto.RutinaDTO): Result<org.example.fitwinkmp.features.training.data.dto.RutinaDTO> {
        return try {
            Result.success(api.saveRutina(rutina))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRutina(id: Int): Result<Unit> {
        return try {
            api.deleteRutina(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveEjercicio(ejercicio: org.example.fitwinkmp.features.training.data.dto.EjercicioDTO): Result<org.example.fitwinkmp.features.training.data.dto.EjercicioDTO> {
        return try {
            Result.success(api.saveEjercicio(ejercicio))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getEjerciciosHoy(usuarioId: Int, diaSemana: String): Result<List<org.example.fitwinkmp.features.training.data.dto.EjercicioDTO>> {
        return try {
            Result.success(api.getEjerciciosHoy(usuarioId, diaSemana))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRutinas(usuarioId: Int): Result<List<org.example.fitwinkmp.features.training.data.dto.RutinaDTO>> {
        return try {
            Result.success(api.getRutinas(usuarioId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEjerciciosPorRutina(rutinaId: Int): Result<List<org.example.fitwinkmp.features.training.data.dto.EjercicioDTO>> {
        return try {
            Result.success(api.getEjerciciosPorRutina(rutinaId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRutina(id: Int, rutina: org.example.fitwinkmp.features.training.data.dto.RutinaDTO): Result<org.example.fitwinkmp.features.training.data.dto.RutinaDTO> {
        return try { Result.success(api.updateRutina(id, rutina)) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteEjercicio(id: Int): Result<Unit> {
        return try { api.deleteEjercicio(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateSerie(id: Int, serie: org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO): Result<org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO> {
        return try { Result.success(api.updateSerie(id, serie)) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteSerie(id: Int): Result<Unit> {
        return try { api.deleteSerie(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteSesion(id: Int): Result<Unit> {
        return try { api.deleteSesion(id); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }
}
