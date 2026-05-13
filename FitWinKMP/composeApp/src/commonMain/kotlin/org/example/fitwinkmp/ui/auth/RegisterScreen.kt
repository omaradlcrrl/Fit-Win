package org.example.fitwinkmp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.fitwinkmp.features.auth.presentation.AuthViewModel
import org.example.fitwinkmp.features.auth.presentation.state.RegisterUiState
import org.example.fitwinkmp.ui.theme.FitwinColors

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var genero by remember { mutableStateOf("MASCULINO") }
    var nivelActividad by remember { mutableStateOf("SEDENTARIO") }
    var pesoActual by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("MANTENIMIENTO") }

    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterUiState.Success -> {
                viewModel.resetRegisterState()
                onLoginClick()
            }
            is RegisterUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetRegisterState()
            }
            else -> Unit
        }
    }

    val isLoading = registerState is RegisterUiState.Loading

    Scaffold(
        containerColor = FitwinColors.Background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = FitwinColors.SurfaceContainerHighest,
                    contentColor = FitwinColors.OnSurface,
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .offset((-60).dp, (-60).dp)
                    .size(280.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                FitwinColors.PrimaryContainer.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FitwinColors.PrimaryContainer)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "JOIN THE ELITE",
                        color = FitwinColors.OnPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Text(
                        text = "FIT-",
                        color = FitwinColors.Primary,
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 68.sp,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text = "WIN",
                        color = FitwinColors.PrimaryContainer,
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 68.sp,
                        letterSpacing = (-2).sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "High-performance engineering\nfor your physical evolution.",
                    color = FitwinColors.OnSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(FitwinColors.SurfaceContainerLow)
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Create Account",
                            color = FitwinColors.OnSurface,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter your details to power up.",
                            color = FitwinColors.OnSurfaceVariant,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                FitwinInputField(
                                    label = "NOMBRE",
                                    value = nombre,
                                    onValueChange = { nombre = it },
                                    placeholder = "Aiden"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                FitwinInputField(
                                    label = "APELLIDOS",
                                    value = apellidos,
                                    onValueChange = { apellidos = it },
                                    placeholder = "Cross"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FitwinInputField(
                            label = "EMAIL ADDRESS",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "aiden@fitwin.io",
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text(
                                text = "PASSWORD",
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "••••••••",
                                        color = FitwinColors.OnSurface.copy(alpha = 0.2f),
                                        fontSize = 15.sp
                                    )
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            contentDescription = null,
                                            tint = FitwinColors.OnSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = FitwinColors.SurfaceContainerHighest,
                                    unfocusedContainerColor = FitwinColors.SurfaceContainerHighest,
                                    focusedTextColor = FitwinColors.OnSurface,
                                    unfocusedTextColor = FitwinColors.OnSurface,
                                    focusedBorderColor = FitwinColors.PrimaryContainer,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = FitwinColors.PrimaryContainer,
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // BIOMETRICS SECTION
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                FitwinInputField(
                                    label = "WEIGHT (KG)",
                                    value = pesoActual,
                                    onValueChange = { pesoActual = it },
                                    placeholder = "75.0",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                FitwinInputField(
                                    label = "HEIGHT (CM)",
                                    value = altura,
                                    onValueChange = { altura = it },
                                    placeholder = "180",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FitwinInputField(
                            label = "DATE OF BIRTH (YYYY-MM-DD)",
                            value = fechaNacimiento,
                            onValueChange = { fechaNacimiento = it },
                            placeholder = "2000-01-01"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // GENDER
                        Text(
                            text = "GENDER",
                            color = FitwinColors.PrimaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("MASCULINO", "FEMENINO").forEach { gen ->
                                val isSelected = genero == gen
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) FitwinColors.PrimaryContainer else FitwinColors.SurfaceContainerHighest)
                                        .clickable { genero = gen },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gen,
                                        color = if (isSelected) FitwinColors.OnPrimary else FitwinColors.OnSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ACTIVITY LEVEL
                        Text(
                            text = "ACTIVITY LEVEL",
                            color = FitwinColors.PrimaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        var activityExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = nivelActividad,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activityExpanded = true },
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledContainerColor = FitwinColors.SurfaceContainerHighest,
                                    disabledTextColor = FitwinColors.OnSurface,
                                    disabledBorderColor = Color.Transparent
                                )
                            )
                            DropdownMenu(
                                expanded = activityExpanded,
                                onDismissRequest = { activityExpanded = false },
                                modifier = Modifier.background(FitwinColors.SurfaceContainerHighest)
                            ) {
                                listOf("SEDENTARIO", "LIGERO", "MODERADO", "ACTIVO", "MUY_ACTIVO").forEach { act ->
                                    DropdownMenuItem(
                                        text = { Text(act, color = FitwinColors.OnSurface) },
                                        onClick = {
                                            nivelActividad = act
                                            activityExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // OBJECTIVE
                        Text(
                            text = "OBJECTIVE",
                            color = FitwinColors.PrimaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        var objectiveExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = objetivo,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { objectiveExpanded = true },
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledContainerColor = FitwinColors.SurfaceContainerHighest,
                                    disabledTextColor = FitwinColors.OnSurface,
                                    disabledBorderColor = Color.Transparent
                                )
                            )
                            DropdownMenu(
                                expanded = objectiveExpanded,
                                onDismissRequest = { objectiveExpanded = false },
                                modifier = Modifier.background(FitwinColors.SurfaceContainerHighest)
                            ) {
                                listOf("PERDIDA_PESO", "MANTENIMIENTO", "GANANCIA_MUSCULAR").forEach { obj ->
                                    DropdownMenuItem(
                                        text = { Text(obj, color = FitwinColors.OnSurface) },
                                        onClick = {
                                            objetivo = obj
                                            objectiveExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (termsAccepted) FitwinColors.PrimaryContainer
                                        else FitwinColors.SurfaceContainerHighest
                                    )
                                    .border(
                                        1.dp,
                                        if (termsAccepted) FitwinColors.PrimaryContainer else FitwinColors.OutlineVariant,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { termsAccepted = !termsAccepted },
                                contentAlignment = Alignment.Center
                            ) {
                                if (termsAccepted) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = FitwinColors.OnPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "I agree to the Terms of Service and Privacy Policy",
                                color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = if (termsAccepted && !isLoading)
                                            listOf(Color(0xFFFFF6DF), Color(0xFFFFD700))
                                        else
                                            listOf(
                                                Color(0xFFFFF6DF).copy(alpha = 0.4f),
                                                Color(0xFFFFD700).copy(alpha = 0.4f)
                                            )
                                    )
                                )
                                .clickable(enabled = termsAccepted && !isLoading) {
                                    viewModel.register(
                                        nombre,
                                        apellidos,
                                        email, 
                                        password,
                                        genero,
                                        nivelActividad,
                                        pesoActual,
                                        altura,
                                        fechaNacimiento,
                                        objetivo
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = FitwinColors.OnPrimary,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "INITIALIZE TRAINING",
                                    color = FitwinColors.OnPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 3.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = FitwinColors.OutlineVariant.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "  OR CONNECT VIA  ",
                                color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = FitwinColors.OutlineVariant.copy(alpha = 0.3f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FitwinSocialButton(label = "Google", modifier = Modifier.weight(1f))
                            FitwinSocialButton(label = "Apple", modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already part of the squad? ",
                                color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Log In",
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onLoginClick() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "© 2024 FIT-WIN PERFORMANCE TECHNOLOGIES",
                    color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
