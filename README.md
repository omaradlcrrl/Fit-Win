<div align="center">

# 🏋️ FitWin

**De un TFG de escritorio a una app móvil moderna con API segura.**

[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot_3.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MariaDB](https://img.shields.io/badge/Database-MariaDB-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)
[![Docker](https://img.shields.io/badge/Deploy-Docker_Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)

</div>

---

## La Historia

FitWin empezó como mi Proyecto de Fin de Ciclo. En su primera versión era una app de escritorio hecha con **JavaFX** que se conectaba a una API REST en **Spring Boot**. Funcionaba, pero era una interfaz de escritorio antigua que no tenía nada que ver con lo que se usa hoy en día en el mercado.

Decidí rehacer el cliente desde cero usando **Kotlin Multiplatform (KMP)** y **Jetpack Compose** para demostrar que puedo saltar de stack, aprender por mi cuenta y llevar un proyecto real a un nivel profesional. El backend lo mantuve (porque la API estaba bien diseñada), pero lo refactoricé para mejorar la seguridad y añadir funcionalidades que faltaban.

El resultado es un ecosistema completo: una app móvil moderna con una estética brutalista que consume una API REST segura, todo dockerizado para que cualquiera pueda levantarlo y probarlo.

---

## Qué Hay en el Repo

```
FitWin/
├── FitWinKMP/        → App móvil (Kotlin Multiplatform + Jetpack Compose)
├── ApiUsuarios/      → API REST (Spring Boot 3.2 + JWT + Rate Limiting)
├── fitwin-javafx/    → App de escritorio original (JavaFX 21 — legacy)
└── docker-compose.yml → Levanta API + MariaDB con un solo comando
```

---

## 📱 La App Móvil (KMP)

`[CAPTURAS DE PANTALLA PENDIENTES — añadir GIFs o screenshots del emulador]`

### Qué hace

- **Nutrición:** Registro diario de comidas por tipo (desayuno, almuerzo, cena, snack), con barras de progreso de macros que se calculan contra el objetivo calórico real del usuario. Navegación entre fechas, edición y eliminación de comidas.
- **Entrenamiento:** Creador de rutinas con días activos, catálogo de ejercicios, sesiones de entrenamiento en vivo con tracking de series y pesos en tiempo real. Detección automática de records personales.
- **Estadísticas:** Gráficas de progreso interactivas dibujadas desde cero sobre el Canvas de Compose (sin librerías). Historial de mediciones corporales y check-ins.
- **Perfil:** Datos del usuario, peso actual vs inicial, IMC calculado, objetivo nutricional con desglose de macros, generación automática de objetivos y cambio de idioma en tiempo real (ES/EN).
- **Autenticación:** Login, registro, gestión de sesión con refresh token automático y logout.

### Con qué está hecho

| Tecnología | Para qué |
|:---|:---|
| **Kotlin Multiplatform** | Compartir lógica de negocio entre plataformas |
| **Jetpack Compose** | UI declarativa y reactiva (Material 3) |
| **Ktor Client** | Llamadas HTTP con interceptor de auth automático |
| **Coroutines + StateFlow** | Estado reactivo (MVVM con flujo unidireccional) |
| **Multiplatform Settings** | Persistencia local de tokens y sesión |
| **Canvas nativo** | Gráficas custom sin dependencias externas |

### Cómo está organizado el código

El código del cliente sigue **Clean Architecture orientada a Features**. Nada de carpetas genéricas con 50 archivos. Cada módulo del dominio está aislado:

```
features/
├── auth/       → data/ (DTOs, API, Repository) + presentation/ (ViewModel)
├── food/       → data/ (DTOs, API, Repository) + presentation/ (ViewModel)
├── training/   → data/ (DTOs, API, Repository) + presentation/ (ViewModel)
├── stats/      → data/ (DTOs, API, Repository) + presentation/ (ViewModel)
└── profile/    → data/ (DTOs, API, Repository) + presentation/ (ViewModel)
```

La UI está separada en `ui/` con una carpeta por pantalla. Los ViewModels exponen un `StateFlow<UiState>` sellado (sealed class) y los Composables solo leen ese estado y emiten eventos. Cero lógica en la vista.

### Cosas que me parecen interesantes de lo que he implementado

**Refresh Token invisible:** El `ApiClient` usa el plugin `Auth` de Ktor. Si una petición devuelve 401, el interceptor automáticamente pide un nuevo token, lo guarda y reintenta la petición original. La UI ni se entera. El usuario nunca ve un "sesión expirada".

**Gráficas a mano en Canvas:** En vez de meter una librería pesada para las gráficas de progreso, las dibujo directamente sobre el Canvas de Compose con curvas de Bézier, gradientes y detección de toques. Es más trabajo, pero demuestra que entiendo cómo funciona el renderizado por debajo.

**Fallbacks locales:** Si el backend falla, la app no se queda bloqueada. Los ViewModels tienen lógica de fallback: si no pueden cargar ejercicios globales usan una lista mock, si no pueden iniciar sesión de entrenamiento crean una local, etc.

**Internacionalización sin reiniciar:** El cambio de idioma (ES/EN) funciona en tiempo real usando `CompositionLocalProvider`. Sin reiniciar la activity, sin recargar nada.

---

## ⚙️ La API (Spring Boot)

### Qué hace

12 controladores REST que cubren todo el dominio: usuarios, autenticación, rutinas, ejercicios, sesiones de entrenamiento, series, comidas, mediciones corporales, objetivos, records personales, fotos de progreso y catálogo de ejercicios.

### Seguridad

La API tiene una cadena de seguridad de dos capas:

1. **Rate Limiting (Bucket4j):** Cada IP tiene un límite de 50 peticiones por minuto. Si lo excede → `429 Too Many Requests`. Esto protege contra fuerza bruta y DDoS antes de que la petición llegue a tocar la base de datos.

2. **JWT Stateless:** Sin sesiones en memoria. El servidor valida la firma criptográfica del token en cada petición. Sistema dual de Access Token (24h) + Refresh Token (30 días) con rotación automática.

Los secretos (contraseña de BD, clave JWT) se inyectan por variables de entorno. Nunca están hardcodeados en el código.

### Comportamientos automáticos

- Cuando registras una serie con `completado: true` y el peso supera tu record anterior → el backend **crea o actualiza el record personal automáticamente**. No hay que llamar a otro endpoint.
- Cuando finalizas una sesión de entrenamiento → el backend **calcula la duración automáticamente** a partir de las timestamps de inicio y fin.

### Stack

- Spring Boot 3.2 (Java 21)
- Spring Security + jjwt 0.12.5
- Spring Data JPA + Hibernate
- MariaDB
- Bucket4j 8.9.0
- Lombok

---

## 🖥️ La App de Escritorio (Legacy)

La primera versión del proyecto. JavaFX 21 con FXML, TilesFX para los dashboards, ControlsFX para los controles avanzados e Ikonli para iconos vectoriales. Arquitectura MVC clásica.

Se queda en el repo como referencia del punto de partida. La gracia está en comparar el código de JavaFX con el de Compose y ver cuánto ha cambiado la forma de hacer interfaces.

---

## 🚀 Cómo Probarlo

### Opción 1: Docker Compose (recomendado)

Solo necesitas [Docker](https://docs.docker.com/get-docker/) instalado. Un solo comando levanta la API + la base de datos:

```bash
docker-compose up --build
```

La API estará lista en `http://localhost:3036/api/v1/FWBBD/`.

Para la app móvil, abre la carpeta `FitWinKMP` en **Android Studio**, selecciona un emulador y dale a Run. La app ya viene configurada para conectar al `localhost` del host a través del emulador (`10.0.2.2:3036`).

### Opción 2: Manual (sin Docker)

1. Tener MariaDB instalado con una base de datos llamada `fit_win`.
2. Arrancar la API:
   ```bash
   cd ApiUsuarios
   ./mvnw spring-boot:run
   ```
3. Abrir `FitWinKMP` en Android Studio y ejecutar en emulador.

---

## Lo que he aprendido con este proyecto

- A migrar un proyecto completo de un stack (JavaFX) a otro (KMP + Compose) sin tirar el backend.
- A implementar un sistema de autenticación real con Access/Refresh Tokens y renovación silenciosa.
- A dibujar interfaces custom sobre Canvas cuando las librerías de terceros no encajan con lo que necesitas.
- A diseñar una arquitectura de código que escala: cada feature es un módulo aislado que puedes tocar sin romper el resto.
- A proteger una API con rate limiting y JWT stateless.
- A dockerizar un backend para que cualquiera pueda levantarlo sin configurar nada.
- A gestionar estado reactivo con StateFlow y flujo unidireccional de datos.
- A internacionalizar una app en tiempo real sin reinicios.

---

## Autor

Desarrollado por **Omar** — primero como PFC y después refactorizado y ampliado por cuenta propia.
