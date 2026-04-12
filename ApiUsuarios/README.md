# 🛡️ Fitwin API 2.0
> **Backend seguro, rápido y escalable.**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-3.2.3-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-Client_3.2.0-003545?style=for-the-badge&logo=mariadb&logoColor=white)

## ¿Qué es Fitwin API 2.0?
Es el motor central (Backend REST) del ecosistema Fitwin. Diseñada en **Java 21** con **Spring Boot**, esta API proporciona una base de datos segura y de alto rendimiento para interactuar con la aplicación de escritorio y futuros clientes móviles.

Se ha diseñado con un enfoque especial en la **Seguridad**, implementando autenticación moderna basada en Tokens (JWT) y defensas contra ataques de fuerza bruta y DDoS mediante Rate Limiting.

---

## ✨ Características

### 🔒 Autenticación Segura (JWT)
Sistema de login robusto apoyado en *Spring Security* y `jjwt`. Todos los endpoints privados requieren de un Token JWT válido. Esto asegura un manejo sin estado (stateless) que incrementa la seguridad y facilita la escalabilidad en la nube.

### 🛡️ Rate Limiting Anti-Ataques
Implementación nativa de limitación de tasa utilizando la librería **Bucket4j**:
- Se asigna un cubo lógico (bucket) a cada Dirección IP.
- Límite estricto de **50 peticiones por minuto**.
- El filtro intercepta y corta ataques antes de que lleguen a los controladores o evalúen el token.
- Responde con un HTTP `429 Too Many Requests` si alguien excede el límite.

### 💾 Persistencia de Datos
Conexión JDBC óptima a una base de datos relacional **MariaDB**, utilizando **Spring Data JPA** para el mapeo objeto-relacional (ORM). Esto permite operaciones CRUD ultrarrápidas y seguras, mitigando los riesgos de inyección SQL gracias al uso de consultas parametrizadas.

---

## 🛠️ Stack Tecnológico

| Tecnología | Herramienta / Versión |
| :--- | :--- |
| **Lenguaje** | Java 21 |
| **Framework Base** | Spring Boot 3.2.3 |
| **Seguridad** | Spring Security 3.2.3 |
| **Tokens JWT** | jjwt 0.12.5 |
| **Arquitectura BD** | JPA / Hibernate 3.2.3 |
| **Rate Limiting** | Bucket4j 8.9.0 |
| **BBDD / Driver** | MariaDB Client 3.2.0 |
| **Boilerplate** | Lombok (Opcional) |
| **Build System** | Maven 3.11+ |

---

## 🔐 Estructura de Seguridad y Filtros

Arquitectura defensiva en capas del servidor:

```text
HTTP Request
     ↓
[ RateLimitFilter ]  ──► (Si IP > 50 req/min) ──► Rechaza con HTTP 429
     ↓
[ JwtAuthFilter ]    ──► (Si token no válido) ──► Rechaza con HTTP 401
     ↓
[ Controladores ]    ──► Endpoints / Lógica de negocio
```

---

## 📡 Endpoints Principales (Resumen)

Aquí tienes un listado de las rutas principales expuestas por el servidor. Las rutas protegidas requieren enviar el token JWT en la cabecera `Authorization: Bearer <token>`.

| Método | Endpoint | Descripción | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/v1/auth/login` | Login y generación de Token JWT. | ❌ |
| `POST` | `/api/v1/auth/register` | Registro de un nuevo usuario. | ❌ |
| `GET`  | `/api/v1/usuarios/perfil` | Obtiene los datos del usuario logueado. | ✅ |
| `PUT`  | `/api/v1/usuarios/actualizar` | Actualiza el perfil del usuario. | ✅ |
| `GET`  | `/api/v1/rutinas` | Obtiene la lista de rutinas del ecosistema. | ✅ |
| `POST` | `/api/v1/estadisticas` | Guarda las métricas de entrenamiento. | ✅ |

---

## 🚀 Cómo ejecutar localmente

**Requisitos previos:** 
- JDK 21 instalada
- Instancia de MariaDB funcionando localmente
- Apache Maven

**1. Clonar y compilar:**
```bash
git clone https://github.com/omaradlcrrl/ApiUsuarios.git
cd ApiUsuarios
./mvnw clean install
```

**2. Configurar la Base de Datos:**
Asegúrate de ejecutar/importar el script de base de datos (`FWBBD.sql`) en tu instancia de MariaDB. La aplicación utiliza variables de entorno para no exponer las contraseñas reales. Para probarlo en local, debes configurar estas variables:
- `DB_USER`: Tu usuario de MariaDB (por defecto `root`)
- `DB_PASSWORD`: Tu contraseña (por defecto `root`)
- `JWT_SECRET`: Una clave secreta larga de al menos 32 caracteres inventada por ti.

**3. Ejecutar Spring Boot:**

**En Windows (PowerShell):**
```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="tu_password"
$env:JWT_SECRET="ClaveSuperSecretaDeFitwinParaPFC123456789"
./mvnw spring-boot:run
```

**En Linux / Mac:**
```bash
DB_USER="root" DB_PASSWORD="tu_password" JWT_SECRET="ClaveSuperSecretaDeFitwinParaPFC123456789" ./mvnw spring-boot:run
```

---
*Desarrollado como Proyecto de Fin de Ciclo (PFC) por Omar.*
