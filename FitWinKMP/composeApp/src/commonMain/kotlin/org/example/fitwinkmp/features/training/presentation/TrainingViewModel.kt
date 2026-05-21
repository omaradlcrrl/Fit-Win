package org.example.fitwinkmp.features.training.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.fitwinkmp.core.api.buildAuthClient
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.training.data.TrainingRepository
import org.example.fitwinkmp.features.training.data.api.TrainingApi
import org.example.fitwinkmp.features.training.data.dto.EjercicioDTO
import org.example.fitwinkmp.features.training.data.dto.EjercicioGlobalDTO
import org.example.fitwinkmp.features.training.data.dto.RutinaDTO
import org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO
import org.example.fitwinkmp.features.training.data.dto.SesionEntrenamientoDTO

sealed class TrainingUiState {
    object Idle : TrainingUiState()
    object Loading : TrainingUiState()
    data class DailyWorkoutView(
        val ejercicios: List<EjercicioDTO>,
        val rutinaActiva: RutinaDTO? = null,
        val rutinasDisponibles: List<RutinaDTO> = emptyList()
    ) : TrainingUiState()
    data class RoutineBuilder(val ejerciciosGlobales: List<EjercicioGlobalDTO>) : TrainingUiState()
    data class ActiveWorkoutSession(
        val sesion: SesionEntrenamientoDTO,
        val ejercicios: List<EjercicioDTO>,
        val seriesRealizadas: List<SerieRealizadaDTO> = emptyList()
    ) : TrainingUiState()
    data class Error(val message: String) : TrainingUiState()
}

class TrainingViewModel : ViewModel() {

    private val tokenStorage = TokenStorage()
    private val authClient = buildAuthClient(tokenStorage)
    private val api = TrainingApi(authClient)
    private val repository = TrainingRepository(api)

    private val _uiState = MutableStateFlow<TrainingUiState>(TrainingUiState.Idle)
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    val currentUserId: Int
        get() = tokenStorage.getUsuarioId() ?: -1

    init {
        loadTodaysWorkout()
    }

    fun loadTodaysWorkout() {
        val uid = currentUserId
        if (uid == -1) {
            _uiState.value = TrainingUiState.Error("No hay sesión activa")
            return
        }
        viewModelScope.launch {
            _uiState.value = TrainingUiState.Loading
            val diaActual = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name

            repository.getRutinas(uid).fold(
                onSuccess = { rutinas ->
                    val savedActiveId = tokenStorage.getActiveRutinaId()
                    val activeRutina = rutinas.find { it.rutinaId == savedActiveId } 
                        ?: rutinas.firstOrNull()?.also { it.rutinaId?.let { id -> tokenStorage.saveActiveRutinaId(id) } }

                    if (activeRutina == null) {
                        _uiState.value = TrainingUiState.DailyWorkoutView(emptyList(), null, emptyList())
                    } else {
                        val actId = activeRutina.rutinaId ?: 0
                        repository.getEjerciciosPorRutina(actId).fold(
                            onSuccess = { ejercicios ->
                                val ejerciciosHoy = ejercicios.filter { it.diaSemana.equals(diaActual, ignoreCase = true) }
                                _uiState.value = TrainingUiState.DailyWorkoutView(ejerciciosHoy, activeRutina, rutinas)
                            },
                            onFailure = {
                                _uiState.value = TrainingUiState.Error(it.message ?: "Error al cargar ejercicios de la rutina activa")
                            }
                        )
                    }
                },
                onFailure = {
                    _uiState.value = TrainingUiState.Error(it.message ?: "Error al cargar rutinas")
                }
            )
        }
    }

    fun setActiveRutina(rutinaId: Int) {
        tokenStorage.saveActiveRutinaId(rutinaId)
        loadTodaysWorkout()
    }

    fun deleteRutina(rutinaId: Int) {
        viewModelScope.launch {
            _uiState.value = TrainingUiState.Loading
            repository.deleteRutina(rutinaId).fold(
                onSuccess = {
                    val activeId = tokenStorage.getActiveRutinaId()
                    if (activeId == rutinaId) {
                        tokenStorage.removeActiveRutinaId()
                    }
                    loadTodaysWorkout()
                },
                onFailure = {
                    _uiState.value = TrainingUiState.Error(it.message ?: "Error al eliminar rutina")
                }
            )
        }
    }

    fun updateRutina(rutinaId: Int, nombre: String, diasActivos: String) {
        val uid = currentUserId
        if (uid == -1) return
        viewModelScope.launch {
            _uiState.value = TrainingUiState.Loading
            val rutina = RutinaDTO(rutinaId = rutinaId, nombre = nombre, diasActivos = diasActivos, usuarioId = uid)
            repository.updateRutina(rutinaId, rutina).fold(
                onSuccess = { loadTodaysWorkout() },
                onFailure = { _uiState.value = TrainingUiState.Error(it.message ?: "Error al editar rutina") }
            )
        }
    }

    fun deleteEjercicio(ejercicioId: Int) {
        viewModelScope.launch {
            repository.deleteEjercicio(ejercicioId).fold(
                onSuccess = { loadTodaysWorkout() },
                onFailure = { _uiState.value = TrainingUiState.Error(it.message ?: "Error al eliminar ejercicio") }
            )
        }
    }

    fun openRoutineBuilder() {
        viewModelScope.launch {
            _uiState.value = TrainingUiState.Loading
            repository.getEjerciciosGlobales().fold(
                onSuccess = { lista ->
                    val ejercicios = if (lista.isEmpty()) EJERCICIOS_BASE else lista
                    _uiState.value = TrainingUiState.RoutineBuilder(ejercicios)
                },
                onFailure = {
                    _uiState.value = TrainingUiState.RoutineBuilder(EJERCICIOS_BASE)
                }
            )
        }
    }

