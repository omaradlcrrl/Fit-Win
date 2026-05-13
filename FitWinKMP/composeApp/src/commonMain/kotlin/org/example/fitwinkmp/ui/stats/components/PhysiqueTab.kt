package org.example.fitwinkmp.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.features.stats.data.dto.ChartMetrica
import org.example.fitwinkmp.features.stats.data.dto.MedicionCorporalDTO
import org.example.fitwinkmp.features.stats.data.dto.PhysiqueData
import org.example.fitwinkmp.ui.theme.FitwinColors
import kotlin.math.roundToInt

@Composable
fun PhysiqueTab(data: PhysiqueData, pesoActual: Double?, onOpenChart: (ChartMetrica) -> Unit = {}) {

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // ─── Header ────────────────────────────────────────────────────────
        Column {
            Text(
                text = "BODY",
                color = FitwinColors.OnSurface,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 36.sp
            )
            Text(
                text = "COMPOSITION",
                color = FitwinColors.PrimaryContainer,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 36.sp
            )
        }

        if (data.ultimaMedicion == null) {
            EmptyMedicionCard()
        } else {
            // ─── Body Composition Card ──────────────────────────────────────
            BodyCompositionCard(data = data)

            // ─── Métricas Derivadas ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                data.imc?.let {
                    DerivedMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "IMC",
                        value = "%.1f".format(it),
                        subtitle = imcCategory(it),
                        color = imcColor(it)
                    )
                }
                data.ffmi?.let {
                    DerivedMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "FFMI",
                        value = "%.1f".format(it),
                        subtitle = ffmiCategory(it),
                        color = FitwinColors.MacroCarbs
                    )
                }
                data.ratioCinturaHombro?.let {
                    DerivedMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "C/H RATIO",
                        value = "%.2f".format(it),
                        subtitle = if (it < 0.75) "V-TAPER" else "NORMAL",
                        color = if (it < 0.75) FitwinColors.PrimaryContainer else FitwinColors.OnSurfaceVariant
                    )
                }
            }

            // ─── Muscle Measurements ────────────────────────────────────────
            SectionHeader("MUSCLE MEASUREMENTS")
            MeasurementsCard(medicion = data.ultimaMedicion, onOpenChart = onOpenChart)

            // ─── Growth Trends ──────────────────────────────────────────────
            if (data.historialMediciones.size >= 2) {
                SectionHeader("GROWTH TRENDS · ÚLTIMO MES")
                GrowthTrendsCard(data = data)
            }

            // ─── Historial de Peso ──────────────────────────────────────────
            if (data.historialMediciones.size >= 2) {
                SectionHeader("EVOLUCIÓN DE PESO")
                WeightHistoryBars(
                    mediciones = data.historialMediciones,
                    onVerGrafica = { onOpenChart(ChartMetrica.PESO) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EmptyMedicionCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FitwinColors.OutlineVariant, RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = FitwinColors.PrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "SIN MEDICIONES AÚN",
                color = FitwinColors.OnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Pulsa el banner de arriba para registrar\ntu primera medición corporal",
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun BodyCompositionCard(data: PhysiqueData) {
    val m = data.ultimaMedicion!!
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Gauge % grasa
            m.porcentajeGrasa?.let { grasa ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GaugeChart(
                        value = grasa.toFloat(),
                        max = 40f,
                        color = FitwinColors.PrimaryContainer,
                        modifier = Modifier.size(100.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${grasa.roundToInt()}%",
                            color = FitwinColors.PrimaryContainer,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "% GRASA",
                            color = FitwinColors.OnSurfaceVariant,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Peso + Masa Magra
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                m.peso?.let {
                    BodyMetricItem("${it}kg", "PESO ACTUAL")
                }
                m.masaMagra?.let {
                    BodyMetricItem("${it}kg", "MASA MAGRA")
                }
                data.tendenciaPeso?.let {
                    val isPositive = it >= 0
                    BodyMetricItem(
                        value = "${if (isPositive) "+" else ""}${"%.1f".format(it)}%",
                        label = "TENDENCIA",
                        valueColor = if (isPositive) FitwinColors.Error else FitwinColors.PrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyMetricItem(value: String, label: String, valueColor: Color = FitwinColors.OnSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(label, color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun DerivedMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtitle: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainerHigh)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MeasurementsCard(
    medicion: org.example.fitwinkmp.features.stats.data.dto.MedicionCorporalDTO,
    onOpenChart: (ChartMetrica) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val measurements = listOf(
                Triple("PECHO", medicion.pecho, ChartMetrica.PECHO),
                Triple("ESPALDA", medicion.espalda, ChartMetrica.ESPALDA),
                Triple("HOMBRO", medicion.hombro, ChartMetrica.HOMBRO),
                Triple("BRAZO", medicion.brazo, ChartMetrica.BRAZO),
                Triple("MUSLO", medicion.muslo, ChartMetrica.MUSLO),
                Triple("CINTURA", medicion.cintura, ChartMetrica.CINTURA)
            ).filter { it.second != null }

            if (measurements.isEmpty()) {
                Text(
                    text = "Sin medidas musculares registradas",
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 10.sp
                )
            } else {
                val maxValue = measurements.maxOf { it.second ?: 0.0 }
                measurements.forEach { (nombre, valor, metrica) ->
                    MeasurementRow(
                        name = nombre,
                        valueCm = valor!!,
                        maxValue = maxValue,
                        onClick = { onOpenChart(metrica) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MeasurementRow(name: String, valueCm: Double, maxValue: Double, onClick: () -> Unit = {}) {
    val progress = (valueCm / maxValue).coerceIn(0.0, 1.0).toFloat()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = FitwinColors.OnSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.width(60.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(FitwinColors.SurfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(FitwinColors.PrimaryContainer)
            )
        }
        Text(
            text = "${valueCm.roundToInt()}cm",
            color = FitwinColors.OnSurface,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(44.dp).wrapContentWidth(Alignment.End)
        )
    }
}

@Composable
private fun GrowthTrendsCard(data: PhysiqueData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            data.tendenciaPeso?.let { t ->
                TrendRow("PESO", t)
            }
            data.tendenciaGrasa?.let { t ->
                TrendRow("% GRASA", t)
            }
            if (data.tendenciaPeso == null && data.tendenciaGrasa == null) {
                Text(
                    text = "Necesitas al menos 2 mediciones para ver tendencias",
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun TrendRow(label: String, percent: Double) {
    val isPositive = percent >= 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = FitwinColors.OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = if (isPositive) FitwinColors.Error else FitwinColors.PrimaryContainer,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "${if (isPositive) "+" else ""}${"%.1f".format(percent)}% este mes",
                color = if (isPositive) FitwinColors.Error else FitwinColors.PrimaryContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun WeightHistoryBars(
    mediciones: List<org.example.fitwinkmp.features.stats.data.dto.MedicionCorporalDTO>,
    onVerGrafica: () -> Unit = {}
) {
    val weights = mediciones.mapNotNull { it.peso }
    if (weights.isEmpty()) return
    val minW = weights.min()
    val maxW = weights.max()
    val range = if (maxW - minW > 0) maxW - minW else 1.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Mini bar chart
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                mediciones.filter { it.peso != null }.takeLast(10).forEach { m ->
                    val h = ((m.peso!! - minW) / range).coerceIn(0.1, 1.0).toFloat()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(h)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(FitwinColors.PrimaryContainer)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${"%.1f".format(minW)}kg", color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp)
                Text("${"%.1f".format(weights.last())}kg", color = FitwinColors.PrimaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text("${"%.1f".format(maxW)}kg", color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp)
            }
            // Botón "VER GRÁFICA COMPLETA"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .clickable { onVerGrafica() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶ VER GRÁFICA COMPLETA",
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ─── Gauge Chart simple con Canvas ───────────────────────────────────────────

@Composable
private fun GaugeChart(
    value: Float,
    max: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = (value / max).coerceIn(0f, 1f)
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val startAngle = 135f
        val sweepMax = 270f
        drawArc(
            color = FitwinColors.SurfaceContainerHighest,
            startAngle = startAngle,
            sweepAngle = sweepMax,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepMax * progress,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun imcCategory(imc: Double): String = when {
    imc < 18.5 -> "BAJO PESO"
    imc < 25.0 -> "NORMAL"
    imc < 30.0 -> "SOBREPESO"
    else -> "OBESIDAD"
}

private fun imcColor(imc: Double): Color = when {
    imc < 18.5 -> FitwinColors.MacroCarbs
    imc < 25.0 -> FitwinColors.PrimaryContainer
    imc < 30.0 -> FitwinColors.Error
    else -> FitwinColors.Error
}

private fun ffmiCategory(ffmi: Double): String = when {
    ffmi < 18.0 -> "BAJO"
    ffmi < 20.0 -> "NORMAL"
    ffmi < 22.0 -> "BUENO"
    ffmi < 24.0 -> "EXCELENTE"
    else -> "ÉLITE"
}
