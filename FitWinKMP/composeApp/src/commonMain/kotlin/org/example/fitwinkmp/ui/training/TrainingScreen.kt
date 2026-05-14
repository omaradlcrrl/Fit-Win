package org.example.fitwinkmp.ui.training

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.fitwinkmp.features.training.data.dto.EjercicioDTO
import org.example.fitwinkmp.features.training.data.dto.EjercicioGlobalDTO
import org.example.fitwinkmp.features.training.data.dto.SerieRealizadaDTO
import org.example.fitwinkmp.features.training.presentation.TrainingUiState
import org.example.fitwinkmp.features.training.presentation.TrainingViewModel
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.ui.theme.FitwinColors

@Composable
fun TrainingScreen(viewModel: TrainingViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    Scaffold(
        containerColor = FitwinColors.Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = s.trainingTitle,
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            when (val state = uiState) {
                is TrainingUiState.Idle, is TrainingUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FitwinColors.PrimaryContainer)
                    }
                }
                is TrainingUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message, color = FitwinColors.Error, modifier = Modifier.padding(16.dp))
                            Button(
                                onClick = { viewModel.loadTodaysWorkout() },
                                colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                            ) {
                                Text(s.profileReintentar, color = FitwinColors.OnPrimary)
                            }
                        }
                    }
                }
                is TrainingUiState.DailyWorkoutView -> {
                    DailyWorkoutView(
                        state = state,
                        onStartWorkout = { viewModel.startWorkout() },
                        onCreateRoutine = { viewModel.openRoutineBuilder() },
                        onSelectRoutine = { id -> viewModel.setActiveRutina(id) },
                        onDeleteRoutine = { id -> viewModel.deleteRutina(id) },
                        onUpdateRoutine = { id, nombre, dias -> viewModel.updateRutina(id, nombre, dias) },
                        onDeleteEjercicio = { id -> viewModel.deleteEjercicio(id) }
                    )
                }
                is TrainingUiState.RoutineBuilder -> {
                    RoutineBuilderView(
                        ejerciciosGlobales = state.ejerciciosGlobales,
                        onSave = { nombre, dias, ejercicios ->
                            viewModel.createRutinaAndAssign(nombre, dias, ejercicios)
                        },
                        onCancel = { viewModel.loadTodaysWorkout() }
                    )
                }
                is TrainingUiState.ActiveWorkoutSession -> {
                    ActiveWorkoutView(
                        state = state,
                        onLogSet = { ejId, peso, reps, orden ->
                            viewModel.logSet(ejId, peso, reps, orden)
                        },
                        onFinish = {
                            viewModel.finalizarSesion(8, 6, "Completado desde la app")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DailyWorkoutView(
    state: TrainingUiState.DailyWorkoutView,
    onStartWorkout: () -> Unit,
    onCreateRoutine: () -> Unit,
    onSelectRoutine: (Int) -> Unit,
    onDeleteRoutine: (Int) -> Unit,
    onUpdateRoutine: (Int, String, String) -> Unit,
    onDeleteEjercicio: (Int) -> Unit
) {
    val s = LocalStrings.current
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.name
    var expanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Diálogo de edición de rutina
    if (showEditDialog && state.rutinaActiva != null) {
        var nombreEdit by remember { mutableStateOf(state.rutinaActiva.nombre) }
        var diasEdit by remember { mutableStateOf(state.rutinaActiva.diasActivos ?: "") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = FitwinColors.SurfaceContainer,
            title = { Text(s.trainingEditarRutina, color = FitwinColors.OnSurface, fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nombreEdit,
                        onValueChange = { nombreEdit = it },
                        label = { Text(s.trainingNombreRutina) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        state.rutinaActiva.rutinaId?.let { id ->
                            onUpdateRoutine(id, nombreEdit, diasEdit)
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                ) { Text(s.trainingGuardarRutina, color = FitwinColors.OnPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text(s.trainingCancelar, color = FitwinColors.OnSurfaceVariant) }
            }
        )
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Active Routine Selector
        if (state.rutinasDisponibles.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FitwinColors.SurfaceContainerHighest)
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.rutinaActiva?.nombre ?: s.trainingSeleccionarRutina,
                        color = FitwinColors.OnSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(FitwinColors.SurfaceContainerHighest)
                ) {
                    state.rutinasDisponibles.forEach { rutina ->
                        DropdownMenuItem(
                            text = { Text(rutina.nombre, color = FitwinColors.OnSurface) },
                            onClick = {
                                expanded = false
                                rutina.rutinaId?.let { onSelectRoutine(it) }
                            }
                        )
                    }
                    Divider(color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.2f))
                    DropdownMenuItem(
                        text = { Text(s.trainingNuevaRutina, color = FitwinColors.PrimaryContainer) },
                        onClick = {
                            expanded = false
                            onCreateRoutine()
                        }
                    )
                    if (state.rutinaActiva != null) {
                        DropdownMenuItem(
                            text = { Text(s.trainingEditarRutina, color = FitwinColors.PrimaryContainer) },
                            onClick = {
                                expanded = false
                                showEditDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(s.trainingEliminarRutina, color = FitwinColors.Error) },
                            onClick = {
                                expanded = false
                                state.rutinaActiva.rutinaId?.let { onDeleteRoutine(it) }
                            }
                        )
                    }
                }
            }
        }

        Text(
            text = s.trainingPlanHoy,
            color = FitwinColors.Secondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = today,
            color = FitwinColors.OnSurface,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (state.ejercicios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = s.trainingDiaDescanso,
                        color = FitwinColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onCreateRoutine,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FitwinColors.PrimaryContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitwinColors.PrimaryContainer)
                    ) {
                        Text(s.trainingCrearRutina, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.ejercicios) { ej ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FitwinColors.SurfaceContainer)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FitwinColors.SurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${ej.series}x", color = FitwinColors.OnSurface, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ej.nombreEjercicio ?: "Ejercicio",
                                color = FitwinColors.OnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${ej.repeticionesMin}-${ej.repeticionesMax} REPS • ${ej.descansoSegundos}s REST",
                                color = FitwinColors.OnSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        // Eliminar ejercicio
                        var showMenuEj by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMenuEj = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "Opciones",
                                    tint = FitwinColors.OnSurfaceVariant
                                )
                            }
                            DropdownMenu(expanded = showMenuEj, onDismissRequest = { showMenuEj = false }) {
                                DropdownMenuItem(
                                    text = { Text(s.foodEliminar, color = FitwinColors.Error) },
                                    onClick = {
                                        showMenuEj = false
                                        ej.ejercicioId?.let { onDeleteEjercicio(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitwinColors.PrimaryContainer)
                    .clickable { onStartWorkout() }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = s.trainingIniciarSesion,
                    color = FitwinColors.OnPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ActiveWorkoutView(
    state: TrainingUiState.ActiveWorkoutSession,
    onLogSet: (Int, Double, Int, Int) -> Unit,
    onFinish: () -> Unit
) {
    val s = LocalStrings.current
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = s.trainingSesionActiva,
            color = FitwinColors.Error, // Red for active
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = s.trainingAplastaReps,
            color = FitwinColors.OnSurface,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(state.ejercicios) { ej ->
                val ejId = ej.ejercicioId ?: return@items
                val seriesCompletadas = state.seriesRealizadas.filter { it.ejercicioId == ejId }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FitwinColors.SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = (ej.nombreEjercicio ?: "Ejercicio").uppercase(),
                        color = FitwinColors.PrimaryContainer,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = "${s.trainingObjetivo} ${ej.series} SETS x ${ej.repeticionesMin}-${ej.repeticionesMax} REPS",
                        color = FitwinColors.OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Mostrar series ya logeadas
                    seriesCompletadas.forEachIndexed { index, serie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FitwinColors.SurfaceContainerHighest)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${s.trainingSerie} ${index + 1}", color = FitwinColors.OnSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${serie.pesoKg} KG x ${serie.repeticionesRealizadas} REPS", color = FitwinColors.OnSurface, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = FitwinColors.PrimaryContainer, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Input para la siguiente serie (si faltan)
                    if (seriesCompletadas.size < ej.series) {
                        var currentWeight by remember { mutableStateOf("") }
                        var currentReps by remember { mutableStateOf("") }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = currentWeight,
                                onValueChange = { currentWeight = it },
                                label = { Text("KG", fontSize = 10.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = FitwinColors.OnSurface,
                                    unfocusedTextColor = FitwinColors.OnSurface
                                )
                            )
                            OutlinedTextField(
                                value = currentReps,
                                onValueChange = { currentReps = it },
                                label = { Text("REPS", fontSize = 10.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = FitwinColors.OnSurface,
                                    unfocusedTextColor = FitwinColors.OnSurface
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FitwinColors.PrimaryContainer)
                                    .clickable {
                                        val w = currentWeight.toDoubleOrNull() ?: 0.0
                                        val r = currentReps.toIntOrNull() ?: 0
                                        if (r > 0) {
                                            onLogSet(ejId, w, r, seriesCompletadas.size + 1)
                                            currentWeight = ""
                                            currentReps = ""
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Text(s.trainingRegistrar, color = FitwinColors.OnPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(s.trainingTodasSeries, color = FitwinColors.PrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FitwinColors.Error)
                .clickable { onFinish() }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = s.trainingFinalizarSesion,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun RoutineBuilderView(
    ejerciciosGlobales: List<EjercicioGlobalDTO>,
    onSave: (String, String, List<EjercicioDTO>) -> Unit,
    onCancel: () -> Unit
) {
    val s = LocalStrings.current
    var nombreRutina by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf("MONDAY") }
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Lista de ejercicios configurados en memoria (aún no guardados)
    val ejerciciosPlanificados = remember { mutableStateListOf<EjercicioDTO>() }
    
    val daysOfWeek = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    val daysInitials = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(s.trainingCrearRutina, color = FitwinColors.PrimaryContainer, fontSize = 24.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = nombreRutina,
            onValueChange = { nombreRutina = it },
            label = { Text(s.trainingNombreRutina) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = FitwinColors.OnSurface,
                unfocusedTextColor = FitwinColors.OnSurface
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Selector de días
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val isSelected = selectedDay == day
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) FitwinColors.PrimaryContainer else FitwinColors.SurfaceContainerHighest)
                        .clickable { selectedDay = day },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = daysInitials[index],
                        color = if (isSelected) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("$selectedDay ${s.trainingTitle}", color = FitwinColors.Secondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de ejercicios para el día seleccionado
        val ejerciciosDelDia = ejerciciosPlanificados.filter { it.diaSemana == selectedDay }
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (ejerciciosDelDia.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(s.trainingSinEjercicios, color = FitwinColors.OnSurfaceVariant, fontSize = 12.sp)
                    }
                }
            } else {
                items(ejerciciosDelDia) { ej ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FitwinColors.SurfaceContainer)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(FitwinColors.SurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${ej.series}x", color = FitwinColors.OnSurface, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = ej.nombreEjercicio ?: "Ejercicio", color = FitwinColors.OnSurface, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${ej.repeticionesMin}-${ej.repeticionesMax} REPS • ${ej.descansoSegundos}s REST",
                                color = FitwinColors.OnSurfaceVariant, fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            
            item {
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FitwinColors.PrimaryContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitwinColors.PrimaryContainer)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(s.trainingAnyadirEjercicio, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(s.trainingCancelar) }
            Button(
                onClick = {
                    val activeDays = ejerciciosPlanificados.map { it.diaSemana }.distinct().joinToString(",")
                    onSave(nombreRutina.ifBlank { "Mi Rutina" }, activeDays, ejerciciosPlanificados)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
            ) {
                Text(s.trainingGuardarRutina, color = FitwinColors.OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showAddDialog) {
        AddExerciseDialog(
            diaSeleccionado = selectedDay,
            onDismiss = { showAddDialog = false },
            onAdd = { nombre, series, minReps, maxReps, descanso ->
                val newEj = EjercicioDTO(
                    ejercicioGlobalId = 0,
                    nombreEjercicio = nombre,
                    rutinaId = 0,
                    usuarioId = 0,
                    diaSemana = selectedDay,
                    series = series,
                    repeticionesMin = minReps,
                    repeticionesMax = maxReps,
                    descansoSegundos = descanso,
                    pesoKg = 0.0,
                    posicion = ejerciciosPlanificados.count { it.diaSemana == selectedDay } + 1
                )
                ejerciciosPlanificados.add(newEj)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddExerciseDialog(
    diaSeleccionado: String,
    onDismiss: () -> Unit,
    onAdd: (String, Int, Int, Int, Int) -> Unit
) {
    val s = LocalStrings.current
    var exerciseName by remember { mutableStateOf("") }
    
    var series by remember { mutableStateOf("4") }
    var minReps by remember { mutableStateOf("8") }
    var maxReps by remember { mutableStateOf("12") }
    var descanso by remember { mutableStateOf("90") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FitwinColors.SurfaceContainer,
        title = {
            Text("${s.trainingAnadir} — $diaSeleccionado", color = FitwinColors.OnSurface, fontWeight = FontWeight.Black)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text(s.trainingNombreEjercicio) },
                    placeholder = { Text(s.trainingEjemploEjercicio) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FitwinColors.OnSurface,
                        unfocusedTextColor = FitwinColors.OnSurface
                    ),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = series,
                        onValueChange = { series = it },
                        label = { Text(s.trainingSeries) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = descanso,
                        onValueChange = { descanso = it },
                        label = { Text(s.trainingDescansoSeg) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minReps,
                        onValueChange = { minReps = it },
                        label = { Text(s.trainingRepsMin) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxReps,
                        onValueChange = { maxReps = it },
                        label = { Text(s.trainingRepsMax) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (exerciseName.isNotBlank()) {
                        onAdd(
                            exerciseName.trim(),
                            series.toIntOrNull() ?: 4,
                            minReps.toIntOrNull() ?: 8,
                            maxReps.toIntOrNull() ?: 12,
                            descanso.toIntOrNull() ?: 90
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
            ) {
                Text(s.trainingAnadir, color = FitwinColors.OnPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(s.trainingCancelar, color = FitwinColors.OnSurfaceVariant)
            }
        }
    )
}