    fun createRutinaAndAssign(nombre: String, diasActivos: String, ejercicios: List<EjercicioDTO>) {
        val uid = currentUserId
        if (uid == -1) return
        
        viewModelScope.launch {
            _uiState.value = TrainingUiState.Loading
            val rutina = RutinaDTO(nombre = nombre, diasActivos = diasActivos, usuarioId = uid)
            
            repository.saveRutina(rutina).fold(
                onSuccess = { rutinaGuardada ->
                    val rutinaId = rutinaGuardada.rutinaId ?: ID_RUTINA_LOCAL
                    ejercicios.forEach { ej ->
                        repository.saveEjercicio(ej.copy(rutinaId = rutinaId, usuarioId = uid))
                    }
                    tokenStorage.saveActiveRutinaId(rutinaId)
                    loadTodaysWorkout()
                },
                onFailure = {
                    val ejerciciosConNombre = ejercicios.mapIndexed { i, ej ->
                        val nombreEj = ej.nombreEjercicio ?: "Ejercicio ${i + 1}"
                        ej.copy(rutinaId = ID_RUTINA_LOCAL, nombreEjercicio = nombreEj)
                    }
                    val rutinaLocal = RutinaDTO(rutinaId = ID_RUTINA_LOCAL, nombre = nombre, diasActivos = diasActivos, usuarioId = uid)
                    val diaActual = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name
                    val ejerciciosHoy = ejerciciosConNombre.filter { it.diaSemana.equals(diaActual, ignoreCase = true) }
                    _uiState.value = TrainingUiState.DailyWorkoutView(ejerciciosHoy, rutinaLocal, listOf(rutinaLocal))
                }
            )
        }
    }

    fun startWorkout() {
        val currentState = _uiState.value
        if (currentState is TrainingUiState.DailyWorkoutView) {
            val uid = currentUserId
            val rutinaId = currentState.rutinaActiva?.rutinaId ?: currentState.ejercicios.firstOrNull()?.rutinaId ?: 1
            viewModelScope.launch {
                _uiState.value = TrainingUiState.Loading
                repository.iniciarSesion(uid, rutinaId).fold(
                    onSuccess = { sesion ->
                        _uiState.value = TrainingUiState.ActiveWorkoutSession(
                            sesion = sesion,
                            ejercicios = currentState.ejercicios
                        )
                    },
                    onFailure = {
                        val sesionLocal = SesionEntrenamientoDTO(sesionId = ID_SESION_LOCAL, usuarioId = uid, rutinaId = rutinaId)
                        _uiState.value = TrainingUiState.ActiveWorkoutSession(
                            sesion = sesionLocal,
                            ejercicios = currentState.ejercicios
                        )
                    }
                )
            }
        }
    }

    fun logSet(ejercicioId: Int, peso: Double, reps: Int, orden: Int) {
        val currentState = _uiState.value
        if (currentState is TrainingUiState.ActiveWorkoutSession) {
            val sesionId = currentState.sesion.sesionId ?: ID_SESION_LOCAL
            val serie = SerieRealizadaDTO(
                sesionId = sesionId,
                ejercicioId = ejercicioId,
                pesoKg = peso,
                repeticionesRealizadas = reps,
                completado = true,
                orden = orden
            )
            viewModelScope.launch {
                repository.registrarSerie(serie).fold(
                    onSuccess = { savedSerie ->
                        _uiState.value = currentState.copy(
                            seriesRealizadas = currentState.seriesRealizadas + savedSerie
                        )
                    },
                    onFailure = {
                        _uiState.value = currentState.copy(
                            seriesRealizadas = currentState.seriesRealizadas + serie.copy(serieId = Random.nextInt(0, 1001))
                        )
                    }
                )
            }
        }
    }

    fun finalizarSesion(intensidad: Int, recuperacion: Int, notas: String) {
        val currentState = _uiState.value
        if (currentState is TrainingUiState.ActiveWorkoutSession) {
            val sesionId = currentState.sesion.sesionId ?: ID_SESION_LOCAL
            viewModelScope.launch {
                _uiState.value = TrainingUiState.Loading
                repository.finalizarSesion(sesionId, intensidad, recuperacion, notas).fold(
                    onSuccess = { loadTodaysWorkout() },
                    onFailure = { loadTodaysWorkout() }
                )
            }
        }
    }

    fun deleteSerie(serieId: Int) {
        val currentState = _uiState.value
        if (currentState is TrainingUiState.ActiveWorkoutSession) {
            viewModelScope.launch {
                repository.deleteSerie(serieId).fold(
                    onSuccess = {
                        _uiState.value = currentState.copy(
                            seriesRealizadas = currentState.seriesRealizadas.filter { it.serieId != serieId }
                        )
                    },
                    onFailure = { /* se mantiene el estado actual */ }
                )
            }
        }
    }

    fun deleteSesion(sesionId: Int) {
        viewModelScope.launch {
            repository.deleteSesion(sesionId)
        }
    }

    companion object {
        private const val ID_RUTINA_LOCAL = 999
        private const val ID_SESION_LOCAL = 999

        val EJERCICIOS_BASE = listOf(
            EjercicioGlobalDTO(ejercicioGlobalId = 1, nombre = "Press Banca", categoria = "FUERZA", musculoPrimario = "Pecho", equipamiento = "Barra"),
            EjercicioGlobalDTO(ejercicioGlobalId = 2, nombre = "Sentadilla", categoria = "FUERZA", musculoPrimario = "Piernas", equipamiento = "Barra"),
            EjercicioGlobalDTO(ejercicioGlobalId = 3, nombre = "Dominadas", categoria = "FUERZA", musculoPrimario = "Espalda", equipamiento = "Peso Corporal")
        )
    }
}
