package com.example.evogym.ui.theme

import android.app.Activity
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

val Grey = Color(0xFF817F82)
val SpaceIndigo = Color(0xFF2E2D4D)
val OceanMist = Color(0xFF49BEAA)
val White = Color(0xFFFFFFFF)
val ApricotCream = Color(0xFFF6D8AE)
private val DarkColorScheme = darkColorScheme(
    primary = OceanMist,
    secondary = Grey,
    tertiary = ApricotCream,
    background = SpaceIndigo,
    surface = SpaceIndigo
)

private val LightColorScheme = lightColorScheme(
    primary = SpaceIndigo,
    secondary = OceanMist,
    tertiary = ApricotCream,
    background = White,
    surface = White
)

@Composable
fun EvoGymTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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