package org.example.fitwinkmp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.fitwinkmp.features.profile.data.dto.UpdateUsuarioRequest
import org.example.fitwinkmp.shared.model.Usuario
import org.example.fitwinkmp.ui.theme.FitwinColors

private val GENEROS = listOf("MASCULINO", "FEMENINO", "OTRO")
private val NIVELES_ACTIVIDAD = listOf("SEDENTARIO", "LIGERO", "MODERADO", "ACTIVO", "MUY_ACTIVO")
private val OBJETIVOS = listOf("PERDIDA_PESO", "MANTENIMIENTO", "GANANCIA_MUSCULAR")

@Composable
fun EditProfileDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onSave: (UpdateUsuarioRequest) -> Unit
) {
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var apellidos by remember { mutableStateOf(usuario.apellidos) }
    var email by remember { mutableStateOf(usuario.correoElectronico) }
    var peso by remember { mutableStateOf(usuario.pesoActual?.toString() ?: "") }
    var altura by remember { mutableStateOf(usuario.altura?.toString() ?: "") }
    var genero by remember { mutableStateOf(usuario.genero ?: GENEROS[0]) }
    var nivelActividad by remember { mutableStateOf(usuario.nivelActividad ?: NIVELES_ACTIVIDAD[0]) }
    var objetivo by remember { mutableStateOf(usuario.objetivo ?: OBJETIVOS[0]) }

    var generoExpanded by remember { mutableStateOf(false) }
    var nivelExpanded by remember { mutableStateOf(false) }
    var objetivoExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(FitwinColors.SurfaceContainer)
                .border(1.dp, FitwinColors.PrimaryContainer.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EDITAR",
                            color = FitwinColors.PrimaryContainer,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "PERFIL",
                            color = FitwinColors.OnSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    TextButton(onClick = onDismiss) {
                        Text("CANCELAR", color = FitwinColors.OnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = FitwinColors.OutlineVariant)

                // Personal data
                SectionLabel("DATOS PERSONALES")

                ProfileTextField(value = nombre, onValueChange = { nombre = it }, label = "Nombre")
                ProfileTextField(value = apellidos, onValueChange = { apellidos = it }, label = "Apellidos")
                ProfileTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)

                // Physical data
                SectionLabel("DATOS FÍSICOS")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileTextField(
                        value = peso,
                        onValueChange = { peso = it },
                        label = "Peso (kg)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileTextField(
                        value = altura,
                        onValueChange = { altura = it },
                        label = "Altura (cm)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Género
                SectionLabel("GÉNERO")
                ProfileDropdown(
                    selected = genero,
                    options = GENEROS,
                    expanded = generoExpanded,
                    onExpandedChange = { generoExpanded = it },
                    onSelect = { genero = it; generoExpanded = false }
                )

                // Nivel actividad
                SectionLabel("NIVEL DE ACTIVIDAD")
                ProfileDropdown(
                    selected = nivelActividad,
                    options = NIVELES_ACTIVIDAD,
                    expanded = nivelExpanded,
                    onExpandedChange = { nivelExpanded = it },
                    onSelect = { nivelActividad = it; nivelExpanded = false }
                )

                // Objetivo
                SectionLabel("OBJETIVO")
                ProfileDropdown(
                    selected = objetivo,
                    options = OBJETIVOS,
                    expanded = objetivoExpanded,
                    onExpandedChange = { objetivoExpanded = it },
                    onSelect = { objetivo = it; objetivoExpanded = false }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Save button
                Button(
                    onClick = {
                        onSave(
                            UpdateUsuarioRequest(
                                nombre = nombre.trim(),
                                apellidos = apellidos.trim(),
                                correoElectronico = email.trim(),
                                pesoActual = peso.toDoubleOrNull(),
                                altura = altura.toDoubleOrNull(),
                                genero = genero,
                                nivelActividad = nivelActividad,
                                objetivo = objetivo,
                                idioma = usuario.idioma ?: "es"
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                ) {
                    Text(
                        text = "GUARDAR CAMBIOS",
                        color = FitwinColors.OnPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = FitwinColors.OnSurfaceVariant,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FitwinColors.PrimaryContainer,
            unfocusedBorderColor = FitwinColors.OutlineVariant,
            focusedLabelColor = FitwinColors.PrimaryContainer,
            unfocusedLabelColor = FitwinColors.OnSurfaceVariant,
            focusedTextColor = FitwinColors.OnSurface,
            unfocusedTextColor = FitwinColors.OnSurface,
            cursorColor = FitwinColors.PrimaryContainer,
            focusedContainerColor = FitwinColors.SurfaceContainerLow,
            unfocusedContainerColor = FitwinColors.SurfaceContainerLow
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDropdown(
    selected: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FitwinColors.PrimaryContainer,
                unfocusedBorderColor = FitwinColors.OutlineVariant,
                focusedTextColor = FitwinColors.OnSurface,
                unfocusedTextColor = FitwinColors.OnSurface,
                focusedContainerColor = FitwinColors.SurfaceContainerLow,
                unfocusedContainerColor = FitwinColors.SurfaceContainerLow,
                focusedTrailingIconColor = FitwinColors.PrimaryContainer,
                unfocusedTrailingIconColor = FitwinColors.OnSurfaceVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(FitwinColors.SurfaceContainerHigh)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = FitwinColors.OnSurface, fontSize = 13.sp) },
                    onClick = { onSelect(option) },
                    colors = MenuDefaults.itemColors(textColor = FitwinColors.OnSurface)
                )
            }
        }
    }
}
