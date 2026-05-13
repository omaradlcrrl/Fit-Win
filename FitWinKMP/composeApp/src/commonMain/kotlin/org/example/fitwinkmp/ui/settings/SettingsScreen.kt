package org.example.fitwinkmp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.fitwinkmp.core.localization.LanguageViewModel
import org.example.fitwinkmp.core.localization.LocalStrings
import org.example.fitwinkmp.ui.theme.FitwinColors

@Composable
fun SettingsScreen(
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    val s = LocalStrings.current
    val currentLang by languageViewModel.language.collectAsState()

    Scaffold(
        containerColor = FitwinColors.Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = FitwinColors.PrimaryContainer,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "FIT-WIN",
                    color = FitwinColors.PrimaryContainer,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = s.settingsTitulo,
                color = FitwinColors.OnSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = s.settingsIdioma,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FitwinColors.SurfaceContainer)
            ) {
                IdiomaItem(
                    code = "es",
                    label = s.settingsEspanyol,
                    sublabel = s.settingsEspanyolSub,
                    selected = currentLang == "es",
                    onClick = { languageViewModel.setLanguage("es") }
                )
                HorizontalDivider(
                    color = FitwinColors.OutlineVariant.copy(alpha = 0.4f),
                    modifier = Modifier.padding(start = 56.dp)
                )
                IdiomaItem(
                    code = "en",
                    label = s.settingsIngles,
                    sublabel = s.settingsInglesSub,
                    selected = currentLang == "en",
                    onClick = { languageViewModel.setLanguage("en") }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitwinColors.SurfaceContainerLow)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = FitwinColors.OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = s.settingsInfoText,
                        color = FitwinColors.OnSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = s.settingsVersion,
                color = FitwinColors.OnSurfaceVariant.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun IdiomaItem(
    code: String,
    label: String,
    sublabel: String,
    selected: Boolean,
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
                .background(
                    if (selected) FitwinColors.PrimaryContainer
                    else FitwinColors.SurfaceContainerHighest
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = code.uppercase(),
                color = if (selected) FitwinColors.OnPrimary else FitwinColors.OnSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
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
            Text(
                text = sublabel,
                color = FitwinColors.OnSurfaceVariant,
                fontSize = 11.sp
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = FitwinColors.PrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
