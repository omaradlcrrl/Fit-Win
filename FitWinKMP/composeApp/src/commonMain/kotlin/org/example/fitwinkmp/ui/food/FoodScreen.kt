package org.example.fitwinkmp.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.features.food.data.dto.ComidaDTO
import org.example.fitwinkmp.features.food.presentation.FoodUiState
import org.example.fitwinkmp.features.food.presentation.FoodViewModel
import org.example.fitwinkmp.features.stats.data.dto.ObjetivoStatsDTO
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.ui.theme.FitwinColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun FoodScreen(viewModel: FoodViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf("DESAYUNO") }
    var editingMeal by remember { mutableStateOf<ComidaDTO?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadComidas()
    }

    if (showAddDialog) {
        AddFoodDialog(
            tipoComida = selectedMealType,
            usuarioId = viewModel.currentUserId,
            fecha = selectedDate,
            onDismiss = { showAddDialog = false },
            onSave = { comida ->
                viewModel.saveComida(comida)
                showAddDialog = false
            }
        )
    }

    if (editingMeal != null) {
        AddFoodDialog(
            tipoComida = editingMeal!!.tipoComida,
            usuarioId = viewModel.currentUserId,
            fecha = selectedDate,
            comidaToEdit = editingMeal,
            onDismiss = { editingMeal = null },
            onSave = { comida ->
                viewModel.saveComida(comida)
                editingMeal = null
            },
            onUpdate = { id, comida ->
                viewModel.updateComida(id, comida)
                editingMeal = null
            }
        )
    }

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
                    text = "FIT-WIN",
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = FitwinColors.PrimaryContainer
                )
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is FoodUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FitwinColors.PrimaryContainer)
                }
            }
            is FoodUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = FitwinColors.Error, modifier = Modifier.padding(16.dp))
                        Button(
                            onClick = { viewModel.loadComidas() },
                            colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                        ) {
                            Text("REINTENTAR", color = FitwinColors.OnPrimary)
                        }
                    }
                }
            }
            is FoodUiState.Success -> {
                FoodContent(
                    comidas = state.comidas,
                    objetivo = state.objetivo,
                    selectedDate = selectedDate,
                    padding = padding,
                    onAddMealClick = { tipo ->
                        selectedMealType = tipo
                        showAddDialog = true
                    },
                    onDeleteMeal = { id -> viewModel.deleteComida(id) },
                    onEditMeal = { meal -> editingMeal = meal },
                    onPrevDay = { viewModel.goToPreviousDay() },
                    onNextDay = { viewModel.goToNextDay() }
                )
            }
        }
    }
}

