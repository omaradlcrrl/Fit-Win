# Fitwin API 2.0

> Backend REST seguro y escalable para el ecosistema Fitwin.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-3.2.3-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-3.2.0-003545?style=for-the-badge&logo=mariadb&logoColor=white)

## Descripcion

Motor central del ecosistema Fitwin. API REST construida en **Java 21** con **Spring Boot 3.2**, pensada para servir tanto a la app de escritorio (JavaFX) como a futuros clientes moviles. Gestiona usuarios, nutricion, entrenamiento, mediciones corporales, objetivos y progreso fotografico.

---

## Stack Tecnologico

| Tecnologia | Version |
| :--- | :--- |
| Java | 21 |
| Spring Boot | 3.2.3 |
| Spring Security | 3.2.3 |
| JWT (jjwt) | 0.12.5 |
| JPA / Hibernate | 3.2.3 |
| Bucket4j (Rate Limiting) | 8.9.0 |
| MariaDB Driver | 3.2.0 |
| Lombok | Ultima |
| Maven | 3.11+ |
| JUnit 5 + Mockito | Via spring-boot-starter-test |

---

## Arquitectura

```
Controller -> Service -> Repository (JPA) -> MariaDB
```

### Cadena de filtros de seguridad

```
HTTP Request
     |
[ RateLimitFilter ]  --> Si IP > 50 req/min --> HTTP 429
     |
[ JwtAuthFilter ]    --> Si token invalido  --> HTTP 401
     |
[ CORS Filter ]      --> Permite origenes movil/web
     |
[ Controladores ]    --> Logica de negocio
```

### Autenticacion

- **JWT (HS256)** con expiracion de 24 horas para access tokens
- **Refresh Tokens** (UUID, 30 dias) para sesiones moviles persistentes
- Passwords hasheados con **BCrypt**
- Endpoints publicos: `POST /usuarios/login` y `POST /usuarios/save`

---

## Modelo de Datos

### Entidades principales

```
Usuario
 |-- Rutina (1:N)
 |    |-- Ejercicio (1:N) --> EjercicioGlobal (N:1, catalogo)
 |-- SesionEntrenamiento (1:N)
 |    |-- SerieRealizada (1:N) --> Ejercicio (N:1)
 |-- Comida (1:N)
 |-- MedicionCorporal (1:N)
 |-- Objetivo (1:N)
 |-- RecordPersonal (1:N) --> EjercicioGlobal (N:1)
 |-- FotoProgreso (1:N)
 |-- RefreshToken (1:N)
```

### Enums

| Enum | Valores |
| :--- | :--- |
| `Role` | `USER`, `ADMIN` |
| `TipoComida` | `DESAYUNO`, `ALMUERZO`, `CENA`, `SNACK` |
| `UnidadComida` | `GRAMOS`, `ML`, `UNIDAD` |
| `CategoriaEjercicio` | `FUERZA`, `HIPERTROFIA`, `RESISTENCIA`, `MOVILIDAD` |
| `Equipamiento` | `BARRA`, `MANCUERNAS`, `CABLE`, `PESO_CORPORAL`, `MAQUINA`, `KETTLEBELL`, `BANDA` |

---

## Endpoints API

Base path: `/api/v1/FWBBD/`

### Usuarios (`/usuarios`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/usuarios/save` | Registro de usuario | No |
| `POST` | `/usuarios/login` | Login, devuelve JWT | No |
| `GET` | `/usuarios` | Listar todos | Si |
| `GET` | `/usuarios/{id}` | Obtener por ID | Si |
| `PUT` | `/usuarios/actualizar/{id}` | Actualizar perfil | Si |
| `DELETE` | `/usuarios/{id}` | Eliminar (admin o propio) | Si |

### Comidas (`/comidas`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/comidas/save` | Registrar comida | Si |
| `GET` | `/comidas` | Listar todas | Si |
| `GET` | `/comidas/{id}` | Obtener por ID | Si |
| `GET` | `/comidas/hoy/{usuarioId}` | Comidas de hoy | Si |
| `PUT` | `/comidas/actualizar/{id}` | Actualizar comida | Si |
| `DELETE` | `/comidas/{id}` | Eliminar por ID | Si |
| `DELETE` | `/comidas/deleteByNombre?usuarioId=&nombre=` | Eliminar por nombre (hoy) | Si |

### Mediciones Corporales (`/mediciones`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/mediciones/save` | Registrar medicion | Si |
| `GET` | `/mediciones` | Listar todas | Si |
| `GET` | `/mediciones/{id}` | Obtener por ID | Si |
| `GET` | `/mediciones/usuario/{usuarioId}` | Por usuario | Si |
| `GET` | `/mediciones/ultima/{usuarioId}` | Ultima medicion | Si |
| `GET` | `/mediciones/range/{usuarioId}?from=&to=` | Por rango de fechas | Si |
| `PUT` | `/mediciones/actualizar/{id}` | Actualizar | Si |
| `DELETE` | `/mediciones/{id}` | Eliminar | Si |
| `DELETE` | `/mediciones/deleteHoy/{usuarioId}` | Eliminar medicion de hoy | Si |

