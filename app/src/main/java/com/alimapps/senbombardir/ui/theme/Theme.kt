package com.alimapps.senbombardir.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E7F2F),              // Травяной зелёный
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA6DAB5),      // Светло-зелёный контейнер
    onPrimaryContainer = Color(0xFF003917),

    inversePrimary = Color(0xFF61B95E),        // Используется в snackbars и т.д.

    secondary = Color(0xFF1C1C1C),             // Тёмно-серый (цвет мяча, формы)
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCCCCC),    // Светло-серый контейнер
    onSecondaryContainer = Color(0xFF121212),

    tertiary = Color(0xFF005CB2),              // Акцент: синий, как на формах или логотипах
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB3D4FF),
    onTertiaryContainer = Color(0xFF00254F),

    background = Color(0xFFF2F2F2),            // Светлый фон
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,                     // Поверхности: карточки и т.п.
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE1E1E1),        // Альтернативная поверхность
    onSurfaceVariant = Color(0xFF3C3C3C),

    surfaceTint = Color(0xFFFFFFFF),           // Для эффектов тонировки (elevation)

    inverseSurface = Color(0xFF1A1A1A),        // Тёмная поверхность
    inverseOnSurface = Color.White,            // Контент на тёмной поверхности

    error = Color(0xFFD32F2F),                 // Ошибка: красный
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFF5C0000),

    outline = Color(0xFFB0B0B0),               // Границы
    outlineVariant = Color(0xFFE5E5E5),        // Декоративные границы
    scrim = Color(0x80000000)                  // Полупрозрачный чёрный (50%) для затемнений
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicLightColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> LightColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}