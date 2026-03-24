<div align="center">

# 🏋️ Fitwin

**Sistema integral de gestión de entrenamientos y progreso físico.**

[![Java](https://img.shields.io/badge/Java-15_%7C_21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot_3.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![JavaFX](https://img.shields.io/badge/Frontend-JavaFX_21-FF9000?logo=java&logoColor=white)](https://openjfx.io/)
[![MariaDB](https://img.shields.io/badge/Database-MariaDB_3.2-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)

</div>

---

## ¿Qué es Fitwin?

Fitwin es una plataforma cliente-servidor desarrollada para la gestión avanzada de rutinas y perfiles de usuario. Se divide en dos piezas clave que funcionan en sintonía:

1. **Una App de Escritorio nativa** (Frontend) pensada para un uso rápido, visual y con dashboards interactivos.
2. **Una API RESTful** (Backend) robusta, segura y escalable que orquesta toda la lógica de negocio y protege los datos.

Diseñada con un enfoque especial en la **Seguridad** y la **Arquitectura Limpia**, separando completamente las responsabilidades del cliente y del servidor.

---

## Características

### 🖥️ App de Escritorio (JavaFX)
- **UI Moderna y Reactiva**: Construida sobre JavaFX 21 usando Vistas FXML y CSS para un diseño fluido desvinculado de la lógica.
- **Dashboards Avanzados**: Integración con **TilesFX** y **ControlsFX** para dibujar medidores, gráficos, notificaciones y paneles visuales dinámicos de alto impacto.
- **Iconografía**: Sistema de iconos vectoriales ligeros gracias a **Ikonli**.
- **Conectividad JSON**: Integración transparente con la API mediante peticiones HTTP asíncronas, empaquetando y desempaquetando DTOs.

### ⚙️ Backend API 2.0 (Spring Boot)
- **Autenticación Segura (JWT)**: Todos los endpoints privados están protegidos y exigen un token válido. Gestión sin estado (stateless) para máxima escalabilidad y seguridad.
- **Defensas Anti-DDoS (Rate Limiting)**: Implementado de forma nativa con **Bucket4j**. Se asigna un "bucket" a cada dirección IP limitando a **50 peticiones HTTP por minuto**. Aborta ataques de fuerza bruta al instante devolviendo código `429 Too Many Requests`.
- **Persistencia Optimizada**: Conexión a MariaDB mediante **Spring Data JPA** e Hibernate. Operaciones CRUD rápidas y seguras contra inyecciones SQL.

---

## Stack Tecnológico

| | Tecnología | Versión |
|:---|:---|:---|
| **Lenguaje (Frontend)**| Java | 15 |
| **Lenguaje (Backend)** | Java | 21 |
| **UI Framework** | [JavaFX](https://openjfx.io/) | 21 |
| **Backend Framework** | [Spring Boot](https://spring.io/projects/spring-boot) | 3.2.3 |
| **Seguridad** | Spring Security + [jjwt](https://github.com/jwtk/jjwt) | 3.2.3 / 0.12.5 |
| **Rate Limiting** | [Bucket4j](https://bucket4j.com/) | 8.9.0 |
| **Persistencia** | MariaDB Client + Hibernate | 3.2.0 |
| **Componentes UI** | TilesFX + ControlsFX | 11.48 / 11.1.2 |
| **Build System** | [Maven](https://maven.apache.org/) | 3.11.0 |

---

## Estructura

```
Fitwin-Project/
├── ApiUsuarios/                  # Backend REST (API 2.0)
│   ├── src/main/java/org/...     # Controladores, Seguridad JWT, Filtros Bucket4j
│   ├── src/main/resources/...    # application.properties (Config. MariaDB)
│   └── pom.xml                   # Dependencias de Spring Boot
│
└── fitwin-javafx/                # Frontend Desktop (App JavaFX)
    ├── Fit-Win/src/main/java...  # MVC: Modelos, Vistas FXML, Controladores UI
    ├── Fit-Win/src/main/resources# Hojas de Estilo CSS e Imágenes
    └── pom.xml                   # Dependencias de UI y JavaFX
```

---

## Arquitectura

```
Cliente Desktop (JavaFX) → Petición HTTP (JSON) → Filtro Rate Limit (50/min) 
                                                         ↓
DB MariaDB ← Repositorio JPA ← Endpoints API ← [Filtro Seguridad JWT]
```

El flujo de trabajo sigue una línea clara y segura:
1. El usuario interactúa con la interfaz gráfica (MVC) en **JavaFX**.
2. El cliente genera una petición HTTP enviando credenciales o tokens.
3. El **Backend (Spring Boot)** recibe la petición. El filtro primario (`RateLimitFilter`) rechaza IPs abusivas.
4. El filtro secundario comprueba la firma criptográfica del **Token JWT**.
5. Si pasa ambos filtros, el controlador atiende la lógica y conecta a **MariaDB** mediante JPA, devolviendo la respuesta al Desktop.

---

## Roadmap

- [ ] Empaquetar el cliente Desktop en instaladores nativos (.exe, .dmg) usando `jpackage`.
- [ ] Desplegar la API 2.0 en un servidor Cloud (AWS, Render o Railway).
- [ ] Migrar el despliegue de desarrollo a un orquestador multi-contenedor (Docker Compose).
- [ ] Implementar sistema de recuperación de contraseñas por correo.

---

## Autor

Desarrollado como Proyecto de Fin de Ciclo (PFC) por **Omar**.
