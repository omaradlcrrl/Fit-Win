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
import org.example.fitwinkmp.features.auth.presentation.state.LoginUiState
import org.example.fitwinkmp.ui.theme.FitwinColors

import org.example.fitwinkmp.core.localization.LanguageViewModel

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
    languageViewModel: LanguageViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val s = org.example.fitwinkmp.core.localization.LocalStrings.current

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val currentLang by languageViewModel.language.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetLoginState()
            }
            is LoginUiState.Success -> {
                viewModel.resetLoginState()
                onLoginSuccess()
            }
            else -> Unit
        }
    }

    val isLoading = loginState is LoginUiState.Loading

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FitwinColors.Background)
        ) {
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
                // Language Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(FitwinColors.SurfaceContainerHighest)
                            .border(1.dp, FitwinColors.OutlineVariant, RoundedCornerShape(16.dp))
                            .clickable {
                                languageViewModel.setLanguage(if (currentLang == "es") "en" else "es")
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (currentLang == "es") "ESPAÑOL" else "ENGLISH",
                            color = FitwinColors.OnSurface,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FitwinColors.PrimaryContainer)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = s.authWelcomeBack,
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
                    text = s.authSubtitle,
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
                            text = s.authSignIn,
                            color = FitwinColors.OnSurface,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = s.authSignInDesc,
                            color = FitwinColors.OnSurfaceVariant,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FitwinInputField(
                            label = s.authEmail,
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "correo@ejemplo.com",
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column {
                            Text(
                                text = s.authPassword,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                text = s.authForgotPassword,
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onForgotPasswordClick() }
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
                                        colors = if (!isLoading)
                                            listOf(Color(0xFFFFF6DF), Color(0xFFFFD700))
                                        else
                                            listOf(
                                                Color(0xFFFFF6DF).copy(alpha = 0.4f),
                                                Color(0xFFFFD700).copy(alpha = 0.4f)
                                            )
                                    )
                                )
                                .clickable(enabled = !isLoading) {
                                    viewModel.login(email, password)
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
                                    text = s.authEnterArena,
                                    color = FitwinColors.OnPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 3.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = s.authNewToSquad,
                                color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = s.authCreateAccount,
                                color = FitwinColors.PrimaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onSignUpClick() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = s.authCopyright,
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

@Composable
internal fun FitwinInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column {
        Text(
            text = label,
            color = FitwinColors.PrimaryContainer,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = FitwinColors.OnSurface.copy(alpha = 0.2f),
                    fontSize = 15.sp
                )
            },
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
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
}


