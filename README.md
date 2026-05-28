<div align="center">

# 🏋️ FitWin

**De un TFG de escritorio en JavaFX a una app móvil moderna con API segura y bien estructurada.**

[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot_3.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MariaDB](https://img.shields.io/badge/Database-MariaDB-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)
[![Docker](https://img.shields.io/badge/Deploy-Docker_Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)

</div>

---

## 📖 La historia detrás del proyecto

FitWin nació originalmente como mi Proyecto de Fin de Ciclo (PFC). Al principio era una aplicación de escritorio clásica en **JavaFX** conectada a un backend de **Spring Boot**. El sistema cumplía su función, pero la interfaz se sentía anticuada y poco práctica para el día a día en un gimnasio.

Para demostrarme que podía adaptarme a tecnologías modernas y aprender por mi cuenta, decidí rehacer toda la parte cliente desde cero usando **Kotlin Multiplatform (KMP)** y **Jetpack Compose**. Aproveché para mantener el backend en Spring Boot, pero le metí un buen repaso: refactoricé el código para estructurarlo mejor y solucioné varios problemas de seguridad graves que tenía la versión inicial (como IDORs, almacenamiento inseguro de tokens y posibles fugas de memoria).

El resultado final es este ecosistema: una aplicación móvil nativa con una estética brutalista muy cuidada y un backend robusto y seguro, todo listo para levantarse con Docker en un par de minutos.

---

## 📁 Qué hay en el repositorio

```
FitWin/
├── FitWinKMP/        → Aplicación móvil (Kotlin Multiplatform + Jetpack Compose)
├── ApiUsuarios/      → API REST (Spring Boot 3.2 + JWT + Seguridad mejorada)
├── fitwin-javafx/    → Cliente de escritorio original (JavaFX 21 — versión legacy)
└── docker-compose.yml → Orquestador para levantar la API y MariaDB juntas
```

---

## 📱 La App Móvil (KMP)

<div align="center">

**Login y Registro**

<img src="docs/Screenshot_20260516_180314.png" width="220"/> &nbsp; <img src="docs/Screenshot_20260516_180333.png" width="220"/>

---

**Nutrición — Macros y comidas diarias**

<img src="docs/Screenshot_20260518_210138.png" width="220"/>

---

**Entrenamiento — Rutina del día y sesión activa**

<img src="docs/Screenshot_20260519_162002.png" width="220"/> &nbsp; <img src="docs/Screenshot_20260519_170233.png" width="220"/>

---

**Estadísticas — Gráficas y medidas corporales**

<img src="docs/Screenshot_20260519_170152.png" width="220"/> &nbsp; <img src="docs/Screenshot_20260516_180204.png" width="220"/> &nbsp; <img src="docs/Screenshot_20260516_180150.png" width="220"/>

---

**Ajustes e idioma en vivo**

<img src="docs/Screenshot_20260516_180231.png" width="220"/> &nbsp; <img src="docs/Screenshot_20260516_180259.png" width="220"/>

</div>

### ¿Qué funciones tiene?

*   **Nutrición:** Registro diario de comidas (desayunos, almuerzos, cenas y snacks). Calcula las barras de macros dinámicamente en base al objetivo calórico real del usuario. Permite navegar entre fechas anteriores, editar porciones y eliminar alimentos.
*   **Entrenamiento:** Creador de rutinas personalizadas, catálogo de ejercicios y un modo "sesión activa" para ir anotando las series, repeticiones y pesos en tiempo real. Si superas tu peso máximo anterior, la app te notifica que has batido un récord personal.
*   **Estadísticas y Medidas:** Gráficas visuales para ver el progreso del peso y las medidas corporales a lo largo del tiempo.
*   **Perfil y Objetivos:** Cálculo automático de IMC, objetivos calóricos y reparto de macros según tus metas de peso. Soporte para cambio de idioma al instante (Español/Inglés) sin recargar la pantalla.

### Tecnologías principales
*   **Kotlin Multiplatform (KMP):** Para compartir toda la lógica de negocio, modelos y llamadas de red entre plataformas.
*   **Jetpack Compose:** Para diseñar una interfaz de usuario declarativa y moderna (Material 3).
*   **Ktor Client:** Para la comunicación HTTP, con un motor de interceptores que gestiona la autenticación automáticamente.
*   **Coroutines & StateFlow:** Para un manejo reactivo del estado de las pantallas (MVVM unidireccional).

### Detalles interesantes de la implementación

*   **Refresco de sesión transparente:** Usando el plugin `Auth` de Ktor, el cliente detecta de forma silenciosa si un token de acceso ha expirado (error 401). Intercepta la petición, solicita un nuevo token con el `RefreshToken`, actualiza la persistencia y reintenta la llamada original. El usuario no nota absolutamente nada.
*   **Gráficas a medida en Canvas:** En lugar de meter dependencias pesadas que limitaran el diseño brutalista, decidí dibujar las gráficas desde cero sobre el Canvas de Compose usando curvas de Bézier, gradientes manuales y detección de coordenadas para los toques.
*   **Internacionalización instantánea:** El cambio de idioma (ES/EN) se propaga al momento en todo el árbol de componentes mediante `CompositionLocalProvider`, sin tener que reiniciar la actividad ni recrear pantallas.

---

## ⚙️ La API (Spring Boot)

La API cuenta con 12 controladores REST para gestionar todo el ciclo: usuarios, autenticación, comidas, ejercicios, rutinas, sesiones de entrenamiento, medidas corporales y récords.

### Mejoras clave de seguridad implementadas

Hice una limpieza profunda en la seguridad del backend para solucionar varios fallos comunes pero peligrosos:

1.  **Protección contra accesos cruzados (Vulnerabilidad IDOR):**
    Antes, cualquier usuario logueado podía modificar o borrar la información de otros (dietas, entrenamientos) si averiguaba el ID numérico en la URL. Para solucionarlo, creé un componente `SecurityUtils` que se conecta a Spring Security y valida en cada operación CRUD que el usuario autenticado sea el dueño real de esos datos (o un administrador). Si no lo es, le rebota un 403.
2.  **Criptografía en Refresh Tokens:**
    Los tokens de refresco (que duran 30 días) se guardaban antes en texto plano en la base de datos. Si alguien vulneraba la base de datos, podía suplantar a cualquier usuario. Ahora, solo se guarda el hash SHA-256 en base de datos. El token real solo lo tiene el dispositivo del cliente.
3.  **Control de peticiones (Rate Limiting) sin fugas de memoria:**
    Para evitar que un atacante sature el backend o intente adivinar contraseñas por fuerza bruta, implementé límites de peticiones con Bucket4j por IP (5 peticiones/min para login y 50 para el resto). Los buckets se gestionan con una caché Caffeine con expiración automática de 30 minutos por inactividad. Esto soluciona un problema de fuga de memoria que tenía el backend anterior al usar un `ConcurrentHashMap` que crecía indefinidamente con cada nueva IP de red.
4.  **Endurecimiento del entorno:**
    Se eliminaron las contraseñas JWT por defecto en el código (ahora exige configurarla en producción como variable de entorno) y se capó la visualización de trazas internas de error al exterior (`include-message=never`) para no dar pistas del diseño de la base de datos a atacantes.

---

## 🚀 Cómo ponerlo en marcha paso a paso

### Opción A: Despliegue automático con Docker (La más rápida)

Si solo quieres ver el sistema funcionando con datos reales precargados, esta es tu opción. Solo necesitas tener [Docker Desktop](https://www.docker.com/products/docker-desktop/) abierto.

1.  **Levanta el entorno**:
    ```bash
    docker-compose up -d --build
    ```
2.  **¿Qué ocurre ahora?**
    Docker levantará MariaDB, compilará y arrancará la API de Spring Boot, y finalmente ejecutará un contenedor `seeder` que introduce automáticamente un usuario de prueba completo para que no tengas que registrarte y crear todo de cero.
3.  **Credenciales para probar la app**:
    Una vez que el proceso termine (puedes verificarlo con `docker logs fitwin-seeder`), abre la aplicación móvil e inicia sesión con estos datos:
    > 📧 **Usuario:** `prueba@fitwin.com`
    > 🔑 **Contraseña:** `fitwin123`
    >
    > *(Este usuario ya tiene cargados 20 ejercicios, una rutina PPL activa, historial de entrenamientos, macros del día y marcas registradas)*

---

### Opción B: Instalar la App Móvil directamente (Recomendado para probar en emulador)

Si no quieres liarte a configurar Android Studio ni compilar código fuente, he dejado la APK actualizada y lista para usar:

1.  **Descarga la APK**: Descarga el archivo de instalación desde mi enlace público de [Google Drive](https://drive.google.com/file/d/1QKFHD6PVfSdMvYYs6gHoOzJBDt9RPxFg/view?usp=drive_link).
2.  **Instálala**: Arrastra el archivo `.apk` descargado directamente dentro de un emulador Android (por ejemplo, el de Android Studio).
3.  **Conexión automática**: La APK viene preconfigurada para buscar la API en la dirección `10.0.2.2:3036` (que es el puente que usan los emuladores Android para conectar con el localhost del ordenador). Si tienes la API de Spring Boot corriendo en tu máquina (con Docker o manual), **la aplicación móvil se conectará de inmediato al arrancar**.

> ⚠️ **Nota para móviles físicos:** Los dispositivos físicos no entienden la dirección `10.0.2.2`. Si vas a instalar la APK en tu teléfono real, necesitarás descargar el código fuente, cambiar la dirección IP por la IP local de tu Wi-Fi en `ApiClient.kt` y compilarla tú mismo (siguiente opción).

---

### Opción C: Compilar la App Móvil desde el código fuente

Si quieres curiosear en el código o probarlo en tu propio teléfono:

1.  **Requisitos:** Tener instalado Java JDK 17 (o superior) y Android Studio.
2.  **Compilar desde terminal:**
    Navega a la carpeta de la app móvil:
    ```bash
    cd FitWinKMP
    ```
    Y compila el proyecto usando el wrapper de Gradle:
    - **En Windows**: `.\gradlew.bat assembleDebug`
    - **En macOS / Linux**: `./gradlew assembleDebug`
3.  **Localizar la APK:** Una vez termine, encontrarás el archivo instalable en:
    `FitWinKMP/composeApp/build/outputs/apk/debug/composeApp-debug.apk`

---

### Opción D: Ejecución manual (Sin Docker)

Si prefieres no usar contenedores y levantar todo de forma nativa en tu ordenador:

1.  **Base de Datos:** Crea una base de datos vacía en tu MariaDB/MySQL local llamada `fit_win`.
2.  **Lanzar la API:**
    La API exige que configures una variable de entorno para firmar los tokens JWT por seguridad. Ejecuta en tu consola:
    - **Windows (PowerShell):**
      ```powershell
      $env:JWT_SECRET="escribe_aqui_una_clave_secreta_muy_larga_de_mas_de_32_bytes_de_ejemplo"
      cd ApiUsuarios
      .\mvnw.cmd spring-boot:run
      ```
    - **macOS / Linux:**
      ```bash
      export JWT_SECRET="escribe_aqui_una_clave_secreta_muy_larga_de_mas_de_32_bytes_de_ejemplo"
      cd ApiUsuarios
      ./mvnw spring-boot:run
      ```
3.  **Lanzar el Cliente:** Abre la carpeta `FitWinKMP` en Android Studio y ejecútala en tu emulador o móvil conectado.

---

## 👨‍💻 Autor

Desarrollado por **Omar** — Diseñado con dedicación para llevar un proyecto académico a un nivel técnico real, profesional y pulido.
