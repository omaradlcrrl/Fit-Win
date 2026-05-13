# Fitwin API — Briefing para Agente Kotlin Multiplatform (Auth)

Documento autónomo para un agente de IA que va a implementar **Register + Login** en una app **Kotlin Multiplatform** (Android + iOS, UI con Compose Multiplatform). No requiere acceso al código backend — todo el contrato está aquí.

---

## 1. Configuración base

| Dato | Valor |
| :--- | :--- |
| Host local (dev) | `http://localhost:3036` |
| Base path | `/api/v1/FWBBD` |
| Content-Type | `application/json` |
| Auth header | `Authorization: Bearer <JWT>` |

**URL completa de ejemplo:** `http://localhost:3036/api/v1/FWBBD/usuarios/login`

> Para emulador Android usar `10.0.2.2`. Para simulador iOS usar `localhost`. Para dispositivo físico, IP local del PC en la misma red.

---

## 2. Arquitectura de tokens

### Dos tokens, dos propósitos

| Token | Formato | Duración | Uso |
| :--- | :--- | :--- | :--- |
| **JWT (access)** | HS256 firmado | 24h | Autoriza cada request al backend |
| **Refresh token** | UUID aleatorio | 30 días | Renueva el JWT sin volver a pedir contraseña |

### Regla de oro para el cliente

1. **Todo request autenticado** envía `Authorization: Bearer <JWT>`.
2. **Si el backend responde 401**, el cliente llama a `/auth/refresh` con el refresh token.
3. **El refresh token rota**: la respuesta de `/auth/refresh` trae un **nuevo** refresh token. El anterior queda invalidado en el servidor. **Hay que reemplazar ambos tokens en almacenamiento siempre**.
4. Si `/auth/refresh` también falla (401) → la sesión caducó → borrar tokens y llevar al login.

### Almacenamiento seguro multiplataforma

Opciones recomendadas:

**Opción A — `multiplatform-settings` con encriptación:**
```kotlin
// commonMain
dependencies {
    implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
}
```
- Android: respaldo con `EncryptedSharedPreferences`
- iOS: respaldo con `Keychain`

**Opción B — expect/actual con implementación nativa:**
- `androidMain`: `EncryptedSharedPreferences` (AndroidX Security)
- `iosMain`: `Keychain Services` vía interop

**No usar `SharedPreferences` ni `NSUserDefaults` planos** — no están cifrados.

### Almacenamiento sugerido

```
secure storage:
  ├─ "jwt"          → <JWT actual>
  ├─ "refreshToken" → <refresh token actual>
  └─ "usuarioId"    → <id numérico del usuario>
```

### Patrón con Ktor Client (interceptor de auth)

Ktor tiene un plugin `Auth` con `bearer` provider que gestiona refresh automático:

```kotlin
// commonMain - ApiClient.kt
val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    install(Logging) { level = LogLevel.INFO }
    install(DefaultRequest) {
        url("http://10.0.2.2:3036/api/v1/FWBBD/")
        contentType(ContentType.Application.Json)
    }
    install(Auth) {
        bearer {
            loadTokens {
                val jwt = tokenStorage.getJwt()
                val refresh = tokenStorage.getRefreshToken()
                if (jwt != null && refresh != null) BearerTokens(jwt, refresh) else null
            }
            refreshTokens {
                // Este bloque se ejecuta automáticamente cuando el backend devuelve 401
                val refreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null
                val response: RefreshResponse = client.post("auth/refresh") {
                    markAsRefreshTokenRequest()
                    setBody(RefreshRequest(refreshToken))
                }.body()
                tokenStorage.saveJwt(response.token)
                tokenStorage.saveRefreshToken(response.refreshToken)
                BearerTokens(response.token, response.refreshToken)
            }
        }
    }
}
```

---

## 3. Endpoints de autenticación

### 3.1 Registro — `POST /usuarios/save`

**Público** (no requiere JWT). Excluir del plugin `Auth` con `.excludeAuth()` o usar un `HttpClient` sin auth para estos endpoints.

**Request:**
```json
{
  "nombre": "Omar",
  "apellidos": "Adel",
  "correoElectronico": "omar@fitwin.com",
  "password": "secreto123",
  "fechaNacimiento": "2000-05-15",
  "altura": 178.0,
  "genero": "MASCULINO",
  "nivelActividad": "MODERADO",
  "pesoActual": 75.0,
  "objetivo": "GANAR_MUSCULO",
  "idioma": "es"
}
```

