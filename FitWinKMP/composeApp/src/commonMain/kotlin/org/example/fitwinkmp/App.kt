package org.example.fitwinkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.fitwinkmp.core.localization.LanguageViewModel
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.core.localization.stringsEn
import org.example.fitwinkmp.core.localization.stringsEs
import org.example.fitwinkmp.ui.navigation.AppNavigation
import org.example.fitwinkmp.ui.theme.FitwinTheme

@Composable
@Preview
fun App() {
    val languageViewModel: LanguageViewModel = viewModel()
    val lang by languageViewModel.language.collectAsState()
    val strings = if (lang == "en") stringsEn else stringsEs

    FitwinTheme {
        CompositionLocalProvider(LocalStrings provides strings) {
            AppNavigation(languageViewModel = languageViewModel)
        }
    }
}
