package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WhatsAppLightGreen,
    onPrimary = Color.Black,
    primaryContainer = WhatsAppDarkTeal,
    onPrimaryContainer = Color.White,
    secondary = WhatsAppTeal,
    onSecondary = Color.White,
    tertiary = WhatsAppLightGreen,
    background = DarkBg,
    onBackground = TextDarkPrimary,
    surface = DarkSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = WhatsAppDarkTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EBE4),
    onPrimaryContainer = WhatsAppDarkTeal,
    secondary = WhatsAppTeal,
    onSecondary = Color.White,
    tertiary = WhatsAppLightGreen,
    background = LightBg,
    onBackground = TextLightPrimary,
    surface = LightSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded WhatsApp emerald styling consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
