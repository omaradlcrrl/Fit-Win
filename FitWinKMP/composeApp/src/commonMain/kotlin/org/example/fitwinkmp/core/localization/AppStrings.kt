package org.example.fitwinkmp.core.localization

data class AppStrings(
    // Bottom nav
    val navNutricion: String,
    val navEntreno: String,
    val navStats: String,
    val navPerfil: String,

    // Food screen
    val foodRegistroDiario: String,
    val foodHoy: String,
    val foodTitulo1: String,   // "ALIMENTA TU" / "FUEL YOUR"
    val foodTitulo2: String,   // "MOTOR" / "ENGINE"
    val foodCaloriasRest: String,
    val foodObjetivoKcal: String, // "OBJETIVO: X KCAL" prefix
    val foodProteinas: String,
    val foodCarbos: String,
    val foodGrasas: String,
    val foodDesayuno: String,
    val foodAlmuerzo: String,
    val foodCena: String,
    val foodSnacks: String,
    val foodRegistrado: String,
    val foodTocaParaRegistrar: String, // prefix, meal name appended
    val foodGuardar: String,
    val foodCancelar: String,
    val foodNombreAlimento: String,
    val foodCantidad: String,
    val foodUnidad: String,
    val foodRegistrar: String, // "REGISTRAR" / "LOG" prefix in dialog title
    val foodEliminar: String,
    val foodEditar: String,

    // Training screen
    val trainingTitle: String,
    val trainingPlanHoy: String,
    val trainingDiaDescanso: String,
    val trainingCrearRutina: String,
    val trainingSesionActiva: String,
    val trainingAplastaReps: String,
    val trainingObjetivo: String, // "OBJETIVO:" prefix
    val trainingSerie: String,
    val trainingTodasSeries: String,
    val trainingIniciarSesion: String,
    val trainingFinalizarSesion: String,
    val trainingNuevaRutina: String,
    val trainingEliminarRutina: String,
    val trainingSeleccionarRutina: String,
    val trainingAnyadirEjercicio: String,
    val trainingNombreEjercicio: String,
    val trainingEjemploEjercicio: String,
    val trainingSeries: String,
    val trainingDescansoSeg: String,
    val trainingRepsMin: String,
    val trainingRepsMax: String,
    val trainingGuardarRutina: String,
    val trainingNombreRutina: String,
    val trainingSinEjercicios: String,
    val trainingAnadir: String,
    val trainingCancelar: String,
    val trainingDescanso: String, // "REST" in reps label
    val trainingRegistrar: String, // "LOG" button
    val trainingEditarRutina: String,

    // Profile
    val profilePesoActual: String,
    val profileAltura: String,
    val profileImc: String,
    val profileActividad: String,
    val profileObjetivoNutricional: String,
    val profileMiCuenta: String,
    val profileEditarPerfil: String,
    val profileInfoCuenta: String,
    val profileCerrarSesion: String,
    val profileActualizado: String,
    val profileReintentar: String,
    val profileDesdeRegistro: String,
    val profileGenerarObjetivo: String,

    // Edit profile dialog
    val editPerfilTitulo1: String,
    val editPerfilTitulo2: String,
    val editPerfilCancelar: String,
    val editPerfilDatosPersonales: String,
    val editPerfilDatosFisicos: String,
    val editPerfilNombre: String,
    val editPerfilApellidos: String,
    val editPerfilEmail: String,
    val editPerfilPeso: String,
    val editPerfilAltura: String,
    val editPerfilGenero: String,
    val editPerfilNivelActividad: String,
    val editPerfilObjetivo: String,
    val editPerfilGuardar: String,

    // Settings
    val settingsTitulo: String,
    val settingsIdioma: String,
    val settingsEspanyol: String,
    val settingsEspanyolSub: String,
    val settingsIngles: String,
    val settingsInglesSub: String,
    val settingsInfoText: String,
    val settingsVersion: String,

    // Common
    val commonProximamente: String,
    val commonEstadisticas: String,
    val commonAjustes: String,
    val commonLoading: String
)
