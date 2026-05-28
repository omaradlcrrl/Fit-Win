package org.example.fitwinkmp.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.fitwinkmp.core.api.buildAuthClient
import org.example.fitwinkmp.core.api.buildPublicClient
import org.example.fitwinkmp.core.error.ApiException
import org.example.fitwinkmp.core.storage.TokenStorage
import org.example.fitwinkmp.features.auth.data.AuthRepository
import org.example.fitwinkmp.features.auth.data.dto.RegisterRequest
import org.example.fitwinkmp.features.auth.presentation.state.LoginUiState
import org.example.fitwinkmp.features.auth.presentation.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val tokenStorage = TokenStorage()
    private val publicClient = buildPublicClient()
    private val authClient = buildAuthClient(tokenStorage)
    private val repository = AuthRepository(publicClient, authClient, tokenStorage)

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState

    fun login(email: String, password: String) {
        if (email.isBlank() || !email.contains("@")) {
            _loginState.value = LoginUiState.Error("Introduce un email válido")
            return
        }
        if (password.length < 8) {
            _loginState.value = LoginUiState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            repository.login(email.trim(), password).fold(
                onSuccess = { _loginState.value = LoginUiState.Success(it.onboardingCompleto) },
                onFailure = { e ->
                    val msg = when (e) {
                        is ApiException -> when (e.statusCode) {
                            400 -> "Datos inválidos"
                            401 -> "Email o contraseña incorrectos"
                            429 -> "Demasiados intentos, espera un momento"
                            else -> e.message
                        }
                        else -> "Error de conexión: ¿Está la API local (puerto 3036) encendida?"
                    }
                    _loginState.value = LoginUiState.Error(msg)
                }
            )
        }
    }

    fun register(
        nombre: String,
        apellidos: String,
        email: String, 
        password: String,
        genero: String,
        nivelActividad: String,
        pesoActual: String,
        altura: String,
        fechaNacimiento: String,
        objetivo: String
    ) {
        if (nombre.isBlank()) {
            _registerState.value = RegisterUiState.Error("Introduce tu nombre")
            return
        }
        if (apellidos.isBlank()) {
            _registerState.value = RegisterUiState.Error("Introduce tus apellidos")
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _registerState.value = RegisterUiState.Error("Introduce un email válido")
            return
        }
        if (password.length < 8) {
            _registerState.value = RegisterUiState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }
        if (genero.isBlank() || nivelActividad.isBlank() || objetivo.isBlank()) {
            _registerState.value = RegisterUiState.Error("Completa todos los selectores (género, actividad, objetivo)")
            return
        }
        val peso = pesoActual.toDoubleOrNull()
        if (peso == null || peso <= 0) {
            _registerState.value = RegisterUiState.Error("Introduce un peso válido")
            return
        }
        val alt = altura.toDoubleOrNull()
        if (alt == null || alt <= 0) {
            _registerState.value = RegisterUiState.Error("Introduce una altura válida")
            return
        }
        if (fechaNacimiento.isBlank()) {
            _registerState.value = RegisterUiState.Error("Introduce tu fecha de nacimiento")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            repository.register(
                RegisterRequest(
                    nombre = nombre.trim(),
                    apellidos = apellidos.trim(),
                    correoElectronico = email.trim(),
                    password = password,
                    genero = genero,
                    nivelActividad = nivelActividad,
                    pesoActual = peso,
                    altura = alt,
                    fechaNacimiento = fechaNacimiento,
                    objetivo = objetivo,
                    idioma = "es"
                )
            ).fold(
                onSuccess = { _registerState.value = RegisterUiState.Success },
                onFailure = { e ->
                    val msg = when (e) {
                        is ApiException -> e.message ?: "Error del servidor"
                        else -> "Error de conexión: ¿Está la API local (puerto 3036) encendida?"
                    }
                    _registerState.value = RegisterUiState.Error(msg)
                }
            )
        }
    }

    fun resetLoginState() { _loginState.value = LoginUiState.Idle }
    fun resetRegisterState() { _registerState.value = RegisterUiState.Idle }
}
