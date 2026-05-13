package org.example.fitwinkmp.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.features.stats.data.dto.*
import org.example.fitwinkmp.features.stats.presentation.ChartUiState
import org.example.fitwinkmp.features.stats.presentation.StatsViewModel
import org.example.fitwinkmp.ui.theme.FitwinColors
import kotlin.math.abs

/**
 * Bottom Sheet de pantalla completa que muestra gráficas de líneas interactivas
 * para cualquier métrica corporal, con selector de métrica y rango temporal.
 *
 * Se abre al tocar cualquier medida en PhysiqueTab.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricChartSheet(
    sheetState: SheetState,
    viewModel: StatsViewModel,
    onDismiss: () -> Unit
) {
    val chartState by viewModel.chartState.collectAsState()
    val selectedMetrica by viewModel.selectedMetrica.collectAsState()
    val selectedRango by viewModel.selectedRango.collectAsState()

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.resetChart()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = FitwinColors.Background,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PROGRESO",
                        color = FitwinColors.OnSurface,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = selectedMetrica.label.uppercase(),
                        color = FitwinColors.PrimaryContainer,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 28.sp
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.resetChart()
                        onDismiss()
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = FitwinColors.OnSurfaceVariant
                    )
                }
            }

            // ─── Selector de Rango ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(FitwinColors.SurfaceContainer),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                ChartRango.entries.forEach { rango ->
                    val isSelected = selectedRango == rango
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) FitwinColors.PrimaryContainer
                                else FitwinColors.SurfaceContainer
                            )
                            .clickable { viewModel.setRango(rango) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rango.label,
                            color = if (isSelected) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // ─── Selector de Métrica (chips scrollables) ──────────────────────
            MetricaChipsRow(
                selectedMetrica = selectedMetrica,
                onSelect = { viewModel.setMetrica(it) }
            )

            // ─── Gráfica ──────────────────────────────────────────────────────
            when (val state = chartState) {
                is ChartUiState.Idle, is ChartUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(FitwinColors.SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = FitwinColors.PrimaryContainer,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                is ChartUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(FitwinColors.SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = FitwinColors.Error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                is ChartUiState.Success -> {
                    val data = state.data
                    // Contenedor de la gráfica
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(FitwinColors.SurfaceContainer)
                            .padding(16.dp)
                    ) {
                        LineChartCanvas(
                            chartData = data,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // ─── Estadísticas ─────────────────────────────────────────
                    if (data.puntos.isNotEmpty()) {
                        ChartStatsCard(data = data)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MetricaChipsRow(
    selectedMetrica: ChartMetrica,
    onSelect: (ChartMetrica) -> Unit
) {
    // Dos filas de chips
    val row1 = listOf(ChartMetrica.PESO, ChartMetrica.PORCENTAJE_GRASA, ChartMetrica.MASA_MAGRA)
    val row2 = listOf(ChartMetrica.PECHO, ChartMetrica.ESPALDA, ChartMetrica.HOMBRO, ChartMetrica.BRAZO, ChartMetrica.MUSLO, ChartMetrica.CINTURA)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            row1.forEach { m ->
                MetricaChip(
                    metrica = m,
                    isSelected = selectedMetrica == m,
                    onClick = { onSelect(m) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            row2.forEach { m ->
                MetricaChip(
                    metrica = m,
                    isSelected = selectedMetrica == m,
                    onClick = { onSelect(m) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricaChip(
    metrica: ChartMetrica,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) FitwinColors.PrimaryContainer
                else FitwinColors.SurfaceContainerHigh
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = FitwinColors.OutlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = metrica.label.uppercase(),
            color = if (isSelected) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun ChartStatsCard(data: ChartData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "RESUMEN DEL PERÍODO",
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            // Inicio → Actual + cambio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatSummaryItem(
                    modifier = Modifier.weight(1f),
                    label = "INICIO",
                    value = "${"%.1f".format(data.primero ?: 0.0)} ${data.metrica.unidad}",
                    color = FitwinColors.OnSurface
                )
                StatSummaryItem(
                    modifier = Modifier.weight(1f),
                    label = "ACTUAL",
                    value = "${"%.1f".format(data.ultimo ?: 0.0)} ${data.metrica.unidad}",
                    color = FitwinColors.PrimaryContainer
                )
                data.cambioAbsoluto?.let { cambio ->
                    val isPositive = cambio >= 0
                    val sign = if (isPositive) "+" else ""
                    val color = colorForCambio(data.metrica, isPositive)
                    StatSummaryItem(
                        modifier = Modifier.weight(1f),
                        label = "CAMBIO",
                        value = "$sign${"%.1f".format(cambio)} ${data.metrica.unidad}",
                        color = color
                    )
                }
            }

            HorizontalDivider(color = FitwinColors.OutlineVariant, thickness = 1.dp)

            // Min / Max / Media
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatSummaryItem(
                    modifier = Modifier.weight(1f),
                    label = "MIN",
                    value = "${"%.1f".format(data.min)} ${data.metrica.unidad}",
                    color = FitwinColors.OnSurface
                )
                StatSummaryItem(
                    modifier = Modifier.weight(1f),
                    label = "MAX",
                    value = "${"%.1f".format(data.max)} ${data.metrica.unidad}",
                    color = FitwinColors.OnSurface
                )
                StatSummaryItem(
                    modifier = Modifier.weight(1f),
                    label = "MEDIA",
                    value = "${"%.1f".format(data.media)} ${data.metrica.unidad}",
                    color = FitwinColors.OnSurface
                )
            }

            // % Cambio
            data.cambioPorcentaje?.let { pct ->
                val isPositive = pct >= 0
                val sign = if (isPositive) "+" else ""
                val color = colorForCambio(data.metrica, isPositive)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cambioTexto(data.metrica, pct),
                        color = FitwinColors.OnSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$sign${"%.1f".format(abs(pct))}%",
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Text(
                text = "${data.puntos.size} mediciones registradas",
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun StatSummaryItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}

/**
 * El color del cambio depende de la métrica:
 * Para CINTURA y PORCENTAJE_GRASA bajar es bueno (verde = PrimaryContainer),
 * para el resto subir es bueno.
 */
private fun colorForCambio(metrica: ChartMetrica, isPositive: Boolean): Color {
    val bajarEsBueno = metrica == ChartMetrica.CINTURA || metrica == ChartMetrica.PORCENTAJE_GRASA
    return if (bajarEsBueno) {
        if (isPositive) FitwinColors.Error else FitwinColors.PrimaryContainer
    } else {
        if (isPositive) FitwinColors.PrimaryContainer else FitwinColors.Error
    }
}

private fun cambioTexto(metrica: ChartMetrica, pct: Double): String {
    val bajarEsBueno = metrica == ChartMetrica.CINTURA || metrica == ChartMetrica.PORCENTAJE_GRASA
    return when {
        pct > 0 -> if (bajarEsBueno) "Ha subido en este período" else "Ha mejorado en este período"
        pct < 0 -> if (bajarEsBueno) "Ha mejorado en este período" else "Ha bajado en este período"
        else -> "Sin cambios en este período"
    }
}
