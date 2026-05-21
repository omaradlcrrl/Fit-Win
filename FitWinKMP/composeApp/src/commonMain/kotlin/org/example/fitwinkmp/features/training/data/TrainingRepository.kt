package org.example.fitwinkmp.features.training.data

import org.example.fitwinkmp.features.training.data.api.TrainingApi
import org.example.fitwinkmp.features.training.data.dto.EjercicioDTO
import org.example.fitwinkmp.features.training.data.dto.EjercicioGlobalDTO
import org.example.fitwinkmp.features.training.data.dto.RutinaDTO
import org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO
import org.example.fitwinkmp.features.training.data.dto.SesionEntrenamientoDTO

class TrainingRepository(private val api: TrainingApi) {

    suspend fun iniciarSesion(usuarioId: Int, rutinaId: Int? = null): Result<SesionEntrenamientoDTO> = runCatching {
        val dto = SesionEntrenamientoDTO(usuarioId = usuarioId, rutinaId = rutinaId)
        api.iniciarSesion(dto)
    }

    suspend fun finalizarSesion(
        sesionId: Int,
        intensidad: Int,
        recuperacion: Int,
        notas: String?
    ): Result<SesionEntrenamientoDTO> = runCatching {
        val dto = SesionEntrenamientoDTO(
            usuarioId = 0, // el backend lo ignora al finalizar
            nivelIntensidad = intensidad,
            nivelRecuperacion = recuperacion,
            notasUsuario = notas
        )
        api.finalizarSesion(sesionId, dto)
    }

    suspend fun registrarSerie(serie: SerieRealizadaDTO): Result<SerieRealizadaDTO> = runCatching {
        api.registrarSerie(serie)
    }

    suspend fun getEjerciciosGlobales(): Result<List<EjercicioGlobalDTO>> = runCatching {
        api.getEjerciciosGlobales()
    }

    suspend fun saveRutina(rutina: RutinaDTO): Result<RutinaDTO> = runCatching {
        api.saveRutina(rutina)
    }

    suspend fun deleteRutina(id: Int): Result<Unit> = runCatching {
        api.deleteRutina(id)
    }

    suspend fun saveEjercicio(ejercicio: EjercicioDTO): Result<EjercicioDTO> = runCatching {
        api.saveEjercicio(ejercicio)
    }

    suspend fun getEjerciciosHoy(usuarioId: Int, diaSemana: String): Result<List<EjercicioDTO>> = runCatching {
        api.getEjerciciosHoy(usuarioId, diaSemana)
    }

    suspend fun getRutinas(usuarioId: Int): Result<List<RutinaDTO>> = runCatching {
        api.getRutinas(usuarioId)
    }

    suspend fun getEjerciciosPorRutina(rutinaId: Int): Result<List<EjercicioDTO>> = runCatching {
        api.getEjerciciosPorRutina(rutinaId)
    }

    suspend fun updateRutina(id: Int, rutina: RutinaDTO): Result<RutinaDTO> = runCatching {
        api.updateRutina(id, rutina)
    }

    suspend fun deleteEjercicio(id: Int): Result<Unit> = runCatching {
        api.deleteEjercicio(id)
    }

    suspend fun updateSerie(id: Int, serie: SerieRealizadaDTO): Result<SerieRealizadaDTO> = runCatching {
        api.updateSerie(id, serie)
    }

    suspend fun deleteSerie(id: Int): Result<Unit> = runCatching {
        api.deleteSerie(id)
    }

    suspend fun deleteSesion(id: Int): Result<Unit> = runCatching {
        api.deleteSesion(id)
    }
}
