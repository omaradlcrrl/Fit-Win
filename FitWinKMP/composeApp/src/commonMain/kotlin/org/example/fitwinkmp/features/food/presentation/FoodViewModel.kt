package org.example.fitwinkmp.features.food.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.fitwinkmp.core.api.buildAuthClient
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.food.data.FoodRepository
import org.example.fitwinkmp.features.food.data.api.FoodApi
import org.example.fitwinkmp.features.food.data.dto.ComidaDTO
import org.example.fitwinkmp.features.stats.data.dto.ObjetivoStatsDTO
import java.time.LocalDate

sealed class FoodUiState {
    object Loading : FoodUiState()
    data class Success(
        val comidas: List<ComidaDTO>,
        val objetivo: ObjetivoStatsDTO? = null
    ) : FoodUiState()
    data class Error(val message: String) : FoodUiState()
}

class FoodViewModel : ViewModel() {

    private val tokenStorage = TokenStorage()
    private val authClient = buildAuthClient(tokenStorage)
    private val api = FoodApi(authClient)
    private val repository = FoodRepository(api)

    private val _uiState = MutableStateFlow<FoodUiState>(FoodUiState.Loading)
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val currentUserId: Int
        get() = tokenStorage.getUsuarioId() ?: -1

    fun loadComidas() {
        val uid = currentUserId
        if (uid == -1) {
            _uiState.value = FoodUiState.Error("No hay sesión activa")
            return
        }
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val fecha = _selectedDate.value
            val isToday = fecha == LocalDate.now()

            val comidasDeferred = async {
                if (isToday) repository.getComidasHoy(uid)
                else repository.getComidasFecha(uid, fecha.toString())
            }
            val objetivoDeferred = async { repository.getObjetivoActual(uid) }

            val comidasResult = comidasDeferred.await()
            val objetivoResult = objetivoDeferred.await()

            comidasResult.onSuccess { comidas ->
                _uiState.value = FoodUiState.Success(
                    comidas = comidas,
                    objetivo = objetivoResult.getOrNull()
                )
            }.onFailure { error ->
                _uiState.value = FoodUiState.Error(error.message ?: "Error al cargar comidas")
            }
        }
    }

    fun goToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
        loadComidas()
    }

    fun goToNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
        loadComidas()
    }

    fun saveComida(comidaDTO: ComidaDTO) {
        viewModelScope.launch {
            repository.saveComida(comidaDTO).onSuccess {
                loadComidas()
            }.onFailure { error ->
                _uiState.value = FoodUiState.Error(error.message ?: "Error al guardar comida")
            }
        }
    }

    fun updateComida(comidaId: Int, comidaDTO: ComidaDTO) {
        viewModelScope.launch {
            repository.updateComida(comidaId, comidaDTO).onSuccess {
                loadComidas()
            }.onFailure { error ->
                _uiState.value = FoodUiState.Error(error.message ?: "Error al actualizar comida")
            }
        }
    }

    fun deleteComida(comidaId: Int) {
        viewModelScope.launch {
            repository.deleteComida(comidaId).onSuccess {
                loadComidas()
            }.onFailure { error ->
                _uiState.value = FoodUiState.Error(error.message ?: "Error al eliminar comida")
            }
        }
    }
}
