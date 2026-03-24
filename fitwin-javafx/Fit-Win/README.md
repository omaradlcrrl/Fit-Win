<div align="center">

# 🏋️ Fitwin

**Aplicación de escritorio para dominar tus entrenamientos.**

[![JavaFX](https://img.shields.io/badge/JavaFX-21-FF9000?logo=java&logoColor=white)](https://openjfx.io/)
[![Java](https://img.shields.io/badge/Java-15-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A22?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Platform](https://img.shields.io/badge/Platform-Windows_%7C_Mac_%7C_Linux-34A853)]()

</div>

---

## ¿Qué es Fitwin?

Fitwin es una aplicación cliente de escritorio (Desktop App) desarrollada completamente en **JavaFX**. Funciona como la cara visible interactiva que consumirá y gestionará tus progresos físicos desde el ordenador.

Este proyecto forma parte de un entorno más grande (PFC) y se comunica e integra directamente con una base de datos propia y un backend dedicado y seguro (**API 2.0**). Diseñada buscando un alto impacto visual con componentes UI avanzados para dashboards.

---

## Características

### 🎨 Interfaces Gráficas Nativas
Construida sobre el motor de **JavaFX 21** junto con vistas FXML inyectadas de forma nativa. Esto permite una separación perfecta entre el diseño gráfico y la lógica de controladores, dando lugar a una aplicación moderna y rápida que parece nativa.

### 📊 Dashboards con Visualizaciones
Hace uso de librerías potentes de UI como **TilesFX** y **ControlsFX** para poder dibujar y presentar interfaces modernas: gráficos, medidores, notificaciones personalizadas, auto-completados e iconos vectoriales (`Ikonli`).

### 🔌 Conectividad Full-Stack
Esta interfaz está preparada para serializar respuestas en JSON (usando `org.json`) e integrarse fuertemente con la arquitectura de backend, enrutando a la base de datos **MariaDB** de forma transparente para el usuario.

---

## Stack Tecnológico

| | Tecnología | Versión |
|:---|:---|:---|
| **Lenguaje** | [Java](https://openjdk.org/) | 15 |
| **UI Framework** | [JavaFX](https://openjfx.io/) | 21 |
| **Dashboard UI** | [TilesFX](https://github.com/HanSolo/tilesfx) | 11.48 |
| **Material UI / Controls** | [ControlsFX](https://controlsfx.github.io/) | 11.1.2 |
| **Librería de Iconos** | [Ikonli (JavaFX)](https://kordamp.org/ikonli/) | 12.3.1 |
| **Parseo Crudo** | JSON-java (`org.json`) | 2023 |
| **Driver Base Datos** | MariaDB Client | 3.2.0 |
| **Testing** | JUnit 5 Jupiter | 5.8.2 |
| **Build System** | [Maven](https://maven.apache.org/) | Maven Compiler 3.11.0 |

---

## Estructura

```
fitwin-javafx/Fit-Win/
├── src/
│   ├── main/
│   │   ├── java/org/example/fitwin/     # Código fuente y Controladores
│   │   └── resources/                   # Archivos FXML (Vistas), CSS e Imágenes
│   └── test/                            # Tests unitarios con JUnit
├── pom.xml                              # Dependencias y configuración de compilación de Maven
```

---

## Arquitectura del Cliente

Dada la naturaleza de JavaFX, el proyecto respeta una arquitectura base **MVC (Model-View-Controller)**:

- **Modelos**: Clases Java puras (POJOs) y DTOs generados con JSON parser (`org.json`) que representan el modelo de dominio y respuesta de la base de datos/API.
- **Vistas**: En archivos `.fxml`, donde se maqueta la disposición empleando FXML y hojas de estilo CSS para el color y theming.
- **Controladores**: Clases de Java unidas mediante decoradores `@FXML`. Capturan eventos de UI, formulan las llamadas a negocio, API o bbdd, y actualizan Reactivamente el UI.

---

## Cómo ejecutar

**Requisitos**: JDK 15 configurada, Maven (`mvnw` incluido), API 2.0 desplegada en el entorno de escritorio.

1. **Clonar repositorio y entrar a la estructura**
```bash
git clone https://github.com/omaradlcrrl/Fitwin.git
cd fitwin-javafx/Fit-Win
```

2. **Compilar y probar App**
Puedes lanzar la app en la propia terminal usando la integración del plugin Maven JavaFX:
```bash
./mvnw clean javafx:run
```

O abriendo a través de **IntelliJ IDEA**, dejando que indexe el `pom.xml`, y dando Run (Shift+F10) en la clase principal `App.java`.

---

## Autor

Desarrollado como Proyecto de Fin de Ciclo (PFC) por **Omar**.
