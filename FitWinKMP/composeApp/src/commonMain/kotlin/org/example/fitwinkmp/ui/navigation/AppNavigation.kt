package org.example.fitwinkmp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.fitwinkmp.core.localization.LanguageViewModel
import org.example.fitwinkmp.features.auth.presentation.AuthViewModel
import org.example.fitwinkmp.ui.auth.LoginScreen
import org.example.fitwinkmp.ui.auth.RegisterScreen

private const val ROUTE_LOGIN = "login"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_MAIN = "main"

@Composable
fun AppNavigation(languageViewModel: LanguageViewModel) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = ROUTE_LOGIN) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                languageViewModel = languageViewModel,
                onSignUpClick = { navController.navigate(ROUTE_REGISTER) },
                onLoginSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                languageViewModel = languageViewModel,
                onLoginClick = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(ROUTE_MAIN) {
            org.example.fitwinkmp.ui.main.MainScreen(
                languageViewModel = languageViewModel,
                onLogout = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