**Validaciones backend:**
- `password`: 6–60 caracteres (hasheada con BCrypt en servidor)
- `correoElectronico`: único en la BD, formato email
- El resto: opcionales (se completan en onboarding)

**Response 201 Created:**
```json
{
  "usuarioId": 1,
  "nombre": "Omar",
  "apellidos": "Adel",
  "correoElectronico": "omar@fitwin.com",
  "fechaNacimiento": "2000-05-15",
  "fechaRegistro": "2025-04-17",
  "altura": 178.0,
  "idioma": "es",
  "estrategia": null,
  "ajusteCalorico": null,
  "genero": "MASCULINO",
  "nivelActividad": "MODERADO",
  "pesoActual": 75.0,
  "objetivo": "GANAR_MUSCULO",
  "onboardingCompleto": false
}
```

**IMPORTANTE:** El registro **no devuelve tokens**. Flujo estándar: registrar → redirigir a login. Alternativa (login automático post-registro): hacer `POST /usuarios/login` inmediatamente con las credenciales.

**`password` es WRITE_ONLY:** entra en el request pero **jamás** aparece en respuestas. El campo no existe en los JSON de respuesta.

**Errores:**
- `400` — Email duplicado, password < 6 chars, email con formato inválido
- `429` — Rate limit 50 req/min
- `500` — Error no controlado

---

### 3.2 Login — `POST /usuarios/login`

**Público**.

**Request:**
```json
{
  "correoElectronico": "omar@fitwin.com",
  "password": "secreto123"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "usuarioId": 1,
  "nombre": "Omar",
  "correoElectronico": "omar@fitwin.com",
  "onboardingCompleto": false
}
```

**Acción del cliente tras recibir respuesta:**
1. Guardar `token`, `refreshToken`, `usuarioId` en storage seguro.
2. Revisar `onboardingCompleto`:
   - `false` → navegar al wizard de onboarding
   - `true` → navegar al home

**Errores:**
- `400` — Email o password ausentes / mal formados
- `401` — Credenciales inválidas
- `429` — Rate limit

---

### 3.3 Refresh — `POST /auth/refresh`

**Público** (el refresh token es la credencial). Si usas Ktor `bearer` auth, esto lo gestiona el plugin automáticamente.

**Request:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response 200 OK:**
```json
{
  "token": "<nuevo JWT>",
  "refreshToken": "<nuevo UUID>"
}
```

**CRÍTICO:** El `refreshToken` devuelto **es nuevo**. El viejo queda revocado. El cliente debe:
1. Reemplazar `jwt` en storage con el nuevo.
2. Reemplazar `refreshToken` en storage con el nuevo.
3. No cachear el token viejo en ningún sitio.

Si el cliente reutiliza el refresh token antiguo → `401`.

**Errores:**
- `401` — Refresh token inválido, expirado o ya rotado → forzar login manual

---

### 3.4 Logout — `POST /auth/logout?usuarioId={id}`

**Requiere JWT**.

**Request:** sin body, `usuarioId` como query param.

**Response 200 OK:** `"Sesión cerrada correctamente"` (texto plano, `Content-Type: text/plain`)

**Efecto:** el backend revoca **todos** los refresh tokens del usuario (cierra sesión en todos los dispositivos).

**Acción del cliente:** borrar storage + `httpClient.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().firstOrNull()?.clearToken()` → navegar al login.

---

## 4. Formato estándar de errores

Todas las respuestas de error siguen este esquema:

```json
{
  "fecha": "2025-04-17T10:30:00",
  "mensaje": "Descripción legible del error"
}
```

| HTTP | Manejo en la app |
| :--- | :--- |
| `400` | Mostrar `mensaje` al usuario (texto amigable del backend) |
| `401` | Intentar refresh → si falla, ir a login |
| `403` | Acción no permitida |
| `404` | Recurso no encontrado |
| `429` | Toast: "Demasiados intentos, espera un minuto" |
| `500` | Toast genérico: "Error del servidor" |

---

## 5. Enums del dominio

Valores exactos que acepta el backend:

```
genero:          MASCULINO, FEMENINO, OTRO
objetivo:        PERDER_PESO, GANAR_MUSCULO, MANTENER
nivelActividad:  SEDENTARIO, LIGERO, MODERADO, ACTIVO, MUY_ACTIVO
idioma:          "es", "en"  (String libre, no enum)
estrategia:      DEFICIT, SUPERAVIT, MANTENIMIENTO  (se fija en onboarding)
```

---

