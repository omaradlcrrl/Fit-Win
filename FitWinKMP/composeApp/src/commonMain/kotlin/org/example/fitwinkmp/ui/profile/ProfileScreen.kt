package org.example.fitwinkmp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import org.example.fitwinkmp.features.profile.data.dto.MedicionDTO
import org.example.fitwinkmp.features.profile.data.dto.ObjetivoDTO
import org.example.fitwinkmp.features.profile.presentation.ProfileUiState
import org.example.fitwinkmp.features.profile.presentation.ProfileViewModel
import org.example.fitwinkmp.shared.model.Usuario
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.ui.theme.FitwinColors
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val logoutDone by viewModel.logoutDone.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    val s = LocalStrings.current

    LaunchedEffect(Unit) { viewModel.loadProfile() }
    LaunchedEffect(logoutDone) { if (logoutDone) onLogout() }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            snackbarHostState.showSnackbar(s.profileActualizado)
            viewModel.resetUpdateSuccess()
        }
    }

    if (showEditDialog && uiState is ProfileUiState.Success) {
        EditProfileDialog(
            usuario = (uiState as ProfileUiState.Success).usuario,
            onDismiss = { showEditDialog = false },
            onSave = { request ->
                viewModel.updateProfile(request)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        containerColor = FitwinColors.Background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = FitwinColors.SurfaceContainerHighest,
                    contentColor = FitwinColors.OnSurface,
                    actionColor = FitwinColors.PrimaryContainer
                )
            }
        },
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
                    contentDescription = "Ajustes",
                    tint = FitwinColors.PrimaryContainer,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onSettingsClick() }
                )
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FitwinColors.PrimaryContainer)
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            color = FitwinColors.Error,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Button(
                            onClick = { viewModel.loadProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = FitwinColors.PrimaryContainer)
                        ) {
                            Text(s.profileReintentar, color = FitwinColors.OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is ProfileUiState.Success -> {
                ProfileContent(
                    usuario = state.usuario,
                    objetivo = state.objetivo,
                    ultimaMedicion = state.ultimaMedicion,
                    padding = padding,
                    onEditClick = { showEditDialog = true },
                    onLogoutClick = { viewModel.logout() },
                    onGenerarObjetivo = { viewModel.generarObjetivo() }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    usuario: Usuario,
    objetivo: ObjetivoDTO?,
    ultimaMedicion: MedicionDTO?,
    padding: PaddingValues,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onGenerarObjetivo: () -> Unit
) {
    val s = LocalStrings.current
    val pesoActual = ultimaMedicion?.peso ?: usuario.pesoActual
    val pesoInicial = usuario.pesoActual
    val diferenciaPeso = if (pesoActual != null && pesoInicial != null) pesoActual - pesoInicial else null

    // IMC: se intenta primero del objetivo (API), si no se calcula localmente
    val imcValue: Double? = objetivo?.imc
        ?: run {
            val peso = pesoActual
            val altCm = usuario.altura
            if (peso != null && altCm != null && altCm > 0) {
                val altM = altCm / 100.0
                peso / (altM * altM)
            } else null
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Avatar + Nombre ──
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    FitwinColors.PrimaryContainer.copy(alpha = 0.3f),
                                    FitwinColors.SurfaceContainerHighest
                                )
                            )
                        )
                        .border(2.dp, FitwinColors.PrimaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = usuario.nombre.take(1).uppercase(),
                        color = FitwinColors.PrimaryContainer,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = usuario.nombre.uppercase(),
                    color = FitwinColors.OnSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 2.sp
                )
                Text(
                    text = usuario.apellidos.uppercase(),
                    color = FitwinColors.OnSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Email label
                Text(
                    text = usuario.correoElectronico,
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Objetivo badge
                if (objetivo?.tipo != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FitwinColors.PrimaryContainer)
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = formatObjetivoTipo(objetivo.tipo),
                            color = FitwinColors.OnPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        // ── Peso Actual destacado ──
        if (pesoActual != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FitwinColors.SurfaceContainer)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = s.profilePesoActual,
                            color = FitwinColors.OnSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${pesoActual.roundToInt()}",
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                text = " KG",
                                color = FitwinColors.OnSurfaceVariant,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        if (diferenciaPeso != null && diferenciaPeso != 0.0) {
                            val sign = if (diferenciaPeso > 0) "+" else ""
                            Text(
                                text = "$sign${String.format("%.1f", diferenciaPeso)} ${s.profileDesdeRegistro}",
                                color = if (diferenciaPeso < 0) FitwinColors.MacroProtein else FitwinColors.Error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // ── Stats físicos ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = s.profileAltura,
                    value = if (usuario.altura != null) "${usuario.altura.roundToInt()} cm" else "—",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = s.profileImc,
                    value = if (imcValue != null) String.format("%.1f", imcValue) else "—",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = s.profileActividad,
                    value = formatActividad(usuario.nivelActividad),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (objetivo != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FitwinColors.SurfaceContainerLow)
                        .border(1.dp, FitwinColors.PrimaryContainer.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = s.profileObjetivoNutricional,
                                color = FitwinColors.OnSurfaceVariant,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            TextButton(
                                onClick = onGenerarObjetivo,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    s.profileGenerarObjetivo,
                                    color = FitwinColors.PrimaryContainer,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutritionGoalItem(
                                label = "KCAL",
                                value = objetivo.caloriasObjetivo?.roundToInt()?.toString() ?: "—",
                                color = FitwinColors.PrimaryContainer
                            )
                            NutritionGoalItem(
                                label = "PROT",
                                value = if (objetivo.proteinasObjetivo != null) "${objetivo.proteinasObjetivo.roundToInt()}g" else "—",
                                color = FitwinColors.MacroProtein
                            )
                            NutritionGoalItem(
                                label = "CARBS",
                                value = if (objetivo.carbohidratosObjetivo != null) "${objetivo.carbohidratosObjetivo.roundToInt()}g" else "—",
                                color = FitwinColors.MacroCarbs
                            )
                            NutritionGoalItem(
                                label = "GRASAS",
                                value = if (objetivo.grasasObjetivo != null) "${objetivo.grasasObjetivo.roundToInt()}g" else "—",
                                color = FitwinColors.MacroFats
                            )
                        }
                    }
                }
            }
        } else {
            // Si no hay objetivo, mostrar botón para generarlo
            item {
                OutlinedButton(
                    onClick = onGenerarObjetivo,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FitwinColors.PrimaryContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitwinColors.PrimaryContainer)
                ) {
                    Text(s.profileGenerarObjetivo, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Opciones de perfil ──
        item {
            Text(
                text = s.profileMiCuenta,
                color = FitwinColors.OnSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic
            )
        }

        item {
            SettingsSection {
                SettingsItem(
                    icon = Icons.Default.Person,
                    label = s.profileEditarPerfil,
                    onClick = onEditClick
                )
                HorizontalDivider(
                    color = FitwinColors.OutlineVariant.copy(alpha = 0.4f),
                    modifier = Modifier.padding(start = 56.dp)
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    label = s.profileInfoCuenta,
                    subtitle = usuario.correoElectronico,
                    onClick = {}
                )
            }
        }

        // ── Logout ──
        item {
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB00020)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = s.profileCerrarSesion,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FitwinColors.SurfaceContainerHighest)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = FitwinColors.OnSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun NutritionGoalItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = FitwinColors.OnSurfaceVariant,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun SettingsSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitwinColors.SurfaceContainer),
        content = content
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(FitwinColors.SurfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = FitwinColors.PrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = FitwinColors.OnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = FitwinColors.OnSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun formatObjetivoTipo(tipo: String): String = when (tipo) {
    "PERDIDA_PESO" -> "PÉRDIDA DE PESO"
    "MANTENIMIENTO" -> "MANTENIMIENTO"
    "GANANCIA_MUSCULAR" -> "VOLUMEN"
    else -> tipo
}

private fun formatActividad(nivel: String?): String = when (nivel) {
    "SEDENTARIO" -> "BAJO"
    "LIGERO" -> "LIGERO"
    "MODERADO" -> "MEDIO"
    "ACTIVO" -> "ALTO"
    "MUY_ACTIVO" -> "MUY ALTO"
    else -> "—"
}
