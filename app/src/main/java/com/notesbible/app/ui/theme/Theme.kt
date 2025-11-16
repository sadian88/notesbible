package com.notesbible.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1C1B1F),
    secondary = PurpleDark
)

private val DarkColors = darkColorScheme(
    primary = PurpleDark,
    onPrimary = Color.White,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    secondary = PurplePrimary
)

@Composable
fun NotesBibleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