### Objetivos (`/objetivos`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/objetivos/save` | Generar objetivo automatico | Si |
| `POST` | `/objetivos/generar/{usuarioId}` | Generar por usuario | Si |
| `GET` | `/objetivos/actual/{usuarioId}` | Objetivo mas reciente | Si |
| `GET` | `/objetivos/hoy/{usuarioId}` | Objetivo de hoy | Si |
| `GET` | `/objetivos/usuario/{usuarioId}` | Historico completo | Si |
| `GET` | `/objetivos/range/{usuarioId}?from=&to=` | Por rango | Si |
| `GET` | `/objetivos/{id}` | Obtener por ID | Si |
| `DELETE` | `/objetivos/{id}` | Eliminar | Si |

### Ejercicios Globales (`/ejercicios-globales`) - Catalogo

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/ejercicios-globales/save` | Crear ejercicio en catalogo | Si |
| `GET` | `/ejercicios-globales` | Listar (filtro: `?categoria=` o `?buscar=`) | Si |
| `GET` | `/ejercicios-globales/{id}` | Obtener por ID | Si |
| `PUT` | `/ejercicios-globales/actualizar/{id}` | Actualizar | Si |
| `DELETE` | `/ejercicios-globales/{id}` | Eliminar | Si |

### Rutinas (`/rutinas`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/rutinas/save` | Crear rutina | Si |
| `GET` | `/rutinas` | Listar (filtro: `?usuarioId=`) | Si |
| `GET` | `/rutinas/{id}` | Obtener por ID | Si |
| `PUT` | `/rutinas/actualizar/{id}` | Actualizar | Si |
| `DELETE` | `/rutinas/{id}` | Eliminar | Si |

### Ejercicios (`/ejercicios`) - Ejercicios dentro de rutinas

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/ejercicios/save` | Crear ejercicio en rutina | Si |
| `GET` | `/ejercicios` | Listar (filtros: `?usuarioId=&diaSemana=`, `?rutinaId=`) | Si |
| `GET` | `/ejercicios/{id}` | Obtener por ID | Si |
| `PUT` | `/ejercicios/actualizar/{id}` | Actualizar | Si |
| `DELETE` | `/ejercicios/{id}` | Eliminar | Si |

### Sesiones de Entrenamiento (`/sesiones`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/sesiones/iniciar` | Iniciar sesion (marca fecha inicio) | Si |
| `PUT` | `/sesiones/finalizar/{id}` | Finalizar (calcula duracion) | Si |
| `GET` | `/sesiones?usuarioId=` | Historial por usuario | Si |
| `GET` | `/sesiones/{id}` | Obtener por ID | Si |
| `DELETE` | `/sesiones/{id}` | Eliminar | Si |

### Series Realizadas (`/series`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/series/save` | Registrar serie | Si |
| `GET` | `/series?sesionId=` | Series de una sesion | Si |
| `PUT` | `/series/actualizar/{id}` | Actualizar serie | Si |
| `DELETE` | `/series/{id}` | Eliminar | Si |

### Records Personales (`/records`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/records/save` | Registrar record | Si |
| `GET` | `/records?usuarioId=` | Records por usuario | Si |
| `GET` | `/records/{id}` | Obtener por ID | Si |
| `DELETE` | `/records/{id}` | Eliminar | Si |

### Fotos de Progreso (`/fotos-progreso`)

| Metodo | Ruta | Descripcion | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/fotos-progreso/save` | Subir foto | Si |
| `GET` | `/fotos-progreso?usuarioId=` | Fotos por usuario | Si |
| `DELETE` | `/fotos-progreso/{id}` | Eliminar | Si |

---

## Ejecucion Local

**Requisitos:** JDK 21, MariaDB corriendo, Maven.

**1. Clonar y compilar:**
```bash
git clone https://github.com/omaradlcrrl/ApiUsuarios.git
cd ApiUsuarios
./mvnw clean package
```

**2. Configurar variables de entorno:**
- `DB_USER` - Usuario MariaDB (default: `root`)
- `DB_PASSWORD` - Password MariaDB (default: `root`)
- `JWT_SECRET` - Clave secreta para JWT (minimo 32 caracteres)

**3. Ejecutar:**

Windows (PowerShell):
```powershell
$env:JWT_SECRET="ClaveSuperSecretaDeFitwinParaPFC123456789"
./mvnw spring-boot:run
```

Linux / Mac:
```bash
JWT_SECRET="ClaveSuperSecretaDeFitwinParaPFC123456789" ./mvnw spring-boot:run
```

El servidor arranca en `http://localhost:3036`.

**4. Tests:**
```bash
./mvnw test
```
77 tests unitarios cubriendo todos los servicios (JUnit 5 + Mockito).

---

## Estructura del Proyecto

```
src/main/java/org/example/apiusuarios/
  controller/      -- 11 controladores REST
  service/         -- 11 servicios de logica de negocio
  repository/      -- 11 repositorios JPA
  model/           -- 11 entidades + 5 enums
  dto/             -- 11 DTOs + login/
  security/        -- JWT, BCrypt, Rate Limiting, CORS
  exception/       -- Excepciones personalizadas + handler global

src/test/java/org/example/apiusuarios/
  service/         -- 6 clases de test (77 tests)
```

---

*Desarrollado como Proyecto de Fin de Ciclo (PFC) por Omar.*
