package com.example.chronicle.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.chronicle.viewmodel.NavigationViewModel

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    surfaceVariant = DarkSurfaceVariant,
    secondaryContainer = DarkSecondaryContainer,
    background = DarkBackground,
    tertiaryContainer = DarkTertiaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.Black,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = Color.Black,
    secondary = LightSecondary,
    onSecondary = Color.Black,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = Color.Black,
    tertiary = LightTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = Color.Black,
    background = LightBackground,
    onBackground = Color.Black,
    surface = LightSurface,
    onSurface = Color.Black,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color.Black,
    error = LightError,
    onError = Color.DarkGray,
    errorContainer = Color.White,
    onErrorContainer = Color.Black,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    inverseSurface = LightInverseSurface,
    scrim = LightScrim,
    surfaceTint = LightSurfaceTint
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApplicationTheme(
    navViewModel: NavigationViewModel,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDarkTheme = navViewModel.isDarkTheme.value || darkTheme

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}




