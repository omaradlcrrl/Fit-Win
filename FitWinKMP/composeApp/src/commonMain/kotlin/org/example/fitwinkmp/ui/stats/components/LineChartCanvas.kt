package org.example.fitwinkmp.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.features.stats.data.dto.ChartData
import org.example.fitwinkmp.features.stats.data.dto.ChartPoint
import org.example.fitwinkmp.ui.theme.FitwinColors
import kotlin.math.roundToInt

/**
 * Gráfica de líneas dibujada con Canvas para las métricas corporales.
 * Soporta selección de puntos al tocar para ver fecha y valor.
 */
@Composable
fun LineChartCanvas(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    val puntos = chartData.puntos
    if (puntos.isEmpty()) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(FitwinColors.SurfaceContainer)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SIN DATOS EN ESTE RANGO",
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val primaryColor = FitwinColors.PrimaryContainer
    val surfaceColor = FitwinColors.SurfaceContainerHighest
    val onSurface = FitwinColors.OnSurface

    Column(modifier = modifier) {
        // Tooltip del punto seleccionado
        val selIdx = selectedIndex
        if (selIdx != null && selIdx in puntos.indices) {
            val p = puntos[selIdx]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatFecha(p.fecha),
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${"%.1f".format(p.valor)} ${chartData.metrica.unidad}",
                    color = primaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        } else {
            Spacer(modifier = Modifier.height(22.dp))
        }

        // Canvas de la gráfica
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(puntos) {
                    detectTapGestures { offset ->
                        // Calcular el índice del punto más cercano al toque
                        val paddingPx = 16.dp.toPx()
                        val chartWidth = size.width - paddingPx * 2
                        if (puntos.size < 2) {
                            selectedIndex = 0
                            return@detectTapGestures
                        }
                        val stepX = chartWidth / (puntos.size - 1)
                        val idx = ((offset.x - paddingPx) / stepX).roundToInt()
                            .coerceIn(0, puntos.size - 1)
                        selectedIndex = if (selectedIndex == idx) null else idx
                    }
                }
        ) {
            drawLineChart(
                puntos = puntos,
                min = chartData.min,
                max = chartData.max,
                primaryColor = primaryColor,
                surfaceColor = surfaceColor,
                onSurface = onSurface,
                selectedIndex = selectedIndex
            )
        }

        // Eje X — primeras y últimas etiquetas
        if (puntos.size >= 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatFecha(puntos.first().fecha),
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 8.sp
                )
                if (puntos.size > 2) {
                    Text(
                        text = formatFecha(puntos[puntos.size / 2].fecha),
                        color = FitwinColors.OnSurfaceVariant,
                        fontSize = 8.sp
                    )
                }
                Text(
                    text = formatFecha(puntos.last().fecha),
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 8.sp
                )
            }
        }
    }
}

private fun DrawScope.drawLineChart(
    puntos: List<ChartPoint>,
    min: Double,
    max: Double,
    primaryColor: Color,
    surfaceColor: Color,
    onSurface: Color,
    selectedIndex: Int?
) {
    val paddingPx = 16.dp.toPx()
    val chartWidth = size.width - paddingPx * 2
    val chartHeight = size.height - paddingPx * 2
    val range = if (max - min > 0) max - min else 1.0

    fun xFor(i: Int): Float {
        if (puntos.size <= 1) return size.width / 2f
        return paddingPx + (i.toFloat() / (puntos.size - 1)) * chartWidth
    }

    fun yFor(valor: Double): Float {
        val normalized = ((valor - min) / range).toFloat()
        return paddingPx + chartHeight * (1f - normalized)
    }

    // Líneas guía horizontales (3)
    for (i in 0..2) {
        val y = paddingPx + chartHeight * (i / 2f)
        drawLine(
            color = surfaceColor,
            start = Offset(paddingPx, y),
            end = Offset(size.width - paddingPx, y),
            strokeWidth = 1.dp.toPx()
        )
    }

    if (puntos.isEmpty()) return

    // Path de relleno (gradiente debajo de la línea)
    val fillPath = Path().apply {
        moveTo(xFor(0), yFor(puntos[0].valor))
        for (i in 1 until puntos.size) {
            // Curva bezier suave
            val x0 = xFor(i - 1)
            val x1 = xFor(i)
            val y0 = yFor(puntos[i - 1].valor)
            val y1 = yFor(puntos[i].valor)
            val cx = (x0 + x1) / 2f
            cubicTo(cx, y0, cx, y1, x1, y1)
        }
        lineTo(xFor(puntos.size - 1), size.height)
        lineTo(xFor(0), size.height)
        close()
    }
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.25f),
                primaryColor.copy(alpha = 0.0f)
            ),
            startY = paddingPx,
            endY = size.height
        )
    )

    // Path de la línea
    val linePath = Path().apply {
        moveTo(xFor(0), yFor(puntos[0].valor))
        for (i in 1 until puntos.size) {
            val x0 = xFor(i - 1)
            val x1 = xFor(i)
            val y0 = yFor(puntos[i - 1].valor)
            val y1 = yFor(puntos[i].valor)
            val cx = (x0 + x1) / 2f
            cubicTo(cx, y0, cx, y1, x1, y1)
        }
    }
    drawPath(
        path = linePath,
        color = primaryColor,
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Puntos
    puntos.forEachIndexed { i, p ->
        val x = xFor(i)
        val y = yFor(p.valor)
        val isSelected = selectedIndex == i

        if (isSelected) {
            // Línea vertical del tooltip
            drawLine(
                color = primaryColor.copy(alpha = 0.3f),
                start = Offset(x, paddingPx),
                end = Offset(x, size.height - paddingPx),
                strokeWidth = 1.dp.toPx()
            )
            // Punto grande seleccionado
            drawCircle(color = primaryColor.copy(alpha = 0.2f), radius = 10.dp.toPx(), center = Offset(x, y))
            drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = Offset(x, y))
        } else {
            // Punto pequeño normal
            drawCircle(color = primaryColor, radius = 3.dp.toPx(), center = Offset(x, y))
        }
    }
}

private fun formatFecha(fecha: String): String {
    // YYYY-MM-DD → DD/MM
    return if (fecha.length >= 10) "${fecha.substring(8, 10)}/${fecha.substring(5, 7)}"
    else fecha
}
