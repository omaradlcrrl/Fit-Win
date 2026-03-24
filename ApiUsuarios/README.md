<div align="center">

# 🛡️ Fitwin API 2.0

**Backend seguro, rápido y escalable.**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-Auth-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MariaDB](https://img.shields.io/badge/MariaDB-3.2.0-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A22?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

</div>

---

## ¿Qué es Fitwin API 2.0?

Es el motor central (Backend REST) del ecosistema Fitwin. Diseñada en **Java 21** con **Spring Boot**, esta API proporciona una base de datos segura y de alto rendimiento para interactuar con la aplicación de escritorio y futuros clientes móviles.

Se ha diseñado con un enfoque especial en la **Seguridad**, implementando autenticación moderna basada en Tokens (JWT) y defensas contra ataques de fuerza bruta y DDoS mediante Rate Limiting.

---

## Características

### 🔒 Autenticación Segura (JWT)
Sistema de login robusto apoyado en `Spring Security` y `jjwt`. Todos los endpoints privados requieren de un Token JWT válido. Esto asegura un manejo sin estado (stateless) que incrementa la seguridad y facilita la escalabilidad.

### 🛡️ Rate Limiting Anti-Ataques
Implementación nativa de limitación de tasa (Rate Limiting) utilizando la librería **Bucket4j**.
- Se asigna un cubo lógico (bucket) a cada **Dirección IP**.
- Límite de **50 peticiones por minuto**.
- El `RateLimitFilter` intercepta y corta ataques antes de llegar a los controladores o chequear el token.
- Responde con un HTTP `429 Too Many Requests` si alguien excede el límite.

### 💾 Persistencia de Datos
Conexión JDBC óptima a una base de datos relacional **MariaDB**, utilizando **Spring Data JPA** para el mapeo objeto-relacional (ORM), lo que permite operaciones CRUD rápidas y seguras, evadiendo inyecciones SQL.

---

## Stack Tecnológico

| | Tecnología | Versión |
|:---|:---|:---|
| **Lenguaje** | [Java](https://openjdk.org/) | 21 |
| **Framework Base** | [Spring Boot](https://spring.io/projects/spring-boot) | 3.2.3 |
| **Seguridad** | [Spring Security](https://spring.io/projects/spring-security) | 3.2.3 |
| **Tokens JWT** | [jjwt](https://github.com/jwtk/jjwt) | 0.12.5 |
| **Arquitectura BD** | JPA / Hibernate | 3.2.3 |
| **Rate Limiting** | [Bucket4j](https://bucket4j.com/) | 8.9.0 |
| **Driver Base Datos**| MariaDB Client | 3.2.0 |
| **Boilerplate** | [Lombok](https://projectlombok.org/) | Opcional |
| **Build System** | [Maven](https://maven.apache.org/) | 3.11+ |

---

## Estructura de Seguridad y Filtros

```
HTTP Request
     ↓
[ RateLimitFilter ]  ──► (Si IP > 50 req/min) ──► rechaza con 429 Too Many Requests
     ↓
[ JwtAuthenticationFilter ] ──► (Si token no válido) ──► rechaza con 401 Unauthorized
     ↓
[ Controllers (Endpoints) ] ──► (Lógica de negocio y base de datos)
```

---

## Cómo ejecutar localmente

**Requisitos**: JDK 21 instalada, base de datos MariaDB funcionando localmente, Apache Maven.

1. **Clonar y compilar:**
```bash
git clone https://github.com/omaradlcrrl/ApiUsuarios.git
cd ApiUsuarios
./mvnw clean install
```

2. **Ejecutar Spring Boot:**
```bash
./mvnw spring-boot:run
```

3. **Configurar BD:** 
Asegúrate de ejecutar/importar el script de base de datos (`FWBBD.sql`) en tu instancia de MariaDB antes de lanzar la aplicación y de tener las credenciales correctas en el archivo `application.properties`.

---

## Autor

Desarrollado como Proyecto de Fin de Ciclo (PFC) por **Omar**.
