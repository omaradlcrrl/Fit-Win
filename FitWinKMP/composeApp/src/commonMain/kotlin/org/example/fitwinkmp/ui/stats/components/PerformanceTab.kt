package org.example.fitwinkmp.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.features.stats.data.dto.NutritionData
import org.example.fitwinkmp.features.stats.data.dto.PerformanceData
import org.example.fitwinkmp.features.stats.data.dto.RecordConRm
import org.example.fitwinkmp.ui.theme.FitwinColors
import kotlin.math.roundToInt

@Composable
fun PerformanceTab(data: PerformanceData, nutritionData: NutritionData) {
    val s = LocalStrings.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // ─── Header ────────────────────────────────────────────────────────
        Column {
            Text(
                text = s.statsKinetic,
                color = FitwinColors.OnSurface,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 36.sp
            )
            Text(
                text = s.statsVelocity,
                color = FitwinColors.PrimaryContainer,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 36.sp
            )
            Text(
                text = s.statsAnalisisRendimiento,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ─── Tonelaje Total ────────────────────────────────────────────────
        TonelajeCard(sesionesMes = data.frecuenciaUltimoMes, s = s)

        // ─── Stats Grid ────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.FitnessCenter,
                value = "${data.frecuenciaUltimaSemana}",
                label = s.statsSesiones7Dias,
                accent = FitwinColors.PrimaryContainer
            )
            StatMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Timer,
                value = "${data.duracionMediaMin.roundToInt()}min",
                label = s.statsDuracionMedia,
                accent = FitwinColors.MacroCarbs
            )
            StatMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                value = "${data.rachaActual}",
                label = s.statsDiasRacha,
                accent = FitwinColors.Error
            )
        }

        // ─── Intensidad & Recuperación ─────────────────────────────────────
        if (data.intensidadMedia > 0 || data.recuperacionMedia > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeelingBar(
                    modifier = Modifier.weight(1f),
                    label = s.statsIntensidad,
                    value = data.intensidadMedia,
                    max = 10.0,
                    color = FitwinColors.PrimaryContainer
                )
                FeelingBar(
                    modifier = Modifier.weight(1f),
                    label = s.statsRecuperacion,
                    value = data.recuperacionMedia,
                    max = 10.0,
                    color = FitwinColors.MacroCarbs
                )
            }
        }

        // ─── Personal Bests ────────────────────────────────────────────────
        if (data.mejoresRecords.isNotEmpty()) {
            SectionHeader(s.statsRecordsPersonales)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                data.mejoresRecords.take(5).forEachIndexed { index, record ->
                    PersonalBestCard(record = record, position = index + 1, s = s)
                }
            }
        }

        // ─── 1RM Estimados (top 3) ─────────────────────────────────────────
        if (data.mejoresRecords.isNotEmpty()) {
            SectionHeader(s.stats1RmEstimados)
            Text(
                text = s.statsEpleyFormula,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.mejoresRecords.take(3).forEach { record ->
                    OneRmCard(record = record)
                }
            }
        }

        // ─── Adherencia Nutricional ────────────────────────────────────────
        SectionHeader(s.statsAdherenciaNutricional)
        NutritionAdherenceCard(nutritionData = nutritionData, s = s)

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TonelajeCard(sesionesMes: Int, s: org.example.fitwinkmp.core.localization.AppStrings) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = s.statsLogroPrincipal,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                    .background(FitwinColors.PrimaryContainer)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$sesionesMes",
                    color = FitwinColors.OnPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = s.statsTonelajeLevantado,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    accent: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Text(
                text = value,
                color = FitwinColors.OnSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                lineHeight = 10.sp
            )
        }
    }
}

@Composable
private fun FeelingBar(
    modifier: Modifier = Modifier,
    label: String,
    value: Double,
    max: Double,
    color: Color
) {
    val progress = (value / max).coerceIn(0.0, 1.0).toFloat()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, color = FitwinColors.OnSurfaceVariant, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Text("${"%.1f".format(value)}/10", color = color, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(FitwinColors.SurfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = FitwinColors.OnSurface,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun PersonalBestCard(record: RecordConRm, position: Int, s: org.example.fitwinkmp.core.localization.AppStrings) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Posición
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (position == 1) FitwinColors.PrimaryContainer
                    else FitwinColors.SurfaceContainerHigh
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$position",
                color = if (position == 1) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.nombreEjercicio.uppercase(),
                color = FitwinColors.OnSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            record.fecha?.take(10)?.let { fecha ->
                Text(
                    text = "${s.statsMejorMarca} · $fecha",
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${record.pesoKg}kg",
                color = FitwinColors.PrimaryContainer,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            record.repeticiones?.let {
                Text(
                    text = "× $it reps",
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun OneRmCard(record: RecordConRm) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainerLow)
            .border(1.dp, FitwinColors.OutlineVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = FitwinColors.PrimaryContainer, modifier = Modifier.size(16.dp))
            Text(
                text = record.nombreEjercicio.uppercase(),
                color = FitwinColors.OnSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "${"%.1f".format(record.rmEstimado)}kg",
            color = FitwinColors.PrimaryContainer,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun NutritionAdherenceCard(nutritionData: NutritionData, s: org.example.fitwinkmp.core.localization.AppStrings) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            nutritionData.adherenciaCaloricaHoy?.let {
                AdherenceBar("CALORÍAS", it, FitwinColors.PrimaryContainer)
            } ?: Text(
                text = s.statsSinDatosNutricionales,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 10.sp
            )
            nutritionData.adherenciaProteinasHoy?.let {
                AdherenceBar("PROTEÍNAS", it, FitwinColors.MacroProtein)
            }
            nutritionData.adherenciaCarbosHoy?.let {
                AdherenceBar("CARBOS", it, FitwinColors.MacroCarbs)
            }
            nutritionData.adherenciaGrasasHoy?.let {
                AdherenceBar("GRASAS", it, FitwinColors.MacroFats)
            }
            if (nutritionData.proteinaPorKg != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(s.statsProteinaPorKg, color = FitwinColors.OnSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${"%.1f".format(nutritionData.proteinaPorKg)}g/kg",
                        color = if (nutritionData.proteinaPorKg >= 1.6) FitwinColors.PrimaryContainer else FitwinColors.OnSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun AdherenceBar(label: String, percent: Double, color: Color) {
    val progress = (percent / 100.0).coerceIn(0.0, 1.0).toFloat()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = FitwinColors.OnSurface, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text("${percent.roundToInt()}%", color = color, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(FitwinColors.SurfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