## 6. Modelos con kotlinx-serialization

```kotlin
// commonMain/model/Usuario.kt
@Serializable
data class Usuario(
    val usuarioId: Int,
    val nombre: String,
    val apellidos: String,
    val correoElectronico: String,
    val fechaNacimiento: String? = null,      // LocalDate como ISO "2000-05-15"
    val fechaRegistro: String? = null,
    val altura: Double? = null,
    val idioma: String? = null,
    val estrategia: String? = null,
    val ajusteCalorico: Int? = null,
    val genero: String? = null,
    val nivelActividad: String? = null,
    val pesoActual: Double? = null,
    val objetivo: String? = null,
    val onboardingCompleto: Boolean = false
)

// commonMain/dto/RegisterRequest.kt
@Serializable
data class RegisterRequest(
    val nombre: String,
    val apellidos: String,
    val correoElectronico: String,
    val password: String,
    val fechaNacimiento: String? = null,
    val altura: Double? = null,
    val genero: String? = null,
    val nivelActividad: String? = null,
    val pesoActual: Double? = null,
    val objetivo: String? = null,
    val idioma: String = "es"
)

// commonMain/dto/LoginRequest.kt
@Serializable
data class LoginRequest(
    val correoElectronico: String,
    val password: String
)

// commonMain/dto/LoginResponse.kt
@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val usuarioId: Int,
    val nombre: String,
    val correoElectronico: String,
    val onboardingCompleto: Boolean
)

// commonMain/dto/RefreshRequest.kt
@Serializable
data class RefreshRequest(val refreshToken: String)

// commonMain/dto/RefreshResponse.kt
@Serializable
data class RefreshResponse(
    val token: String,
    val refreshToken: String
)

// commonMain/dto/ApiError.kt
@Serializable
data class ApiError(
    val fecha: String,
    val mensaje: String
)
```

> `password` solo aparece en `RegisterRequest` y `LoginRequest`. Jamás en `Usuario` (el modelo de respuesta).

---

## 7. Estructura KMP sugerida

```
shared/
  src/
    commonMain/kotlin/com/fitwin/
      core/
        api/
          ApiClient.kt              # HttpClient Ktor configurado
          endpoints/
            AuthEndpoints.kt        # "auth/refresh", "auth/logout"
            UsuarioEndpoints.kt     # "usuarios/save", "usuarios/login"
        storage/
          TokenStorage.kt           # interface (expect class o interface común)
        error/
          ApiException.kt
      features/
        auth/
          data/
            AuthRepository.kt       # register(), login(), refresh(), logout()
            dto/                    # RegisterRequest, LoginRequest, etc.
          domain/
            AuthUseCases.kt
          presentation/
            AuthViewModel.kt        # MVVM compartido (si usas Compose MP)
            state/
              LoginUiState.kt
              RegisterUiState.kt
      shared/model/
        Usuario.kt
    androidMain/kotlin/com/fitwin/
      core/storage/
        TokenStorage.android.kt     # EncryptedSharedPreferences
    iosMain/kotlin/com/fitwin/
      core/storage/
        TokenStorage.ios.kt         # Keychain Services

composeApp/  (o androidApp/ + iosApp/)
  src/
    commonMain/kotlin/com/fitwin/ui/
      auth/
        LoginScreen.kt
        RegisterScreen.kt
      navigation/
        AppNavigation.kt
```

---

## 8. Dependencias mínimas (`build.gradle.kts`)

```kotlin
// shared/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:2.3.12")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
            implementation("io.ktor:ktor-client-auth:2.3.12")
            implementation("io.ktor:ktor-client-logging:2.3.12")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:2.3.12")
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.12")
        }
    }
}

plugins {
    kotlin("plugin.serialization") version "2.0.0"
}
```

Para UI compartida con Compose Multiplatform, añadir:
```kotlin
implementation(compose.runtime)
implementation(compose.foundation)
implementation(compose.material3)
```

---

## 9. Ejemplo de AuthRepository

```kotlin
class AuthRepository(
    private val client: HttpClient,
    private val publicClient: HttpClient,   // sin plugin Auth, para register/login
    private val tokenStorage: TokenStorage
) {

    suspend fun register(request: RegisterRequest): Result<Usuario> = runCatching {
        publicClient.post("usuarios/save") {
            setBody(request)
        }.body<Usuario>()
    }

    suspend fun login(request: LoginRequest): Result<LoginResponse> = runCatching {
        val response = publicClient.post("usuarios/login") {
            setBody(request)
        }.body<LoginResponse>()

        tokenStorage.saveJwt(response.token)
        tokenStorage.saveRefreshToken(response.refreshToken)
        tokenStorage.saveUsuarioId(response.usuarioId)
        response
    }

    suspend fun logout(): Result<Unit> = runCatching {
        val usuarioId = tokenStorage.getUsuarioId() ?: return@runCatching
        client.post("auth/logout") {
            parameter("usuarioId", usuarioId)
        }
        tokenStorage.clear()
    }
}
```

