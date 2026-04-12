package com.example.mangabanglalive.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RedBlackColors = darkColorScheme(
    primary = RedPrimary,
    onPrimary = OnDark,
    secondary = RedPrimaryDark,
    onSecondary = OnDark,
    background = BlackBackground,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark
)

@Composable
fun MangaBanglaLiveTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = RedBlackColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
