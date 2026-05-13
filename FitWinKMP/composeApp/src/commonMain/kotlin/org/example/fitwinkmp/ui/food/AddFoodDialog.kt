package org.example.fitwinkmp.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.fitwinkmp.features.food.data.dto.ComidaDTO
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.ui.theme.FitwinColors
import java.time.LocalDate

@Composable
fun AddFoodDialog(
    tipoComida: String,
    usuarioId: Int,
    fecha: LocalDate = LocalDate.now(),
    comidaToEdit: ComidaDTO? = null,
    onDismiss: () -> Unit,
    onSave: (ComidaDTO) -> Unit,
    onUpdate: ((Int, ComidaDTO) -> Unit)? = null
) {
    val isEditMode = comidaToEdit != null
    var nombre by remember { mutableStateOf(comidaToEdit?.nombre ?: "") }
    var calorias by remember { mutableStateOf(comidaToEdit?.calorias?.toString() ?: "") }
    var proteinas by remember { mutableStateOf(comidaToEdit?.proteinas?.toString() ?: "") }
    var carbohidratos by remember { mutableStateOf(comidaToEdit?.carbohidratos?.toString() ?: "") }
    var grasas by remember { mutableStateOf(comidaToEdit?.grasasSaturadas?.toString() ?: "") }
    var cantidad by remember { mutableStateOf(comidaToEdit?.cantidad?.toString() ?: "") }
    var unidad by remember { mutableStateOf(comidaToEdit?.unidad ?: "GRAMOS") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val s = LocalStrings.current
        val titlePrefix = if (isEditMode) s.foodEditar else s.foodRegistrar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FitwinColors.Background.copy(alpha = 0.95f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(FitwinColors.SurfaceContainer)
                    .padding(24.dp)
            ) {
                Text(
                    text = "$titlePrefix $tipoComida",
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(s.foodNombreAlimento, color = FitwinColors.OnSurfaceVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FitwinColors.OnSurface,
                        unfocusedTextColor = FitwinColors.OnSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text(s.foodCantidad, color = FitwinColors.OnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unidad,
                        onValueChange = { unidad = it },
                        label = { Text(s.foodUnidad, color = FitwinColors.OnSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("MACROS", color = FitwinColors.OnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calorias,
                        onValueChange = { calorias = it },
                        label = { Text("Kcal", color = FitwinColors.OnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = proteinas,
                        onValueChange = { proteinas = it },
                        label = { Text("Prot (g)", color = FitwinColors.OnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbohidratos,
                        onValueChange = { carbohidratos = it },
                        label = { Text("Carb (g)", color = FitwinColors.OnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = grasas,
                        onValueChange = { grasas = it },
                        label = { Text("Grasa (g)", color = FitwinColors.OnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FitwinColors.OnSurface, unfocusedTextColor = FitwinColors.OnSurface),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(s.foodCancelar, color = FitwinColors.OnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val dto = ComidaDTO(
                                comidaId = comidaToEdit?.comidaId,
                                usuarioId = usuarioId,
                                nombre = nombre,
                                calorias = calorias.toDoubleOrNull() ?: 0.0,
                                proteinas = proteinas.toDoubleOrNull() ?: 0.0,
                                carbohidratos = carbohidratos.toDoubleOrNull() ?: 0.0,
                                grasasSaturadas = grasas.toDoubleOrNull() ?: 0.0,
                                tipoComida = tipoComida,
                                cantidad = cantidad.toDoubleOrNull() ?: 1.0,
                                unidad = unidad,
                                fecha = fecha.toString()
                            )
                            if (isEditMode && comidaToEdit?.comidaId != null) {
                                onUpdate?.invoke(comidaToEdit.comidaId, dto)
                            } else {
                                onSave(dto)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                    ) {
                        Text(s.foodGuardar, color = FitwinColors.OnPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