@Composable
fun FoodContent(
    comidas: List<ComidaDTO>,
    objetivo: ObjetivoStatsDTO?,
    selectedDate: LocalDate,
    padding: PaddingValues,
    onAddMealClick: (String) -> Unit,
    onDeleteMeal: (Int) -> Unit,
    onEditMeal: (ComidaDTO) -> Unit,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val s = LocalStrings.current
    val totalCal = comidas.sumOf { it.calorias }.roundToInt()
    val totalProt = comidas.sumOf { it.proteinas }.roundToInt()
    val totalCarbs = comidas.sumOf { it.carbohidratos }.roundToInt()
    val totalFats = comidas.sumOf { it.grasasSaturadas }.roundToInt()

    // Usar metas dinámicas del objetivo o fallback sensato
    val goalCal = objetivo?.caloriasObjetivo?.toInt() ?: 2500
    val goalProt = objetivo?.proteinasObjetivo?.toInt() ?: 150
    val goalCarbs = objetivo?.carbohidratosObjetivo?.toInt() ?: 250
    val goalFats = objetivo?.grasasObjetivo?.toInt() ?: 60

    val isToday = selectedDate == LocalDate.now()
    val dateLabel = if (isToday) s.foodHoy else {
        selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")).uppercase()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = s.foodRegistroDiario,
                color = FitwinColors.Secondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Día anterior",
                    tint = FitwinColors.PrimaryContainer,
                    modifier = Modifier.size(20.dp).clickable { onPrevDay() }
                )
                Text(
                    text = dateLabel,
                    color = FitwinColors.OnSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Día siguiente",
                    tint = if (isToday) FitwinColors.OnSurfaceVariant else FitwinColors.PrimaryContainer,
                    modifier = Modifier.size(20.dp).clickable { if (!isToday) onNextDay() }
                )
            }

            Text(
                text = s.foodTitulo1,
                color = FitwinColors.OnSurface,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 32.sp
            )
            Text(
                text = s.foodTitulo2,
                color = FitwinColors.PrimaryContainer,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 32.sp
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                            .background(FitwinColors.SurfaceContainerHighest)
                            .border(1.dp, FitwinColors.PrimaryContainer, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${goalCal - totalCal}",
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = s.foodCaloriasRest,
                                color = FitwinColors.OnSurfaceVariant,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Text(
                        text = "${s.foodObjetivoKcal} $goalCal KCAL",
                        color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FitwinColors.SurfaceContainer)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    MacroBar(s.foodProteinas, totalProt, goalProt, FitwinColors.MacroProtein)
                    MacroBar(s.foodCarbos, totalCarbs, goalCarbs, FitwinColors.MacroCarbs)
                    MacroBar(s.foodGrasas, totalFats, goalFats, FitwinColors.MacroFats)
                }
            }
        }

        val grouped = comidas.groupBy { it.tipoComida }
        item { MealSection(s.foodDesayuno, "DESAYUNO", grouped["DESAYUNO"] ?: emptyList(), onAddMealClick, onDeleteMeal, onEditMeal) }
        item { MealSection(s.foodAlmuerzo, "ALMUERZO", grouped["ALMUERZO"] ?: emptyList(), onAddMealClick, onDeleteMeal, onEditMeal) }
        item { MealSection(s.foodCena, "CENA", grouped["CENA"] ?: emptyList(), onAddMealClick, onDeleteMeal, onEditMeal) }
        item { MealSection(s.foodSnacks, "SNACK", grouped["SNACK"] ?: emptyList(), onAddMealClick, onDeleteMeal, onEditMeal) }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun MacroBar(name: String, current: Int, goal: Int, color: Color) {
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, color = FitwinColors.OnSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "${current}g", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = " / ${goal}g", color = FitwinColors.OnSurfaceVariant, fontSize = 9.sp)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(FitwinColors.SurfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun MealSection(
    title: String,
    apiType: String,
    meals: List<ComidaDTO>,
    onAddClick: (String) -> Unit,
    onDeleteMeal: (Int) -> Unit,
    onEditMeal: (ComidaDTO) -> Unit
) {
    val s = LocalStrings.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = FitwinColors.OnSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
                if (meals.isNotEmpty()) {
                    Text(
                        text = s.foodRegistrado,
                        color = FitwinColors.OnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FitwinColors.PrimaryContainer)
                    .clickable { onAddClick(apiType) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir $title", tint = FitwinColors.OnPrimary, modifier = Modifier.size(20.dp))
            }
        }

        if (meals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FitwinColors.SurfaceContainerHighest, RoundedCornerShape(12.dp))
                    .clickable { onAddClick(apiType) }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${s.foodTocaParaRegistrar} $title",
                    color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                meals.forEach { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FitwinColors.SurfaceContainer)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FitwinColors.SurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(meal.nombre.take(1).uppercase(), color = FitwinColors.OnSurface, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = meal.nombre,
                                color = FitwinColors.OnSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${meal.calorias.roundToInt()} KCAL",
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Menú contextual con Editar y Eliminar
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = FitwinColors.OnSurfaceVariant)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(s.foodEditar, color = FitwinColors.OnSurface) },
                                    onClick = {
                                        showMenu = false
                                        onEditMeal(meal)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(s.foodEliminar, color = FitwinColors.Error) },
                                    onClick = {
                                        showMenu = false
                                        meal.comidaId?.let { onDeleteMeal(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
