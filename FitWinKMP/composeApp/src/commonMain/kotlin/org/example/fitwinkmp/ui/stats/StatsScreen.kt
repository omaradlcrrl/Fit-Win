package org.example.fitwinkmp.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.features.stats.data.dto.*
import org.example.fitwinkmp.features.stats.presentation.StatsUiState
import org.example.fitwinkmp.features.stats.presentation.StatsViewModel
import org.example.fitwinkmp.ui.stats.components.CheckInSheet
import org.example.fitwinkmp.ui.stats.components.MetricChartSheet
import org.example.fitwinkmp.ui.stats.components.PerformanceTab
import org.example.fitwinkmp.ui.stats.components.PhysiqueTab
import org.example.fitwinkmp.ui.theme.FitwinColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val s = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()
    val checkInSuccess by viewModel.checkInSuccess.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showCheckIn by remember { mutableStateOf(false) }
    var showChart by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val chartSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) { viewModel.loadStats() }

    LaunchedEffect(checkInSuccess) {
        if (checkInSuccess) {
            showCheckIn = false
            viewModel.resetCheckInSuccess()
        }
    }

    // ─── CheckIn Sheet ────────────────────────────────────────────────────────
    if (showCheckIn) {
        val successState = uiState as? StatsUiState.Success
        CheckInSheet(
            sheetState = sheetState,
            onDismiss = { showCheckIn = false },
            onSave = { peso, grasa, pecho, espalda, hombro, brazo, muslo, cintura ->
                viewModel.saveCheckIn(peso, grasa, pecho, espalda, hombro, brazo, muslo, cintura)
            },
            medicionHoy = successState?.medicionHoy,
            ultimaMedicion = successState?.physiqueData?.ultimaMedicion
        )
    }

    // ─── Chart Sheet ──────────────────────────────────────────────────────────
    if (showChart) {
        MetricChartSheet(
            sheetState = chartSheetState,
            viewModel = viewModel,
            onDismiss = { showChart = false }
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
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = FitwinColors.PrimaryContainer
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StatsTabSelector(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            Spacer(modifier = Modifier.height(8.dp))

            when (val state = uiState) {
                is StatsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FitwinColors.PrimaryContainer)
                    }
                }

                is StatsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = FitwinColors.Error,
                                modifier = Modifier.padding(horizontal = 32.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { viewModel.loadStats() },
                                colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                            ) {
                                Text(s.profileReintentar, color = FitwinColors.OnPrimary, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                is StatsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Banners de check-in (solo en tab Physique)
                        if (selectedTab == 1) {
                            if (state.needsCheckIn) {
                                item { CheckInBanner(onClick = { showCheckIn = true }) }
                            } else {
                                item { EditCheckInBanner(onClick = { showCheckIn = true }) }
                            }
                        }

                        if (selectedTab == 0) {
                            item {
                                PerformanceTab(
                                    data = state.performanceData,
                                    nutritionData = state.nutritionData
                                )
                            }
                        } else {
                            item {
                                PhysiqueTab(
                                    data = state.physiqueData,
                                    pesoActual = state.pesoActual,
                                    onOpenChart = { metrica ->
                                        viewModel.openChart(metrica)
                                        showChart = true
                                    }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsTabSelector(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("PERFORMANCE", "PHYSIQUE")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainer)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) FitwinColors.PrimaryContainer else FitwinColors.SurfaceContainer)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
private fun CheckInBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainerHigh)
            .border(1.dp, FitwinColors.PrimaryContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "REGISTRA TU PROGRESO DE HOY",
                color = FitwinColors.PrimaryContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(text = "Sin datos de hoy aún", color = FitwinColors.OnSurfaceVariant, fontSize = 10.sp)
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(FitwinColors.PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = FitwinColors.OnPrimary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EditCheckInBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainer)
            .border(1.dp, FitwinColors.OutlineVariant, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓ Progreso de hoy registrado — toca para editar",
            color = FitwinColors.OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "EDITAR",
            color = FitwinColors.PrimaryContainer,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}