---

## 10. TokenStorage — expect/actual

```kotlin
// commonMain
expect class TokenStorage {
    suspend fun saveJwt(token: String)
    suspend fun getJwt(): String?
    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun saveUsuarioId(id: Int)
    suspend fun getUsuarioId(): Int?
    suspend fun clear()
}
```

Opción simple: usar `multiplatform-settings` con `ObservableSettings` y delegados, así la implementación es única en `commonMain`.

---

## 11. Casos de prueba mínimos

### Registro
- [ ] Email nuevo → 201, navegar a login con mensaje "Cuenta creada"
- [ ] Email duplicado → 400, mostrar `ApiError.mensaje` del body
- [ ] Password < 6 chars → validación en el ViewModel antes de llamar API
- [ ] Sin conexión → capturar `IOException`, toast + preservar formulario

### Login
- [ ] Credenciales OK + `onboardingCompleto: false` → guardar tokens → wizard
- [ ] Credenciales OK + `onboardingCompleto: true` → guardar tokens → home
- [ ] Credenciales incorrectas → 401 → "Email o contraseña incorrectos"
- [ ] 429 rate limit → toast "Demasiados intentos"

### Persistencia / auto-login
- [ ] Al arrancar: si hay JWT válido en storage → home
- [ ] Al arrancar: si JWT expiró pero refresh válido → Ktor `Auth` refresca solo → home
- [ ] Al arrancar: ambos inválidos → login

---

## 12. Gotchas importantes

1. **`password` nunca aparece en respuestas** — no existe en el modelo `Usuario`.
2. **El refresh token rota siempre** — si usas Ktor `bearer` auth, el plugin lo guarda automáticamente en el callback `refreshTokens`.
3. **`onboardingCompleto` dirige la navegación post-login** — no navegar a home a ciegas.
4. **Fechas en formato ISO:** `"2000-05-15"` (LocalDate), `"2025-04-17T10:30:00"` (LocalDateTime). kotlinx-datetime funciona, pero lo más simple en esta fase es tratar fechas como `String` en los DTO y parsear solo cuando se muestra.
5. **Rate limit 50 req/min por IP** — cuidado en desarrollo con hot reload + retry agresivo.
6. **CORS ya está abierto** para clientes móviles, no hace falta configuración extra.
7. **Dos `HttpClient`**: uno con plugin `Auth` para endpoints protegidos, otro sin `Auth` para `/usuarios/save`, `/usuarios/login` y `/auth/refresh`. Evita loops infinitos de refresh si el login falla.
8. **`logout` devuelve texto plano, no JSON** — no intentes deserializarlo con `body<String>()` con expectativas de JSON.

---

## 13. Flujo visual

```
┌──────────────┐
│   Splash     │
└──────┬───────┘
       │
       ▼
 tokenStorage.getJwt() != null ?
       │
   ┌───┴────┐
  Sí        No
   │        │
   ▼        ▼
 Ping a   ┌────────┐
 endpoint │ Login  │◄──────────┐
 protegido└───┬────┘           │
 (si 401 →    │                │
  refresh     │ POST /usuarios/login
  auto del    │                │
  plugin)     ▼                │
   │    Guardar tokens         │
   │          │                │
   │    ¿onboardingCompleto?   │
   │     ┌────┴─────┐          │
   │    Sí         No          │
   │     │          │          │
   └─────┤          ▼          │
         ▼     ┌─────────┐     │
       ┌────┐  │ Wizard  │     │
       │Home│  │Onboard  │     │
       └────┘  └────┬────┘     │
                    │          │
                    ▼          │
               PUT /usuarios   │
               onboarding=true │
                    │          │
                    ▼          │
                  Home         │
                               │
┌──────────────┐               │
│   Register   │───────────────┘
└──────┬───────┘  (201 → navegar a Login)
       │
       ▼
  POST /usuarios/save
       │
   ┌───┴────┐
  201      400
   │        │
   ▼        ▼
Navegar   Mostrar
a Login   ApiError.mensaje
```
