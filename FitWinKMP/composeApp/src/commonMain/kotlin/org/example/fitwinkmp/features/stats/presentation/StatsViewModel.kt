package org.example.fitwinkmp.features.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.fitwinkmp.core.api.buildAuthClient
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.stats.data.StatsRepository
import org.example.fitwinkmp.features.stats.data.api.StatsApi
import org.example.fitwinkmp.features.stats.data.dto.*

sealed class StatsUiState {
    object Loading : StatsUiState()
    data class Success(
        val physiqueData: PhysiqueData,
        val performanceData: PerformanceData,
        val nutritionData: NutritionData,
        val needsCheckIn: Boolean,
        val pesoActual: Double?,
        val medicionHoy: MedicionCorporalDTO? = null,
        val fotos: List<FotoProgresoDTO> = emptyList()
    ) : StatsUiState()
    data class Error(val message: String) : StatsUiState()
}

sealed class ChartUiState {
    object Idle : ChartUiState()
    object Loading : ChartUiState()
    data class Success(val data: ChartData) : ChartUiState()
    data class Error(val message: String) : ChartUiState()
}

class StatsViewModel : ViewModel() {

    private val tokenStorage = TokenStorage()
    private val authClient = buildAuthClient(tokenStorage)
    private val repository = StatsRepository(StatsApi(authClient))

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState

    private val _checkInSuccess = MutableStateFlow(false)
    val checkInSuccess: StateFlow<Boolean> = _checkInSuccess

    private val _checkInError = MutableStateFlow<String?>(null)
    val checkInError: StateFlow<String?> = _checkInError

    // ─── Chart state ─────────────────────────────────────────────────────────
    private val _chartState = MutableStateFlow<ChartUiState>(ChartUiState.Idle)
    val chartState: StateFlow<ChartUiState> = _chartState

    private val _selectedMetrica = MutableStateFlow(ChartMetrica.PESO)
    val selectedMetrica: StateFlow<ChartMetrica> = _selectedMetrica

    private val _selectedRango = MutableStateFlow(ChartRango.DIAS_30)
    val selectedRango: StateFlow<ChartRango> = _selectedRango

    val currentUserId: Int get() = tokenStorage.getUsuarioId() ?: -1

    fun loadStats() {
        val userId = tokenStorage.getUsuarioId() ?: run {
            _uiState.value = StatsUiState.Error("No hay sesión activa")
            return
        }
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading

            val physiqueDeferred = async { repository.getPhysiqueData(userId) }
            val performanceDeferred = async { repository.getPerformanceData(userId) }
            val fotosDeferred = async { repository.getFotosProgreso(userId) }

            val physiqueResult = physiqueDeferred.await()
            val performanceResult = performanceDeferred.await()
            val fotosResult = fotosDeferred.await()

            if (physiqueResult.isFailure && performanceResult.isFailure) {
                _uiState.value = StatsUiState.Error(
                    physiqueResult.exceptionOrNull()?.message ?: "Error cargando stats"
                )
                return@launch
            }

            val physique = physiqueResult.getOrElse { PhysiqueData(null, emptyList(), null, null, null, null, null, null) }
            val performance = performanceResult.getOrElse {
                PerformanceData(emptyList(), emptyList(), 0.0, 0, 0, 0.0, 0.0, 0.0, 0, emptyList())
            }

            val pesoActual = physique.ultimaMedicion?.peso
            val nutritionResult = repository.getNutritionData(userId, pesoActual)
            val nutrition = nutritionResult.getOrElse {
                NutritionData(null, null, null, null, null, 0.0)
            }

            // Medición de hoy para prerellenar el check-in
            val medicionHoy = repository.getMedicionDeHoy(userId)
            val needsCheckIn = medicionHoy == null

            val fotos = fotosResult.getOrElse { emptyList() }

            _uiState.value = StatsUiState.Success(
                physiqueData = physique,
                performanceData = performance,
                nutritionData = nutrition,
                needsCheckIn = needsCheckIn,
                pesoActual = pesoActual,
                medicionHoy = medicionHoy,
                fotos = fotos
            )
        }
    }

    fun saveCheckIn(
        peso: Double?,
        porcentajeGrasa: Double?,
        pecho: Double?,
        espalda: Double?,
        hombro: Double?,
        brazo: Double?,
        muslo: Double?,
        cintura: Double?
    ) {
        val userId = tokenStorage.getUsuarioId() ?: return
        viewModelScope.launch {
            _checkInError.value = null
            val dto = MedicionCorporalDTO(
                usuarioId = userId,
                peso = peso,
                porcentajeGrasa = porcentajeGrasa,
                pecho = pecho,
                espalda = espalda,
                hombro = hombro,
                brazo = brazo,
                muslo = muslo,
                cintura = cintura
            )
            // Usa save-or-update automático
            repository.saveOrUpdateMedicion(dto).fold(
                onSuccess = {
                    _checkInSuccess.value = true
                    loadStats()
                },
                onFailure = { e ->
                    _checkInError.value = e.message ?: "Error guardando medición"
                }
            )
        }
    }

    // ─── Chart actions ───────────────────────────────────────────────────────

    /** Abre la gráfica para una métrica específica — se llama al tocar una medida en PhysiqueTab */
    fun openChart(metrica: ChartMetrica) {
        _selectedMetrica.value = metrica
        loadChart()
    }

    fun setRango(rango: ChartRango) {
        _selectedRango.value = rango
        loadChart()
    }

    fun setMetrica(metrica: ChartMetrica) {
        _selectedMetrica.value = metrica
        loadChart()
    }

    fun loadChart() {
        val userId = tokenStorage.getUsuarioId() ?: return
        viewModelScope.launch {
            _chartState.value = ChartUiState.Loading
            repository.getChartData(userId, _selectedMetrica.value, _selectedRango.value).fold(
                onSuccess = { data -> _chartState.value = ChartUiState.Success(data) },
                onFailure = { e -> _chartState.value = ChartUiState.Error(e.message ?: "Error cargando gráfica") }
            )
        }
    }

    fun resetChart() {
        _chartState.value = ChartUiState.Idle
    }

    fun resetCheckInSuccess() { _checkInSuccess.value = false }
    fun resetCheckInError() { _checkInError.value = null }
}
