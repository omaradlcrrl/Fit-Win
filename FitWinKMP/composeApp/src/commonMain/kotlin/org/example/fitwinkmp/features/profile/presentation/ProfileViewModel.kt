package org.example.fitwinkmp.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.fitwinkmp.core.api.buildAuthClient
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.auth.data.AuthRepository
import org.example.fitwinkmp.features.profile.data.ProfileRepository
import org.example.fitwinkmp.features.profile.data.api.ProfileApi
import org.example.fitwinkmp.features.profile.data.dto.UpdateUsuarioRequest

class ProfileViewModel : ViewModel() {

    private val tokenStorage = TokenStorage()
    private val authClient = buildAuthClient(tokenStorage)
    private val profileRepository = ProfileRepository(ProfileApi(authClient))
    private val authRepository = AuthRepository(
        publicClient = org.example.fitwinkmp.core.api.buildPublicClient(),
        authClient = authClient,
        tokenStorage = tokenStorage
    )

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _logoutDone = MutableStateFlow(false)
    val logoutDone: StateFlow<Boolean> = _logoutDone

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    val currentUserId: Int get() = tokenStorage.getUsuarioId() ?: -1

    fun loadProfile() {
        val userId = tokenStorage.getUsuarioId() ?: run {
            _uiState.value = ProfileUiState.Error("No hay sesión activa")
            return
        }
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val usuarioDeferred = async { profileRepository.getProfile(userId) }
            val objetivoDeferred = async { profileRepository.getObjetivoActual(userId) }
            val medicionDeferred = async { profileRepository.getUltimaMedicion(userId) }

            val usuarioResult = usuarioDeferred.await()
            val objetivoResult = objetivoDeferred.await()
            val medicionResult = medicionDeferred.await()

            if (usuarioResult.isFailure) {
                _uiState.value = ProfileUiState.Error(
                    usuarioResult.exceptionOrNull()?.message ?: "Error cargando perfil"
                )
                return@launch
            }

            _uiState.value = ProfileUiState.Success(
                usuario = usuarioResult.getOrThrow(),
                objetivo = objetivoResult.getOrNull(),
                ultimaMedicion = medicionResult.getOrNull()
            )
        }
    }

    fun updateProfile(request: UpdateUsuarioRequest) {
        val userId = tokenStorage.getUsuarioId() ?: return
        viewModelScope.launch {
            _updateSuccess.value = false
            profileRepository.updateProfile(userId, request).fold(
                onSuccess = { updatedUser ->
                    val current = _uiState.value
                    if (current is ProfileUiState.Success) {
                        _uiState.value = current.copy(usuario = updatedUser)
                    }
                    _updateSuccess.value = true
                },
                onFailure = {
                    // Keep current state, error could be shown via snackbar
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutDone.value = true
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun generarObjetivo() {
        val userId = tokenStorage.getUsuarioId() ?: return
        viewModelScope.launch {
            profileRepository.generarObjetivo(userId).fold(
                onSuccess = { loadProfile() },
                onFailure = { /* silent fail — objetivo opcional */ }
            )
        }
    }
}
