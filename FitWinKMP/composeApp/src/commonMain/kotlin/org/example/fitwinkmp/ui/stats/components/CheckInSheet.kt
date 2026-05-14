package org.example.fitwinkmp.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.features.stats.data.dto.MedicionCorporalDTO
import org.example.fitwinkmp.ui.theme.FitwinColors

/**
 * Bottom Sheet de check-in diario mejorado:
 * - Si existe medicionHoy (modo UPDATE) prerrellena los campos con los valores de hoy
 * - Botón "REPETIR ÚLTIMA" para copiar la última medición disponible
 * - Feedback visual inline
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (
        peso: Double?,
        porcentajeGrasa: Double?,
        pecho: Double?,
        espalda: Double?,
        hombro: Double?,
        brazo: Double?,
        muslo: Double?,
        cintura: Double?
    ) -> Unit,
    medicionHoy: MedicionCorporalDTO? = null,      // Si no null → modo UPDATE (ya registró hoy)
    ultimaMedicion: MedicionCorporalDTO? = null    // Para el botón "Repetir última"
) {
    val isUpdate = medicionHoy != null

    // Inicializar campos con medición de hoy si existe
    var peso by remember { mutableStateOf(medicionHoy?.peso?.toString() ?: ultimaMedicion?.peso?.let { "$it" } ?: "") }
    var porcentajeGrasa by remember { mutableStateOf(medicionHoy?.porcentajeGrasa?.toString() ?: "") }
    var pecho by remember { mutableStateOf(medicionHoy?.pecho?.toString() ?: "") }
    var espalda by remember { mutableStateOf(medicionHoy?.espalda?.toString() ?: "") }
    var hombro by remember { mutableStateOf(medicionHoy?.hombro?.toString() ?: "") }
    var brazo by remember { mutableStateOf(medicionHoy?.brazo?.toString() ?: "") }
    var muslo by remember { mutableStateOf(medicionHoy?.muslo?.toString() ?: "") }
    var cintura by remember { mutableStateOf(medicionHoy?.cintura?.toString() ?: "") }
    var showDetailed by remember { mutableStateOf(hasDetailedValues(medicionHoy)) }
    val s = org.example.fitwinkmp.core.localization.LocalStrings.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FitwinColors.SurfaceContainerLow,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(FitwinColors.OutlineVariant)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Header ──────────────────────────────────────────────────────
            Column {
                Text(
                    text = if (isUpdate) s.statsActualizar else s.statsCheckIn,
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = if (isUpdate) s.statsMedicionDeHoy else s.statsDiario,
                    color = FitwinColors.OnSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = if (isUpdate)
                        s.statsYaRegistrasteHoy
                    else
                        s.statsRegistraProgreso,
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // ─── Botón "Repetir última" (solo si hay medición anterior y no es update) ─
            if (!isUpdate && ultimaMedicion != null) {
                TextButton(
                    onClick = {
                        peso = ultimaMedicion.peso?.toString() ?: peso
                        porcentajeGrasa = ultimaMedicion.porcentajeGrasa?.toString() ?: porcentajeGrasa
                        pecho = ultimaMedicion.pecho?.toString() ?: pecho
                        espalda = ultimaMedicion.espalda?.toString() ?: espalda
                        hombro = ultimaMedicion.hombro?.toString() ?: hombro
                        brazo = ultimaMedicion.brazo?.toString() ?: brazo
                        muslo = ultimaMedicion.muslo?.toString() ?: muslo
                        cintura = ultimaMedicion.cintura?.toString() ?: cintura
                        showDetailed = hasDetailedValues(ultimaMedicion)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = FitwinColors.PrimaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = s.statsRepetirUltima,
                        color = FitwinColors.PrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            // ─── Peso (obligatorio) ──────────────────────────────────────────
            CheckInField(
                label = s.statsPesoInput,
                value = peso,
                onValueChange = { peso = it },
                placeholder = "Ej. 82.5"
            )

            // ─── % Grasa (opcional) ──────────────────────────────────────────
            CheckInField(
                label = s.statsGrasaInput,
                value = porcentajeGrasa,
                onValueChange = { porcentajeGrasa = it },
                placeholder = "Ej. 18.5"
            )

            // ─── Toggle medidas detalladas ───────────────────────────────────
            TextButton(
                onClick = { showDetailed = !showDetailed },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (showDetailed) s.statsOcultarMedidas else s.statsAnadirMedidas,
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            if (showDetailed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FitwinColors.SurfaceContainer)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = s.statsMedidasMuscularesCm,
                            color = FitwinColors.OnSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CheckInField(label = "PECHO", value = pecho, onValueChange = { pecho = it }, placeholder = "cm", modifier = Modifier.weight(1f))
                            CheckInField(label = "ESPALDA", value = espalda, onValueChange = { espalda = it }, placeholder = "cm", modifier = Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CheckInField(label = "HOMBRO", value = hombro, onValueChange = { hombro = it }, placeholder = "cm", modifier = Modifier.weight(1f))
                            CheckInField(label = "BRAZO", value = brazo, onValueChange = { brazo = it }, placeholder = "cm", modifier = Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CheckInField(label = "MUSLO", value = muslo, onValueChange = { muslo = it }, placeholder = "cm", modifier = Modifier.weight(1f))
                            CheckInField(label = "CINTURA", value = cintura, onValueChange = { cintura = it }, placeholder = "cm", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ─── Botones ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FitwinColors.OnSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitwinColors.OutlineVariant)
                ) {
                    Text(s.statsCancelar, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = {
                        onSave(
                            peso.toDoubleOrNull(),
                            porcentajeGrasa.toDoubleOrNull(),
                            pecho.toDoubleOrNull(),
                            espalda.toDoubleOrNull(),
                            hombro.toDoubleOrNull(),
                            brazo.toDoubleOrNull(),
                            muslo.toDoubleOrNull(),
                            cintura.toDoubleOrNull()
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = peso.toDoubleOrNull() != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitwinColors.PrimaryContainer,
                        disabledContainerColor = FitwinColors.SurfaceContainerHighest
                    )
                ) {
                    Text(
                        if (isUpdate) s.statsActualizar else s.statsGuardar,
                        color = if (peso.toDoubleOrNull() != null) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckInField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = FitwinColors.OnSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.4f), fontSize = 12.sp)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FitwinColors.PrimaryContainer,
                unfocusedBorderColor = FitwinColors.OutlineVariant,
                focusedTextColor = FitwinColors.OnSurface,
                unfocusedTextColor = FitwinColors.OnSurface,
                cursorColor = FitwinColors.PrimaryContainer,
                focusedContainerColor = FitwinColors.SurfaceContainer,
                unfocusedContainerColor = FitwinColors.SurfaceContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

private fun hasDetailedValues(m: MedicionCorporalDTO?): Boolean {
    if (m == null) return false
    return m.pecho != null || m.espalda != null || m.hombro != null ||
            m.brazo != null || m.muslo != null || m.cintura != null
}
