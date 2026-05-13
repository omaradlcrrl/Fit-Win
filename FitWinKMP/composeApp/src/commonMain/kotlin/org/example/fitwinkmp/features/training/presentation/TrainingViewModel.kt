package org.example.fitwinkmp.features.training.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name
            
            // 1. Fetch user's routines
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
                                val ejerciciosHoy = ejercicios.filter { it.diaSemana.equals(today, ignoreCase = true) }
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
                onSuccess = {
                    val list = if (it.isEmpty()) MOCK_GLOBAL_EXERCISES else it
                    _uiState.value = TrainingUiState.RoutineBuilder(list)
                },
                onFailure = {
                    _uiState.value = TrainingUiState.RoutineBuilder(MOCK_GLOBAL_EXERCISES)
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
                onSuccess = { savedRutina ->
                    val rutinaId = savedRutina.rutinaId ?: 999
                    ejercicios.forEach { ej ->
                        // Ignoramos el resultado para no bloquear la UI si el EjercicioGlobal no existe en BD
                        repository.saveEjercicio(ej.copy(rutinaId = rutinaId, usuarioId = uid))
                    }
                    // Set newly created routine as active
                    tokenStorage.saveActiveRutinaId(rutinaId)
                    loadTodaysWorkout() // Let loadTodaysWorkout fetch everything freshly
                },
                onFailure = {
                    // Fallback puramente local si la API falla por completo
                    val ejerciciosConNombre = ejercicios.mapIndexed { index, ej ->
                        val globalName = ej.nombreEjercicio ?: "Ejercicio ${index+1}"
                        ej.copy(rutinaId = 999, nombreEjercicio = globalName)
                    }
                    val dummyRutina = RutinaDTO(rutinaId = 999, nombre = nombre, diasActivos = diasActivos, usuarioId = uid)
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name
                    val ejerciciosHoy = ejerciciosConNombre.filter { it.diaSemana.equals(today, ignoreCase = true) }
                    _uiState.value = TrainingUiState.DailyWorkoutView(ejerciciosHoy, dummyRutina, listOf(dummyRutina))
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
                        val mockSesion = SesionEntrenamientoDTO(sesionId = 999, usuarioId = uid, rutinaId = rutinaId)
                        _uiState.value = TrainingUiState.ActiveWorkoutSession(
                            sesion = mockSesion,
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
            val sesionId = currentState.sesion.sesionId ?: 999
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
                        // Fallback local
                        _uiState.value = currentState.copy(
                            seriesRealizadas = currentState.seriesRealizadas + serie.copy(serieId = (0..1000).random())
                        )
                    }
                )
            }
        }
    }

    fun finalizarSesion(intensidad: Int, recuperacion: Int, notas: String) {
        val currentState = _uiState.value
        if (currentState is TrainingUiState.ActiveWorkoutSession) {
            val sesionId = currentState.sesion.sesionId ?: 999
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
                    onFailure = { /* keep current state */ }
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
        val MOCK_GLOBAL_EXERCISES = listOf(
            EjercicioGlobalDTO(ejercicioGlobalId = 1, nombre = "Press Banca", categoria = "FUERZA", musculoPrimario = "Pecho", equipamiento = "Barra"),
            EjercicioGlobalDTO(ejercicioGlobalId = 2, nombre = "Sentadilla", categoria = "FUERZA", musculoPrimario = "Piernas", equipamiento = "Barra"),
            EjercicioGlobalDTO(ejercicioGlobalId = 3, nombre = "Dominadas", categoria = "FUERZA", musculoPrimario = "Espalda", equipamiento = "Peso Corporal")
        )
    }
}
