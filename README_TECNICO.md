# FitWin — Documentación Técnica

Referencia técnica interna del proyecto. Documenta contratos con el backend, decisiones de arquitectura, patrones de estado, configuración de red y gotchas conocidos.

---

## 1. Configuración de Red (ApiClient)

### Base URL

```kotlin
private const val BASE_URL = "http://10.0.2.2:3036/api/v1/FWBBD/"
```

`10.0.2.2` es el alias de loopback del emulador Android para acceder al `localhost` del host. En producción habría que cambiar a la URL del servidor real y migrar a HTTPS. El `AndroidManifest.xml` tiene `android:usesCleartextTraffic="true"` activado para desarrollo.

### Autenticación (Ktor Bearer)

El `ApiClient.kt` expone dos factorías:
- `buildPublicClient()` — Para login y registro. Sin interceptor de auth.
- `buildAuthClient(tokenStorage)` — Para todo lo demás. Incluye el plugin `Auth` de Ktor con Bearer.

El interceptor `sendWithoutRequest` tiene una whitelist de endpoints públicos (`login`, `usuarios/save`, `refresh`). Cualquier endpoint nuevo que sea público tiene que ser añadido aquí o recibirá un 401 al intentar enviar un token que no existe.

### Renovación silenciosa de tokens

Cuando una petición devuelve 401, el interceptor:
1. Lee el refresh token del `TokenStorage`.
2. Hace `POST /auth/refresh` con ese token.
3. Guarda el nuevo par (JWT + refresh) en `TokenStorage`.
4. Reintenta la petición original con el nuevo JWT.

El refresh token **rota** en cada llamada. Si se usa el viejo después de haber refrescado, el servidor lo invalida y devuelve 401 (hay que volver a hacer login).

### Serialización de fechas

El backend tiene `spring.jackson.serialization.write-dates-as-timestamps=false`, así que las fechas llegan como strings ISO (`"2025-04-17"`) y no como arrays `[2025,4,17]`. Los DTOs del cliente usan `String` para fechas.

---

## 2. Almacenamiento Local (TokenStorage)

```kotlin
class TokenStorage(private val settings: Settings = Settings()) {
    fun saveJwt(token: String)
    fun getJwt(): String?
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?
    fun saveUsuarioId(id: Int)
    fun getUsuarioId(): Int?
    fun hasSession(): Boolean  // JWT != null && refresh != null
    fun clear()                // Logout completo
}
```

Usa `multiplatform-settings` (Russhwolf) que mapea a `SharedPreferences` en Android y `NSUserDefaults` en iOS.

El `usuarioId` se persiste para evitar decodificar el JWT en cada petición.

---

## 3. Contratos de la API (Request / Response)

### Autenticación

```
POST /usuarios/login
Body: { "correoElectronico": "...", "password": "..." }
→ 200: { "token", "refreshToken", "usuarioId", "nombre", "onboardingCompleto" }

POST /auth/refresh
Body: { "refreshToken": "<UUID>" }
→ 200: { "token", "refreshToken" }   // El anterior queda invalidado

POST /auth/logout?usuarioId=1
→ 200: "Sesión cerrada correctamente"  // Invalida TODOS los refresh tokens
```

### Registro

```
POST /usuarios/save
Body: { nombre, apellidos, correoElectronico, password, fechaNacimiento,
        altura, genero, nivelActividad, pesoActual, objetivo, idioma }
```

`password` es WRITE_ONLY — nunca aparece en ninguna respuesta de la API.

### Nutrición

```
POST /comidas/save            → Registrar comida
GET  /comidas/hoy/{userId}    → Diario de hoy
GET  /comidas/fecha/{userId}?fecha=2025-04-17  → Comidas de una fecha
GET  /comidas/range/{userId}?from=...&to=...   → Rango de fechas
PUT  /comidas/actualizar/{id} → Actualizar comida
DELETE /comidas/{id}          → Eliminar comida
```

Tipos válidos: `DESAYUNO`, `ALMUERZO`, `CENA`, `SNACK`. Unidades: `GRAMOS`, `ML`, `UNIDAD`.

### Entrenamiento — Flujo completo de una sesión

```
1. POST /sesiones/iniciar         → Crea sesión, devuelve sesionId
2. POST /series/save              → Registra serie. Si completado=true y pesoKg
                                     supera el record → se crea/actualiza RecordPersonal
                                     automáticamente (no hay que llamar a /records/save)
3. PUT  /sesiones/finalizar/{id}  → Calcula duracionMinutos automáticamente
```

### Rutinas y Ejercicios

```
POST /rutinas/save                         → Crear rutina
PUT  /rutinas/actualizar/{id}              → Editar rutina
DELETE /rutinas/{id}                       → Eliminar rutina
GET  /rutinas/usuario/{userId}             → Listar rutinas del usuario
POST /ejercicios/save                      → Añadir ejercicio a rutina
DELETE /ejercicios/{id}                    → Eliminar ejercicio
GET  /ejercicios?usuarioId=&diaSemana=     → Ejercicios del día
GET  /ejercicios-globales                  → Catálogo completo
GET  /ejercicios-globales?buscar=press     → Búsqueda por nombre
```

### Mediciones Corporales

```
POST /mediciones/save              → Registrar medición (solo campos que se midan)
GET  /mediciones/usuario/{userId}  → Historial completo
GET  /mediciones/ultima/{userId}   → Última medición (para dashboard)
```

Campos disponibles: `peso`, `porcentajeGrasa`, `masaMagra`, `cintura`, `pecho`, `brazo`, `muslo`. Todos opcionales excepto `usuarioId`.

### Objetivos

```
GET  /objetivos/actual/{userId}      → Objetivo activo
POST /objetivos/generar/{userId}     → Genera objetivo automáticamente
```

