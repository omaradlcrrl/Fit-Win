package org.example.fitwinkmp.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.fitwinkmp.core.localization.LanguageViewModel
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.ui.theme.FitwinColors

private const val ROUTE_SETTINGS = "settings"

sealed class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Food : BottomNavItem("food", Icons.Filled.Restaurant)
    object Training : BottomNavItem("training", Icons.Filled.FitnessCenter)
    object Stats : BottomNavItem("stats", Icons.Filled.BarChart)
    object Profile : BottomNavItem("profile", Icons.Filled.Person)
}

@Composable
fun MainScreen(
    languageViewModel: LanguageViewModel,
    onLogout: () -> Unit = {}
) {
    val s = LocalStrings.current
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Food,
        BottomNavItem.Training,
        BottomNavItem.Stats,
        BottomNavItem.Profile
    )
    val itemLabels = listOf(s.navNutricion, s.navEntreno, s.navStats, s.navPerfil)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != ROUTE_SETTINGS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = FitwinColors.SurfaceContainerHighest,
                    contentColor = FitwinColors.OnSurface
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = itemLabels[index]) },
                            label = {
                                Text(
                                    text = itemLabels[index],
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().route ?: "") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FitwinColors.PrimaryContainer,
                                unselectedIconColor = FitwinColors.OnSurfaceVariant,
                                selectedTextColor = FitwinColors.PrimaryContainer,
                                unselectedTextColor = FitwinColors.OnSurfaceVariant,
                                indicatorColor = FitwinColors.SurfaceContainerLow
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Food.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Food.route) {
                val foodViewModel: org.example.fitwinkmp.features.food.presentation.FoodViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                org.example.fitwinkmp.ui.food.FoodScreen(viewModel = foodViewModel)
            }
            composable(BottomNavItem.Training.route) {
                val trainingViewModel: org.example.fitwinkmp.features.training.presentation.TrainingViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                org.example.fitwinkmp.ui.training.TrainingScreen(viewModel = trainingViewModel)
            }
            composable(BottomNavItem.Stats.route) {
                val statsViewModel: org.example.fitwinkmp.features.stats.presentation.StatsViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                org.example.fitwinkmp.ui.stats.StatsScreen(viewModel = statsViewModel)
            }
            composable(BottomNavItem.Profile.route) {
                val profileViewModel: org.example.fitwinkmp.features.profile.presentation.ProfileViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                org.example.fitwinkmp.ui.profile.ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = onLogout,
                    onSettingsClick = {
                        navController.navigate(ROUTE_SETTINGS)
                    }
                )
            }
            composable(ROUTE_SETTINGS) {
                org.example.fitwinkmp.ui.settings.SettingsScreen(
                    languageViewModel = languageViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