### Records Personales

```
GET /records/ejercicio/max?usuarioId=&ejercicioGlobalId=  → Mejor marca
GET /records/ejercicio?usuarioId=&ejercicioGlobalId=      → Historial (para gráficas)
POST /records/save                                         → Manual (calistenia)
```

### Fotos de Progreso

```
POST /fotos-progreso/save                → Subir foto con ángulo
GET  /fotos-progreso?usuarioId=          → Listar (ordenadas por fecha DESC)
DELETE /fotos-progreso/{id}              → Eliminar foto
```

Tipos de foto: `FRONTAL`, `LATERAL_DERECHA`, `LATERAL_IZQUIERDA`, `ESPALDA`.

### Errores

Todas las respuestas de error siguen el formato: `{ "fecha": "...", "mensaje": "..." }`

| HTTP | Significado |
|:---|:---|
| 400 | Validación fallida, campo obligatorio ausente |
| 401 | JWT ausente, expirado o inválido |
| 403 | Token válido pero sin permisos |
| 404 | Recurso no encontrado |
| 429 | Rate limit excedido (50 req/min por IP) |
| 500 | Error interno del servidor |

---

## 4. Máquina de Estados del Training

El `TrainingViewModel` gestiona 5 estados mutuamente excluyentes:

```
sealed class TrainingUiState {
    Idle                     → Carga inicial
    Loading                  → Spinner
    DailyWorkoutView(...)    → Tabla de ejercicios del día + START WORKOUT
    RoutineBuilder(...)      → Constructor de rutinas + ADD EXERCISE
    ActiveWorkoutSession     → Tracking de series en vivo + FINISH
    Error(String)            → Mensaje de error + REINTENTAR
}
```

### Transiciones

```
init
  └→ loadTodaysWorkout()
       ├→ DailyWorkoutView(ejercicios)     // Hay ejercicios para hoy
       └→ DailyWorkoutView(empty)          // Día de descanso

DailyWorkoutView
  ├→ [BUILD ROUTINE] → openRoutineBuilder() → RoutineBuilder
  └→ [START WORKOUT] → startWorkout()       → ActiveWorkoutSession

RoutineBuilder
  ├→ [SAVE]   → createRutinaAndAssign() → DailyWorkoutView
  └→ [CANCEL] → loadTodaysWorkout()     → DailyWorkoutView

ActiveWorkoutSession
  ├→ [LOG]    → logSet()           → ActiveWorkoutSession (series actualizado)
  └→ [FINISH] → finalizarSesion() → DailyWorkoutView
```

### Fallbacks locales

El ViewModel nunca deja la app bloqueada si el backend falla:
- `openRoutineBuilder()`: Si la API no devuelve ejercicios globales, usa una lista mock local.
- `startWorkout()`: Si `iniciarSesion` falla, crea una sesión mock local.
- `logSet()`: Si `registrarSerie` falla, añade la serie al estado local igualmente.
- `finalizarSesion()`: Si falla, vuelve a `loadTodaysWorkout()` de todos modos.

---

## 5. Máquina de Estados de Food

El `FoodViewModel` gestiona fecha seleccionada y carga paralela:

```
sealed class FoodUiState {
    Loading
    Success(comidas, objetivo)  → Lista de comidas + objetivo calórico dinámico
    Error(message)
}
```

La navegación de fechas (`goToPreviousDay()`, `goToNextDay()`) actualiza un `StateFlow<LocalDate>` que dispara una recarga automática de comidas y objetivo para esa fecha. No se puede avanzar más allá del día actual.

---

## 6. Tema Visual (FitwinColors)

Paleta brutalista oscura definida en `FitwinTheme.kt`:

| Token | Uso |
|:---|:---|
| `Background` | Fondo general (negro/gris oscuro) |
| `PrimaryContainer` | Botones principales, acentos (amarillo) |
| `OnPrimary` | Texto sobre botones primarios (negro) |
| `SurfaceContainer` | Cards y contenedores (gris medio) |
| `SurfaceContainerHighest` | Badges y sub-contenedores |
| `OnSurface` | Texto principal (blanco/claro) |
| `OnSurfaceVariant` | Texto secundario (gris claro) |
| `Error` | Estados de error y botón FINISH (rojo) |
| `Secondary` | Labels de sección (dorado/amarillo tenue) |
| `MacroProtein` / `MacroCarbs` / `MacroFats` | Colores de las barras de macros |

---

## 7. Internacionalización (i18n)

Sistema propio inyectado mediante `CompositionLocalProvider`. Permite cambiar entre español e inglés en tiempo real sin reiniciar la app.

Archivos involucrados:
- `AppStrings.kt` — Interfaz con todas las keys.
- `StringsEs.kt` — Implementación en español.
- `StringsEn.kt` — Implementación en inglés.

Se accede desde cualquier Composable con `LocalStrings.current.foodDesayuno`.

---

## 8. Gotchas Conocidos

| Problema | Solución aplicada |
|:---|:---|
| Emulador Android no accede a `localhost` del host | Base URL usa `10.0.2.2` en lugar de `127.0.0.1` |
| Backend enviaba fechas como arrays `[2025,4,17]` | Configurado `write-dates-as-timestamps=false` |
| `userId` hardcodeado a `1` en las primeras versiones | Migrado a `TokenStorage.getUsuarioId()` dinámico |
| App se bloqueaba si la BD estaba vacía | Fallbacks locales con datos mock en los ViewModels |
| Día de la semana inconsistente (ES vs EN) | El constructor de rutinas usa los nombres en inglés de `kotlinx.datetime` |
| Endpoint público nuevo recibe 401 | Hay que añadirlo a la whitelist de `sendWithoutRequest` en `ApiClient.kt` |
